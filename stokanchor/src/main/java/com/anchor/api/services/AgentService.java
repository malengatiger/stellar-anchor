package com.anchor.api.services;

import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.currencies.GBP;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sendgrid.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class AgentService {
    public AgentService() {
        LOGGER.info("\uD83E\uDD4F \uD83E\uDD4F AgentService - not quite the Secret Service \uD83E\uDD4F \uD83E\uDD4F ");
    }
    public static final Logger LOGGER = LoggerFactory.getLogger(AgentService.class);
    public static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private AccountService accountService;
    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private CryptoService cryptoService;

    @Value("${sendgrid}")
    private String sendgridAPIKey;

    @Value("${fromMail}")
    private String fromMail;

    @Value("${anchorName}")
    private String anchorName;
    @Value("${agentStartingBalance}")
    private String agentStartingBalance;

    public Agent createAgent(Agent agent) throws Exception {
        //todo - create Stellar account; add to Firestore;
        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 " +
                        "creating Agent ... ");
        Anchor anchor = firebaseService.getAnchorByName(anchorName);
        if (anchor == null) {
            throw new Exception("Missing anchor");
        }
        Agent mAgent = firebaseService.getAgentByNameAndAnchor(anchor.getAnchorId(),agent.getPersonalKYCFields().getFirst_name(),
                agent.getPersonalKYCFields().getLast_name());
        if (mAgent != null) {
            LOGGER.info("\uD83D\uDE21 \uD83D\uDE21 Agent already exists for this Anchor: ".concat(anchorName)
            .concat(" \uD83D\uDE21 \uD83D\uDE21 "));
            throw new Exception("\uD83D\uDE21 Agent \uD83D\uDE21 already exists for this \uD83D\uDE21 Anchor");
        }
        //todo - create firebase auth user
        UserRecord record = firebaseService.createUser(agent.getFullName(),
                agent.getPersonalKYCFields().getEmail_address(),"thisShouldBeChanged");
        agent.setAgentId(record.getUid());
        agent.setDateRegistered(new DateTime().toDateTimeISO().toString());
        agent.setDateUpdated(new DateTime().toDateTimeISO().toString());
        cryptoService.downloadSeedFile(anchor.getBaseAccount().getAccountId());
        byte[] bytes = cryptoService.readFile(anchor.getBaseAccount().getAccountId());
        String seed = cryptoService.decrypt(bytes);

        AccountResponseBag bag = accountService.createAndFundStellarAccount(seed,agentStartingBalance);
        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 " +
                "Agent Stellar account has been created and funded with ... "
                        .concat(agentStartingBalance).concat(" XLM"));
        cryptoService.encrypt(bag.getAccountResponse().getAccountId(),bag.getSecretSeed());
        agent.setStellarAccountId(bag.getAccountResponse().getAccountId());
        agent.setExternalAccountId("Not Known Yet");

        firebaseService.addAgent(agent);
        sendEmail(agent);
        LOGGER.info(("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 " +
                "Agent has been added to Firestore ").concat(G.toJson(agent)));
        return agent;
    }

    private void sendEmail(Agent agent) throws IOException {

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Sending registration email to user: "
                + agent.getPersonalKYCFields().getEmail_address());

        //todo - finish registration email composition, links and all, html etc.
        StringBuilder sb = new StringBuilder();
        sb.append("Hi Anchor Admin,\n");
        sb.append("Welcome to The Anchor Network\n");
        sb.append("Click on this link to complete the registration\n");
        sb.append("\nRegards,\n");
        sb.append("Anchor Network Team");

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid: send mail from: " + fromMail);

        Email from = new Email(fromMail);
        String subject = "Welcome to Anchor registration";
        Email to = new Email(agent.getPersonalKYCFields().getEmail_address());
        Content content = new Content("text/plain", sb.toString());
        Mail mail = new Mail(from, subject, to, content);

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid apiKey: " + sendgridAPIKey);
        SendGrid sg = new SendGrid(sendgridAPIKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Registration email sent to Anchor user: \uD83E\uDD66  " + agent.getFullName() +
                    " \uD83E\uDD66 status code: " + response.getStatusCode());
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid: " +
                    " response headers: " + response.getHeaders());

        } catch (IOException ex) {
            throw ex;
        }

    }
}
