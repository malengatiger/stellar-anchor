package com.anchor.api.services;

import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.util.Emoji;
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

@Service
public class AgentService {
    public AgentService() {
        LOGGER.info(Emoji.DRUM + Emoji.DRUM + "AgentService - not quite the Secret Service" + Emoji.DRUM + Emoji.DRUM);
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

    @Value("${clientStartingBalance}")
    private String clientStartingBalance;

    public com.anchor.api.data.anchor.Client createClient(com.anchor.api.data.anchor.Client client) throws Exception {

        LOGGER.info(Emoji.LEMON + Emoji.LEMON +
                        "....... creating Client ....... ");
        Anchor anchor = firebaseService.getAnchorByName(anchorName);
        if (anchor == null) {
            throw new Exception("Missing anchor");
        }
        Client mClient = firebaseService.getClientByNameAndAnchor(
                anchor.getAnchorId(),
                client.getPersonalKYCFields().getFirstName(),
                client.getPersonalKYCFields().getLastName());

        if (mClient != null) {
            LOGGER.info(Emoji.ERROR + "Client already exists for this Anchor: ".concat(anchorName)
            .concat(Emoji.ERROR));
            throw new Exception(Emoji.ERROR + "Client already exists for this Anchor");
        }
        //create firebase auth user
        UserRecord record = firebaseService.createUser(client.getFullName(),
                client.getPersonalKYCFields().getEmailAddress(), client.getPassword());
        client.setClientId(record.getUid());
        client.setDateRegistered(new DateTime().toDateTimeISO().toString());
        client.setDateUpdated(new DateTime().toDateTimeISO().toString());
        //handle encryption of secret seed
        cryptoService.downloadSeedFile(anchor.getBaseAccount().getAccountId());
        byte[] bytes = cryptoService.readFile(anchor.getBaseAccount().getAccountId());
        String seed = cryptoService.decrypt(bytes);

        AccountResponseBag bag = accountService.createAndFundStellarAccount(seed,clientStartingBalance);
        LOGGER.info(Emoji.HEART_PURPLE + Emoji.HEART_PURPLE +
                "Client Stellar account has been created and funded with ... "
                        .concat(clientStartingBalance).concat(" XLM"));
        //handle seed encryption
        cryptoService.encrypt(bag.getAccountResponse().getAccountId(),bag.getSecretSeed());

        client.setAccount(bag.getAccountResponse().getAccountId());
        client.setExternalAccountId("Not Known Yet");
        String savePassword = client.getPassword();
        client.setPassword(null);
        firebaseService.addClient(client);
        sendEmail(client);
        client.setPassword(savePassword);
        LOGGER.info((Emoji.BLUE_DOT + Emoji.BLUE_DOT +
                "Client has been added to Firestore ").concat(G.toJson(client)));
        client.setSecretSeed(bag.getSecretSeed());
        return client;
    }

    public Agent createAgent(Agent agent) throws Exception {
        //todo - create Stellar account; add to Firestore;
        LOGGER.info(Emoji.YELLOW_STAR + Emoji.YELLOW_STAR + Emoji.YELLOW_STAR +
                "....... creating Agent ....... ");
        Anchor anchor = firebaseService.getAnchorByName(anchorName);
        if (anchor == null) {
            throw new Exception("Missing anchor");
        }
        Agent mAgent = firebaseService.getAgentByNameAndAnchor(
                anchor.getAnchorId(),
                agent.getPersonalKYCFields().getFirstName(),
                agent.getPersonalKYCFields().getLastName());
        if (mAgent != null) {
            LOGGER.info(Emoji.ERROR.concat(anchorName)
                    .concat(" ").concat(Emoji.ERROR));
            throw new Exception(Emoji.ERROR + "Agent already exists for this Anchor");
        }
        //create firebase auth user
        UserRecord record = firebaseService.createUser(agent.getFullName(),
                agent.getPersonalKYCFields().getEmailAddress(),agent.getPassword());
        agent.setAgentId(record.getUid());
        agent.setDateRegistered(new DateTime().toDateTimeISO().toString());
        agent.setDateUpdated(new DateTime().toDateTimeISO().toString());
        cryptoService.downloadSeedFile(anchor.getBaseAccount().getAccountId());
        byte[] bytes = cryptoService.readFile(anchor.getBaseAccount().getAccountId());
        String seed = cryptoService.decrypt(bytes);
//  🍅
        AccountResponseBag bag = accountService.createAndFundStellarAccount(seed,agentStartingBalance);
        LOGGER.info(Emoji.RED_APPLE + Emoji.RED_APPLE +
                "Agent Stellar account has been created and funded with ... "
                        .concat(agentStartingBalance).concat(" XLM"));
        cryptoService.encrypt(bag.getAccountResponse().getAccountId(),bag.getSecretSeed());
        agent.setStellarAccountId(bag.getAccountResponse().getAccountId());
        agent.setExternalAccountId("Not Known Yet");
        String savePassword = agent.getPassword();
        agent.setPassword(null);
        firebaseService.addAgent(agent);
        sendEmail(agent);
        LOGGER.info((Emoji.LEAF + Emoji.LEAF +
                "Agent has been added to Firestore ").concat(G.toJson(agent)));
        agent.setPassword(savePassword);
        agent.setSecretSeed(bag.getSecretSeed());
        return agent;
    }

    private void sendEmail(Agent agent) throws IOException {

        LOGGER.info(Emoji.PLANE + Emoji.PLANE + "Sending registration email to user: "
                + agent.getPersonalKYCFields().getEmailAddress());

        //todo - finish registration email composition, links and all, html etc.
        StringBuilder sb = new StringBuilder();
        sb.append("Hi Anchor Admin,\n");
        sb.append("Welcome to The Anchor Network\n");
        sb.append("Click on this link to complete the registration\n");
        sb.append("\nRegards,\n");
        sb.append("Anchor Network Team");

        LOGGER.info(Emoji.RAIN_DROPS + Emoji.RAIN_DROP + "SendGrid: send mail from: " + fromMail);

        Email from = new Email(fromMail);
        String subject = "Welcome to Anchor registration";
        Email to = new Email(agent.getPersonalKYCFields().getEmailAddress());
        Content content = new Content("text/plain", sb.toString());
        Mail mail = new Mail(from, subject, to, content);
// 🌼
        LOGGER.info(Emoji.FLOWER_YELLOW + Emoji.FLOWER_YELLOW + "SendGrid apiKey: " + sendgridAPIKey);
        SendGrid sg = new SendGrid(sendgridAPIKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            LOGGER.info(Emoji.BLUE_DISC + Emoji.BLUE_DISC +
                    "Registration email sent to Anchor user: "
                    + Emoji.BLUE_THINGY + agent.getFullName() + Emoji.PANDA +
                    " status code: " + response.getStatusCode());
            LOGGER.info(Emoji.FLOWER_YELLOW + Emoji.FLOWER_YELLOW + "SendGrid: " +
                    " response headers: " + response.getHeaders());

        } catch (IOException ex) {
            throw ex;
        }

    }

    private void sendEmail(Client agent) throws IOException {

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Sending registration email to user: "
                + agent.getPersonalKYCFields().getEmailAddress());

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
        Email to = new Email(agent.getPersonalKYCFields().getEmailAddress());
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
