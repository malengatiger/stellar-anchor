package com.anchor.api.controllers;

import com.anchor.api.data.anchor.*;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.AgentService;
import com.anchor.api.services.AnchorAccountService;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
public class AgentController {
    public static final Logger LOGGER = LoggerFactory.getLogger(AgentController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public AgentController() {
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
    @Value("${status}")
    private String status;

    /**
     * This endpoint creates an Agent. It is called by the Anchor itself or is part of a registration by the Agent
     * on some kind of app (web or mobile). The Agent, in turn,
     *
     * @param agent
     * @return A new Agent
     * @throws Exception
     */
    @PostMapping(value = "/createAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAgent(@RequestBody Agent agent) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:createAgent ...");
        try {
            Agent mAgent = agentService.createAgent(agent);
            LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + "Anchor returns Agent with a new Stellar account:" +
                    " \uD83C\uDF4E "
                    + G.toJson(mAgent));
            return ResponseEntity.ok(mAgent);
        } catch (Exception e) {
            LOGGER.info("CreateAgent Failed", e);
            return ResponseEntity.badRequest()
                    .body("Create Agent Failed");
        }
    }

    @PostMapping(value = "/updateAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateAgent(@RequestBody Agent agent) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:createAgent ...");
        String message = agentService.updateAgent(agent);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + message);
        return message;
    }

    @PostMapping(value = "/apply", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication addApplication(@RequestBody LoanApplication agent) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:apply ...");
        LoanApplication application = agentService.addLoanApplication(agent);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(application));
        return application;
    }

    @PostMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication approveApplication(@RequestBody LoanApplication agent) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:approve ...");
        LoanApplication application = agentService.approveApplication(agent);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(application));
        return application;
    }

    @PostMapping(value = "/decline", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication declineApplication(@RequestBody LoanApplication agent) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:decline ...");
        LoanApplication application = agentService.declineApplication(agent);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(application));
        return application;
    }

    @PostMapping(value = "/makeLoanPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanPayment makeLoanPayment(@RequestBody LoanPayment loanPayment) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:makeLoanPayment ...");
        LoanPayment payment = agentService.addLoanPayment(loanPayment);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(payment));
        return payment;
    }

    @PostMapping(value = "/addOrganization", produces = MediaType.APPLICATION_JSON_VALUE)
    public Organization addOrganization(@RequestBody Organization organization) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:addOrganization ...");

            Organization org = agentService.addOrganization(organization);
            LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(org));
            return org;
    }
    @PostMapping(value = "/loanApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication loanApplication(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:loanApplication ...");
        LoanApplication org = agentService.addLoanApplication(loanApplication);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(org));
        return org;
    }
    @PostMapping(value = "/approveLoanApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication approveLoanApplication(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:approveLoanApplication ...");
        LoanApplication org = agentService.approveApplication(loanApplication);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(org));
        return org;
    }
    @PostMapping(value = "/declineLoanApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication declineLoanApplication(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:declineLoanApplication ...");
        LoanApplication org = agentService.declineApplication(loanApplication);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(org));
        return org;
    }
    @PostMapping(value = "/loanPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanPayment loanPayment(@RequestBody LoanPayment loanPayment) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:loanPayment ...");
        LoanPayment org = agentService.addLoanPayment(loanPayment);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(org));
        return org;
    }

    @GetMapping(value = "/getLoanPayments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LoanPayment> getLoanPayments(@RequestParam String loanId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:getLoanPayments ...");
        List<LoanPayment> loanPayments = agentService.getLoanPayments(loanId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(loanPayments));
        return loanPayments;
    }

    @GetMapping(value = "/getAgentClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> getAgentClients(@RequestParam String agentId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:getAgentClients ...");
        List<Client> agentClients = agentService.getAgentClients(agentId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat(
                ("Found " + agentClients.size() + " clients for this Agent ")) + G.toJson(agentClients));
        return agentClients;
    }

    @GetMapping(value = "/getAgentLoans", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LoanApplication> getAgentLoans(@RequestParam String agentId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:getAgentLoans ...");
        List<LoanApplication> agentLoans = agentService.getAgentLoans(agentId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat
                (" found " + agentLoans.size() + " ") + G.toJson(agentLoans));
        return agentLoans;
    }

    @GetMapping(value = "/removeClient", produces = MediaType.TEXT_PLAIN_VALUE)
    public String removeClient(@RequestParam String clientId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:removeClient ...");
        String message = agentService.removeClient(clientId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(message));
        return message;
    }

    /**
     * This endpoint creates a new Client. Every Client is registered via the Agent who ensures the veracity of
     * the KYC/AML data required for a Client to be registered with both the Agent and the Anchor. The Client accesses
     * loans via an 'application' to the Agent that registered them.
     *
     * @param client
     * @return A new Client with brand-new Stellar account
     * @throws Exception
     */
    @PostMapping(value = "/createClient", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createClient(@RequestBody Client client) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS).concat(Emoji.DRUM)
                + "AnchorController:createClient...".concat(Emoji.DRUM));
        try {
            Client mClient = agentService.createClient(client);
            LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + "Agent returns Client with brand new Stellar account: \uD83C\uDF4E "
                    + G.toJson(mClient));
            return ResponseEntity.ok(mClient);
        } catch (Exception e) {
            String msg = Emoji.ERROR + "Create Client Failed";
            LOGGER.info(msg, e);
            return ResponseEntity.badRequest()
                    .body(msg);
        }
    }

    @PostMapping(value = "/updateClient", produces = MediaType.TEXT_PLAIN_VALUE)
    public String updateClient(@RequestBody Client client) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:updateClient...");
        String mClient = agentService.updateClient(client);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + mClient);
        return mClient;
    }


}
