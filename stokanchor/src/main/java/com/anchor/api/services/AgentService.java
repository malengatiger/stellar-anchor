package com.anchor.api.services;

import com.anchor.api.controllers.AgentController;
import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.*;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.util.Emoji;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import com.sendgrid.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

@Service
public class AgentService {
    public AgentService() {
        LOGGER.info(Emoji.DRUM + Emoji.DRUM + "AgentService - not quite the Secret Service" + Emoji.DRUM + Emoji.DRUM);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(AgentService.class);
    public static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private AccountService accountService;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private CryptoService cryptoService;

    @Value("${sendgrid}")
    private String sendgridAPIKey;

    @Value("${fromMail}")
    private String fromMail;


    @Value("${agentStartingBalance}")
    private String agentStartingBalance;

    @Value("${clientStartingBalance}")
    private String clientStartingBalance;

    @Value("${minimumInterestRate}")
    private String minimumInterestRate;

    @Value("${maximumInterestRate}")
    private String maximumInterestRate;

    public String removeClient(String clientId) throws Exception {
        return null;
    }

    public List<Client> getAgentClients(String agentId) throws Exception {
        return firebaseService.getAgentClients(agentId);
    }

    public List<LoanApplication> getAgentLoans(String agentId) throws Exception {
        return firebaseService.getAgentLoans(agentId);
    }

    public List<AgentController.PaymentRequest> getPaymentRequests(String anchorId) throws Exception {
        return firebaseService.getPaymentRequests(anchorId);
    }

    public List<LoanPayment> getLoanPayments(String loanId) throws Exception {
        return firebaseService.getLoanPayments(loanId);
    }

    public String approveApplicationByClient(String loanId) throws Exception {

        LoanApplication application = firebaseService.getLoanApplication(loanId);
        if (application == null) {
            throw new Exception(Emoji.NOT_OK + "LoanApplication not found");
        }

        application.setApprovedByClient(true);
        application.setClientApprovalDate(new DateTime().toDateTimeISO().toString());
        String msg = firebaseService.updateLoanApplication(application);
        return msg;
    }

    public LoanApplication approveApplicationByAgent(LoanApplication application) throws Exception {

        Agent agent = firebaseService.getAgent(application.getAgentId());
        if (agent == null) {
            throw new Exception("Agent not found");
        }
        //todo - check agent balance for this asset code ...
        String seed = cryptoService.getDecryptedSeed(agent.getStellarAccountId());
        if (application.getAgentSeed() != null) {
            if (!seed.equalsIgnoreCase(application.getAgentSeed())) {
                throw new Exception(Emoji.ERROR.concat(Emoji.ERROR) + "Bad Agent seed rising");
            }
        }
        if (!application.isApprovedByClient()) {
            LOGGER.info("Check this LoanApplication: apparently not approved by client");
            throw new Exception(Emoji.NOT_OK + "LoanApplication not approved by Client");
        }


        AgentController.PaymentRequest request = new AgentController.PaymentRequest();
        request.setPaymentRequestId(UUID.randomUUID().toString());
        request.setSeed(seed);
        request.setAmount(application.getAmount());
        request.setAnchorId(application.getAnchorId());
        request.setAssetCode(application.getAssetCode());
        request.setDate(new DateTime().toDateTimeISO().toString());
        request.setDestinationAccount(application.getClientAccount());
        SubmitTransactionResponse response = paymentService.sendPayment(request);

        application.setPaid(false);
        application.setApprovedByAgent(true);
        application.setLastPaymentRequestId(request.getPaymentRequestId());
        application.setLastDatePaid(new DateTime().toDateTimeISO().toString());
        application.setAgentApprovalDate(new DateTime().toDateTimeISO().toString());

        firebaseService.updateLoanApplication(application);
        //todo - send email to Client notifying approval and payment
        LOGGER.info(Emoji.HAND2.concat(Emoji.HAND2.concat(Emoji.HAND2).concat(Emoji.LEAF)) +
                "Loan application approved and funds transferred to Client ");
        return application;

    }

    public LoanApplication declineApplication(LoanApplication application) throws Exception {
        application.setApprovedByAgent(false);
        application.setPaid(false);
        application.setLastDatePaid(null);

        firebaseService.updateLoanApplication(application);
        //todo - send email to Client notifying declination
        return application;


    }

    private static DecimalFormat currencyFormat = new DecimalFormat("#.00");

    public LoanApplication addLoanApplication(LoanApplication application) throws Exception {
        //todo - check application for correctness prior to adding ...
        if (application.getInterestRate() == 0.0) {
            throw new Exception("Interest Rate is missing");
        }
        if (application.getAmount() == null) {
            throw new Exception("Amount is missing");
        }
        if (application.getAnchorId() == null) {
            throw new Exception("Anchor is missing");
        }
        if (application.getAgentId() == null) {
            throw new Exception("Agent is missing");
        }
        if (application.getClientId() == null) {
            throw new Exception("Client is missing");
        }
        if (application.getLoanPeriodInMonths() == 0 && application.getLoanPeriodInWeeks() == 0) {
            throw new Exception("LoanPeriod should be > 0");
        }

        double min = Double.parseDouble(minimumInterestRate);
        double max = Double.parseDouble(maximumInterestRate);

        if (application.getInterestRate() < min) {
            throw new Exception("\uD83D\uDE21 Interest Rate must be at least ".concat(minimumInterestRate));
        }
        if (application.getInterestRate() > max) {
            throw new Exception("\uD83D\uDE21 Interest Rate must be less than ".concat(maximumInterestRate));
        }

        if (application.getLoanPeriodInMonths() > 0) {
            double monthlyPayment = calculateMonthlyPayment(
                    application.getAmount(),
                    application.getLoanPeriodInMonths(),
                    application.getInterestRate());

            application.setMonthlyPayment(currencyFormat.format(monthlyPayment));
            double total = monthlyPayment * application.getLoanPeriodInMonths();
            application.setTotalAmountPayable(currencyFormat.format(total));
        }
        if (application.getLoanPeriodInWeeks() > 0) {
            double weeklyPayment = calculateWeeklyPayment(
                    application.getAmount(),
                    application.getLoanPeriodInWeeks(),
                    application.getInterestRate());

            application.setWeeklyPayment(currencyFormat.format(weeklyPayment));
            double total = (weeklyPayment * application.getLoanPeriodInWeeks());
            application.setTotalAmountPayable(currencyFormat.format(total));
        }

        application.setDate(new DateTime().toDateTimeISO().toString());
        application.setLoanId(UUID.randomUUID().toString());
        application.setApprovedByAgent(false);
        application.setApprovedByClient(false);
        firebaseService.addLoanApplication(application);
        return application;
    }

    public static double calculateMonthlyPayment(
            String amount, int loanPeriodInMonths, double interestRate) {
        interestRate /= 100.0;
        double monthlyRate = interestRate / 12.0;
        double loanAmount = Double.parseDouble(amount);
        double monthlyPayment =
                (loanAmount * monthlyRate) /
                        (1 - Math.pow(1 + monthlyRate, -loanPeriodInMonths));
        return monthlyPayment;
    }

    public static double calculateWeeklyPayment(
            String amount, int loanPeriodInWeeks, double interestRate) {

        interestRate /= 100.0;
        // Monthly interest rate
        // is the yearly rate divided by 12

        double weeklyRate = interestRate / 52.0;
        // Calculate the weekly payment
        double loanAmount = Double.parseDouble(amount);
        double weeklyPayment =
                (loanAmount * weeklyRate) /
                        (1 - Math.pow(1 + weeklyRate, -loanPeriodInWeeks));
        return weeklyPayment;
    }

    /**
     * A Client pays off part or all of the outstanding balance on the loan ...
     * that is, the Client is paying the Agent for the loan
     *
     * @param loanPayment
     * @return
     * @throws Exception
     */
    public LoanPayment addLoanPayment(LoanPayment loanPayment) throws Exception {

        if (loanPayment.getAnchorId() == null) {
            throw new Exception("Anchor missing");
        }
        if (loanPayment.getAgentId() == null) {
            throw new Exception("Agent missing");
        }
        if (loanPayment.getAgentAccount() == null) {
            throw new Exception("Agent account missing");
        }
        if (loanPayment.getClientId() == null) {
            throw new Exception("Client missing");
        }
        if (loanPayment.getClientSeed() == null) {
            throw new Exception("Client seed missing");
        }
        if (loanPayment.getAssetCode() == null) {
            throw new Exception("Asset code missing");
        }
        if (loanPayment.getAmount() == null) {
            throw new Exception("Amount is missing");
        }
        //todo - check account balance for this asset before attempting payment
        AccountResponse accountResponse = accountService.getAccountUsingSeed(loanPayment.getClientSeed());
        AccountResponse.Balance balance = null;
        for (AccountResponse.Balance bal : accountResponse.getBalances()) {
            if (!bal.getAssetType().equalsIgnoreCase("native")) {
                if (bal.getAssetCode().equalsIgnoreCase(loanPayment.getAssetCode())) {
                    balance = bal;
                }
            }
        }
        if (balance == null) {
            throw new Exception("Asset Balance is missing");
        }
        double loanAmt = Double.parseDouble(loanPayment.getAmount());
        double balanceAmt = Double.parseDouble(balance.getBalance());
        if (loanAmt > balanceAmt) {
            String msg = "Not enough money in account to cover necessary amount ".concat(Emoji.PIG)
                    .concat(" \uD83C\uDF4E balance: ".concat(balance.getBalance()
                    .concat(" \uD83C\uDF4E loan amount: ").concat(loanPayment.getAmount())));
            LOGGER.info(msg);
            throw new Exception(msg);
        }
        AgentController.PaymentRequest request = new AgentController.PaymentRequest();
        request.setPaymentRequestId(UUID.randomUUID().toString());
        request.setAmount(loanPayment.getAmount());
        request.setSeed(loanPayment.getClientSeed());
        request.setAnchorId(loanPayment.getAnchorId());
        request.setAssetCode(loanPayment.getAssetCode());
        request.setDate(new DateTime().toDateTimeISO().toString());
        request.setDestinationAccount(loanPayment.getAgentAccount());

        SubmitTransactionResponse response = paymentService.sendPayment(request);
        if (response.isSuccess()) {
            //todo - royalties to Anchor and to Agent ...  🍎 set up ROYALTY REGIME!!
            loanPayment.setCompleted(true);
            loanPayment.setPaymentRequestId(request.getPaymentRequestId());
            String res = firebaseService.addLoanPayment(loanPayment);

            LoanApplication app = firebaseService.getLoanApplication(loanPayment.getLoanId());
            app.setLastDatePaid(new DateTime().toDateTimeISO().toString());
            app.setLastPaymentRequestId(request.getPaymentRequestId());
            String res2 = firebaseService.updateLoanApplication(app);

            LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat("...... LoanPayment COMPLETE! "
                    + Emoji.BLUE_DOT.concat(" messages: ")).concat(res).concat(" - ").concat(res2));

            return loanPayment;
        } else {
            throw new Exception("LoanPayment failed");
        }



    }

    public Organization addOrganization(Organization organization) throws Exception {
        //todo - check organization for correctness prior to adding ...
        organization.setOrganizationId(UUID.randomUUID().toString());
        organization.setDateRegistered(new DateTime().toDateTimeISO().toString());
        organization.setDateUpdated(new DateTime().toDateTimeISO().toString());
        firebaseService.addOrganization(organization);
        return organization;
    }

    public String updateClient(com.anchor.api.data.anchor.Client client) throws Exception {

        LOGGER.info(Emoji.LEMON + Emoji.LEMON + "....... updating Client ....... ");
        return firebaseService.updateClient(client);
    }

    @Autowired
    private TOMLService tomlService;
    private Anchor anchor;

    private void setAnchor(String anchorId) throws Exception {
        if (anchor != null) {
            return;
        }
        Toml toml = tomlService.getToml(anchorId);
        if (toml == null) {
            throw new Exception("anchor.toml has not been found. upload the file from your computer");
        } else {
            String id = toml.getString("anchorId");
            anchor = firebaseService.getAnchor(id);
            if (anchor == null) {
                throw new Exception("AgentService:\uD83D\uDE21 \uD83D\uDE21 anchor is missing");
            }
        }


    }

    public com.anchor.api.data.anchor.Client createClient(com.anchor.api.data.anchor.Client client) throws Exception {

        LOGGER.info(Emoji.LEMON + Emoji.LEMON +
                "....... creating Client ....... ");
        setAnchor(client.getAnchorId());
        Agent agent = firebaseService.getAgent(client.getAgentId());
        if (anchor == null) {
            throw new Exception("Missing anchor");
        }
        Client mClient = firebaseService.getClientByNameAndAnchor(
                anchor.getAnchorId(),
                client.getPersonalKYCFields().getFirst_name(),
                client.getPersonalKYCFields().getLast_name());

        if (mClient != null) {
            LOGGER.info(Emoji.ERROR + "Client already exists for this Anchor: ".concat(anchor.getName())
                    .concat(Emoji.ERROR));
            throw new Exception(Emoji.ERROR + "Client already exists for this Anchor");
        }

        AccountResponseBag bag = accountService.createAndFundUserAccount(
                client.getAnchorId(),
                clientStartingBalance,
                client.getStartingFiatBalance(), agent.getFiatLimit());
        LOGGER.info(Emoji.HEART_PURPLE + Emoji.HEART_PURPLE +
                "Client Stellar account has been created and funded with ... "
                        .concat(clientStartingBalance).concat(" XLM"));

        List<AccountService.AssetBag> assetBags = accountService.getDefaultAssets(
                anchor.getIssuingAccount().getAccountId());
        LOGGER.info(Emoji.WARNING.concat(Emoji.WARNING) + "createClient:.... Creating Client Fiat Trustlines .... userSeed: "
                .concat(bag.getSecretSeed()));
        for (AccountService.AssetBag assetBag : assetBags) {
            accountService.createTrustLine(
                    anchor.getIssuingAccount().getAccountId(),
                    bag.getSecretSeed(),
                    agent.getFiatLimit(),
                    assetBag.assetCode);

        }
        encryptAndSave(client, bag);
        return client;
    }

    private void encryptAndSave(Client client, AccountResponseBag bag) throws Exception {
        //create firebase auth user
        UserRecord record = firebaseService.createUser(client.getFullName(),
                client.getPersonalKYCFields().getEmail_address(), client.getPassword());
        client.setClientId(record.getUid());
        client.setDateRegistered(new DateTime().toDateTimeISO().toString());
        client.setDateUpdated(new DateTime().toDateTimeISO().toString());
        //handle seed encryption
        cryptoService.encrypt(bag.getAccountResponse().getAccountId(), bag.getSecretSeed());
        client.setAccount(bag.getAccountResponse().getAccountId());
        client.setExternalAccountId("Not Known Yet");
        String savePassword = client.getPassword();
        client.setPassword(null);
        firebaseService.addClient(client);
        sendEmail(client);
        client.setPassword(savePassword);
        client.setSecretSeed(bag.getSecretSeed());
        LOGGER.info((Emoji.BLUE_DOT + Emoji.BLUE_DOT +
                "Client has been added to Firestore ").concat(client.getFullName()));
    }

    public String updateAgent(Agent agent) throws Exception {

        LOGGER.info(Emoji.LEMON + Emoji.LEMON + "....... updating Agent ....... ");
        return firebaseService.updateAgent(agent);
    }

    public Agent createAgent(Agent agent) throws Exception {
        //todo - create Stellar account; add to Firestore;
        LOGGER.info(Emoji.YELLOW_STAR + Emoji.YELLOW_STAR + Emoji.YELLOW_STAR +
                "....... creating Agent ....... ");
        setAnchor(agent.getAnchorId());
        if (anchor == null) {
            anchor = firebaseService.getAnchor(agent.getAnchorId());
        }
        Agent mAgent = firebaseService.getAgentByNameAndAnchor(
                anchor.getAnchorId(),
                agent.getPersonalKYCFields().getFirst_name(),
                agent.getPersonalKYCFields().getLast_name());
        if (mAgent != null) {
            LOGGER.info(Emoji.ERROR.concat(anchor.getName())
                    .concat(" ").concat(Emoji.ERROR));
            throw new Exception(Emoji.ERROR + "Agent already exists for this Anchor");
        }

        AccountResponseBag bag = accountService.createAndFundUserAccount(
                agent.getAnchorId(),
                agentStartingBalance,
                agent.getFiatBalance(), agent.getFiatLimit());
        LOGGER.info(Emoji.RED_APPLE + Emoji.RED_APPLE +
                "Agent Stellar account has been created and funded with ... "
                        .concat(agentStartingBalance).concat(" XLM; secretSeed: ".concat(bag.getSecretSeed())));
        cryptoService.encrypt(bag.getAccountResponse().getAccountId(), bag.getSecretSeed());

        //todo - create trustlines and pay from anchor distribution account
        List<AccountService.AssetBag> assetBags = accountService.getDefaultAssets(
                anchor.getIssuingAccount().getAccountId());

        String issuingAccountSeed = cryptoService.getDecryptedSeed(anchor.getIssuingAccount().getAccountId());

        LOGGER.info(Emoji.WARNING.concat(Emoji.WARNING) + "createAgent:.... Creating Agent Fiat Asset Balances .... userSeed: "
                .concat(bag.getSecretSeed()));
        //todo - what, exactly is limit?
        for (AccountService.AssetBag assetBag : assetBags) {
            accountService.createTrustLine(anchor.getIssuingAccount().getAccountId(), bag.getSecretSeed(), agent.getFiatLimit(),
                    assetBag.assetCode);
            accountService.createAsset(issuingAccountSeed, anchor.getDistributionAccount().getAccountId(),
                    assetBag.assetCode, agent.getFiatBalance());
        }
        //create firebase auth user
        String savedPassword;
        try {
            agent.setStellarAccountId(bag.getAccountResponse().getAccountId());
            agent.setExternalAccountId("Not Known Yet");

            UserRecord record = firebaseService.createUser(agent.getFullName(),
                    agent.getPersonalKYCFields().getEmail_address(), agent.getPassword());
            agent.setAgentId(record.getUid());
            agent.setDateRegistered(new DateTime().toDateTimeISO().toString());
            agent.setDateUpdated(new DateTime().toDateTimeISO().toString());
            savedPassword = agent.getPassword();
            agent.setPassword(null);
            firebaseService.addAgent(agent);
            sendEmail(agent);
            LOGGER.info((Emoji.LEAF + Emoji.LEAF +
                    "Agent has been added to Firestore without seed or password ").concat(agent.getFullName()));
            agent.setPassword(savedPassword);
            agent.setSecretSeed(bag.getSecretSeed());
        } catch (Exception e) {
            String msg = Emoji.NOT_OK.concat(Emoji.ERROR)
                    .concat("Firebase error: ".concat(e.getMessage()));
            LOGGER.info(msg);
            throw new Exception(msg);
        }


        return agent;
    }

    @Autowired
    private PaymentService paymentService;

    private void sendEmail(Agent agent) throws IOException {

        LOGGER.info(Emoji.PLANE + Emoji.PLANE + "Sending registration email to user: "
                + agent.getPersonalKYCFields().getEmail_address());

        //todo - finish registration email composition, links and all, html etc.
        StringBuilder sb = new StringBuilder();
        sb.append("Hi Anchor Admin,\n");
        sb.append("Welcome to The Anchor Network\n");
        sb.append("Click on this link to complete the registration\n");
        sb.append("\nRegards,\n");
        sb.append("Anchor Network Team");

        LOGGER.info(Emoji.RAIN_DROPS + Emoji.RAIN_DROP + "SendGrid: send mail from: " + fromMail);

        Email from = new Email(fromMail);
        String subject = "Welcome to Anchor registration";
        Email to = new Email(agent.getPersonalKYCFields().getEmail_address());
        Content content = new Content("text/plain", sb.toString());
        Mail mail = new Mail(from, subject, to, content);
// 🌼
        LOGGER.info(Emoji.FLOWER_YELLOW + Emoji.FLOWER_YELLOW + "SendGrid apiKey: " + sendgridAPIKey);
        SendGrid sg = new SendGrid(sendgridAPIKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            LOGGER.info(Emoji.BLUE_DISC + Emoji.BLUE_DISC +
                    "Registration email sent to Anchor user: "
                    + Emoji.BLUE_THINGY + agent.getFullName() + Emoji.PANDA +
                    " status code: " + response.getStatusCode());
            LOGGER.info(Emoji.FLOWER_YELLOW + Emoji.FLOWER_YELLOW + "SendGrid: " +
                    " response headers: " + response.getHeaders());

        } catch (IOException ex) {
            throw ex;
        }

    }

    private void sendEmail(Client agent) throws IOException {

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Sending registration email to user: "
                + agent.getPersonalKYCFields().getEmail_address());

        //todo - finish registration email composition, links and all, html etc.
        StringBuilder sb = new StringBuilder();
        sb.append("Hi Anchor Admin,\n");
        sb.append("Welcome to The Anchor Network\n");
        sb.append("Click on this link to complete the registration\n");
        sb.append("\nRegards,\n");
        sb.append("Anchor Network Team");

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid: send mail from: " + fromMail);

        Email from = new Email(fromMail);
        String subject = "Welcome to Anchor registration";
        Email to = new Email(agent.getPersonalKYCFields().getEmail_address());
        Content content = new Content("text/plain", sb.toString());
        Mail mail = new Mail(from, subject, to, content);

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid apiKey: " + sendgridAPIKey);
        SendGrid sg = new SendGrid(sendgridAPIKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Registration email sent to Anchor user: \uD83E\uDD66  " + agent.getFullName() +
                    " \uD83E\uDD66 status code: " + response.getStatusCode());
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid: " +
                    " response headers: " + response.getHeaders());

        } catch (IOException ex) {
            throw ex;
        }

    }
}
