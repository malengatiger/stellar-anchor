package com.anchor.api.controllers;

import com.anchor.api.data.info.Info;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.AnchorAccountService;
import com.anchor.api.services.FirebaseService;
import com.anchor.api.util.Crypto;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorBag;
import com.anchor.api.data.User;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    @Value("${status}")
    private String status;

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AnchorApplication / ...");
        return "\uD83D\uDC99 \uD83D\uDC9C AnchorApplication up and running ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/.well-known/stellar.toml", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getStellarToml() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 get stellar.toml file and return to caller...");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("_well-known/stellar.toml")).getFile());
        if (file.exists()) {
            LOGGER.info(" \uD83C\uDF45 File has been found \uD83C\uDF45 " + file.getAbsolutePath());
            Toml toml = new Toml().read(file);
            List<Object> currencies = toml.getList("CURRENCIES");

            return IOUtils.toByteArray(new FileInputStream(file));
        } else {
            LOGGER.info(" \uD83C\uDF45 File NOT found. this is where .toml needs to go;  \uD83C\uDF45 ");
            throw new Exception("stellar.toml not found");
        }
    }
    @GetMapping(value = "/.stellar.toml", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getStellarTomlToo() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 get stellar.toml file and return to caller...");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("_well-known/stellar.toml")).getFile());
        if (file.exists()) {
            LOGGER.info(" \uD83C\uDF45 File has been found \uD83C\uDF45 " + file.getAbsolutePath());
            return IOUtils.toByteArray(new FileInputStream(file));
        } else {
            LOGGER.info(" \uD83C\uDF45 File NOT found. this is where .toml needs to go;  \uD83C\uDF45 ");
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
        AccountService accountService = context.getBean(AccountService.class);
        AccountResponse response = accountService.getAccount(seed);
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C MainController getAccount returned"
                + response.getAccountId() + " \uD83D\uDC99 \uD83D\uDC9C");
        return response;
    }
    @PostMapping(value = "/createAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor createAnchor(@RequestBody AnchorBag anchorBag) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createAnchor ...");
        AnchorAccountService service = context.getBean(AnchorAccountService.class);
        Anchor anchor = service.createAnchorAccounts(anchorBag.getAnchor(),
                anchorBag.getPassword(),anchorBag.getAssetCode(),anchorBag.getAssetAmount());
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Stellar returns Anchor: \uD83C\uDF4E "
                + anchor.getName() + "  \uD83C\uDF4E anchorId: " + anchor.getAnchorId()
        + "  \uD83C\uDF4E");
        return anchor;
    }
    @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody User user) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createUser ...");
        AccountService service = context.getBean(AccountService.class);
        User bag = service.createUser(user);
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Stellar returns User: \uD83C\uDF4E "
                + bag.getFullName() + " userId: " + bag.getUserId());
        return bag;
    }

    @PostMapping(value = "/createUserWithExistingAccount", produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUserWithExistingAccount(@RequestBody User user) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createUserWithExistingAccount ...");
        AccountService service = context.getBean(AccountService.class);
        User realUser = service.createUserWithExistingAccount(user);
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Stellar returns User: \uD83C\uDF4E "
                + realUser.getFullName() + " userId: " + realUser.getUserId());
        return realUser;
    }

    @GetMapping("/createCrypto")
    public String createDefaultCrypto(@RequestParam boolean isDevelopment) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StokkieAnchorApplication: createDefaultCrypto ... ... ...");
        Crypto service = context.getBean(Crypto.class);
        service.createDefaults();
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 createDefaultCrypto done!: \uD83C\uDF4E ");
        return "We cooking with GAS!";
    }

    @GetMapping("/createTestInfo")
    public Info createTestInfo() {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createTestInfo ...");
        return Util.createTestInfo();
    }
}
