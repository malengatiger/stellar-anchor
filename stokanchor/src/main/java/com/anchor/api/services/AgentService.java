package com.anchor.api.services;

import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.*;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.util.Emoji;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sendgrid.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;
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

    @Value("${anchorName}")
    private String anchorName;

    @Value("${agentStartingBalance}")
    private String agentStartingBalance;

    @Value("${clientStartingBalance}")
    private String clientStartingBalance;

    public String removeClient(String clientId) throws Exception {
        return null;
    }

    public List<Client> getAgentClients(String agentId) throws Exception {
        return firebaseService.getAgentClients(agentId);
    }

    public List<LoanApplication> getAgentLoans(String agentId) throws Exception {
        return firebaseService.getAgentLoans(agentId);
    }

    public List<LoanPayment> getLoanPayments(String loanId) throws Exception {
        return firebaseService.getLoanPayments(loanId);
    }

    public LoanApplication approveApplication(LoanApplication application) throws Exception {

        LOGGER.info(Emoji.HEART_GREEN.concat(Emoji.HEART_GREEN)
        .concat("Approve LoanApplication: ".concat(G.toJson(application))));
        Agent agent = firebaseService.getAgent(application.getAgentId());
        if (agent == null) {
            throw new Exception("Agent not found");
        }
        //todo - check agent balance for this asset code ...
        String seed = cryptoService.getDecryptedSeed(agent.getStellarAccountId());
        LOGGER.info(Emoji.HAND1.concat(Emoji.HAND2) + "Agent seed for eventual payment to Client: ".concat(seed));
        if (application.getAgentSeed() != null) {
            if (!seed.equalsIgnoreCase(application.getAgentSeed())) {
                throw new Exception(Emoji.ERROR.concat(Emoji.ERROR) + "Bad seed rising");
            }
        } else {
            throw new Exception("Agent seed missing");
        }
        application.setApproved(true);
        boolean ok = sendPayment(seed, application.getAssetCode(), application.getAmount(),
                application.getClientAccount());
        application.setPaid(ok);
        if (ok) {
            application.setDatePaid(new DateTime().toDateTimeISO().toString());
            firebaseService.updateLoanApplication(application);
            //todo - send email to Client notifying approval and payment
            LOGGER.info(Emoji.OK.concat(Emoji.OK.concat(Emoji.OK)) +
                    "Loan application approved and funds transferred to Client "
                            .concat(G.toJson(application)));
            return application;
        } else {
            String msg = Emoji.NOT_OK.concat(Emoji.ERROR).concat(Emoji.ERROR)
                    .concat("LoanApplication Approval Failed, looks like Payment fell down");
            LOGGER.info(msg);
            throw new Exception(msg);
        }
    }

    public LoanApplication declineApplication(LoanApplication application) throws Exception {
        application.setApproved(false);
        application.setPaid(false);
        application.setDatePaid(null);

        firebaseService.updateLoanApplication(application);
        //todo - send email to Client notifying declination
        return application;


    }

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
        if (application.getLoanPeriodInMonths() == 0) {
            throw new Exception("LoanPeriodInMonths should be > 0");
        }
        application.setDate(new DateTime().toDateTimeISO().toString());
        application.setLoanId(UUID.randomUUID().toString());
        application.setApproved(false);
        String msg = firebaseService.addLoanApplication(application);
        LOGGER.info(msg);
        return application;
    }

    /**
     * A Client pays off part or all of the outstanding balance on the loan ...
     * that is, the Client is paying the Agent for the loan
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
        SubmitTransactionResponse response = paymentService.sendPayment(
                loanPayment.getClientSeed(),
                loanPayment.getAssetCode(),
                loanPayment.getAmount(),
                loanPayment.getAgentAccount());

        if (response.isSuccess()) {
            //todo - royalties to Anchor and to Agent ...  🍎 set up ROYALTY REGIME!!
            loanPayment.setCompleted(true);
            firebaseService.addLoanPayment(loanPayment);
            return loanPayment;
        } else {
            String msg = Emoji.ERROR + "LoanPayment failed";
            LOGGER.info(msg);
            throw new Exception(msg);
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

    public com.anchor.api.data.anchor.Client createClient(com.anchor.api.data.anchor.Client client) throws Exception {

        LOGGER.info(Emoji.LEMON + Emoji.LEMON +
                "....... creating Client ....... ");
        Anchor anchor = firebaseService.getAnchorByName(anchorName);
        Agent agent = firebaseService.getAgent(client.getAgentId());
        if (anchor == null) {
            throw new Exception("Missing anchor");
        }
        Client mClient = firebaseService.getClientByNameAndAnchor(
                anchor.getAnchorId(),
                client.getPersonalKYCFields().getFirst_name(),
                client.getPersonalKYCFields().getLast_name());

        if (mClient != null) {
            LOGGER.info(Emoji.ERROR + "Client already exists for this Anchor: ".concat(anchorName)
                    .concat(Emoji.ERROR));
            throw new Exception(Emoji.ERROR + "Client already exists for this Anchor");
        }

        AccountResponseBag bag = accountService.createAndFundUserAccount(
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
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF.concat(Emoji.LEAF))+
                "Client created OK: ".concat(G.toJson(client)));
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
                "Client has been added to Firestore ").concat(G.toJson(client)));
    }

    public String updateAgent(Agent agent) throws Exception {

        LOGGER.info(Emoji.LEMON + Emoji.LEMON + "....... updating Agent ....... ");
        return firebaseService.updateAgent(agent);
    }

    public Agent createAgent(Agent agent) throws Exception {
        //todo - create Stellar account; add to Firestore;
        LOGGER.info(Emoji.YELLOW_STAR + Emoji.YELLOW_STAR + Emoji.YELLOW_STAR +
                "....... creating Agent ....... ");
        Anchor anchor = firebaseService.getAnchorByName(anchorName);
        if (anchor == null) {
            throw new Exception("Missing anchor");
        }
        Agent mAgent = firebaseService.getAgentByNameAndAnchor(
                anchor.getAnchorId(),
                agent.getPersonalKYCFields().getFirst_name(),
                agent.getPersonalKYCFields().getLast_name());
        if (mAgent != null) {
            LOGGER.info(Emoji.ERROR.concat(anchorName)
                    .concat(" ").concat(Emoji.ERROR));
            throw new Exception(Emoji.ERROR + "Agent already exists for this Anchor");
        }

        AccountResponseBag bag = accountService.createAndFundUserAccount(agentStartingBalance,
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
            accountService.createTrustLine(anchor.getIssuingAccount().getAccountId(),bag.getSecretSeed(),agent.getFiatLimit(),
                    assetBag.assetCode);
            accountService.createAsset(issuingAccountSeed,anchor.getDistributionAccount().getAccountId(),
                    assetBag.assetCode, agent.getFiatBalance());
        }
        //create firebase auth user
        String savedPassword;
        try {
            LOGGER.info(Emoji.LIGHTNING.concat(Emoji.LIGHTNING.concat(Emoji.LIGHTNING))
            .concat("Check agent, password seems to be missing .......".concat(G.toJson(agent))));
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
                    "Agent has been added to Firestore without seed or password ").concat(G.toJson(agent)));
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

    public boolean sendPayment(String seed, String assetCode, String amount,
                               String destinationAccount) throws Exception {
        LOGGER.info(Emoji.DICE.concat(Emoji.DICE) + ".... Sending to PaymentService ....");
        SubmitTransactionResponse response = paymentService.sendPayment(
                seed, assetCode, amount, destinationAccount);
        LOGGER.info(Emoji.LEAF + Emoji.RED_APPLE +
                "Payment was successful?? : " + response.isSuccess() + " " + Emoji.RED_APPLE);
        return response.isSuccess();
    }

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
