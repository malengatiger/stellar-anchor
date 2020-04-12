package com.anchor.api.controllers;

import com.anchor.api.services.AccountService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.stellar.sdk.responses.AccountResponse;

import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
@RestController
public class AuthController {
    public static final Logger LOGGER = Logger.getLogger(AuthController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public AuthController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C MainController  " +
                "\uD83C\uDF51 constructed and ready to go! \uD83C\uDF45 CORS enabled for the controller");
    }

    @Autowired
    private ApplicationContext context;
    @Value("${status}")
    private String status;

    @Value("${anchorName}")
    private String anchorName;

    @GetMapping(value = "/challenge", produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse getAccount(@RequestParam String account) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 AuthController : challenge ...");
        AccountService accountService = context.getBean(AccountService.class);
        AccountResponse response = accountService.getAccount(account);
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C MainController getAccount returned"
                + response.getAccountId() + " \uD83D\uDC99 \uD83D\uDC9C");
        return response;
    }

}
