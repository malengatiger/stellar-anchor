package com.anchor.api.services;


import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.transfer.sep10.AnchorSep10Challenge;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.RootResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import shadow.com.google.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AccountService {
    public static final Logger LOGGER = Logger.getLogger(AccountService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final String FRIEND_BOT = "https://friendbot.stellar.org/?addr=%s",
            LUMENS = "lumens";
    private static final int TIMEOUT_IN_SECONDS = 180;
    private boolean isDevelopment;
    private Server server;
    private Network network;

    public AccountService() {
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C AccountService Constructor fired ... \uD83C\uDF3C Manage Stellar Accounts");
    }

    public void printStellarHorizonServer() {
        setServerAndNetwork();
        try {
            RootResponse serverResponse = server.root();
            LOGGER.info("\uD83E\uDD8B \uD83E\uDD8B \uD83C\uDF3C HorizonVersion: ".concat(serverResponse.getHorizonVersion()
                    .concat(" \uD83E\uDD8B NetworkPassphrase: ").concat(serverResponse.getNetworkPassphrase()
                            .concat(" \uD83E\uDD8B StellarCoreVersion: ").concat(serverResponse.getStellarCoreVersion()
                                    .concat(" \uD83E\uDD8B CurrentProtocolVersion: ").concat("" + serverResponse.getCurrentProtocolVersion())))));
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C \uD83C\uDF3C Connected to Stellar Horizon Server \uD83E\uDD8B \uD83E\uDD8B ".concat(G.toJson(serverResponse)
                    .concat(" \uD83C\uDF3C \uD83C\uDF3C ")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private FirebaseService firebaseService;

    @Value("${status}")
    private String status;


    @Value("${defaultCurrencies}")
    private String defaultCurrencies;

    @Value("${stellarUrl}")
    private String stellarUrl;

    @Value("${domain}")
    private String domain;

    public void talkToFriendBot(String accountId) throws IOException {
        LOGGER.info("\uD83E\uDD6C ... Begging Ms. FriendBot for some \uD83C\uDF51 pussy \uD83C\uDF51 ... " +
                " \uD83D\uDD34 I heard she gives out!!");
        isDevelopment = status.equalsIgnoreCase("dev");
        setServerAndNetwork();
        InputStream response;
        String friendBotUrl = String.format(FRIEND_BOT, accountId);
        response = new URL(friendBotUrl).openStream();
        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        LOGGER.info("\uD83E\uDD6C " +
                "FriendBot responded with largess: \uD83E\uDD6C 10,000 Lumens obtained. ... Yebo, Gogo!! \uD83E\uDD6C ");

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
                            newAccountKeyPair.getAccountId(), startingBalance)
                            .build())
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
                LOGGER.warning(Emoji.NOT_OK + "CreateAccountOperation ERROR: \uD83C\uDF45 resultXdr: "
                        + submitTransactionResponse.getResultXdr().get());
                if (submitTransactionResponse.getResultXdr().get()
                        .equalsIgnoreCase("AAAAAAAAAGT/////AAAAAQAAAAAAAAAA/////gAAAAA=")) {
                    throw new AccountUnderfundedException(sourceAccount.getAccountId()
                    .concat(" is UNDER FUNDED"));
                }

                throw new Exception("CreateAccountOperation transactionResponse is NOT success");
            }


        } catch (IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }
    static class AccountUnderfundedException extends Exception {
        public AccountUnderfundedException(String message) {
            super(message);
        }
    }

    @Value("${anchorName}")
    private String anchorName;
    private Anchor anchor;
    @Autowired
    private CryptoService cryptoService;

    public SubmitTransactionResponse fundAgentAccount(
                                                      String accountToFund,
                                                      String amount,
                                                      String assetCode,
                                                      String agentId,
                                                      String limit) throws Exception {
        LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 .......... fundAgentAccount ........ \uD83C\uDF40 " +
                " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 amount: " + amount
                + " accountToFund: " + accountToFund);
        if (anchor == null) {
            anchor = firebaseService.getAnchorByName(anchorName);
        }
        Agent agent = firebaseService.getAgent(agentId);
        cryptoService.downloadSeedFile(anchor.getIssuingAccount().getAccountId());
        byte[] bytes = cryptoService.readFile(anchor.getIssuingAccount().getAccountId());
        String issuingSeed = cryptoService.decrypt(bytes);
        KeyPair keyPair = KeyPair.fromSecretSeed(issuingSeed);

        List<AssetBag> assets = getDefaultAssets(keyPair.getAccountId(), assetCode);
        AccountResponse issuingAcct = server.accounts().account(keyPair.getAccountId());
        Transaction.Builder transactionBuilder = new Transaction.Builder(issuingAcct, network);
        Asset asset = null;
        for (AssetBag mAsset : assets) {
            if (mAsset.toString().equalsIgnoreCase(assetCode)) {
                asset = mAsset.asset;
            }
        }
        if (asset == null) {
            throw new Exception(Emoji.NOT_OK + "Asset ".concat(assetCode)
                    .concat(" not found ").concat(Emoji.NOT_OK));
        }
        transactionBuilder.addOperation(new ChangeTrustOperation.Builder(
                asset, limit)
                .build());


        transactionBuilder.addMemo(Memo.text("Agent Fiat Token"));
        transactionBuilder.setOperationFee(100);
        transactionBuilder.setTimeout(360);
        Transaction transaction = transactionBuilder.build();

        transaction.sign(keyPair);
        LOGGER.info("\uD83C\uDF40 GetTransactionsResponse has been signed by issuing KeyPair... \uD83C\uDF51 on to submission ... ");

        SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
        if (submitTransactionResponse.isSuccess()) {
            LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  " +
                    "Stellar issueAsset: ChangeTrustOperation has been executed OK: \uD83C\uDF4E \uD83C\uDF4E isSuccess: " + submitTransactionResponse.isSuccess());
            //todo - remove check ...
            AccountResponse finalResp = server.accounts().account(keyPair.getAccountId());
            try {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 Distribution account after trust operations, " +
                        "\uD83C\uDF4E check assets and balances \uD83C\uDF4E " + G.toJson(finalResp) + " \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ");
            } catch (Exception e) {
                //ignore
            }

        } else {
            LOGGER.warning("ChangeTrustOperation ERROR: \uD83C\uDF45 resultXdr: " + submitTransactionResponse.getResultXdr().get());
            throw new Exception("ChangeTrustOperation transactionResponse is \uD83C\uDF45 NOT success \uD83C\uDF45");
        }
        return submitTransactionResponse;
    }
/*
    🍊 🍊 🍊
    Anchors: issuing assets
    Any account can issue assets on the Stellar network. Entities that issue assets are called anchors.
    Anchors can be run by individuals, small businesses, local communities, nonprofits, organizations, etc.
    Any type of financial institution–a bank, a payment processor–can be an anchor.

    🍎 Each anchor has an issuing account from which it issues the asset.
 */
    public SubmitTransactionResponse createTrustLines(
            String issuingAccount, String distributionSeed, String limit, String assetCode) throws Exception {
        LOGGER.info("\uD83C\uDF40 \uD83C\uDF40 .......... createTrustLines ........ \uD83C\uDF40 " +
                " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 limit: " + limit
                + " issuingAccount: " + issuingAccount);
        try {
            KeyPair distKeyPair = KeyPair.fromSecretSeed(distributionSeed);
            AssetBag assetBag = new AssetBag(assetCode,Asset.createNonNativeAsset(assetCode, issuingAccount));
            List<AssetBag> assets = getDefaultAssets(issuingAccount, assetCode);
            assets.add(assetBag);
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51 " + assets.size()
                    + " Fiat Currency Assets created,  1 custom asset: " + assetBag.assetCode);

            server.accounts().forAsset((AssetTypeCreditAlphaNum) assetBag.asset)
                    .stream(new EventListener<AccountResponse>() {
                @Override
                public void onEvent(AccountResponse object) {
                    LOGGER.info("onEvent ... accountId".concat(object.getAccountId()));
                }

                @Override
                public void onFailure(Optional<Throwable> optional, Optional<Integer> optional1) {

                }
            });
            setServerAndNetwork();
            AccountResponse distributionAccountResponse = server.accounts().account(distKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Distribution account: " + distributionAccountResponse.getAccountId()
                    + " \uD83C\uDF51 ... add trust lines ...");

            Transaction.Builder transactionBuilder = new Transaction.Builder(distributionAccountResponse, network);
            LOGGER.info(Emoji.STAR.concat(Emoji.STAR) + "Creating ChangeTrustOperation for "
            .concat(" " + assets.size()).concat(" assets ".concat(Emoji.RED_TRIANGLE)));
            for (AssetBag mAsset : assets) {
                transactionBuilder.addOperation(new ChangeTrustOperation.Builder(
                        mAsset.asset, limit)
                        .build());

            }

            transactionBuilder.addMemo(Memo.text("Create Trust Lines"));
            transactionBuilder.setOperationFee(100);
            transactionBuilder.setTimeout(360);
            Transaction transaction = transactionBuilder.build();

            transaction.sign(distKeyPair);
            LOGGER.info("\uD83C\uDF40 GetTransactionsResponse has been signed by distribution KeyPair... \uD83C\uDF51 on to submission ... ");

            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  " +
                        "Stellar issueAsset: ChangeTrustOperation has been executed OK: \uD83C\uDF4E \uD83C\uDF4E isSuccess: " + submitTransactionResponse.isSuccess());
                //todo - remove check ...
                AccountResponse finalResp = server.accounts().account(distKeyPair.getAccountId());
                try {
                    LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 Distribution account after trust operations, " +
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

    public List<AssetBag> getDefaultAssets(String issuingAccount, String customAsset) {
        List<AssetBag> mList = new ArrayList<>();
        boolean addCurrencies = defaultCurrencies.equalsIgnoreCase("true");
        if (!addCurrencies) {
            return mList;
        }

        if (!customAsset.equalsIgnoreCase("USD")) {
            mList.add(new AssetBag("USD", Asset.createNonNativeAsset("USD", issuingAccount)));
        }
        if (!customAsset.equalsIgnoreCase("GBP")) {
            mList.add(new AssetBag("GBP",Asset.createNonNativeAsset("GBP", issuingAccount)));
        }
        if (!customAsset.equalsIgnoreCase("CNY")) {
            mList.add(new AssetBag("CNY",Asset.createNonNativeAsset("CNY", issuingAccount)));
        }
        if (!customAsset.equalsIgnoreCase("CHF")) {
            mList.add(new AssetBag("CHF", Asset.createNonNativeAsset("CHF", issuingAccount)));
        }
        if (!customAsset.equalsIgnoreCase("EUR")) {
            mList.add(new AssetBag("EUR",Asset.createNonNativeAsset("EUR", issuingAccount)));
        }

        return mList;
    }

    static class AssetBag {
        String assetCode;
        Asset asset;

        public AssetBag(String assetCode, Asset asset) {
            this.assetCode = assetCode;
            this.asset = asset;
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
            AssetBag asset = new AssetBag(assetCode,Asset.createNonNativeAsset(assetCode, issuingKeyPair.getAccountId()));
            List<AssetBag> assets = getDefaultAssets(issuingKeyPair.getAccountId(), assetCode);
            assets.add(asset);
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51 " + assets.size()
                    + " Fiat Currency Assets created,  1 custom asset: " + asset.assetCode);

            AccountResponse issuingAccount = server.accounts().account(issuingKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Issuing account: " + issuingAccount.getAccountId()
                    + " \uD83C\uDF51 ... create transaction with multiple payment operations ... starting ...");

            Transaction.Builder trBuilder = new Transaction.Builder(issuingAccount, network);
            for (AssetBag mAsset : assets) {
                trBuilder.addOperation(new PaymentOperation.Builder(
                        distributionAccount, mAsset.asset, amount)
                        .setSourceAccount(issuingKeyPair.getAccountId())
                        .build());
            }
            trBuilder.addMemo(Memo.text("Create Fiat Tokens"));
            trBuilder.setOperationFee(100);
            trBuilder.setTimeout(360);
            Transaction transaction = trBuilder.build();

            transaction.sign(issuingKeyPair);
            LOGGER.info("\uD83C\uDF40 GetTransactionsResponse has been signed by issuing KeyPair ... \uD83C\uDF51 on to submission ... ");

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

    public void listenForTransactions() throws Exception {
        setServerAndNetwork();
        //todo - what do we want to listen for ???
        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 ... Listen For Stellar Accounts ...");
        server.accounts().stream(new EventListener<AccountResponse>() {
            @Override
            public void onEvent(AccountResponse accountResponse) {
                LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 Accounts Listener fired, " +
                        "accountResponse received, add to Firestore ...");
                try {
                    firebaseService.addAccountResponse(accountResponse);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "AccountListener failed", e);
                }
            }

            @Override
            public void onFailure(Optional<Throwable> optional, Optional<Integer> optional1) {
                try {
                    LOGGER.info("\uD83C\uDF45 accountListener onFailure: " + optional.get().getMessage());
                    LOGGER.severe("AccountListener failed");
                } catch (Exception e) {
                    //ignore
                }
            }
        });

//        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 ... Listen For Stellar Payments ...");
//        SSEStream<OperationResponse> mm = server.payments().stream(new EventListener<OperationResponse>() {
//            @Override
//            public void onEvent(OperationResponse operationResponse) {
//                LOGGER.info("isTransactionSuccessful: ".concat(operationResponse.isTransactionSuccessful().toString()
//                .concat(" SourceAccount: ").concat(operationResponse.getSourceAccount())));
//                try {
//                    firebaseService.addOperationResponse(operationResponse);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Optional<Throwable> optional, Optional<Integer> optional1) {
//                try {
//                    LOGGER.info("\uD83C\uDF45 operationResponse onFailure: " + optional.get().getMessage());
//                    LOGGER.severe("operationResponse failed");
//                } catch (Exception e) {
//                    //ignore
//                }
//            }
//        });
//        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 ... Listen For Stellar Transactions ...");
//        server.transactions().stream(new EventListener<TransactionResponse>() {
//            @Override
//            public void onEvent(TransactionResponse transactionResponse) {
//                LOGGER.info("\uD83C\uDF4E  transactionListener:onEvent:TransactionResponse: isSuccessful: "
//                        + transactionResponse.isSuccessful()
//                        + " \uD83D\uDC99 source account: " + transactionResponse.getSourceAccount());
//                try {
//                    firebaseService.addTransactionResponse(transactionResponse);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Optional<Throwable> optional, Optional<Integer> optional1) {
//                try {
//                    LOGGER.info("\uD83C\uDF45 transactionListener onFailure: " + optional.get().getMessage());
//                    LOGGER.severe("transactionListener failed");
//                } catch (Exception e) {
//                    //ignore
//                }
//            }
//        });

    }

    public SubmitTransactionResponse setOptions(final String seed, int clearFlags, int highThreshold, int lowThreshold,
                                                String inflationDestination, int masterKeyWeight) throws Exception {

        setServerAndNetwork();
        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        SetOptionsOperation operation = new SetOptionsOperation.Builder()
                .setClearFlags(clearFlags)
                .setHighThreshold(highThreshold)
                .setLowThreshold(lowThreshold)
                .setInflationDestination(inflationDestination)
                .setSourceAccount(keyPair.getAccountId())
                .setMasterKeyWeight(masterKeyWeight)
                .setHomeDomain(domain)
                .build();

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("setOptions: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            LOGGER.info(response.isSuccess() ? "setOptions transaction is SUCCESSFUL" : "setOptions transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to setOptions: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    @Autowired
    AnchorSep10Challenge anchorSep10Challenge;

    public String handleChallenge(final String seed) throws Exception {

        setServerAndNetwork();
        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());


        return null;

    }

    private void setServerAndNetwork() {
        if (status == null) {
            LOGGER.info("\uD83D\uDE08 \uD83D\uDC7F Set status to dev because status is NULL");
            status = "dev";
        }
        isDevelopment = status.equalsIgnoreCase("dev");
        server = new Server(stellarUrl);
        if (isDevelopment) {
            network = Network.TESTNET;
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F DEVELOPMENT: ... Stellar TestNet Server and Network ... \uD83C\uDF4F \uD83C\uDF4F \n");

        } else {
            network = Network.PUBLIC;
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F PRODUCTION: ... Stellar Public Server and Network... \uD83C\uDF4F \uD83C\uDF4F \n");

        }
    }

    public Server getServer() {
        setServerAndNetwork();
        return server;
    }
}
