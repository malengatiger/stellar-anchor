package com.anchor.api.controllers;

import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.AgentService;
import com.anchor.api.services.AnchorAccountService;
import com.anchor.api.util.DemoDataGenerator;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
@RestController
public class DataGenerationController {
    public static final Logger LOGGER = Logger.getLogger(AnchorController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
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

    @GetMapping(value = "/generateAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor generateAnchor(@RequestParam String anchorName) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateAnchor ...");
        Anchor anchor = demoDataGenerator.startAnchor(anchorName);
        LOGGER.info(Emoji.DICE.concat(Emoji.DICE.concat(Emoji.DICE)
                .concat("New Anchor Returned")
                .concat(G.toJson(anchor))));
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData:generateAnchor completed and returning external caller... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status);
        return anchor;
    }
    @GetMapping(value = "/generateDemo", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateDemo(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateDemo ...");
        demoDataGenerator.startGeneration(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateLoans", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateLoans(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateLoans ...");
        demoDataGenerator.generateLoanApplications(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateLoans completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateStokvel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Stokvel generateStokvel(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateStokvel ...");
        Stokvel stokvel = demoDataGenerator.generateStokvel(anchorId);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C GenerateStokvel completed ... "
                + stokvel.getName() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return stokvel;
    }
    @GetMapping(value = "/generateStokvelMembers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Member> generateStokvelMembers(@RequestParam String anchorId, @RequestParam String stokvelId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateStokvelMembers ...");
        List<Member> stokvel = demoDataGenerator.generateStokvelMembers(stokvelId, anchorId);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C GenerateStokvelMembers completed ... "
                + stokvel + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return stokvel;
    }
    @GetMapping(value = "/generateAgentFunding", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateAgentFunding(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateAgentFunding ...");
        demoDataGenerator.generateAgentFunding(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateAgentFunding completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateLoanApprovals", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateLoanApprovals(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateLoanApprovals ...");
        demoDataGenerator.generateLoanApprovals(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateLoanApprovals completed ... "
                + new DateTime().toDateTimeISO().toString() + " \uD83D\uDC99 STATUS: " + status;
    }
    @GetMapping(value = "/generatePayments", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generatePayments(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generatePayments ...");
        demoDataGenerator.generatePayments(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GeneratePayments completed ... "
                + new DateTime().toDateTimeISO().toString() + " \uD83D\uDC99 STATUS: " + status;
    }
}
