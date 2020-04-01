package com.anchor.api;

import com.anchor.api.data.Anchor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.logging.Logger;

@RestController
public class TransferController {
    public static final Logger LOGGER = Logger.getLogger(TransferController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public TransferController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C TransferController  \uD83C\uDF51 constructed and ready to go!");
    }

    @Autowired
    private ApplicationContext context;

    /*
    POST TRANSFER_SERVER_SEP0024/transactions/deposit/interactive
    Content-Type: multipart/form-data
    asset_code=USD&email_address=myaccount@gmail.com&account=GACW7NONV43MZIFHCOKCQJAKSJSISSICFVUJ2C6EZIW5773OU3HD64VI

     */
    @PostMapping("/transactions/deposit/interactive")
    public Anchor deposit(@RequestBody Anchor anchor) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:deposit ...");
       
        return null;
    }
    @PostMapping("/transactions/withdraw/interactive")
    public Anchor withdraw(@RequestBody Anchor anchor) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:withdraw ...");
        
        return null;
    }
    @GetMapping("/info")
    public String info(@RequestParam String seed) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:info ...");
        
        return null;
    }
    @GetMapping("/fee")
    public String fee(@RequestParam String seed) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:fee ...");

        return null;
    }
    @GetMapping("/transaction")
    public String transaction(@RequestParam String seed) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:transaction ...");

        return null;
    }
    @GetMapping("/transactions")
    public String transactions(@RequestParam String seed) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:transactions ...");

        return null;
    }

}
