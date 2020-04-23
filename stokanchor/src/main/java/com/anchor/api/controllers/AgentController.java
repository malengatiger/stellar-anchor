package com.anchor.api.controllers;

import com.anchor.api.data.anchor.*;
import com.anchor.api.services.*;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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
    public Agent createAgent(@RequestBody Agent agent) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:createAgent ...");

        //todo - externalize variables .....
        Agent mAgent = agentService.createAgent(agent);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + "Anchor returns Agent with a new Stellar account:" +
                " \uD83C\uDF4E "
                + G.toJson(mAgent));
        return mAgent;

    }

    @PostMapping(value = "/updateAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateAgent(@RequestBody Agent agent) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:createAgent ...");
        String message = agentService.updateAgent(agent);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + message);
        return message;
    }

    @PostMapping(value = "/approveApplicationByAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication approveApplicationByAgent(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:approve ...");
        LoanApplication application = agentService.approveApplicationByAgent(loanApplication);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + application.getAmount() + " "
        + application.getAssetCode());
        return application;
    }

    //approveApplicationByClient
    @PostMapping(value = "/approveApplicationByClient", produces = MediaType.APPLICATION_JSON_VALUE)
    public String approveApplicationByClient(
            @RequestParam String loanId) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS)
                + "AgentController:approveApplicationByClient ...");
        String msg =  agentService.approveApplicationByClient(loanId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + msg);
        return msg;
    }

    @PostMapping(value = "/decline", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication declineApplication(@RequestBody LoanApplication agent) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:decline ...");
        LoanApplication application = agentService.declineApplication(agent);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + application.getAmount() + " "
                + application.getAssetCode());
        return application;
    }

    @PostMapping(value = "/makeLoanPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanPayment makeLoanPayment(@RequestBody LoanPayment loanPayment) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:makeLoanPayment ...");
        LoanPayment payment = agentService.addLoanPayment(loanPayment);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + loanPayment.getAmount() + " "
                + loanPayment.getAssetCode());
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
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS).concat(Emoji.RAIN_DROPS)
                + "AgentController:loanApplication ...");
        LoanApplication org = agentService.addLoanApplication(loanApplication);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(org));
        return org;
    }

    @PostMapping(value = "/approveLoanApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication approveLoanApplication(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AgentController:approveLoanApplication ...");
        LoanApplication org = agentService.approveApplicationByAgent(loanApplication);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + G.toJson(org));
        return org;
    }
    @Autowired
    private PaymentService paymentService;
    @PostMapping(value = "/sendPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean sendPayment(@RequestBody PaymentRequest paymentRequest) throws Exception {
        SubmitTransactionResponse response = paymentService.sendPayment(paymentRequest);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat("Payment sent? ")
        .concat("" + response.isSuccess()));

        return response.isSuccess();
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
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + " payments found:" + loanPayments.size());
        return loanPayments;
    }

    @Autowired
    private FirebaseService firebaseService;
    @Value("${anchorName}")
    private String anchorName;
    @GetMapping(value = "/getAgents", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Agent> getAgents(@RequestParam String anchorId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:getAgents ...");

        List<Agent> agents = firebaseService.getAgents(anchorId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat(
                ("Found " + agents.size() + " agents for this Anchor ")));
        return agents;
    }
    @GetMapping(value = "/getPaymentRequests", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PaymentRequest> getPaymentRequests(@RequestParam String anchorId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:getPaymentRequests ...");

        List<PaymentRequest> requests = firebaseService.getPaymentRequests(anchorId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat(
                ("Found " + requests.size() + " PaymentRequests for this Anchor "))
               );
        return requests;
    }
    @GetMapping(value = "/getAgentClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> getAgentClients(@RequestParam String agentId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:getAgentClients ...");
        List<Client> agentClients = agentService.getAgentClients(agentId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat(
                ("Found " + agentClients.size() + " clients for this Agent ")));
        return agentClients;
    }

    @GetMapping(value = "/getAgentLoans", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LoanApplication> getAgentLoans(@RequestParam String agentId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:getAgentLoans ...");
        List<LoanApplication> agentLoans = agentService.getAgentLoans(agentId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat
                (" found " + agentLoans.size() + " ") );
        return agentLoans;
    }

    @GetMapping(value = "/removeClient", produces = MediaType.TEXT_PLAIN_VALUE)
    public String removeClient(@RequestParam String clientId) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "AgentController:removeClient ...");
        String message = agentService.removeClient(clientId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + message);
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
    public Client createClient(@RequestBody Client client) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS).concat(Emoji.DRUM)
                + "AnchorController:createClient...".concat(Emoji.DRUM));

        Client mClient = agentService.createClient(client);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + "Agent returns Client with brand new Stellar account: \uD83C\uDF4E "
                + mClient.getFullName());
        return mClient;

    }

    @PostMapping(value = "/updateClient", produces = MediaType.TEXT_PLAIN_VALUE)
    public String updateClient(@RequestBody Client client) throws Exception {
        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:updateClient...");
        String mClient = agentService.updateClient(client);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + mClient);
        return mClient;
    }

    @Autowired
    private TOMLService tomlService;
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
        tomlService.encryptAndUploadFile(anchorId,mFile);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!" );
        return bytes;
    }
    @GetMapping(value = "/getTOML", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTOML(@RequestParam("anchorId") String anchorId) throws Exception {

        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:getTOML...");
        Toml toml = tomlService.getToml(anchorId);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned TOML from download .... "
                .concat(" databaseUrl: ")
        .concat(toml.getString("databaseUrl")));
        return toml.toMap();
    }


    public static class PaymentRequest {
        private String paymentRequestId, seed,
        assetCode,
        amount,
        date, anchorId,
        destinationAccount, sourceAccount, loanId;
        private Long ledger;
        public PaymentRequest() {
        }

        public PaymentRequest(String paymentRequestId, String seed, String assetCode,
                              String amount, String date, String anchorId, String destinationAccount,
                              String sourceAccount) {
            this.paymentRequestId = paymentRequestId;
            this.seed = seed;
            this.assetCode = assetCode;
            this.amount = amount;
            this.date = date;
            this.anchorId = anchorId;
            this.destinationAccount = destinationAccount;
            this.sourceAccount = sourceAccount;
        }

        public String getLoanId() {
            return loanId;
        }

        public void setLoanId(String loanId) {
            this.loanId = loanId;
        }

        public String getPaymentRequestId() {
            return paymentRequestId;
        }

        public void setPaymentRequestId(String paymentRequestId) {
            this.paymentRequestId = paymentRequestId;
        }

        public String getSourceAccount() {
            return sourceAccount;
        }

        public void setSourceAccount(String sourceAccount) {
            this.sourceAccount = sourceAccount;
        }

        public String getAnchorId() {
            return anchorId;
        }

        public void setAnchorId(String anchorId) {
            this.anchorId = anchorId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getLedger() {
            return ledger;
        }

        public void setLedger(Long ledger) {
            this.ledger = ledger;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }

        public String getAssetCode() {
            return assetCode;
        }

        public void setAssetCode(String assetCode) {
            this.assetCode = assetCode;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDestinationAccount() {
            return destinationAccount;
        }

        public void setDestinationAccount(String destinationAccount) {
            this.destinationAccount = destinationAccount;
        }
    }
}
