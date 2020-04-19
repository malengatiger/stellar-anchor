package com.anchor.api.util;

import com.anchor.api.controllers.AgentController;
import com.anchor.api.controllers.AnchorController;
import com.anchor.api.data.anchor.*;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.data.transfer.sep9.OrganizationKYCFields;
import com.anchor.api.data.transfer.sep9.PersonalKYCFields;
import com.anchor.api.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class DemoDataGenerator {
    public static final Logger LOGGER = Logger.getLogger(AnchorController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    public DemoDataGenerator() {
        LOGGER.info(Emoji.RED_CAR.concat(Emoji.RED_CAR) + "Demo data DemoDataGenerator ready and able!");
    }

    public static final String FUNDING_ACCOUNT = "GBGIRK3ZV6WKBXCFLJRYPARZENSYKPIHQDZNOKHC6FAE7PRFCYQUU6ZU",
    FUNDING_SEED = "SCJAWLHN4R7JOX6BM6B4DP22TL4SB6XB2AZJQBSRNK4LPZULVXQCUMAX";
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
    @Value("${anchorName}")
    private String anchorName;

    Anchor anchor;
    private static final String PASSWORD = "pass3#TWord!";

    public void startGeneration() throws Exception {
        if (!status.equalsIgnoreCase("dev")) {
            throw new Exception(Emoji.NOT_OK + "Demo Data Generation may not be run in PRODUCTION");
        }
        if (anchorName == null) {
            throw new Exception(Emoji.NOT_OK + "Anchor name missing from application properties file");
        }
        LOGGER.info(Emoji.HEART_BLUE + "Start Data Generation "
                .concat(Emoji.HEART_BLUE.concat(Emoji.HEART_BLUE)));

        //delete users and collections
        firebaseService.deleteAuthUsers();
        LOGGER.info(Emoji.SOCCER_BALL.concat(Emoji.SOCCER_BALL)
                +"Firebase auth users have been cleaned out");
        firebaseService.deleteCollections();
        LOGGER.info(Emoji.BASKET_BALL.concat(Emoji.BASKET_BALL)
                +"Firestore collections have been cleaned out");
        //add data
        addAnchor();
        addAgents();
        addAgentClients();
        generateAgentFunding();
        generateLoanApplications();

        Stokvel stokvel = generateStokvel();
        generateStokvelMembers(stokvel.getStokvelId());

        //todo - agents approve the loans and send payment
        //todo - clients pay on monthly schedule
        //for testing
        //todo - retrieve data for overall status of anchor ...
        // ... (anchor, agent, client dashboard basics here ....)

    }

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StokvelService stokvelService;
    @Autowired
    private CryptoService cryptoService;
    public void generateAgentFunding() throws Exception {
        LOGGER.info("Generating agent funds ...".concat(Emoji.PEAR.concat(Emoji.PEAR)));
        if (anchor == null) {
            anchor = firebaseService.getAnchorByName(anchorName);
        }
        agents = firebaseService.getAgents(anchor.getAnchorId());
        List<AccountService.AssetBag> assetBags = accountService.getDefaultAssets(
                anchor.getIssuingAccount().getAccountId());
        String seed = cryptoService.getDecryptedSeed(anchor.getDistributionAccount().getAccountId());
        for (Agent agent : agents) {
            for (AccountService.AssetBag assetBag : assetBags) {
                AgentController.PaymentRequest request = new AgentController.PaymentRequest();
                request.setAssetCode(assetBag.getAssetCode());
                request.setDate(new DateTime().toDateTimeISO().toString());
                request.setAmount(getRandomAgentAmount());
                request.setAnchorId(agent.getAnchorId());
                request.setDestinationAccount(agent.getStellarAccountId());
                request.setSeed(seed);
                paymentService.sendPayment(request);
            }
        }
    }
    public Stokvel generateStokvel() throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH).concat("Generate Stokvel"));
        Stokvel stokvel = new Stokvel();
        stokvel.setName("OriginalGroup");
        stokvel.setPassword(PASSWORD);
        stokvel.setActive(true);
        stokvel.setDate(new DateTime().toDateTimeISO().toString());
        OrganizationKYCFields fields = new OrganizationKYCFields();
        fields.setEmail("stokvel_".concat(""+System.currentTimeMillis()).concat("@modernachor.com"));
        stokvel.setKycFields(fields);
        stokvel = stokvelService.createStokvel(stokvel);
        LOGGER.info(Emoji.LEMON.concat(Emoji.LEMON.concat(Emoji.LEMON)
        .concat("Stokvel generated: ".concat(G.toJson(stokvel)))));
        return stokvel;
    }
    public List<Member> generateStokvelMembers(String stokvelId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH).concat("Generate Member"));
        setFirstNames();
        setLastNames();
        List<Member> list = new ArrayList<>();
        int cnt = 0;
        for (int i = 0; i < 3; i++) {
            Member m = new Member();
            m.setStokvelIds(new ArrayList<>());
            m.getStokvelIds().add(stokvelId);
            m.setActive(true);
            m.setDate(new DateTime().toDateTimeISO().toString());
            m.setExternalAccountId("Are we there yet?");
            m.setPassword(PASSWORD);
            PersonalKYCFields fields = new PersonalKYCFields();
            int index1 = rand.nextInt(firstNames.size() - 1);
            int index2 = rand.nextInt(lastNames.size()  - 1);
            fields.setFirst_name(firstNames.get(index1));
            fields.setLast_name(lastNames.get(index2));
            fields.setEmail_address("member_" + System.currentTimeMillis() + "@modernanchor.com");
            m.setKycFields(fields);
            m = stokvelService.createMember(m);
            list.add(m);
            cnt++;
            LOGGER.info(Emoji.RED_CAR.concat(Emoji.RED_CAR.concat(Emoji.RED_CAR)
                    .concat("Member #"+cnt+" generated: ".concat(G.toJson(m)))));
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
        if (num > 899)  return "" + (2500 * 1000) + ".00";
        int total = num * 10000;
        return "" + total + ".00";
    }
    public void generateLoanApplications() throws Exception {
        LOGGER.info(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD)
        .concat(" Generating LoanApplications ....")));
        if (anchor == null) {
            anchor = firebaseService.getAnchorByName(anchorName);
        }
        List<Client> clients = firebaseService.getAnchorClients(anchor.getAnchorId());
        List<AccountService.AssetBag> assetBags = accountService.getDefaultAssets(anchor.getIssuingAccount().getAccountId());
        for (Client client : clients) {
            LOGGER.info(Emoji.PANDA.concat(Emoji.PANDA).concat("Generate LoanApplication for: ")
            .concat(client.getFullName()));
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
                agentService.addLoanApplication(app);
            }
        }
    }

    private int getLoanPeriodInMonths() {
        int num = rand.nextInt(24);
        if (num < 7) return 6;
        if (num > 16) return 24;
        return 12;
    }
    private int getLoanPeriodInWeeks() {
        int num = rand.nextInt(26);
        if (num < 7) return 4;
        if (num > 16) return 8;
        return 16;
    }
    private String getRandomAmount() {
        int num = rand.nextInt(100);
        if (num < 3) num = 10;
        int total = num * 100;
        return "" + total + ".00";
    }
    private double getRandomInterestRate() {
        int num = rand.nextInt(15);
        if (num < 6) num = 6;
        return num * 1.5;
    }
    private void addAnchor() throws Exception {
        //create bag ...
        AnchorBag bag = new AnchorBag();
        bag.setFundingSeed(FUNDING_SEED);
        bag.setAssetAmount("99999999000");
        bag.setStartingBalance("9900");
        bag.setPassword(PASSWORD);

        Anchor mAnchor = new Anchor();
        mAnchor.setName(anchorName);
        mAnchor.setEmail("anchor_".concat("" + new DateTime().getMillis()).concat("@anchorahoy.com"));
        mAnchor.setCellphone("+27911447786");
        mAnchor.setDate(new DateTime().toDateTimeISO().toString());

        bag.setAnchor(mAnchor);
        anchor = anchorAccountService.createAnchorAccounts(mAnchor,bag.getPassword(),bag.getAssetAmount(),
                bag.getFundingSeed(),bag.getStartingBalance());
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF.concat(Emoji.LEAF))
        .concat("Anchor created OK: ".concat(G.toJson(mAnchor))));
    }

    private List<Agent> agents = new ArrayList<>();
    private void addAgents() throws Exception {
        Agent agent1 = buildAgent();
        agent1.getPersonalKYCFields().setFirst_name("Tiger");
        agent1.getPersonalKYCFields().setLast_name("MLB 23");
        agents.add(agentService.createAgent(agent1));
        LOGGER.info(Emoji.ALIEN.concat(Emoji.ALIEN.concat(Emoji.LEAF))
                .concat("Agent created OK: ".concat(G.toJson(agent1))));

//        Agent agent2 = buildAgent();
//        agent2.getPersonalKYCFields().setFirst_name("Aubrey");
//        agent2.getPersonalKYCFields().setLast_name("SuperAgent");
//        agents.add(agentService.createAgent(agent2));
//        LOGGER.info(Emoji.ALIEN.concat(Emoji.ALIEN.concat(Emoji.LEAF))
//                .concat("Agent created OK: ".concat(G.toJson(agent2))));

        Agent agent3 = buildAgent();
        agent3.getPersonalKYCFields().setFirst_name("Mmathabo");
        agent3.getPersonalKYCFields().setLast_name("Marule-Smythe");
        agents.add(agentService.createAgent(agent3));
        LOGGER.info(Emoji.ALIEN.concat(Emoji.ALIEN.concat(Emoji.LEAF))
                .concat("Agent created OK: ".concat(G.toJson(agent3))));
    }
    private void addAgentClients() throws Exception {
        setFirstNames();
        setLastNames();
        for (Agent agent : agents) {
            for (int i = 0; i < 3; i++) {
                Client c1 = buildClient(agent.getAgentId());
                int index1 = rand.nextInt(firstNames.size() - 1);
                int index2 = rand.nextInt(lastNames.size() - 1);
                c1.getPersonalKYCFields().setFirst_name(firstNames.get(index1));
                c1.getPersonalKYCFields().setLast_name(lastNames.get(index2));
                Client result = agentService.createClient(c1);
                LOGGER.info(Emoji.LEMON.concat(Emoji.LEMON).concat("Client created: ").concat(G.toJson(result)));
            }
        }
    }

    private Client buildClient(String agentId) {
        Client c = new Client();
        c.setAnchorId(anchor.getAnchorId());
        c.setAgentId(agentId);
        c.setDateRegistered(new DateTime().toDateTimeISO().toString());
        c.setDateUpdated(new DateTime().toDateTimeISO().toString());
        c.setPassword(PASSWORD);
        c.setStartingFiatBalance("0.01");
        PersonalKYCFields fields = new PersonalKYCFields();
        fields.setMobile_number("+27998001212");
        fields.setEmail_address("client_"+System.currentTimeMillis()+"@modernanchor.com");
        c.setPersonalKYCFields(fields);
        return c;
    }
    private Agent buildAgent() {
        Agent agent1 = new Agent();
        agent1.setAnchorId(anchor.getAnchorId());
        agent1.setDateRegistered(new DateTime().toDateTimeISO().toString());
        agent1.setDateUpdated(new DateTime().toDateTimeISO().toString());
        agent1.setFiatBalance("0.01");
        agent1.setFiatLimit("900000000000.0000000");
        agent1.setPassword(PASSWORD);
        PersonalKYCFields fields = new PersonalKYCFields();
        fields.setMobile_number("+27998001212");
        fields.setEmail_address("agent_"+System.currentTimeMillis()+"@modernanchor.com");
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
        firstNames.add("Ouma");
        firstNames.add("Lucy");
    }
    private void setLastNames() {
        lastNames.add("Maluleke");
        lastNames.add("Mhinga");
        lastNames.add("Macheke");
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
