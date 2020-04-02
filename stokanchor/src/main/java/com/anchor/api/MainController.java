package com.anchor.api;

import com.anchor.api.data.Anchor;
import com.anchor.api.data.AnchorBag;
import com.anchor.api.data.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import shadow.org.apache.commons.io.FileUtils;
import shadow.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
public class MainController {
    public static final Logger LOGGER = Logger.getLogger(MainController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    public MainController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C MainController  \uD83C\uDF51 constructed and ready to go!");
    }

    @Autowired
    private ApplicationContext context;

    @GetMapping(value = "/.well-known/stellar.toml", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getStellarToml() throws Exception {
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
    @GetMapping(value = "/.stellar.toml", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getStellarTomlToo() throws Exception {
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
    @GetMapping("/ping")
    public String ping() {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Pinging StokkieAnchorApplication ...");
        return "\uD83D\uDC99 \uD83D\uDC9C StokkieAnchorApplication pinged at " + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C";
    }
    @GetMapping("/getAccount")
    public String getAccount(@RequestParam String seed) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:getAccount ...");
        return "\uD83D\uDC99 \uD83D\uDC9C MainController getAccount at "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C";
    }
    @PostMapping("/createAnchor")
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
    @PostMapping("/createUser")
    public User createUser(@RequestBody User user) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createUser ...");
        AccountService service = context.getBean(AccountService.class);
        User bag = service.createUser(user);
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Stellar returns User: \uD83C\uDF4E "
                + bag.getFullName() + " userId: " + bag.getUserId());
        return bag;
    }
    @PostMapping("/createUserWithExistingAccount")
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
}
