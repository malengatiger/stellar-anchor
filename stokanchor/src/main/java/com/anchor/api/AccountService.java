package com.anchor.api;


import com.anchor.api.data.Account;
import com.anchor.api.data.AccountResponseBag;
import com.anchor.api.data.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

@Service
public class AccountService {
    public static final Logger LOGGER = Logger.getLogger(AccountService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEV_SERVER = "https://horizon-testnet.stellar.org";
    private static final String PROD_SERVER = "https://horizon.stellar.org'";
    private static final String FRIEND_BOT = "https://friendbot.stellar.org/?addr=%s",
            LUMENS = "lumens";
    private static final int TIMEOUT_IN_SECONDS = 180;
    private boolean isDevelopment;
    private Server server;
    private Network network;

    public AccountService() {
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C AccountService Constructor fired ... \uD83C\uDF3C Manage Stellar Accounts");
    }

    @Autowired
    private ApplicationContext context;
    @Value("${status}")
    private String status;

    @Value("${seed}")
    private String seed;

    @Value("${account}")
    private String account;

    @Value("${startingBalance}")
    private String startingBalance;

    public User createUserWithExistingAccount(User user) throws Exception {
        if (user.getAnchorId() == null) {
            throw new Exception("Missing anchorId");
        }
        if (user.getAccounts() == null || user.getAccounts().isEmpty()) {
            throw new Exception("Account is missing");
        }
        FirebaseScaffold scaffold = context.getBean(FirebaseScaffold.class);
        UserRecord record = scaffold.createUser(user.getFullName(), user.getEmail(), "temp#33pass");
        user.setUserId(record.getUid());

        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection("users").add(user);
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C User created and added to Firestore at path: \uD83E\uDD6C " + future.get().getPath());
        LOGGER.info(G.toJson(user));

        return user;
    }

    public User createUser(User user) throws Exception {
        if (user.getAnchorId() == null) {
            throw new Exception("Missing anchorId");
        }
        AccountResponseBag bag = createAndFundStellarAccount(seed, startingBalance);
        Account account = new Account(bag);
        user.addAccount(account);
        FirebaseScaffold scaffold = context.getBean(FirebaseScaffold.class);
        UserRecord record = scaffold.createUser(user.getFullName(), user.getEmail(), "temp#33pass");
        user.setUserId(record.getUid());

        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection("users").add(user);
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C User created and added to Firestore at path: \uD83E\uDD6C " + future.get().getPath());
        LOGGER.info(G.toJson(user));

        return user;
    }

//    public AccountResponseBag createStellarAccount() throws Exception {
//        LOGGER.info("\uD83D\uDC99 ... ... ... createStellarAccount starting .......");
//
//        AccountResponse accountResponse;
//        try {
//            setServerAndNetwork();
//            KeyPair pair = KeyPair.random();
//            String secret = new String(pair.getSecretSeed());
//            LOGGER.info("\uD83D\uDC99 ... new secret seed generated: \uD83D\uDC99 : " + secret
//            + " accountId: " + pair.getAccountId() + "\ngetting NEW account from server IS A PROBLEM ??? ...");
//            if (isDevelopment) {
//                talkToFriendBot(pair.getAccountId());
//            }
//            accountResponse = server.accounts().account(pair.getAccountId());
//            LOGGER.info("\uD83D\uDC99  " +
//                    "Stellar account has been created!: \uD83C\uDF4E \uD83C\uDF4E YEBOOOO!!!");
//
//            AccountResponseBag bag = new AccountResponseBag(accountResponse, secret);
//            LOGGER.info(("\uD83C\uDF4E \uD83C\uDF4E RESPONSE from Stellar; " +
//                    "\uD83D\uDC99 new Account: ").concat(bag.getAccountResponse().getAccountId()));
//            return bag;
//        } catch (Exception e) {
//            LOGGER.warning(" \uD83D\uDD34 Failed to create account -  \uD83D\uDD34 message:"
//            + e.getMessage());
//            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
//        }
//    }

    public void talkToFriendBot(String accountId) throws IOException {
        LOGGER.info("\uD83E\uDD6C ... Begging Ms. FriendBot for some \uD83C\uDF51 pussy \uD83C\uDF51 ... " +
                " \uD83D\uDD34 I heard she gives out!!");
        InputStream response;
        String friendBotUrl = String.format(FRIEND_BOT, accountId);
        response = new URL(friendBotUrl).openStream();
        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        LOGGER.info("\uD83E\uDD6C " +
                "FriendBot responded with largess: \uD83E\uDD6C 10,000 Lumens obtained. ... Yebo, Gogo!! \uD83E\uDD6C ");
        setServerAndNetwork();
        if (isDevelopment) {
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 Booty from Ms. FriendBot: \uD83C\uDF51 " + body);
        }
    }

    public AccountResponse getAccount(String seed) throws IOException {
        LOGGER.info("\uD83E\uDD6C getting account");
        setServerAndNetwork();
        KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
        return sourceAccount;
    }

    public AccountResponseBag createAndFundStellarAccount(String seed, String startingBalance) throws Exception {
        LOGGER.info("\uD83D\uDC99 ... ... ... ... createAndFundStellarAccount starting ....... startingBalance: " + startingBalance);
        setServerAndNetwork();
        AccountResponse accountResponse;
        try {
            KeyPair newAccountKeyPair = KeyPair.random();
            KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
            String secret = new String(newAccountKeyPair.getSecretSeed());
            AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(new CreateAccountOperation.Builder(
                            newAccountKeyPair.getAccountId(), startingBalance).build())
                    .addMemo(Memo.text("CreateAccount Tx"))
                    .setTimeout(180)
                    .setOperationFee(100)
                    .build();

            transaction.sign(sourceKeyPair);

            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                accountResponse = server.accounts().account(newAccountKeyPair.getAccountId());
                LOGGER.info("\uD83D\uDC99  " +
                        "Stellar account has been created and funded!: \uD83C\uDF4E \uD83C\uDF4E YEBO!!!");
                AccountResponseBag bag = new AccountResponseBag(accountResponse, secret);
                LOGGER.info(("\uD83C\uDF4E \uD83C\uDF4E RESPONSE from Stellar; " +
                        "\uD83D\uDC99 new accountId: ").concat(bag.getAccountResponse().getAccountId()));
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Account created, check the funded balance, " +
                        "\uD83D\uDC99 should be: " + startingBalance + " " + G.toJson(bag));
                return bag;
            } else {
                LOGGER.warning("CreateAccountOperation ERROR: \uD83C\uDF45 resultXdr: "
                        + submitTransactionResponse.getResultXdr().get());

                throw new Exception("CreateAccountOperation transactionResponse is NOT success");
            }


        } catch (IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }

    public SubmitTransactionResponse issueAsset(String issuingAccount, String distributionSeed, String limit, String assetCode) throws Exception {
        LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 .......... issueAsset ........ \uD83C\uDF40 " +
                " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 limit: " + limit
                + " issuingAccount: " + issuingAccount);
        try {
            KeyPair distKeyPair = KeyPair.fromSecretSeed(distributionSeed);
            Asset asset = Asset.createNonNativeAsset(assetCode, issuingAccount);
            Asset dollar = Asset.createNonNativeAsset("USD", issuingAccount);
            Asset euro = Asset.createNonNativeAsset("EUR", issuingAccount);
            Asset pound = Asset.createNonNativeAsset("GBP", issuingAccount);
            Asset yuan = Asset.createNonNativeAsset("CNY", issuingAccount);
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51 5 Assets created, 4 default and 1 custom asset: " + asset.getType());

            setServerAndNetwork();
            AccountResponse distributionAccountResponse = server.accounts().account(distKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Distribution account: " + distributionAccountResponse.getAccountId() + " \uD83C\uDF51 ... add trust line");
            Transaction transaction = new Transaction.Builder(distributionAccountResponse, network)
                    .addOperation(new ChangeTrustOperation.Builder(asset, limit)
                            .build())
                    .addOperation(new ChangeTrustOperation.Builder(dollar, limit)
                            .build())
                    .addOperation(new ChangeTrustOperation.Builder(euro, limit)
                            .build())
                    .addOperation(new ChangeTrustOperation.Builder(pound, limit)
                            .build())
                    .addOperation(new ChangeTrustOperation.Builder(yuan, limit)
                            .build())
                    .addMemo(Memo.text("Issue Fiat Tokens"))
                    .setOperationFee(100)
                    .setTimeout(360)
                    .build();

            transaction.sign(distKeyPair);
            LOGGER.info("\uD83C\uDF40 Transaction has been signed by distribution KeyPair... \uD83C\uDF51 on to submission ... ");

            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  " +
                        "Stellar issueAsset: ChangeTrustOperation has been executed OK: \uD83C\uDF4E \uD83C\uDF4E isSuccess: " + submitTransactionResponse.isSuccess());
                //todo - remove check ...
                AccountResponse finalResp = server.accounts().account(distKeyPair.getAccountId());
                try {
                    LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 Distribution account after trust operation, " +
                            "\uD83C\uDF4E check assets and balances \uD83C\uDF4E " + G.toJson(finalResp) + " \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ");
                } catch (Exception e) {
                    //ignore
                }

            } else {
                LOGGER.warning("ChangeTrustOperation ERROR: \uD83C\uDF45 resultXdr: " + submitTransactionResponse.getResultXdr().get());
                throw new Exception("ChangeTrustOperation transactionResponse is \uD83C\uDF45 NOT success \uD83C\uDF45");
            }
            return submitTransactionResponse;
        } catch (Exception e) {
            throw new Exception("ChangeTrustOperation failed", e);
        }
    }

    public SubmitTransactionResponse createAsset(String issuingAccountSeed, String distributionAccount,
                                                 String assetCode, String amount) throws Exception {
        LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51  .......... createAsset ........ \uD83C\uDF40 " +
                " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 " + " amount:" + amount
                + "\n \uD83C\uDF51 issuingAccountSeed: " + issuingAccountSeed + " \uD83C\uDF51 distributionAccount: "
                + distributionAccount);
        try {
            setServerAndNetwork();
            KeyPair issuingKeyPair = KeyPair.fromSecretSeed(issuingAccountSeed);
            Asset asset = Asset.createNonNativeAsset(assetCode, issuingKeyPair.getAccountId());
            Asset dollar = Asset.createNonNativeAsset("USD", issuingKeyPair.getAccountId());
            Asset euro = Asset.createNonNativeAsset("EUR", issuingKeyPair.getAccountId());
            Asset pound = Asset.createNonNativeAsset("GBP", issuingKeyPair.getAccountId());
            Asset yuan = Asset.createNonNativeAsset("CNY", issuingKeyPair.getAccountId());

            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51 5 Assets created, 4 default and 1 custom asset: " + asset.getType());

            AccountResponse issuingAccount = server.accounts().account(issuingKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Issuing account: " + issuingAccount.getAccountId()
                    + " \uD83C\uDF51 ... add payment operation starting ...");

            Transaction transaction = new Transaction.Builder(issuingAccount, network)
                    .addOperation(new PaymentOperation.Builder(
                            distributionAccount, asset, amount)
                            .setSourceAccount(issuingKeyPair.getAccountId())
                            .build())
                    .addOperation(new PaymentOperation.Builder(
                            distributionAccount, dollar, amount)
                            .setSourceAccount(issuingKeyPair.getAccountId())
                            .build())
                    .addOperation(new PaymentOperation.Builder(
                            distributionAccount, euro, amount)
                            .setSourceAccount(issuingKeyPair.getAccountId())
                            .build())
                    .addOperation(new PaymentOperation.Builder(
                            distributionAccount, pound, amount)
                            .setSourceAccount(issuingKeyPair.getAccountId())
                            .build())
                    .addOperation(new PaymentOperation.Builder(
                            distributionAccount, yuan, amount)
                            .setSourceAccount(issuingKeyPair.getAccountId())
                            .build())
                    .addMemo(Memo.text("Create Fiat Tokens"))
                    .setOperationFee(100)
                    .setTimeout(360)
                    .build();

            transaction.sign(issuingKeyPair);
            LOGGER.info("\uD83C\uDF40 Transaction has been signed by issuing KeyPair ... \uD83C\uDF51 on to submission ... ");

            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  " +
                        "Stellar createAsset: PaymentOperation has been executed OK: \uD83C\uDF4E \uD83C\uDF4E isSuccess: " + submitTransactionResponse.isSuccess());
                //todo - remove check ...
                AccountResponse finalResp = server.accounts().account(distributionAccount);
                try {
                    LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 Distribution account after Payment operation, " +
                            "\uD83C\uDF4E check assets and balances \uD83C\uDF4E " + G.toJson(finalResp) + " \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ");
                } catch (Exception e) {
                    //ignore
                }

            } else {
                LOGGER.info("ERROR: \uD83C\uDF45 resultXdr: " + submitTransactionResponse.getResultXdr().get());
                throw new Exception("PaymentOperation transactionResponse is \uD83C\uDF45 NOT success \uD83C\uDF45");
            }
            return submitTransactionResponse;
        } catch (Exception e) {
            throw new Exception("PaymentOperation failed", e);
        }
    }

    private void setServerAndNetwork() {
        isDevelopment = status.equalsIgnoreCase("dev");
        if (isDevelopment) {
            server = new Server(DEV_SERVER);
            network = Network.TESTNET;
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F DEVELOPMENT: ... Stellar TestNet Server and Network ... \uD83C\uDF4F \uD83C\uDF4F \n");

        } else {
            server = new Server(PROD_SERVER);
            network = Network.PUBLIC;
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F PRODUCTION: ... Stellar Public Server and Network... \uD83C\uDF4F \uD83C\uDF4F \n");

        }

    }
}
