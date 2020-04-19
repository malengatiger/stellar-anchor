package com.anchor.api.util;

import com.anchor.api.controllers.AnchorController;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorBag;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.data.transfer.sep9.PersonalKYCFields;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.AgentService;
import com.anchor.api.services.AnchorAccountService;
import com.anchor.api.services.FirebaseService;
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

    public static final String FUNDING_ACCOUNT = "GAXDOJ43FXS4D64M3DA3AVPT7OV27XZI4ZZ5FCO5YV43KBCTZB4L6MWG",
    FUNDING_SEED = "SDHFRH4CDZ3XEWPENU423XMBEUFCC3V4XVSNGFBGP6KNO4ADQWOP4C7N";
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

    public void startGeneration() throws Exception {
        if (!status.equalsIgnoreCase("dev")) {
            throw new Exception(Emoji.NOT_OK + "Demo Data Generation may not be run in PRODUCTION");
        }
        LOGGER.info(Emoji.HEART_BLUE + "Start Data Generation "
                .concat(Emoji.HEART_BLUE.concat(Emoji.HEART_BLUE)));

        firebaseService.deleteAuthUsers();
        LOGGER.info(Emoji.SOCCER_BALL.concat(Emoji.SOCCER_BALL)
                +"Firebase auth users have been cleaned out");
        firebaseService.deleteCollections();
        LOGGER.info(Emoji.BASKET_BALL.concat(Emoji.BASKET_BALL)
                +"Firestore collections have been cleaned out");
        addAnchor();
        addAgents();
        addAgentClients();

        //todo - generate payments and loans ...

    }

    private void addAnchor() throws Exception {
        //create bag ...
        AnchorBag bag = new AnchorBag();
        bag.setFundingSeed(FUNDING_SEED);
        bag.setAssetAmount("99999999000");
        bag.setStartingBalance("9000");
        bag.setPassword("pass123%T");

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

        Agent agent2 = buildAgent();
        agent2.getPersonalKYCFields().setFirst_name("Aubrey");
        agent2.getPersonalKYCFields().setLast_name("SuperAgent");
        agents.add(agentService.createAgent(agent2));
        LOGGER.info(Emoji.ALIEN.concat(Emoji.ALIEN.concat(Emoji.LEAF))
                .concat("Agent created OK: ".concat(G.toJson(agent2))));

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
        c.setPassword("pass3#W!ord");
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
        agent1.setPassword("pass3$TWord");
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
