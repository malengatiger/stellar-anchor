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
import org.stellar.sdk.responses.*;
import org.stellar.sdk.xdr.TransactionEnvelope;
import org.stellar.sdk.xdr.XdrDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
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
        AccountResponseBag bag = createStellarAccount();
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

    public AccountResponseBag createStellarAccount() throws Exception {
        this.isDevelopment = status.equalsIgnoreCase("dev");
        setServerAndNetwork();
        AccountResponse accountResponse;
        try {

            KeyPair pair = KeyPair.random();
            String secret = new String(pair.getSecretSeed());
            if (isDevelopment) {
                talkToFriendBot(pair.getAccountId());
                server = new Server(DEV_SERVER);
            } else {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 ...... looks like we are in PRODUCTION ..." +
                        "Toto, we are not in Kansas anymore ... ");
                server = new Server(PROD_SERVER);
            }
            accountResponse = server.accounts().account(pair.getAccountId());
            LOGGER.info("\uD83D\uDC99  " +
                    "Stellar account has been created Kool!: \uD83C\uDF4E \uD83C\uDF4E YEBOOOO!!!");
            AccountResponseBag bag = new AccountResponseBag(accountResponse, secret);
            LOGGER.info(("\uD83C\uDF4E \uD83C\uDF4E RESPONSE BAG from Stellar; " +
                    "\uD83D\uDC99 new accountId: ").concat(bag.getAccountResponse().getAccountId()));
            return bag;
        } catch (IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }

    private void talkToFriendBot(String accountId) throws IOException {
        InputStream response;
        String friendBotUrl = String.format(FRIEND_BOT, accountId);
        response = new URL(friendBotUrl).openStream();
        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        LOGGER.info("\uD83E\uDD6C " +
                "FriendBot responded with largess: \uD83E\uDD6C 10,000 Lumens. ... Yebo, Gogo!! \uD83E\uDD6C " + body);
    }

    public AccountResponseBag createAndFundStellarAccount(String seed, String startingBalance) throws Exception {
        this.isDevelopment = status.equalsIgnoreCase("dev");
        setServerAndNetwork();
        AccountResponse accountResponse;
        try {
            KeyPair newAccountKeyPair = KeyPair.random();
            KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
            String secret = new String(newAccountKeyPair.getSecretSeed());
            if (isDevelopment) {
                server = new Server(DEV_SERVER);
            } else {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 ...... looks like we are in PRODUCTION ..." +
                        "Toto, we are not in Kansas anymore ... ");
                server = new Server(PROD_SERVER);
            }
            AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(new CreateAccountOperation.Builder(newAccountKeyPair.getAccountId(), startingBalance).build())
                    .addMemo(Memo.text("CreateAccount Tx"))
                    .setTimeout(180)
                    .setOperationFee(100)
                    .build();

            transaction.sign(sourceKeyPair);

            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                accountResponse = server.accounts().account(newAccountKeyPair.getAccountId());
                LOGGER.info("\uD83D\uDC99  " +
                        "Stellar account has been created and funded!: \uD83C\uDF4E \uD83C\uDF4E YEBOOOO!!!");
                AccountResponseBag bag = new AccountResponseBag(accountResponse, secret);
                LOGGER.info(("\uD83C\uDF4E \uD83C\uDF4E RESPONSE BAG from Stellar; " +
                        "\uD83D\uDC99 new accountId: ").concat(bag.getAccountResponse().getAccountId()));
                return bag;
            } else {
                throw new Exception("CreateAccount transactionResponse is NOT success");
            }


        } catch (IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }

    public SubmitTransactionResponse issueAsset(String sourceAccount, String distributionSeed, String limit, String assetType, String assetCode) throws Exception {
        LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 .......... issueAsset ........ \uD83C\uDF40 " +
                "type: " + assetType + " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 limit: " + limit
        + " sourceAccount: " + sourceAccount);
        try {
            isDevelopment = status.equalsIgnoreCase("dev");
            KeyPair distKeyPair = KeyPair.fromSecretSeed(distributionSeed);
            Asset asset = Asset.create(assetType,assetCode, sourceAccount);
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51 Asset created: " + asset.getType());
            if (isDevelopment) {
                server = new Server(DEV_SERVER);
            } else {
                server = new Server(PROD_SERVER);
            }
            AccountResponse distributionAccountResponse = server.accounts().account(distKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Distribution account: " + distributionAccountResponse.getAccountId() + " \uD83C\uDF51 ... add trust line");
            Transaction transaction = new Transaction.Builder(distributionAccountResponse, network)
                    .addOperation(new ChangeTrustOperation.Builder(asset, limit )
                            .build())
                    .addMemo(Memo.text("CreateToken Tx"))
                    .setOperationFee(100)
                    .setTimeout(180)
                    .build();

            transaction.sign(distKeyPair);
            LOGGER.info("\uD83C\uDF40 Transaction has been signed by BOTH KeyPairs ... \uD83C\uDF51 on to submission ... ");
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
                LOGGER.info("ERROR: \uD83C\uDF45 resultXdr: " + submitTransactionResponse.getResultXdr().get());
                byte[] bytes = Base64.getDecoder().decode(submitTransactionResponse.getResultXdr().get());
                XdrDataInputStream in = new XdrDataInputStream(new ByteArrayInputStream(bytes));
                String envelope = TransactionEnvelope.decode(in).toString();
                LOGGER.info("Printing xdr?: \uD83C\uDF45 " + envelope + " \uD83C\uDF45");
                throw new Exception("ChangeTrustOperation transactionResponse is \uD83C\uDF45 NOT success \uD83C\uDF45");
            }
            return submitTransactionResponse;
        } catch (Exception e) {
            throw new Exception("ChangeTrustOperation failed", e);
        }
    }

    public SubmitTransactionResponse createAsset(String issuingAccountSeed, String distributionAccount,
                                                 String assetType, String assetCode, String amount) throws Exception {
        LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 .......... createAsset ........ \uD83C\uDF40 " +
                "type: " + assetType + " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 "
                + " issuingAccountSeed: " + issuingAccountSeed);
        try {
            isDevelopment = status.equalsIgnoreCase("dev");
            KeyPair issuingKeyPair = KeyPair.fromSecretSeed(issuingAccountSeed);
            Asset asset = Asset.create(assetType,assetCode, issuingKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51 Asset created: " + asset.getType());
            if (isDevelopment) {
                server = new Server(DEV_SERVER);
            } else {
                server = new Server(PROD_SERVER);
            }
            AccountResponse issuingAccountResponse = server.accounts().account(issuingKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Issuing account: " + issuingAccountResponse.getAccountId() + " \uD83C\uDF51 ... add payment operation");
            Transaction transaction = new Transaction.Builder(issuingAccountResponse, network)
                    .addOperation(new PaymentOperation.Builder(distributionAccount,asset,amount).build())
                    .addMemo(Memo.text("Payment Tx"))
                    .setOperationFee(100)
                    .setTimeout(180)
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

        if (isDevelopment) {
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Setting up Stellar Testnet Server ...");
            server = new Server(DEV_SERVER);
            network = Network.TESTNET;
        } else {
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Setting up Stellar Public Server ...");
            server = new Server(PROD_SERVER);
            network = Network.PUBLIC;
        }

    }
}
