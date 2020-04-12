package com.anchor.api.services;

import com.anchor.api.data.account.Account;
import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorUser;
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
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;
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

    @Value("${limit}")
    private String limit;

    @Value("${anchorStartingBalance}")
    private String anchorStartingBalance;
    @Autowired
    private AccountService accountService;

    @Autowired
    private CryptoService cryptoService;

    public AnchorAccountService() {
        LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 AnchorAccountService Constructor fired ...\uD83C\uDF40 " +
                "manages the setup of Anchor base and issuing accounts \uD83C\uDF51 ");
    }

    public Anchor createAnchorAccounts(Anchor newAnchor, String password, String assetCode,
                                       String assetAmount, String fundingSeed, String startingBalance)
            throws Exception {
        LOGGER.info("\n\uD83C\uDF40 \uD83C\uDF40 AnchorAccountService: creating Anchor Accounts " +
                ".... \uD83C\uDF40 DEV STATUS: " + status + " \uD83C\uDF51 " +
                "startingBalance: " + startingBalance + " \uD83C\uDF51 seed: " + fundingSeed);
        accountService = context.getBean(AccountService.class);
        Anchor anchor = new Anchor();

        DateTime dateTime = new DateTime();
        anchor.setDate(dateTime.toDateTimeISO().toString());
        anchor.setName(newAnchor.getName());
        anchor.setEmail(newAnchor.getEmail());
        anchor.setCellphone(newAnchor.getCellphone());
        anchor.setAnchorId(UUID.randomUUID().toString());

        AnchorUser anchorUser = createAnchorUser(anchor, password);
        anchor.setAnchorUser(anchorUser);

        AccountResponseBag baseAccount = accountService.createAndFundStellarAccount(
                fundingSeed,startingBalance);
        AccountResponseBag distributionAccount = accountService.createAndFundStellarAccount(
                baseAccount.getSecretSeed(),anchorStartingBalance);
        AccountResponseBag issuingAccount = accountService.createAndFundStellarAccount(
                baseAccount.getSecretSeed(),anchorStartingBalance);

        Account base = new Account();
        base.setAccountId(baseAccount.getAccountResponse().getAccountId());
        base.setDate(new DateTime().toMutableDateTimeISO().toString());



        Account issuing = new Account();
        issuing.setAccountId(issuingAccount.getAccountResponse().getAccountId());
        issuing.setDate(new DateTime().toMutableDateTimeISO().toString());

        Account distribution = new Account();
        distribution.setAccountId(distributionAccount.getAccountResponse().getAccountId());
        distribution.setDate(new DateTime().toMutableDateTimeISO().toString());

        anchor.setBaseAccount(base);
        anchor.setIssuingAccount(issuing);
        anchor.setDistributionAccount(distribution);

        encryptAndUploadSeedFile(baseAccount.getAccountResponse().getAccountId(),
                baseAccount.getSecretSeed());

        encryptAndUploadSeedFile(issuingAccount.getAccountResponse().getAccountId(),
                issuingAccount.getSecretSeed());

        encryptAndUploadSeedFile(distributionAccount.getAccountResponse().getAccountId(),
                distributionAccount.getSecretSeed());

        try {
            SubmitTransactionResponse transactionResponse = accountService.issueAsset(
                    issuingAccount.getAccountResponse().getAccountId(),
                    distributionAccount.getSecretSeed(),
                    limit, assetCode);
            LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 AnchorAccountService: createAnchorAccounts " +
                    ".... \uD83C\uDF45 TrustLine GetTransactionsResponse Response isSuccess:  " + transactionResponse.isSuccess());

            SubmitTransactionResponse response = accountService.createAsset(
                    issuingAccount.getSecretSeed(),
                    distributionAccount.getAccountResponse().getAccountId(),
                    assetCode,assetAmount);
            LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 AnchorAccountService: createAnchorAccounts " +
                    ".... \uD83C\uDF45 Payment GetTransactionsResponse Response isSuccess:  " + response.isSuccess());

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("GetTransactionsResponse for Asset Issue/Payment failed \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45");
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
//        AccountResponse response1 = accountService.getAccount(baseAccount.getSecretSeed());
//        AccountResponse response2 = accountService.getAccount(issuingAccount.getSecretSeed());
//        AccountResponse response3 = accountService.getAccount(distributionAccount.getSecretSeed());
//        LOGGER.info("\n\n \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ..... CHECKING ACCOUNTS AFTER ALL THAT .... \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ");
//        LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 BASE ACCOUNT: " + G.toJson(response1));
//        LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ISSUING ACCOUNT: " + G.toJson(response2));
//        LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 DISTRIBUTION ACCOUNT: " + G.toJson(response3));
        return anchor;
    }

    private void encryptAndUploadSeedFile(String accountId, String seed) throws IOException {
        //todo - have to check if keyRing etc. exists .....
        LOGGER.info("................ ♦️ ♦️ encryptAndUploadSeedFile ♦️ ♦️ ................");
        try {
            cryptoService.createKeyRing();
        } catch (Exception e) {
            LOGGER.severe("cryptoService.createKeyRing Failed: " + e.getMessage());
        }
        try {
            cryptoService.createCryptoKey();
        } catch (Exception e) {
            LOGGER.severe(" cryptoService.createCryptoKey Failed: " + e.getMessage());
        }
        try {
            cryptoService.encrypt(accountId, seed);
        } catch (Exception e) {
            LOGGER.severe("cryptoService.encrypt Failed: " + e.getMessage());
        }
    }

    private AnchorUser createAnchorUser(Anchor anchor, String password) throws Exception {

        FirebaseService scaffold = context.getBean(FirebaseService.class);

        UserRecord userRecord = scaffold.createUser(anchor.getName(),anchor.getEmail(),password);
        AnchorUser anchorUser = new AnchorUser();
        anchorUser.setFirstName(anchor.getName());
        anchorUser.setEmail(anchor.getEmail());
        anchorUser.setCellphone(anchor.getCellphone());
        anchorUser.setUserId(userRecord.getUid());
        anchorUser.setAnchorId(anchor.getAnchorId());
        DateTime dateTime = new DateTime();
        anchorUser.setDate(dateTime.toDateTimeISO().toString());
        anchorUser.setActive(true);
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 about to write Anchor USER to Firestore: ".concat(anchorUser.getFirstName()));
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection("anchorUsers").add(anchorUser);
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C Anchor AnchorUser created and added to Firestore at path:" +
                " \uD83E\uDD6C " + future.get().getPath());
        LOGGER.info(G.toJson(anchorUser));

        return anchorUser;
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
