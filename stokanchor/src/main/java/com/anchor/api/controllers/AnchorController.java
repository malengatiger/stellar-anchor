package com.anchor.api.controllers;

import com.anchor.api.data.anchor.Client;
import com.anchor.api.data.info.Info;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.services.*;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorBag;
import com.anchor.api.util.DemoDataGenerator;
import com.anchor.api.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
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
public class AnchorController {
    public static final Logger LOGGER = Logger.getLogger(AnchorController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public AnchorController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C AnchorController  " +
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
    @Autowired
    private DemoDataGenerator demoDataGenerator;
    @Value("${status}")
    private String status;

    @GetMapping(value = "/generateDemo", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateDemo() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication /generateDemo ...");
        demoDataGenerator.startGeneration();
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateLoans", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateLoans() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication /generateLoans ...");
        demoDataGenerator.generateLoanApplications();
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateLoans completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateStokvel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Stokvel generateStokvel() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication /generateStokvel ...");
        Stokvel stokvel = demoDataGenerator.generateStokvel();
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C GenerateStokvel completed ... "
                + G.toJson(stokvel) + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return stokvel;
    }
    @GetMapping(value = "/generateStokvelMembers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Member> generateStokvelMembers(@RequestParam String stokvelId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication /generateStokvelMembers ...");
        List<Member> stokvel = demoDataGenerator.generateStokvelMembers(stokvelId);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C GenerateStokvelMembers completed ... "
                + G.toJson(stokvel) + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return stokvel;
    }

    @GetMapping(value = "/generateAgentFunding", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateAgentFunding() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication /generateAgentFunding ...");
        demoDataGenerator.generateAgentFunding();
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateAgentFunding completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateLoanApprovals", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateLoanApprovals() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication /generateLoanApprovals ...");
        demoDataGenerator.generateLoanApprovals();
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateLoanApprovals completed ... "
                + new DateTime().toDateTimeISO().toString() + " \uD83D\uDC99 STATUS: " + status;
    }
    @GetMapping(value = "/generatePayments", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generatePayments() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication /generatePayments ...");
        demoDataGenerator.generatePayments();
        return "\uD83D\uDC99 \uD83D\uDC9C GeneratePayments completed ... "
                + new DateTime().toDateTimeISO().toString() + " \uD83D\uDC99 STATUS: " + status;
    }

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
    public AccountBag getAccount(@RequestParam String seed) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorController:getAccount ...");
        AccountResponse response = accountService.getAccount(seed);
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C AnchorController getAccount returned: "
                + response.getAccountId() + " \uD83D\uDC99 \uD83D\uDC9C");
        AccountResponse.Balance[] balances = response.getBalances();
        List<AccountResponse.Balance> balanceList = new ArrayList<>();
        Collections.addAll(balanceList, balances);
        AccountBag bag = new AccountBag(balanceList,response.getAccountId(),response.getSequenceNumber());
        return bag;
    }

    @Autowired
    private FirebaseService firebaseService;
    @GetMapping(value = "/getAnchorClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> getClients(@RequestParam String anchorId) throws Exception {
        return firebaseService.getAnchorClients(anchorId);
    }

    @PostMapping(value = "/createAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor createAnchor(@RequestBody AnchorBag anchorBag) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorController:createAnchor ...");
        if (anchorBag.getFundingSeed() == null) {
            throw new Exception("Funding Account Seed missing");
        }
        Anchor anchor = anchorAccountService.createAnchorAccounts(anchorBag.getAnchor(),
                anchorBag.getPassword(),anchorBag.getAssetAmount(), anchorBag.getFundingSeed(), anchorBag.getStartingBalance());
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 AnchorAccountService returns Anchor: \uD83C\uDF4E "
                + anchor.getName() + "  \uD83C\uDF4E anchorId: " + anchor.getAnchorId()
        + "  \uD83C\uDF4E");
        LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F \uD83C\uDF4F ANCHOR CREATED: ".concat(G.toJson(anchor)));
        return anchor;
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
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorController:createTestInfo ...");
        return Util.createTestInfo();
    }

    class AccountBag {
        List<AccountResponse.Balance> balances;
        String account;
        private Long sequenceNumber;

        public AccountBag(List<AccountResponse.Balance> balances, String account, Long sequenceNumber) {
            this.balances = balances;
            this.account = account;
            this.sequenceNumber = sequenceNumber;
        }

        public List<AccountResponse.Balance> getBalances() {
            return balances;
        }

        public void setBalances(List<AccountResponse.Balance> balances) {
            this.balances = balances;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public Long getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(Long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
    }

}
