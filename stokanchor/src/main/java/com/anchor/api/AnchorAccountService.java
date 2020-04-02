package com.anchor.api;

import com.anchor.api.data.Account;
import com.anchor.api.data.AccountResponseBag;
import com.anchor.api.data.Anchor;
import com.anchor.api.data.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sendgrid.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class AnchorAccountService {
    public static final Logger LOGGER = Logger.getLogger(AnchorAccountService.class.getSimpleName());
    public static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private ApplicationContext context;

    @Value("${status}")
    private String status;

    @Value("${sendgrid}")
    private String sendgridAPIKey;

    @Value("${fromMail}")
    private String fromMail;

    @Value("${seed}")
    private String seed;

    @Value("${account}")
    private String account;

    @Value("${startingBalance}")
    private String startingBalance;
    @Value("${distributionStartingBalance}")
    private String distributionStartingBalance;
    @Value("${issuingStartingBalance}")
    private String issuingStartingBalance;
    @Value("${limit}")
    private String limit;

    public AnchorAccountService() {
        LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 AnchorAccountService Constructor fired ...\uD83C\uDF40 " +
                "manages the setup of Anchor base and issuing accounts \uD83C\uDF51 ");
    }

    public Anchor createAnchorAccounts(Anchor newAnchor, String password, String assetCode, String assetAmount) throws Exception {
        LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 AnchorAccountService: creating Anchor Accounts .... \uD83C\uDF40 DEV STATUS: " + status);
        List<AccountResponseBag> mList = new ArrayList<>();
        AccountService service = context.getBean(AccountService.class);
        Anchor anchor = new Anchor();

        DateTime dateTime = new DateTime();
        anchor.setDate(dateTime.toDateTimeISO().toString());
        anchor.setName(newAnchor.getName());
        anchor.setEmail(newAnchor.getEmail());
        anchor.setCellphone(newAnchor.getCellphone());
        anchor.setAnchorId(UUID.randomUUID().toString());

        User user = createAnchorUser(anchor, password);
        anchor.setUser(user);

        AccountResponseBag baseAccount = service.createAndFundStellarAccount(seed,startingBalance);
        AccountResponseBag distributionAccount = service.createAndFundStellarAccount(baseAccount.getSecretSeed(),distributionStartingBalance);
        AccountResponseBag issuingAccount = service.createAndFundStellarAccount(baseAccount.getSecretSeed(),issuingStartingBalance);

        anchor.setBaseAccount(new Account(baseAccount));
        anchor.setIssuingAccount(new Account(issuingAccount));
        anchor.setDistributionAccount(new Account(distributionAccount));

        try {
            SubmitTransactionResponse transactionResponse = service.issueAsset(
                    issuingAccount.getAccountResponse().getAccountId(),
                    distributionAccount.getSecretSeed(),
                    limit, assetCode);
            LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 AnchorAccountService: createAnchorAccounts " +
                    ".... \uD83C\uDF45 TrustLine Transaction Response isSuccess:  " + transactionResponse.isSuccess());

            SubmitTransactionResponse response = service.createAsset(
                    issuingAccount.getSecretSeed(),
                    distributionAccount.getAccountResponse().getAccountId(),
                    assetCode,assetAmount);
            LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 AnchorAccountService: createAnchorAccounts " +
                    ".... \uD83C\uDF45 Payment Transaction Response isSuccess:  " + response.isSuccess());

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("Transaction for Asset Issue/Payment failed \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45");
        }
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C Anchor created and will be added to Firestore: " +
                " " + anchor.getName());
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection("anchors").add(anchor);
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C Anchor added to Firestore at path: \uD83E\uDD6C " + future.get().getPath());
        LOGGER.info(G.toJson(anchor));
        //todo - send email to confirm the anchor with link ...
        try {
            sendEmail(anchor);
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Email has been sent ... ");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("Email sending failed");
        }
        //todo - list accounts and check balances
        AccountResponse response1 = service.getAccount(baseAccount.getSecretSeed());
        AccountResponse response2 = service.getAccount(issuingAccount.getSecretSeed());
        AccountResponse response3 = service.getAccount(distributionAccount.getSecretSeed());
        LOGGER.info("\n\n \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ..... CHECKING ACCOUNTS AFTER ALL THAT .... \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ");
        LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 BASE ACCOUNT: " + G.toJson(response1));
        LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ISSUING ACCOUNT: " + G.toJson(response2));
        LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 DISTRIBUTION ACCOUNT: " + G.toJson(response3));
        return anchor;
    }

    private User createAnchorUser(Anchor anchor, String password) throws Exception {

        FirebaseScaffold scaffold = context.getBean(FirebaseScaffold.class);

        UserRecord userRecord = scaffold.createUser(anchor.getName(),anchor.getEmail(),password);
        User user = new User();
        user.setFirstName(anchor.getName());
        user.setEmail(anchor.getEmail());
        user.setCellphone(anchor.getCellphone());
        user.setUserId(userRecord.getUid());
        user.setAnchorId(anchor.getAnchorId());
        DateTime dateTime = new DateTime();
        user.setDate(dateTime.toDateTimeISO().toString());
        user.setActive(true);
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 about to write Anchor USER to Firestore: ".concat(user.getFirstName()));
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection("users").add(anchor);
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C Anchor User created and added to Firestore at path:" +
                " \uD83E\uDD6C " + future.get().getPath());
        LOGGER.info(G.toJson(user));

        return user;
    }


    private void sendEmail(Anchor anchor) throws IOException {

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Sending registration email to user: " + anchor.getEmail());

        //todo - finish registration email composition, links and all, html etc.
        StringBuilder sb = new StringBuilder();
        sb.append("Hi Anchor Admin,\n");
        sb.append("Welcome to The Anchor Network\n");
        sb.append("Click on this link to complete the registration\n");
        sb.append("\nRegards,\n");
        sb.append("Anchor Network Team");

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid: send mail from: " + fromMail);

        Email from = new Email(fromMail);
        String subject = "Welcome to " + anchor.getName() + " registration";
        Email to = new Email(anchor.getEmail());
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
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Registration email sent to Anchor user: \uD83E\uDD66  " + anchor.getName() +
                    " \uD83E\uDD66 status code: " + response.getStatusCode());
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid: " +
                    " response headers: " + response.getHeaders());

        } catch (IOException ex) {
            throw ex;
        }

    }
}
