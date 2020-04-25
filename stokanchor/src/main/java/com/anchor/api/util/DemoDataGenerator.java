package com.anchor.api.util;

import com.anchor.api.controllers.AgentController;
import com.anchor.api.data.anchor.*;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.data.transfer.sep9.OrganizationKYCFields;
import com.anchor.api.data.transfer.sep9.PersonalKYCFields;
import com.anchor.api.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

@Service
public class DemoDataGenerator {
    public static final Logger LOGGER = Logger.getLogger(DemoDataGenerator.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public DemoDataGenerator() {
        LOGGER.info(Emoji.RED_CAR.concat(Emoji.RED_CAR) + "Demo data DemoDataGenerator ready and able!");
    }

    public static final String FUNDING_ACCOUNT = "GAGAJ2CFIYCE2VJDA7PZGVFYAC3EFHUI7M3RFSFXB47TBSHHQF3X4Y3D",
            FUNDING_SEED = "SDHJPRWY4345IPVD7H2CH7VKFMBELW2ZYFPSRAVDYDSCU6ZD5EYCBFUH";
    @Autowired
    private ApplicationContext context;
    @Autowired
    private AnchorAccountService anchorAccountService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private FirebaseService firebaseService;
    @Value("${status}")
    private String status;


    @Value("${basePassword}")
    private String basePassword;

    Anchor anchor;

    public Anchor startAnchor(String anchorName) throws Exception {
        if (!status.equalsIgnoreCase("dev")) {
            throw new Exception(Emoji.NOT_OK + "Demo Data Generation may not be run in PRODUCTION");
        }
        deleteFirebaseArtifacts();
        Anchor mAnchor = addAnchor(anchorName);
        LOGGER.info(Emoji.FERN.concat(Emoji.FIRE.concat(Emoji.FIRE))
                + "Start Anchor complete. Complete generation after copying anchorId to anchor.toml");
        return mAnchor;
    }

    public void startGeneration(String anchorId) throws Exception {
        if (!status.equalsIgnoreCase("dev")) {
            throw new Exception(Emoji.NOT_OK + "Demo Data Generation may not be run in PRODUCTION");
        }
        if (anchorId == null) {
            throw new Exception(Emoji.NOT_OK + "Anchor ID missing");
        }
        anchor = firebaseService.getAnchor(anchorId);
        if (anchor == null) {
            throw new Exception("Generator: Anchor is missing: " + anchorId);
        }
        File file = new File("anchor.toml");
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD Do We Have A File? ...".concat(file.getAbsolutePath()));
        if (file.exists()) {
            tomlService.encryptAndUploadFile(anchor.getAnchorId(), file);
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E anchor.toml has been encrypted and uploaded via KMS \n" +
                    "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ANCHOR TOML uploaded OK!\n");
        }
        LOGGER.info(Emoji.HEART_BLUE + "Start Data Generation "
                .concat(Emoji.HEART_BLUE.concat(Emoji.HEART_BLUE)));
        //

        //add data

        addAgents();
        addAgentClients();

        LOGGER.info("\n\n\n".concat(Emoji.PRETZEL.concat(Emoji.PRETZEL.concat(Emoji.PRETZEL)
                .concat(Emoji.ALIEN.concat(Emoji.ALIEN))
                .concat(" ......... starting Agent funding, LoanApplications generation " +
                        " \uD83C\uDF3C \uD83C\uDF3C "))));
        generateAgentFunding(anchor.getAnchorId());
        generateLoanApplications(anchor.getAnchorId());

//        LOGGER.info("\n\n\n".concat(Emoji.PRETZEL.concat(Emoji.PRETZEL.concat(Emoji.PRETZEL)
//                .concat(Emoji.PANDA.concat(Emoji.PANDA))
//                .concat(" ......... LoanApplication approvals " +
//                        "and payments to clients ...  \uD83C\uDF3C \uD83C\uDF3C "))));
//        generateLoanApprovals(anchor.getAnchorId());
//
//        LOGGER.info("\n\n\n".concat(Emoji.PRETZEL.concat(Emoji.PRETZEL.concat(Emoji.PRETZEL)
//                .concat(Emoji.BUTTERFLY.concat(Emoji.BUTTERFLY))
//                .concat(" ......... starting LoanPayment settling ... Clients paying back the loans " +
//                        " \uD83C\uDF3C \uD83C\uDF3C "))));
//        generatePayments(anchor.getAnchorId());

        //for testing
        //todo - retrieve data for overall status of anchor ...
        // ... (anchor, agent, client dashboard basics here ....)

        //stokvel shit
//        LOGGER.info("\n\n\n".concat(Emoji.PRETZEL.concat(Emoji.PRETZEL.concat(Emoji.PRETZEL)
//                .concat(" ......... starting STOKVEL data generation ....  \uD83C\uDF3C \uD83C\uDF3C ")
//                .concat(Emoji.WARNING))));
//        Stokvel stokvel = generateStokvel(anchor.getAnchorId());
//        generateStokvelMembers(stokvel.getStokvelId(), anchor.getAnchorId());

    }

    private void setAnchor(String anchorId) throws Exception {
        if (anchor != null) {
            return;
        }
        Toml toml = tomlService.getToml(anchorId);
        if (toml == null) {
            throw new Exception("anchor.toml has not been found. upload the file from your computer");
        } else {
            String name = toml.getString("anchorId");
            anchor = firebaseService.getAnchor(name);
        }


    }

    public void generatePayments(String anchorId) throws Exception {
        LOGGER.info(Emoji.STAR.concat(Emoji.STAR.concat(Emoji.STAR.concat(Emoji.STAR.concat(Emoji.STAR)
                .concat(".......... Generating client payments for all loans until the bitter end !..... ")
                .concat(Emoji.HAND2.concat(Emoji.HAND2))))));
        setAnchor(anchorId);
        List<Agent> agents = firebaseService.getAgents(anchorId);
        DateTime now = new DateTime();
        long day = 1000 * 60 * 60 * 24;
        for (Agent agent : agents) {
            List<LoanApplication> list = firebaseService.getAgentLoans(agent.getAgentId());
            Collections.sort(list, Comparator.comparing(LoanApplication::getDate));
            for (LoanApplication application : list) {
                DateTime mDate = DateTime.parse(application.getLastDatePaid());
                if (now.getMillis() - mDate.getMillis() < day ) {
                    continue;
                }
                if (application.isApprovedByAgent() && application.isApprovedByClient()) {
                    if (!application.isPaid()) {
                        if (application.getLoanPeriodInMonths() > 0) {
                            generateMonthlyPayments(application);
                        }
                        if (application.getLoanPeriodInWeeks() > 0) {
                            generateWeeklyPayments(application);
                        }
                    }
                }
            }
        }

    }

    private void generateMonthlyPayments(LoanApplication application) throws Exception {
//        List<LoanPayment> payments = firebaseService.getLoanPayments(
//                application.getLoanId());
        int numberOfPayments = application.getLoanPeriodInMonths();
        Agent agent = firebaseService.getAgent(application.getAgentId());
        Client client = firebaseService.getClientById(application.getClientId());
        String clientSeed = cryptoService.getDecryptedSeed(client.getAccount());
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < numberOfPayments; i++) {
            LoanPayment payment = new LoanPayment();
            payment.setAgentAccount(agent.getStellarAccountId());
            payment.setClientSeed(clientSeed);
            payment.setAgentId(client.getAgentId());
            payment.setClientId(client.getClientId());
            payment.setAnchorId(client.getAnchorId());
            payment.setAssetCode(application.getAssetCode());
            payment.setAmount(application.getMonthlyPayment());
            payment.setLoanId(application.getLoanId());
            //calculate next monthly date
            DateTime dateTime1 = DateTime.parse(application.getDate());
            cal.set(dateTime1.getYear(), dateTime1.getMonthOfYear(), dateTime1.getDayOfMonth());
            int lastDate = cal.getActualMaximum(Calendar.DATE);
            DateTime dateTime = dateTime1.plusMonths(i + 1);
            payment.setDate(dateTime.toDateTimeISO().toString());

            try {
                sendPaymentAndSaveOnFuckingDatabase(application, client, clientSeed, payment);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info(Emoji.NOT_OK.concat(Emoji.NOT_OK)
                        + "This MONTHLY payment did not happen: " + e.getMessage() == null ? "" : e.getMessage());
                if (e instanceof PaymentService.UnderFundedException) {
                    LOGGER.info("\n\n\n".concat(Emoji.PIG.concat(Emoji.PIG.concat(Emoji.PIG) +
                            "Client ".concat(client.getFullName()).concat(" has run out of MONTHLY money.  \uD83C\uDFB2 SUCKS!! .... \uD83D\uDD35 "))));
                }
                break;
            }

        }
    }

    private void sendPaymentAndSaveOnFuckingDatabase(LoanApplication application, Client client,
                                                     String clientSeed, LoanPayment payment) throws Exception {

        if (!isWithinBalance(payment)) {
            return;
        }
        AgentController.PaymentRequest request = new AgentController.PaymentRequest();
        request.setSeed(clientSeed);
        request.setPaymentRequestId(UUID.randomUUID().toString());
        request.setAmount(payment.getAmount());
        request.setAnchorId(anchor.getAnchorId());
        request.setAssetCode(application.getAssetCode());
        request.setDate(new DateTime().toDateTimeISO().toString());
        request.setDestinationAccount(payment.getAgentAccount());

        SubmitTransactionResponse response = paymentService.sendPayment(request);
        if (response.isSuccess()) {
            payment.setLedger(response.getLedger());
            payment.setOnTime(true);
            try {
                payment = agentService.addLoanPayment(payment);
                payment.setPaymentRequestId(request.getPaymentRequestId());
                LOGGER.info(Emoji.PRETZEL.concat(Emoji.PRETZEL.concat(Emoji.HAPPY)) +
                        "MONTHLY LoanPayment made on Stellar and stored in database; getPaymentRequestId: "
                                .concat(payment.getPaymentRequestId())
                                .concat(" " + Emoji.RED_TRIANGLE.concat(Emoji.RED_TRIANGLE)));
            } catch (Exception e) {
                //ignore
                if (e.getMessage() == null) {
                    LOGGER.info(Emoji.ERROR.concat("Unknown error"));
                    e.printStackTrace();
                } else {
                    LOGGER.info(Emoji.ERROR.concat(e.getMessage()));
                }

            }
        } else {
            LOGGER.info("LoanPayment fucked. what the ? ".concat(Emoji.NOT_OK));
            throw new Exception("Payment failed");
        }

    }

    private void generateWeeklyPayments(LoanApplication application) throws Exception {



        int numberOfPayments = application.getLoanPeriodInWeeks();
        Agent agent = firebaseService.getAgent(application.getAgentId());
        Client client = firebaseService.getClientById(application.getClientId());
        String clientSeed = cryptoService.getDecryptedSeed(client.getAccount());
        for (int i = 0; i < numberOfPayments; i++) {
            LoanPayment payment = new LoanPayment();
            payment.setAgentAccount(agent.getStellarAccountId());
            payment.setClientSeed(clientSeed);
            payment.setAgentId(client.getAgentId());
            payment.setClientId(client.getClientId());
            payment.setAnchorId(client.getAnchorId());
            payment.setAssetCode(application.getAssetCode());
            payment.setAmount(application.getWeeklyPayment());
            payment.setLoanId(application.getLoanId());
            //calculate next weekly date
            DateTime dateTime1 = DateTime.parse(application.getDate());
            DateTime dateTime = dateTime1.plusDays((i + 1) * 7);
            payment.setDate(dateTime.toDateTimeISO().toString());

            try {
                sendAndSave(application, client, clientSeed, payment);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info(Emoji.PIG.concat(Emoji.PIG.concat(Emoji.PIG))
                        .concat("This WEEKLY payment failed ".concat(e.getMessage() == null ? "" : e.getMessage())));
                if (e instanceof PaymentService.UnderFundedException) {
                    LOGGER.info("\n\n\n".concat(Emoji.PIG.concat(Emoji.PIG.concat(Emoji.PIG) +
                            "Client ".concat(client.getFullName()).concat(" has run out of WEEKLY money.  \uD83C\uDF51 SUCKS!! .... \uD83D\uDD35 "))));
                }
                break;
            }


        }
    }

    private boolean isWithinBalance(LoanPayment loanPayment) throws Exception {
        //todo - check account balance for this asset before attempting payment
        AccountResponse accountResponse = accountService.getAccountUsingSeed(loanPayment.getClientSeed());
        AccountResponse.Balance balance = null;
        try {
            for (AccountResponse.Balance bal : accountResponse.getBalances()) {
                if (!bal.getAssetType().equalsIgnoreCase("native")) {
                    if (bal.getAssetCode().equalsIgnoreCase(loanPayment.getAssetCode())) {
                        balance = bal;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
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
            return false;
        } else {
            LOGGER.info("LoanPayment is less than the account balance. returning TRUE");
            return true;
        }
    }

    private void sendAndSave(LoanApplication application, Client client, String clientSeed, LoanPayment payment) throws Exception {


        if (!isWithinBalance(payment)) {
            return;
        }
        AgentController.PaymentRequest request = new AgentController.PaymentRequest();
        request.setSeed(clientSeed);
        request.setPaymentRequestId(UUID.randomUUID().toString());
        request.setAmount(payment.getAmount());
        request.setAnchorId(anchor.getAnchorId());
        request.setAssetCode(application.getAssetCode());
        request.setDate(new DateTime().toDateTimeISO().toString());
        //
        request.setDestinationAccount(payment.getAgentAccount());
        SubmitTransactionResponse response = paymentService.sendPayment(request);

        if (response.isSuccess()) {
            payment.setLedger(response.getLedger());
            payment.setOnTime(true);
            payment.setPaymentRequestId(request.getPaymentRequestId());

            try {
                payment = agentService.addLoanPayment(payment);
                LOGGER.info(Emoji.PRETZEL.concat(Emoji.PRETZEL) +
                        "Weekly LoanPayment made on Stellar and stored in database; getPaymentRequestId: "
                                .concat(payment.getPaymentRequestId())
                                .concat(Emoji.RED_TRIANGLE));
            } catch (Exception e) {
                //ignore
                if (e.getMessage() == null) {
                    LOGGER.info(Emoji.ERROR.concat("Unknown LoanPayment error"));
                    e.printStackTrace();
                } else {
                    LOGGER.info(Emoji.ERROR.concat(e.getMessage()));
                }

            }

        } else {
            LOGGER.info("Weekly Payment is fucked!");
        }

    }

    private void deleteFirebaseArtifacts() throws Exception {
        //delete users and collections
        firebaseService.deleteAuthUsers();
        LOGGER.info(Emoji.SOCCER_BALL.concat(Emoji.SOCCER_BALL)
                + "Firebase auth users have been cleaned out");
        firebaseService.deleteCollections();
        LOGGER.info(Emoji.BASKET_BALL.concat(Emoji.BASKET_BALL)
                + "Firestore collections have been cleaned out");
    }

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StokvelService stokvelService;
    @Autowired
    private CryptoService cryptoService;

    public void generateLoanApprovals(String anchorId) throws Exception {
        LOGGER.info("\n\n" + Emoji.BLUE_DOT.concat(Emoji.BLUE_DOT)
                .concat("======================= Approval of LoanApplication by Clients and Agents ..."));
        setAnchor(anchorId);
        int cnt = 0;
        agents = firebaseService.getAgents(anchor.getAnchorId());
        for (Agent agent : agents) {
            List<LoanApplication> loanApplications = firebaseService.getAgentLoans(agent.getAgentId());
            for (LoanApplication loanApplication : loanApplications) {
                if (!loanApplication.isApprovedByClient()) {
                    agentService.approveApplicationByClient(loanApplication.getLoanId());
                    cnt++;
                }
            }
        }
        LOGGER.info(Emoji.BLUE_DOT.concat(Emoji.BLUE_DOT.concat(Emoji.BLUE_DOT))
                .concat(" .... Clients have completed " + cnt + " loan approvals"));
        int cnt2 = 0;
        for (Agent agent : agents) {
            List<LoanApplication> loanApplications = firebaseService.getAgentLoans(agent.getAgentId());
            for (LoanApplication loanApplication : loanApplications) {
                String seed = cryptoService.getDecryptedSeed(agent.getStellarAccountId());
                loanApplication.setAgentSeed(seed);
                try {
                    if (!loanApplication.isApprovedByAgent()) {
                        agentService.approveApplicationByAgent(loanApplication);
                        cnt2++;
                    }
                } catch (Exception e) {
                    LOGGER.info(Emoji.NOT_OK + "Bad shit, IGNORED ... : ".concat(e.getMessage()));
                }
            }
        }
        LOGGER.info(Emoji.BLUE_DOT.concat(Emoji.BLUE_DOT.concat(Emoji.BLUE_DOT))
                .concat(" .... Agents have completed " + cnt2 + " loan approvals"));
    }

    public void generateAgentFunding(String anchorId) throws Exception {
        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR.concat(Emoji.PEACH)) +
                "...... Generating agent funds ...".concat(Emoji.PEAR.concat(Emoji.PEAR)));
        setAnchor(anchorId);
        agents = firebaseService.getAgents(anchor.getAnchorId());
        List<AccountService.AssetBag> assetBags = accountService.getDefaultAssets(
                anchor.getIssuingAccount().getAccountId());
        String seed = cryptoService.getDecryptedSeed(anchor.getDistributionAccount().getAccountId());
        int cnt = 0;
        for (Agent agent : agents) {
            for (AccountService.AssetBag assetBag : assetBags) {
                AgentController.PaymentRequest paymentRequest = new AgentController.PaymentRequest();
                paymentRequest.setSeed(seed);
                paymentRequest.setPaymentRequestId(UUID.randomUUID().toString());
                paymentRequest.setAnchorId(anchor.getAnchorId());
                paymentRequest.setAssetCode(assetBag.getAssetCode());
                paymentRequest.setDate(new DateTime().toDateTimeISO().toString());
                paymentRequest.setDestinationAccount(agent.getStellarAccountId());
                paymentRequest.setAmount(getRandomAgentAmount());
                try {
                    SubmitTransactionResponse response = paymentService.sendPayment(paymentRequest);
                    if (response.isSuccess()) {
                        cnt++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.info("Agent funding failed; ignored for this purpose: ");
                }
            }
        }
        LOGGER.info(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD)
                .concat(" Agent Funding complete ...............  \uD83C\uDF51 " +
                        "total funding transactions: " + cnt)));
    }

    public Stokvel generateStokvel(String anchorId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH).concat("Generate Stokvel"));
        Stokvel stokvel = new Stokvel();
        stokvel.setName("OriginalGroup II");
        stokvel.setAnchorId(anchorId);
        stokvel.setPassword(basePassword);
        stokvel.setActive(true);
        stokvel.setDate(new DateTime().toDateTimeISO().toString());
        OrganizationKYCFields fields = new OrganizationKYCFields();
        fields.setEmail("stokvel_".concat("" + System.currentTimeMillis()).concat("@modernachor.com"));
        stokvel.setKycFields(fields);
        stokvel = stokvelService.createStokvel(stokvel);
        LOGGER.info(Emoji.LEMON.concat(Emoji.LEMON.concat(Emoji.LEMON)
                .concat("Stokvel generated: ".concat(stokvel.getName()))));
        return stokvel;
    }

    public List<Member> generateStokvelMembers(String stokvelId, String anchorId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH).concat("Generate Member"));
        setFirstNames();
        setLastNames();
        List<Member> list = new ArrayList<>();
        int cnt = 0;
        for (int i = 0; i < 3; i++) {
            Member m = new Member();
            m.setStokvelIds(new ArrayList<>());
            m.setAnchorId(anchorId);
            m.getStokvelIds().add(stokvelId);
            m.setActive(true);
            m.setDate(new DateTime().toDateTimeISO().toString());
            m.setExternalAccountId("Are we there yet?");
            m.setPassword(basePassword);
            PersonalKYCFields fields = new PersonalKYCFields();
            int index1 = rand.nextInt(firstNames.size() - 1);
            int index2 = rand.nextInt(lastNames.size() - 1);
            fields.setFirst_name(firstNames.get(index1));
            fields.setLast_name(lastNames.get(index2));
            fields.setEmail_address("member_" + System.currentTimeMillis() + "@modernanchor.com");
            m.setKycFields(fields);
            m = stokvelService.createMember(m);
            list.add(m);
            cnt++;
            LOGGER.info(Emoji.RED_CAR.concat(Emoji.RED_CAR.concat(Emoji.RED_CAR)
                    .concat("Member #" + cnt + " generated: ".concat(m.getFullName()))));
        }
        LOGGER.info(Emoji.RED_CAR.concat(Emoji.RED_CAR.concat(Emoji.RED_CAR)
                .concat("Members generated: ".concat("" + list.size()))));
        return list;
    }

    private String getRandomAgentAmount() {
        int num = rand.nextInt(1000);
        if (num < 250) return "" + (250 * 1000) + ".00";
        if (num > 249 && num < 500) return "" + (500 * 1000) + ".00";
        if (num > 499 && num < 900) return "" + (1000 * 1000) + ".00";
        if (num > 899) return "" + (2500 * 1000) + ".00";
        int total = num * 100000;
        return "" + total + ".00";
    }

    public void generateLoanApplications(String anchorId) throws Exception {

        setAnchor(anchorId);
        List<Client> clients = firebaseService.getAnchorClients(anchor.getAnchorId());
        List<AccountService.AssetBag> assetBags = accountService.getDefaultAssets(anchor.getIssuingAccount().getAccountId());
        LOGGER.info(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD)
                .concat(" Generating LoanApplications ............... total clients: " + clients.size())));
        int cnt = 0;
        for (Client client : clients) {
            for (AccountService.AssetBag assetBag : assetBags) {
                LoanApplication app = new LoanApplication();
                app.setAnchorId(client.getAnchorId());
                app.setAgentId(client.getAgentId());
                app.setAmount(getRandomAmount());
                app.setAssetCode(assetBag.getAssetCode());
                app.setInterestRate(getRandomInterestRate());
                int num = rand.nextInt(10);
                if (num > 5) {
                    app.setLoanPeriodInMonths(getLoanPeriodInMonths());
                } else {
                    app.setLoanPeriodInWeeks(getLoanPeriodInWeeks());
                }
                app.setClientAccount(client.getAccount());
                app.setClientId(client.getClientId());
                app.setDate(new DateTime().toDateTimeISO().toString());
                try {
                    LOGGER.info(Emoji.PANDA.concat(Emoji.PANDA).concat(Emoji.PANDA)
                            .concat("Generate LoanApplication for: ")
                            .concat(client.getFullName()).concat("  \uD83C\uDF51 asset: ")
                            .concat(assetBag.getAssetCode()).concat(" \uD83D\uDC26 interest rate: " + app.getInterestRate()
                                    + " % ".concat("  \uD83C\uDFB2 ")));
                    agentService.addLoanApplication(app);
                    cnt++;
                } catch (Exception e) {
                    LOGGER.info(("..... \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 " +
                            "Interest Rate: " + app.getInterestRate() +
                            " \uD83D\uDE21 LoanApplication failed; ignored for this purpose: ").concat(e.getMessage()));
                }
            }
        }

        LOGGER.info("\n\n" + Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD)
                .concat(" LoanApplications generation completed ............... total loans: " + cnt)));
    }

    private int getLoanPeriodInMonths() {
        int num = rand.nextInt(6);
        if (num == 0) return 3;
        return num;
    }

    private int getLoanPeriodInWeeks() {
        int num = rand.nextInt(4);
        if (num == 0) return 2;
        return num;
    }

    private String getRandomAmount() {
        int num = rand.nextInt(100);
        if (num < 3) num = 10;
        int total = num * 100;
        return "" + total + ".00";
    }

    private double getRandomInterestRate() {
        int num = rand.nextInt(20);
        if (num < 5) num = 8;
        return num * 1.5;
    }

    @Autowired
    private TOMLService tomlService;

    private Anchor addAnchor(String anchorName) throws Exception {
        //create bag ...
        AnchorBag bag = new AnchorBag();
        bag.setFundingSeed(FUNDING_SEED);
        bag.setAssetAmount("99999999000");
        bag.setStartingBalance("9900");
        bag.setPassword(basePassword);

        Anchor mAnchor = new Anchor();
        mAnchor.setName(anchorName);
        mAnchor.setEmail("a_".concat("" + new DateTime().getMillis()).concat("@anchor.com"));
        mAnchor.setCellphone("+27911447786");
        mAnchor.setDate(new DateTime().toDateTimeISO().toString());

        bag.setAnchor(mAnchor);
        anchor = anchorAccountService.createAnchorAccounts(mAnchor, bag.getPassword(), bag.getAssetAmount(),
                bag.getFundingSeed(), bag.getStartingBalance());
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF.concat(Emoji.LEAF))
                .concat("Anchor created OK: ".concat(mAnchor.getName())));


        return anchor;

    }

    private List<Agent> agents = new ArrayList<>();

    private void addAgents() throws Exception {
        Agent agent1 = buildAgent();
        agent1.getPersonalKYCFields().setFirst_name("Tiger");
        agent1.getPersonalKYCFields().setLast_name("Wannamaker");
        agents.add(agentService.createAgent(agent1));
        LOGGER.info(Emoji.ALIEN.concat(Emoji.ALIEN.concat(Emoji.LEAF))
                .concat("Agent Wannamaker created OK: ".concat(G.toJson(agent1))));

        Agent agent3 = buildAgent();
        agent3.getPersonalKYCFields().setFirst_name("Kgabzela");
        agent3.getPersonalKYCFields().setLast_name("Marule-Smythe");
        agents.add(agentService.createAgent(agent3));
        LOGGER.info(Emoji.ALIEN.concat(Emoji.ALIEN.concat(Emoji.LEAF))
                .concat(agent3.getFullName() + " - \uD83D\uDC26 Agent Marule-Smythe created OK: "
                        .concat(agent3.getFullName())));
    }

    private void addAgentClients() throws Exception {
        setFirstNames();
        setLastNames();
        for (Agent agent : agents) {
            for (int i = 0; i < 4; i++) {
                Client c1 = buildClient(agent.getAgentId());
                int index1 = rand.nextInt(firstNames.size() - 1);
                int index2 = rand.nextInt(lastNames.size() - 1);
                c1.getPersonalKYCFields().setFirst_name(firstNames.get(index1));
                c1.getPersonalKYCFields().setLast_name(lastNames.get(index2));
                Client result = agentService.createClient(c1);
                LOGGER.info(Emoji.LEMON.concat(Emoji.LEMON).concat("..........Client created: ")
                        .concat(result.getFullName()));
            }
        }
    }

    private Client buildClient(String agentId) {
        Client c = new Client();
        c.setAnchorId(anchor.getAnchorId());
        c.setAgentId(agentId);
        c.setDateRegistered(new DateTime().toDateTimeISO().toString());
        c.setDateUpdated(new DateTime().toDateTimeISO().toString());
        c.setPassword(basePassword);
        c.setStartingFiatBalance("0.01");
        PersonalKYCFields fields = new PersonalKYCFields();
        fields.setMobile_number("+27998001212");
        fields.setEmail_address("c" + System.currentTimeMillis() + "@anchor.com");
        c.setPersonalKYCFields(fields);
        return c;
    }

    @Value("${fiatLimit}")
    private String fiatLimit;

    private Agent buildAgent() {
        Agent agent1 = new Agent();
        agent1.setAnchorId(anchor.getAnchorId());
        agent1.setDateRegistered(new DateTime().toDateTimeISO().toString());
        agent1.setDateUpdated(new DateTime().toDateTimeISO().toString());
        agent1.setFiatBalance("0.01");
        agent1.setFiatLimit(fiatLimit);
        agent1.setPassword(basePassword);
        PersonalKYCFields fields = new PersonalKYCFields();
        fields.setMobile_number("+27998001212");
        fields.setEmail_address("a" + System.currentTimeMillis() + "@anchor.com");
        agent1.setPersonalKYCFields(fields);
        return agent1;
    }

    List<String> firstNames = new ArrayList<>();
    List<String> lastNames = new ArrayList<>();
    Random rand = new Random(System.currentTimeMillis());

    private void setFirstNames() {
        firstNames.add("Anna");
        firstNames.add("Charlie");
        firstNames.add("Thabo");
        firstNames.add("Vusi");
        firstNames.add("Mmathabo");
        firstNames.add("Nana");
        firstNames.add("Marks");
        firstNames.add("Charlie");
        firstNames.add("Ouma");
        firstNames.add("Darren");
        firstNames.add("Sammy");
        firstNames.add("Cookie");
        firstNames.add("Brandy");
        firstNames.add("Riley");
        firstNames.add("Maria");
        firstNames.add("Mmabatho");
        firstNames.add("Mmapule");
        firstNames.add("Johnny");
        firstNames.add("Xavier");
        firstNames.add("Freddie");
        firstNames.add("Butiki");
        firstNames.add("Samuel");
        firstNames.add("David");
        firstNames.add("Peter");
        firstNames.add("Mashamba");
        firstNames.add("John");
        firstNames.add("Bobby");
        firstNames.add("Gilbert");
        firstNames.add("Sydney");
        firstNames.add("Don Ray");
        firstNames.add("Tsakane");
        firstNames.add("Fiona");
        firstNames.add("Kgabi");
        firstNames.add("Morena");
        firstNames.add("Ouma");
        firstNames.add("Lucy");
    }

    private void setLastNames() {
        lastNames.add("Maluleke");
        lastNames.add("Mhinga");
        lastNames.add("Macheke");
        lastNames.add("Masinga");
        lastNames.add("Moleketi");
        lastNames.add("Morakane");
        lastNames.add("Mokone");
        lastNames.add("Raymond");
        lastNames.add("Donald");
        lastNames.add("Mshengu");
        lastNames.add("Manthata");
        lastNames.add("Sithole");
        lastNames.add("Mafumbedzi");
        lastNames.add("Sono");
        lastNames.add("Macheke");
        lastNames.add("Mokone");
        lastNames.add("Mokoena");
        lastNames.add("Ntuli");
        lastNames.add("Nkuna");
        lastNames.add("Nkosi");
        lastNames.add("Maringa");
        lastNames.add("Titi");
        lastNames.add("Johnson");

        lastNames.add("van der Merwe");
        lastNames.add("Smith");
        lastNames.add("Thunberg");
        lastNames.add("Zuckerberg");
        lastNames.add("Lennon");
        lastNames.add("Mashamba");
        lastNames.add("Johnson");

        lastNames.add("Bodibe");
        lastNames.add("Rhangane");
        lastNames.add("Buthelezi");
    }
}
