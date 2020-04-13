package com.anchor.api.controllers;

import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.data.info.Info;
import com.anchor.api.services.*;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorBag;
import com.anchor.api.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.stellar.sdk.responses.AccountResponse;
import shadow.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
@RestController
public class MainController {
    public static final Logger LOGGER = Logger.getLogger(MainController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public MainController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C MainController  " +
                "\uD83C\uDF51 constructed and ready to go! \uD83C\uDF45 CORS enabled for the controller");
    }

    @Autowired
    private ApplicationContext context;
    @Autowired
    private AnchorAccountService anchorAccountService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private AccountService accountService;
    @Value("${status}")
    private String status;

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication / ...");
        return "\uD83D\uDC99 \uD83D\uDC9C AnchorApplication up and running ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/.well-known/stellar.toml", produces = MediaType.TEXT_PLAIN_VALUE)
    public byte[] getStellarToml() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 get stellar.toml file and return to caller...");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("_well-known/stellar.toml")).getFile());
        if (file.exists()) {
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C ... stellar.toml File has been found \uD83C\uDF45 " + file.getAbsolutePath());
            Toml toml = new Toml().read(file);
            List<HashMap> currencies = toml.getList("CURRENCIES");
            for (HashMap currency : currencies) {
                LOGGER.info("\uD83C\uDF3C stellar.toml: \uD83C\uDF3C Currency: ".concat((currency.get("code").toString())
                .concat(" \uD83D\uDE21 issuer: ").concat(currency.get("issuer").toString())));
            }

            return IOUtils.toByteArray(new FileInputStream(file));
        } else {
            LOGGER.info(" \uD83C\uDF45 stellar.toml : File NOT found. this is where .toml needs to go;  \uD83C\uDF45 ");
            throw new Exception("stellar.toml not found");
        }
    }
    @GetMapping(value = "/.stellar.toml", produces = MediaType.TEXT_PLAIN_VALUE)
    public byte[] getStellarTomlToo() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 get stellar.toml file and return to caller...");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("_well-known/stellar.toml")).getFile());
        if (file.exists()) {
            LOGGER.info(" \uD83C\uDF45 ... stellar.tomlFile has been found \uD83C\uDF45 " + file.getAbsolutePath());
            Toml toml = new Toml().read(file);
            List<HashMap> currencies = toml.getList("CURRENCIES");
            for (HashMap currency : currencies) {
                LOGGER.info("\uD83C\uDF3C stellar.toml: \uD83C\uDF3C Currency: ".concat((currency.get("code").toString())));
            }
            return IOUtils.toByteArray(new FileInputStream(file));
        } else {
            LOGGER.info(" \uD83C\uDF45 stellar.toml : File NOT found. this is where .toml needs to go;  \uD83C\uDF45 ");
            throw new Exception("stellar.toml not found");
        }
    }
    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> ping() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Pinging AnchorApplication and getting anchors...");
        List<Anchor> anchors = context.getBean(FirebaseService.class).getAnchors();
        List<String > mList = new ArrayList<>();
        int cnt = 0;
        for (Anchor anchor : anchors) {
            cnt++;
            mList.add(anchor.getName());
        }
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C AnchorApplication pinged at "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C anchors found: " + anchors.size());
        return mList;
    }

    @GetMapping(value = "/getAccount", produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse getAccount(@RequestParam String seed) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:getAccount ...");
        AccountResponse response = accountService.getAccount(seed);
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C MainController getAccount returned"
                + response.getAccountId() + " \uD83D\uDC99 \uD83D\uDC9C");
        return response;
    }

    @PostMapping(value = "/createAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor createAnchor(@RequestBody AnchorBag anchorBag) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createAnchor ...");
        if (anchorBag.getFundingSeed() == null) {
            throw new Exception("Funding Account Seed missing");
        }
        Anchor anchor = anchorAccountService.createAnchorAccounts(anchorBag.getAnchor(),
                anchorBag.getPassword(),anchorBag.getAssetCode(),anchorBag.getAssetAmount(), anchorBag.getFundingSeed(), anchorBag.getStartingBalance());
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 AnchorAccountService returns Anchor: \uD83C\uDF4E "
                + anchor.getName() + "  \uD83C\uDF4E anchorId: " + anchor.getAnchorId()
        + "  \uD83C\uDF4E");
        LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F \uD83C\uDF4F ANCHOR CREATED: ".concat(G.toJson(anchor)));
        return anchor;
    }

    @PostMapping(value = "/createAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public Agent createAgent(@RequestBody Agent agent) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createAgent ...");
        Agent mAgent = agentService.createAgent(agent);
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Stellar returns Agent: \uD83C\uDF4E "
                + mAgent.getAgentId() + " anchor: " + mAgent.getAnchorId());
        return mAgent;
    }

    @PostMapping(value = "/createClient", produces = MediaType.APPLICATION_JSON_VALUE)
    public Client createClient(@RequestBody Client client) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createClient...");
        Client mClient = agentService.createClient(client);
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Stellar returns Client: \uD83C\uDF4E "
                + mClient.getClientId() + " anchor: " + mClient.getAnchorId());
        return mClient;
    }

    @Autowired
    private CryptoService cryptoService;

    @GetMapping("/createKeyRing")
    public String createKeyRing(@RequestParam String keyRingId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication: createKeyRing ... ... ...");
        String keyRing = cryptoService.createKeyRing();
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 createKeyRing done!: \uD83C\uDF4E "
        .concat(keyRing));
        return keyRing;
    }
    @GetMapping("/createCryptoKey")
    public String createCryptoKey() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication: createCryptoKey ... ... ...");
        String cryptoKey = cryptoService.createCryptoKey();
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 createCryptoKey done!: \uD83C\uDF4E "
                .concat(cryptoKey));
        return cryptoKey;
    }

    @GetMapping("/createTestInfo")
    public Info createTestInfo() {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createTestInfo ...");
        return Util.createTestInfo();
    }

}
