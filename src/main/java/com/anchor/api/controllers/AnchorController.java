package com.anchor.api.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import com.anchor.api.data.AgentFundingRequest;
import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorBag;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.data.info.Info;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.AgentService;
import com.anchor.api.services.AnchorAccountService;
import com.anchor.api.services.CryptoService;
import com.anchor.api.services.FirebaseService;
import com.anchor.api.services.PaymentService;
import com.anchor.api.services.TOMLService;
import com.anchor.api.util.DemoDataGenerator;
import com.anchor.api.util.Emoji;
import com.anchor.api.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.stellar.sdk.responses.AccountResponse;

import shadow.org.apache.commons.io.IOUtils;

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

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        LOGGER.info(em + " StellarAnchorApplication / ...");
        return "\uD83D\uDC99 \uD83D\uDC9C StellarAnchorApplication up and running ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }

    @Autowired
    private TOMLService tomlService;

    /**
     * Upload the Anchor properties in anchor.toml nee stellar. This file has private properties
     * that the Anchor needs for proper operation
     * @param anchorId anchor identifier
     * @param multipartFile  toml file with anchor properties
     * @return byte[] - bytes from the file
     * @throws Exception upload failed
     */
    @PostMapping(value = "/uploadTOML", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] uploadTOML(@RequestParam("anchorId") String anchorId,
                             @RequestParam("file") MultipartFile multipartFile) throws Exception {

        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:uploadTOML...");
        byte[] bytes = multipartFile.getBytes();
        File mFile = new File("file_" + System.currentTimeMillis());
        Path path = Paths.get(mFile.getAbsolutePath());
        Files.write(path, bytes);
        LOGGER.info("....... multipart TOML file received: \uD83C\uDFBD "
                .concat(" length: " + mFile.length() + " bytes"));
        tomlService.encryptAndUploadFile(anchorId, mFile);
        Files.delete(path);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!");
        return bytes;
    }

    /*
        Get the Anchor's properties from anchor.toml
     */
    @GetMapping(value = "/getTOML", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTOML(@RequestParam("anchorId") String anchorId) throws Exception {

        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:getTOML...");
        Toml toml = tomlService.getToml(anchorId);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned TOML from download .... "
                .concat(" databaseUrl: ")
                .concat(toml.getString("databaseUrl")));
        return toml.toMap();
    }


    @GetMapping(value = "/.well-known/stellar.toml", produces = MediaType.TEXT_PLAIN_VALUE)
    public byte[] getStellarToml() throws Exception {
        LOGGER.info(em + " get stellar.toml file and return to caller...");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("_well-known/stellar.toml")).getFile());
        if (file.exists()) {
            LOGGER.info(em + " ... stellar.toml File has been found \uD83C\uDF45 " + file.getAbsolutePath());
            Toml toml = new Toml().read(file);
            List<HashMap> currencies = toml.getList("CURRENCIES");
            for (HashMap currency : currencies) {
                LOGGER.info(em + "  stellar.toml: \uD83C\uDF3C Currency: ".concat((currency.get("code").toString())
                .concat(" \uD83D\uDE21 issuer: ").concat(currency.get("issuer").toString())));
            }

            return IOUtils.toByteArray(new FileInputStream(file));
        } else {
            LOGGER.info(em + "  stellar.toml : File NOT found. this is where .toml needs to go;  \uD83C\uDF45 ");
            throw new Exception("stellar.toml not found");
        }

    }
    @GetMapping(value = "/.stellar.toml", produces = MediaType.TEXT_PLAIN_VALUE)
    public byte[] getStellarTomlToo() throws Exception {
        LOGGER.info(em + " get stellar.toml file and return to caller...");
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
            LOGGER.info(em + "  stellar.toml : File NOT found. this is where .toml needs to go;  \uD83C\uDF45 ");
            throw new Exception("stellar.toml not found");
        }
    }
    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> ping() throws Exception {
        LOGGER.info(em + " Pinging StellarAnchorApplication and getting anchors...");
        List<Anchor> anchors = context.getBean(FirebaseService.class).getAnchors();
        List<String > mList = new ArrayList<>();
        int cnt = 0;
        for (Anchor anchor : anchors) {
            cnt++;
            mList.add(anchor.getName());
        }
        LOGGER.info( em + "  StellarAnchorApplication pinged at "
                + new Date().toString() + em + "  anchors found: " + anchors.size());
        return mList;
    }

    @GetMapping(value = "/getAccountUsingSeed", produces = MediaType.APPLICATION_JSON_VALUE)
    public Balances getAccountUsingSeed(@RequestParam String seed) throws Exception {
        LOGGER.info(em+" AnchorController:getAccountUsingSeed ...");
        AccountResponse response = accountService.getAccountUsingSeed(seed);
        LOGGER.info( em+" AnchorController getAccount returned: "
                + response.getAccountId() +Emoji.LEAF);
        AccountResponse.Balance[] balances = response.getBalances();
        List<AccountResponse.Balance> balanceList = new ArrayList<>();
        Collections.addAll(balanceList, balances);

        Balances mBalances = new Balances(balanceList,response.getAccountId(),response.getSequenceNumber(),
                new DateTime().toDateTimeISO().toString());
        firebaseService.addBalances(mBalances);
        LOGGER.info(Emoji.PEPPER.concat(Emoji.PEPPER) + "Returning balances " + G.toJson(mBalances));
        return mBalances;
    }

   
    @GetMapping(value = "/getAccountUsingAccountId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Balances getAccountUsingAccountId(@RequestParam String accountId) throws Exception {
        LOGGER.info(em + " AnchorController:getAccountUsingAccountId ...");
        AccountResponse response = accountService.getAccountUsingAccount(accountId);
        LOGGER.info( Emoji.LEAF.concat(Emoji.LEAF) +" AnchorController getAccount returned: "
                + response.getAccountId() +Emoji.LEAF);
        AccountResponse.Balance[] balances = response.getBalances();
        List<AccountResponse.Balance> balanceList = new ArrayList<>();
        Collections.addAll(balanceList, balances);
        Balances mBalances = new Balances(balanceList,response.getAccountId(),response.getSequenceNumber(),
                new DateTime().toDateTimeISO().toString());
        firebaseService.addBalances(mBalances);
        LOGGER.info(Emoji.PEPPER.concat(Emoji.PEPPER) + "Returning balances " + G.toJson(mBalances));
        return mBalances;
    }


    @GetMapping(value = "/getAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor getAnchor(@RequestParam String anchorId) throws Exception {
        LOGGER.info(em + " AnchorController:getAnchor ..."
        .concat(anchorId));
        Anchor response = firebaseService.getAnchor(anchorId);
        LOGGER.info( Emoji.LEAF.concat(Emoji.LEAF) + " AnchorController getAnchor returned: "
                + response.getName());

        return response;
    }

    @Autowired
    private FirebaseService firebaseService;
    @GetMapping(value = "/getAnchorClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> getClients(@RequestParam String anchorId) throws Exception {
        return firebaseService.getAnchorClients(anchorId);
    }

    @PostMapping(value = "/createAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor createAnchor(@RequestBody AnchorBag anchorBag) throws Exception {
        LOGGER.info(em + " AnchorController:createAnchor ...");
        if (anchorBag.getFundingSeed() == null) {
            throw new Exception("Funding Account Seed missing");
        }
        Anchor anchor = anchorAccountService.createAnchorAccounts(
                anchorBag.getAnchor(),
                anchorBag.getPassword(),
                anchorBag.getAssetAmount(),
                anchorBag.getFundingSeed(),
                anchorBag.getStartingBalance());

        LOGGER.info(Emoji.LEAF + " AnchorAccountService returns Anchor: \uD83C\uDF4E "
                + anchor.getName() + "  \uD83C\uDF4E anchorId: " + anchor.getAnchorId());
        //todo - upload file toml
        File file = new File("anchor.toml");
        LOGGER.info("We have a file? ...".concat(file.getAbsolutePath()));
        if (file.exists()) {
            tomlService.encryptAndUploadFile(anchor.getAnchorId(), file);
        }
        LOGGER.info(Emoji.LEAF + " ANCHOR CREATED: ".concat(G.toJson(anchor)));
        return anchor;
    }

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/fundAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public AgentFundingRequest fundAgent(@RequestBody AgentFundingRequest fundingRequest) throws Exception {
        LOGGER.info(em + "AnchorController: fundAgent requested .... " + G.toJson(fundingRequest));
        fundingRequest.setAgentFundingRequestId(UUID.randomUUID().toString());
        fundingRequest = paymentService.fundAgent(fundingRequest);
        String msg = Emoji.LEAF + "Agent Funding complete: " + fundingRequest.getAgentFundingRequestId();
        LOGGER.info(Emoji.LEAF.concat(msg));
        return fundingRequest;
    }

    @GetMapping("/createKeyRing")
    public String createKeyRing(@RequestParam String keyRingId) throws Exception {
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "StellarAnchorApplication: createKeyRing ... ... ...");
        String keyRing = cryptoService.createKeyRing();
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + " createKeyRing done!: \uD83C\uDF4E "
        .concat(keyRing));
        return keyRing;
    }
    @GetMapping("/createCryptoKey")
    public String createCryptoKey() throws Exception {
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "StellarAnchorApplication: createCryptoKey ... ... ...");
        String cryptoKey = cryptoService.createCryptoKey();
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "createCryptoKey done!: \uD83C\uDF4E "
                .concat(cryptoKey));
        return cryptoKey;
    }
    @PostMapping(value = "/sendPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public PaymentRequest sendPayment(PaymentRequest paymentRequest) throws Exception {
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + 
            "............. send payment ... ".concat(G.toJson(paymentRequest)));
        PaymentRequest response = paymentService.sendPayment(paymentRequest);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + "Payment has been successfully sent: ".concat(G.toJson(response)));
        return response;
    }

    @GetMapping("/createTestInfo")
    public Info createTestInfo() {
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + " AnchorController:createTestInfo ...");
        return Util.createTestInfo();
    }
    private static final String em ="\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ";
    public class Balances {
        List<AccountResponse.Balance> balances;
        String account, date;
        private Long sequenceNumber;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Balances(List<AccountResponse.Balance> balances, String account, Long sequenceNumber, String date) {
            this.balances = balances;
            this.account = account;
            this.date = date;
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
