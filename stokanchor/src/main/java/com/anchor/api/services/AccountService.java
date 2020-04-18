package com.anchor.api.services;


import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.transfer.sep10.AnchorSep10Challenge;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
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

import java.io.*;
import java.net.URL;
import java.util.*;
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
        LOGGER.info(Emoji.HEART_ORANGE.concat(Emoji.HEART_GREEN.concat(Emoji.HEART_BLUE))
                +"Account Retrieved: ".concat(G.toJson(sourceAccount)));
        return sourceAccount;
    }

    public AccountResponseBag createAndFundAnchorAccount(String seed, String startingBalance) throws Exception {
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

    public AccountResponseBag createAndFundUserAccount(String startingXLMBalance,
                                                       String startingFiatBalance, String fiatLimit) throws Exception {
        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR) + "\uD83D\uDC99 ... ... ... ... createAndFundAgentAccount starting " +
                "....... startingXLMBalance: " + startingXLMBalance + " startingFiatBalance:" + startingFiatBalance
         + " fiatLimit: " + fiatLimit);
        setServerAndNetwork();
        if (anchor == null) {
            anchor = firebaseService.getAnchorByName(anchorName);
        }

        try {
            String baseSeed = cryptoService.getDecryptedSeed(anchor.getBaseAccount().getAccountId());
            KeyPair agentKeyPair = KeyPair.random();
            KeyPair sourceKeyPair = KeyPair.fromSecretSeed(baseSeed);

            AccountResponse baseAccount = server.accounts().account(sourceKeyPair.getAccountId());

            String distributionSeed = cryptoService.getDecryptedSeed(anchor.getDistributionAccount().getAccountId());
            KeyPair distributionKeyPair = KeyPair.fromSecretSeed(distributionSeed);

            Transaction transaction = new Transaction.Builder(baseAccount, network)
                    .addOperation(new CreateAccountOperation.Builder(
                            agentKeyPair.getAccountId(), startingXLMBalance)
                            .build())
                    .addMemo(Memo.text("CreateAccount Tx"))
                    .setTimeout(180)
                    .setOperationFee(100)
                    .build();

            transaction.sign(sourceKeyPair);
            LOGGER.info(Emoji.RED_CAR + "Submit tx with CreateAccountOperation ");
            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                //add trustlines and first payment for all fiat tokens
                LOGGER.info(Emoji.LEAF + "Stellar account created: "
                        .concat(Emoji.LEAF).concat(" ").concat(agentKeyPair.getAccountId())
                .concat(" ... about to start creating trustlines"));
                AccountResponseBag agentAccountResponseBag = addTrustlinesAndOriginalBalances(fiatLimit,
                        startingFiatBalance, agentKeyPair, distributionKeyPair);
                LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF.concat(Emoji.LEAF)) +
                        "It is indeed possible that everything worked. WTF?");
                String secret = new String(agentKeyPair.getSecretSeed());
                agentAccountResponseBag.setSecretSeed(secret);
                return agentAccountResponseBag;
            } else {
                LOGGER.warning(Emoji.NOT_OK + "CreateAccountOperation ERROR: \uD83C\uDF45 resultXdr: "
                        + submitTransactionResponse.getResultXdr().get());
                if (submitTransactionResponse.getResultXdr().get()
                        .equalsIgnoreCase("AAAAAAAAAGT/////AAAAAQAAAAAAAAAA/////gAAAAA=")) {
                    throw new AccountUnderfundedException(baseAccount.getAccountId()
                            .concat(" is UNDER FUNDED"));
                }

                throw new Exception("CreateAccountOperation transactionResponse is NOT success");
            }


        } catch (IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }

    private AccountResponseBag addTrustlinesAndOriginalBalances(String limit, String startingFiatBalance,
                                                                KeyPair userKeyPair,
                                                                KeyPair distributionKeyPair) throws Exception {

        List<AssetBag> assetBags = getDefaultAssets(anchor.getIssuingAccount().getAccountId());
        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR).concat(("addTrustlinesAndOriginalBalances: " +
                "Building transaction with trustline operations ... FIAT ASSETS: " + assetBags.size())
                .concat(Emoji.RED_DOT)));
        AccountResponse account = server.accounts().account(userKeyPair.getAccountId());
        Transaction.Builder trustlineTxBuilder = new Transaction.Builder(account, network);
        for (AssetBag assetBag : assetBags) {
            trustlineTxBuilder.addOperation(new ChangeTrustOperation.Builder(
                    assetBag.asset,limit).build());
        }
        Transaction userTrustlineTx = trustlineTxBuilder.addMemo(Memo.text("User Trustline Tx"))
                .setTimeout(180)
                .setOperationFee(100)
                .build();

        userTrustlineTx.sign(userKeyPair);
        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR).concat(("addTrustlinesAndOriginalBalances: " +
                "Submitting transaction with trustline operations ... ")
                .concat(Emoji.RED_DOT)));
        SubmitTransactionResponse trustlineTransactionResponse = server.submitTransaction(userTrustlineTx);
        LOGGER.info(Emoji.HAND1.concat(Emoji.HAND2.concat(Emoji.HAND3)) +
               "User Trustline transaction response; isSuccess: ".concat("" + trustlineTransactionResponse.isSuccess()));
        if (trustlineTransactionResponse.isSuccess()) {
            return sendFiatPayments(startingFiatBalance, userKeyPair, distributionKeyPair, assetBags);
        } else {
            String xdr = trustlineTransactionResponse.getResultXdr().get();
            String msg = Emoji.NOT_OK.concat(Emoji.NOT_OK.concat(Emoji.ERROR)
                    .concat("Trustline Transaction Failed: xdr: ".concat(xdr)));
            if (xdr.contains(TX_BadAuth)) {
                msg = Emoji.NOT_OK.concat(Emoji.NOT_OK.concat(Emoji.ERROR)
                        .concat("Bad Auth for Trustline Transaction Response; xdr: ".concat(xdr)));
            }
            if (xdr.contains(TX_ChangeTrustLowReserve)) {
                msg = Emoji.NOT_OK.concat(Emoji.NOT_OK.concat(Emoji.ERROR)
                        .concat("ChangeTrustLowReserve Response; xdr: ".concat(xdr)));
            }
            LOGGER.info(msg);
            throw new Exception(msg);
        }

    }
    public static final String TX_BadAuth = "AAAAAAAAAlj////6AAAAAA==",
    TX_ChangeTrustSuccess = "AAAAAACYloD/////AAAAAQAAAAAAAAAGAAAAAAAAAAA=",
    TX_ChangeTrustLowReserve = "AAAAAACYloD/////AAAAAQAAAAAAAAAG/////AAAAAA=",
    TX_ChangeTrustInvalidLimit = "AAAAAACYloD/////AAAAAQAAAAAAAAAG/////QAAAAA=",
    TX_ChangeTrustMalformed = "AAAAAACYloD/////AAAAAQAAAAAAAAAG/////wAAAAA=",
    TX_ChangeTrustSelfNotAllowed = "AAAAAACYloD/////AAAAAQAAAAAAAAAG////+wAAAAA=",
    TX_ChangeTrustNoIssuer = "AAAAAACYloD/////AAAAAQAAAAAAAAAG/////gAAAAA=";

    private AccountResponseBag sendFiatPayments(String amount,
                                                KeyPair destinationKeyPair,
                                                KeyPair sourceKeyPair,
                                                List<AssetBag> assetBags) throws Exception {

        LOGGER.info(Emoji.BLUE_BIRD.concat(Emoji.BLUE_BIRD).concat("sendFiatPayments: Creating payment transaction ... "
                + assetBags.size() + " FIAT assets to be paid; destinationAccount: "
                .concat(destinationKeyPair.getAccountId()).concat(" sourceAccount: ").concat(sourceKeyPair.getAccountId())
                .concat(Emoji.FIRE).concat(Emoji.FIRE)));
        setServerAndNetwork();
        AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
        Transaction.Builder paymentTxBuilder = new Transaction.Builder(sourceAccount, network);
        for (AssetBag assetBag : assetBags) {
            paymentTxBuilder.addOperation(new PaymentOperation.Builder(
                    destinationKeyPair.getAccountId(), assetBag.asset, amount).build());
        }
        Transaction paymentTx = paymentTxBuilder.addMemo(Memo.text("User Payment Tx"))
                .setTimeout(180)
                .setOperationFee(100)
                .build();

        paymentTx.sign(sourceKeyPair);

        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR).concat(("sendPayment: " +
                "Submitting transaction with payment operations ... ")
                .concat(Emoji.RED_DOT)));
        SubmitTransactionResponse payTransactionResponse = server.submitTransaction(paymentTx);
        if (payTransactionResponse.isSuccess()) {
            String msg = Emoji.LEAF.concat(Emoji.LEAF.concat(Emoji.LEAF)
                    .concat("Payment Transaction is successful. Check fiat balances on user account"));
            LOGGER.info(msg);
            AccountResponse userAccountResponse = server.accounts().account(destinationKeyPair.getAccountId());
            String seed = new String(destinationKeyPair.getSecretSeed());
            AccountResponseBag bag = new AccountResponseBag(userAccountResponse,seed);
//            LOGGER.info(Emoji.PEACH + "Payment Destination Account after TrustLines and Fiat Payments "
//                    .concat(Emoji.PEACH.concat(Emoji.PEACH)).concat(G.toJson(bag)));
//            AccountResponse distAccountResponse = server.accounts().account(sourceAccount.getAccountId());
//            LOGGER.info(Emoji.BLUE_BIRD.concat(Emoji.BLUE_BIRD).concat(Emoji.BLUE_BIRD).concat("........ " +
//                    "DISTRIBUTION (SOURCE) account after all the shit; check balances ....").concat(G.toJson(distAccountResponse)));
            return bag;
        } else {
            String xdr = payTransactionResponse.getResultXdr().get();
            String msg = Emoji.NOT_OK.concat(Emoji.NOT_OK.concat(Emoji.ERROR)
                    .concat("Payment Transaction Failed; xdr: ".concat(xdr)));
            LOGGER.info(msg);

            throw new Exception(msg);
        }
    }

    public String sendPayment(String amount,
                                       String sourceSeed,
                                       String destinationAccount,
                                       String assetCode) throws Exception {

        LOGGER.info(Emoji.BLUE_BIRD.concat(Emoji.BLUE_BIRD).concat("sendPayment: ... Creating payment transaction ... "
                .concat(Emoji.FIRE)));
        setServerAndNetwork();
        if (anchor == null) {
            anchor = firebaseService.getAnchorByName(anchorName);
        }
        List<AssetBag> assetBags = getDefaultAssets(anchor.getIssuingAccount().getAccountId());
        Asset asset = null;
        for (AssetBag assetBag : assetBags) {
            if (assetBag.assetCode.equalsIgnoreCase(assetCode)) {
                asset = assetBag.asset;
            }
        }
        if (asset == null) {
            throw new Exception(Emoji.NOT_OK + "Asset not found: ".concat(assetCode));
        }
        KeyPair sourceKeyPair = KeyPair.fromSecretSeed(sourceSeed);
        AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
        Transaction.Builder paymentTxBuilder = new Transaction.Builder(sourceAccount, network);
        paymentTxBuilder.addOperation(new PaymentOperation.Builder(
                destinationAccount, asset, amount).build());
        Transaction paymentTx = paymentTxBuilder.addMemo(Memo.text("User Payment Tx"))
                .setTimeout(180)
                .setOperationFee(100)
                .build();

        paymentTx.sign(sourceKeyPair);

        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR).concat(("sendPayment: " +
                "Submitting transaction with payment operations ... ")
                .concat(Emoji.RED_DOT)));
        SubmitTransactionResponse payTransactionResponse = server.submitTransaction(paymentTx);
        if (payTransactionResponse.isSuccess()) {
            String msg = Emoji.LEAF.concat(Emoji.LEAF.concat(Emoji.LEAF)
                    .concat("Payment Transaction is successful. Check fiat balances on user account"));
            LOGGER.info(msg);
//            AccountResponse userAccountResponse = server.accounts().account(destinationAccount);
//            LOGGER.info(Emoji.PEACH + "Payment Destination Account after Payment "
//                    .concat(Emoji.PEACH.concat(Emoji.PEACH)).concat(G.toJson(userAccountResponse)));
//            AccountResponse sourceAccountResponse = server.accounts().account(sourceAccount.getAccountId());
//            LOGGER.info(Emoji.BLUE_BIRD.concat(Emoji.BLUE_BIRD).concat(Emoji.BLUE_BIRD).concat("........ " +
//                    "DISTRIBUTION (SOURCE) account after all the payment; check balances ....").concat(G.toJson(sourceAccountResponse)));
            return msg;
        } else {

            String xdr = payTransactionResponse.getResultXdr().get();
            String msg = Emoji.NOT_OK.concat(Emoji.NOT_OK.concat(Emoji.ERROR)
                    .concat("Payment Transaction Failed; xdr: ".concat(xdr)));
            LOGGER.info(msg);
            throw new Exception(msg);
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

        String issuingSeed = cryptoService.getDecryptedSeed(anchor.getIssuingAccount().getAccountId());
        KeyPair keyPair = KeyPair.fromSecretSeed(issuingSeed);

        List<AssetBag> assets = getDefaultAssets(keyPair.getAccountId());
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

    🔺🔺🔺🔺🔺 ChangeTrustOperation Possible errors:

        Error	                        Code	Description
        CHANGE_TRUST_MALFORMED	        -1	    The input to this operation is invalid.
        CHANGE_TRUST_NO_ISSUER	        -2	    The issuer of the asset cannot be found.
        CHANGE_TRUST_INVALID_LIMIT	    -3	    The limit is not sufficient to hold the current balance of the trustline and still satisfy its buying liabilities.
        CHANGE_TRUST_LOW_RESERVE	    -4	    This account does not have enough XLM to satisfy the minimum XLM reserve increase caused by adding a subentry and still satisfy its XLM selling liabilities. For every new trustline added to an account, the minimum reserve of XLM that account must hold increases.
        CHANGE_TRUST_SELF_NOT_ALLOWED	-5	    The source account attempted to create a trustline for itself, which is not allowed.
 */
    public SubmitTransactionResponse createTrustLine(
            String issuingAccount, String userSeed, String limit, String assetCode) throws Exception {
        LOGGER.info("\uD83C\uDF40 .......... createTrustLines ........ \uD83C\uDF40 " +
                " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 limit: " + limit
                + " issuingAccount: " + issuingAccount);
        try {
            setServerAndNetwork();
            KeyPair userKeyPair = KeyPair.fromSecretSeed(userSeed);
            Asset asset = Asset.createNonNativeAsset(assetCode, issuingAccount);
            server.accounts().forAsset((AssetTypeCreditAlphaNum) asset)
                    .stream(new EventListener<AccountResponse>() {
                @Override
                public void onEvent(AccountResponse object) {
                    LOGGER.info(Emoji.BASKET_BALL.concat(Emoji.BASKET_BALL) +
                            "stream onEvent ... accountId".concat(object.getAccountId()));
                }

                @Override
                public void onFailure(Optional<Throwable> optional, Optional<Integer> optional1) {
                    LOGGER.info(Emoji.NOT_OK.concat(Emoji.NOT_OK) +
                            "server.accounts().forAsset stream onFailure event fired ... "
                    .concat(Emoji.LEMON.concat(Emoji.LEMON)));
                }
            });

            AccountResponse userAccountResponse = server.accounts().account(userKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 createTrustLine: User account: " + userAccountResponse.getAccountId()
                    + " \uD83C\uDF51 ... add trust lines ...");

            Transaction.Builder transactionBuilder = new Transaction.Builder(userAccountResponse, network);
            LOGGER.info(Emoji.STAR.concat(Emoji.STAR) + "Creating ChangeTrustOperation for "
            .concat(" asset ".concat(assetCode).concat(" ").concat(Emoji.RED_TRIANGLE)));

            transactionBuilder.addOperation(new ChangeTrustOperation.Builder(
                    asset, limit)
                    .build());

            transactionBuilder.addMemo(Memo.text("Create Trust Line"));
            transactionBuilder.setOperationFee(100);
            transactionBuilder.setTimeout(360);
            Transaction transaction = transactionBuilder.build();

            transaction.sign(userKeyPair);
            LOGGER.info("\uD83C\uDF40 ChangeTrustOperation transaction has been signed by distribution KeyPair... \uD83C\uDF51 on to submission ... ");

            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);

            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  " +
                        "Stellar issueAsset: ChangeTrustOperation has been executed OK: \uD83C\uDF4E \uD83C\uDF4E isSuccess: " + submitTransactionResponse.isSuccess());
            } else {
                if (submitTransactionResponse.getResultXdr().get().contains(LIMIT_ERROR)) {
                    String msg = Emoji.NOT_OK.concat(Emoji.GOLD_BELL.concat(Emoji.GOLD_BELL))
                            .concat("The limit is not sufficient to hold the current balance of the trustline and still satisfy its buying liabilities.");
                    LOGGER.info(msg);
                    throw new Exception(msg);
                }
                LOGGER.warning("ChangeTrustOperation ERROR: \uD83C\uDF45 resultXdr: " + submitTransactionResponse.getResultXdr().get());
                throw new Exception("ChangeTrustOperation transactionResponse is \uD83C\uDF45 NOT success \uD83C\uDF45");
            }
            return submitTransactionResponse;
        } catch (Exception e) {
            throw new Exception("ChangeTrustOperation failed", e);
        }
    }

    public static final String LIMIT_ERROR = "AAAAAAAAAGT/////AAAAAQAAAAAAAAAG/////QAAAAA=";
    private static File TOML_FILE;
    public List<AssetBag> getDefaultAssets(String issuingAccount) throws Exception {
        List<AssetBag> mList = new ArrayList<>();
        LOGGER.info(Emoji.PRESCRIPTION.concat(Emoji.PRESCRIPTION) +
                "getDefaultAssets: get stellar.toml file and return to caller...");
        ClassLoader classLoader = getClass().getClassLoader();
        if (TOML_FILE == null) {
            TOML_FILE = new File(Objects.requireNonNull(
                    classLoader.getResource("_well-known/stellar.toml")).getFile());
            Toml toml = new Toml().read(TOML_FILE);
            List<HashMap> currencies = toml.getList("CURRENCIES");
            for (HashMap currency : currencies) {
                if (issuingAccount.equalsIgnoreCase(currency.get("issuer").toString())) {
                    String code = currency.get("code").toString();
                    LOGGER.info("\uD83C\uDF3C stellar.toml: \uD83C\uDF3C Currency: ".concat((code)
                            .concat(" \uD83D\uDE21 issuer: ").concat(currency.get("issuer").toString())));
                }
            }
        }
        if (TOML_FILE.exists()) {
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C ... stellar.toml File has been found: "
                    + TOML_FILE.getAbsolutePath());
            Toml toml = new Toml().read(TOML_FILE);
            List<HashMap> currencies = toml.getList("CURRENCIES");
            for (HashMap currency : currencies) {
                if (issuingAccount.equalsIgnoreCase(currency.get("issuer").toString())) {
                    String code = currency.get("code").toString();
                    mList.add(new AssetBag(code, Asset.createNonNativeAsset(code, issuingAccount)));
                }
            }
        } else {
            LOGGER.info(" \uD83C\uDF45 stellar.toml : File NOT found. this is where .toml needs to go;  \uD83C\uDF45 ");
            throw new Exception("stellar.toml not found");
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
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) +
                "  .......... createAsset ........ \uD83C\uDF40 " +
                " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 " + " amount:" + amount
                + "\n \uD83C\uDF51 issuingAccountSeed: " + issuingAccountSeed + " \uD83C\uDF51 distributionAccount: "
                + distributionAccount);
        try {
            setServerAndNetwork();
            KeyPair issuingKeyPair = KeyPair.fromSecretSeed(issuingAccountSeed);
            AssetBag asset = new AssetBag(assetCode,Asset.createNonNativeAsset(assetCode, issuingKeyPair.getAccountId()));

            AccountResponse issuingAccount = server.accounts().account(issuingKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Issuing account: " + issuingAccount.getAccountId()
                    + " \uD83C\uDF51 ... create transaction with payment operation ... starting ...");

            Transaction.Builder trBuilder = new Transaction.Builder(issuingAccount, network);
            trBuilder.addOperation(new PaymentOperation.Builder(
                    distributionAccount, asset.asset, amount)
                    .setSourceAccount(issuingKeyPair.getAccountId())
                    .build());

            trBuilder.addMemo(Memo.text("Fiat Token ".concat(assetCode)));
            trBuilder.setOperationFee(100);
            trBuilder.setTimeout(360);
            Transaction transaction = trBuilder.build();

            transaction.sign(issuingKeyPair);
            LOGGER.info("\uD83C\uDF40 PaymentOperation tx has been signed by issuing KeyPair ... \uD83C\uDF51 on to submission ... ");

            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  " +
                        "Stellar createAsset: PaymentOperation has been executed OK: \uD83C\uDF4E \uD83C\uDF4E isSuccess: " + submitTransactionResponse.isSuccess());

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
        if (server != null) return;
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
