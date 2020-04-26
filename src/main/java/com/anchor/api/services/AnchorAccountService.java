package com.anchor.api.services;

import com.anchor.api.data.account.Account;
import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorUser;
import com.anchor.api.util.Emoji;
import com.google.firebase.auth.UserRecord;
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

    @Value("${limit}")
    private String limit;

    @Value("${anchorStartingBalance}")
    private String anchorStartingBalance;
    @Autowired
    private AccountService accountService;
    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private CryptoService cryptoService;
//🍀 🍀
    public AnchorAccountService() {
        LOGGER.info(Emoji.DRUM + Emoji.DRUM + "AnchorAccountService Constructor fired ..." +
                Emoji.HEART_ORANGE + "manages the setup of Anchor base and issuing accounts");
    }
/*
    🍎 🍎 🍎 Assets
    The Stellar distributed network can be used to track, hold, and transfer any type of asset: dollars, euros, bitcoin, stocks, gold, and other tokens of value. Any asset on the network can be traded and exchanged with any other.

    Other than lumens (see below), all assets have

    Asset type: e.g., USD or BTC
    Issuer: the account that created the asset
    Trustlines
    When you hold assets in Stellar, you’re actually holding credit from a particular issuer.
    The issuer has agreed that it will trade you its credit on the Stellar network for the corresponding asset–e.g.,
    fiat currency, precious metal–outside of Stellar. Let’s say that Scott issues oranges as credit on the network.
    If you hold orange credits, you and Scott have an agreement based on trust, or a trustline: you both agree that when you give Scott an orange credit, he gives you an orange.

    When you hold an asset, you must trust the issuer to properly redeem its credit.
    Since users of Stellar will not want to trust just any issuer, accounts must explicitly
    trust an issuing account before they’re able to hold the issuer’s credit.
    In the example above, you must explicitly trust Scott before you can hold orange credits.

    To trust an issuing account, you create a trustline. Trustlines are entries that persist in the Stellar ledger. They track the limit for which your account trusts the issuing account and the amount of credit from the issuing account that your account currently holds.
 */
    public Anchor createAnchorAccounts(Anchor newAnchor, String password,
                                       String assetAmount, String fundingSeed, String startingBalance)
            throws Exception {
        LOGGER.info(Emoji.FERN + Emoji.FERN + "AnchorAccountService: creating Anchor Accounts " +
                ".... \uD83C\uDF40 DEV STATUS: " + status + " \uD83C\uDF51 " +
                "startingBalance: " + startingBalance + " \uD83C\uDF51 seed: " + fundingSeed);
        Anchor anchor = new Anchor();

        DateTime dateTime = new DateTime();
        anchor.setDate(dateTime.toDateTimeISO().toString());
        anchor.setName(newAnchor.getName());
        anchor.setEmail(newAnchor.getEmail());
        anchor.setCellphone(newAnchor.getCellphone());
        anchor.setAnchorId(UUID.randomUUID().toString());

        AccountResponseBag baseAccount = accountService.createAndFundAnchorAccount(
                fundingSeed,startingBalance);
        AccountResponseBag distributionAccount = accountService.createAndFundAnchorAccount(
                baseAccount.getSecretSeed(),anchorStartingBalance);
        AccountResponseBag issuingAccount = accountService.createAndFundAnchorAccount(
                baseAccount.getSecretSeed(),anchorStartingBalance);

        Account base = new Account();
        base.setAccountId(baseAccount.getAccountResponse().getAccountId());
        base.setName("Base Account");
        base.setDate(new DateTime().toDateTimeISO().toString());

        Account issuing = new Account();
        issuing.setAccountId(issuingAccount.getAccountResponse().getAccountId());
        issuing.setDate(new DateTime().toDateTimeISO().toString());
        issuing.setName("Issuing Account");

        Account distribution = new Account();
        distribution.setAccountId(distributionAccount.getAccountResponse().getAccountId());
        distribution.setDate(new DateTime().toDateTimeISO().toString());
        distribution.setName("Distribution Account");

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
            List< AccountService.AssetBag > assets = accountService.getDefaultAssets(
                    issuingAccount.getAccountResponse().getAccountId());
            // Create trustlines for all asset types
            for (AccountService.AssetBag assetBag : assets) {
                SubmitTransactionResponse createTrustResponse = accountService.createTrustLine(
                        issuingAccount.getAccountResponse().getAccountId(),
                        distributionAccount.getSecretSeed(),
                        limit, assetBag.assetCode);

                LOGGER.info(Emoji.FLOWER_RED + Emoji.FLOWER_RED + "AnchorAccountService: createTrustLine for asset: " + assetBag.assetCode +
                        ".... "+ Emoji.HAPPY+" TrustLine Response isSuccess:  " + createTrustResponse.isSuccess());
            }

            for (AccountService.AssetBag assetBag : assets) {
                LOGGER.info(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD) +
                        "Creating Asset .... ".concat(assetBag.assetCode)
                                .concat(" with assetAmount: ".concat(assetAmount)));
                SubmitTransactionResponse createAssetResponse = accountService.createAsset(
                        issuingAccount.getSecretSeed(),
                        distributionAccount.getAccountResponse().getAccountId(),
                        assetBag.assetCode,assetAmount);

                LOGGER.info(Emoji.HASH.concat(Emoji.HAND2)
                        .concat(" Asset " + assetBag.assetCode + " \uD83C\uDF4E created? "
                                + createAssetResponse.isSuccess())
                .concat(" asset amount: ").concat(assetAmount));
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe(Emoji.NOT_OK + "Trustline/Asset creation failed" + Emoji.ERROR);
            throw e;
        }

        LOGGER.info(Emoji.LEAF + Emoji.LEAF + Emoji.LEAF +
                "Anchor created and will be added to Firestore: " +
                " " + anchor.getName());
        AnchorUser anchorUser = createAnchorUser(anchor, password);
        anchor.setAnchorUser(anchorUser);
        firebaseService.addAnchor(anchor);
        LOGGER.info(G.toJson(anchor));
        //todo - send email to confirm the anchor with link ...
        try {
            sendEmail(anchor);
            LOGGER.info(Emoji.FLOWER_YELLOW + Emoji.FLOWER_YELLOW + "Email has been sent ... ");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("Email sending failed");
        }
        //todo - list accounts and check balances, REMOVE after dev
//        if (status.equalsIgnoreCase("dev")) {
//            AccountResponse response1 = accountService.getAccount(baseAccount.getSecretSeed());
//            AccountResponse response2 = accountService.getAccount(issuingAccount.getSecretSeed());
//            AccountResponse response3 = accountService.getAccount(distributionAccount.getSecretSeed());
//            LOGGER.info("\n\n \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ..... CHECKING ACCOUNTS AFTER ALL THAT .... \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ");
//            LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 BASE ACCOUNT: " + G.toJson(response1));
//            LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 ISSUING ACCOUNT: " + G.toJson(response2));
//            LOGGER.info(" \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 DISTRIBUTION ACCOUNT: " + G.toJson(response3));
//        }
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
        firebaseService.addAnchorUser(anchorUser);

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
