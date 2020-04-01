package com.anchor.api;

import com.anchor.api.data.Anchor;
import com.anchor.api.data.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public Anchor createAnchor(@RequestBody Anchor anchor) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 MainController:createAnchor ...");
        AnchorAccountService service = context.getBean(AnchorAccountService.class);
        Anchor bag = service.createAnchorAccounts(anchor,"anchor#01Pass","100");
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Stellar returns Anchor: \uD83C\uDF4E "
                + bag.getName() + " anchorId: " + bag.getAnchorId());
        return bag;
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
