package com.anchor.api.services;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.transfer.sep10.AnchorSep10Challenge;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.stellar.sdk.Asset;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.CreateAccountOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.SetOptionsOperation;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.RootResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import shadow.com.google.common.base.Optional;

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
            final RootResponse serverResponse = server.root();
            LOGGER.info("\uD83E\uDD8B \uD83E\uDD8B \uD83C\uDF3C Stellar Horizon Server: "
                    .concat(G.toJson(serverResponse).concat(" \uD83C\uDF3C \uD83C\uDF3C ")));
        } catch (final IOException e) {
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

    public void talkToFriendBot(final String accountId) throws IOException {
        LOGGER.info("\uD83E\uDD6C ... Begging Ms. FriendBot for some \uD83C\uDF51 pussy \uD83C\uDF51 ... "
                + " \uD83D\uDD34 I heard she gives out!!");
        isDevelopment = status.equalsIgnoreCase("dev");
        setServerAndNetwork();
        InputStream response;
        final String friendBotUrl = String.format(FRIEND_BOT, accountId);
        response = new URL(friendBotUrl).openStream();
        final String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        LOGGER.info("\uD83E\uDD6C "
                + "FriendBot responded with largess: \uD83E\uDD6C 10,000 Lumens obtained. ... Yebo, Gogo!! \uD83E\uDD6C ");

        if (isDevelopment) {
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 Booty from Ms. FriendBot: \uD83C\uDF51 " + body);
        }
    }

    @Autowired
    private TOMLService tomlService;
    private Anchor anchor;

    private void setAnchor(final String anchorId) throws Exception {
        if (anchor != null) {
            return;
        }
        final Toml toml = tomlService.getToml(anchorId);
        if (toml == null) {
            throw new Exception("anchor.toml has not been found. upload the file from your computer");
        } else {
            final String id = toml.getString("anchorId");
            anchor = firebaseService.getAnchor(id);
            if (anchor == null) {
                LOGGER.info(Emoji.FIRE
                        .concat(Emoji.FIRE.concat(Emoji.FIRE).concat("We are fucked! There is no ANCHOR !!!")));
            }
        }

    }

    public AccountResponse getAccountUsingSeed(final String seed) throws IOException {
        setServerAndNetwork();
        final KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
        final AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

        return sourceAccount;
    }

    // todo - ensure auth as anchor user for this call
    public AccountResponse getAccountUsingAccount(final String accountId) throws Exception {
        setServerAndNetwork();
        LOGGER.info(Emoji.PEAR + " ..... Getting account: " + accountId);
        try {
            return server.accounts().account(accountId);
        } catch (Exception e) {
            LOGGER.info(Emoji.NOT_OK.concat(Emoji.NOT_OK) + "getAccountUsingAccount FAILED");
            e.printStackTrace();
            throw new Exception(Emoji.ERROR + "Failed to get Account data from Stellar");
        }
    }

    public String retrieveKey(final String agentId) throws Exception {
        final Agent mAgent = firebaseService.getAgent(agentId);
        if (mAgent == null) {
            throw new Exception("Agent not found");
        }
        final String seed = cryptoService.getDecryptedSeed(mAgent.getStellarAccountId());
        LOGGER.info("Seed decrypted and returned");
        return seed;
    }

    public AccountResponseBag createAndFundAnchorAccount(final String seed, final String startingBalance)
            throws Exception {
        LOGGER.info("\uD83D\uDC99 ... ... ... ... createAndFundStellarAccount starting ....... startingBalance: "
                + startingBalance);
        setServerAndNetwork();
        AccountResponse accountResponse;

        try {
            final KeyPair newAccountKeyPair = KeyPair.random();
            final KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
            final String secret = new String(newAccountKeyPair.getSecretSeed());
            final AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

            final Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(new CreateAccountOperation.Builder(newAccountKeyPair.getAccountId(), startingBalance)
                            .build())
                    .addMemo(Memo.text("CreateAccount Tx")).setTimeout(180).setOperationFee(100).build();

            transaction.sign(sourceKeyPair);

            final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                accountResponse = server.accounts().account(newAccountKeyPair.getAccountId());
                LOGGER.info("\uD83D\uDC99  "
                        + "Stellar account has been created and funded!: \uD83C\uDF4E \uD83C\uDF4E YEBO!!!");
                final AccountResponseBag bag = new AccountResponseBag(accountResponse, secret);
                LOGGER.info(("\uD83C\uDF4E \uD83C\uDF4E RESPONSE from Stellar; " + "\uD83D\uDC99 new accountId: ")
                        .concat(bag.getAccountResponse().getAccountId()));
                return bag;
            } else {
                LOGGER.warning(Emoji.NOT_OK + "CreateAccountOperation ERROR: \uD83C\uDF45 resultXdr: "
                        + submitTransactionResponse.getResultXdr().get());
                if (submitTransactionResponse.getResultXdr().get().equalsIgnoreCase(TX_UNDER_FUNDED)) {
                    LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 transaction is UNDER FUNDED");
                    throw new AccountUnderfundedException(sourceAccount.getAccountId().concat(" is UNDER FUNDED"));
                }
                if (submitTransactionResponse.getResultXdr().get().equalsIgnoreCase(TX_MALFORMED)) {
                    LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 transaction is MALFORMED");
                    throw new AccountUnderfundedException(
                            sourceAccount.getAccountId().concat(" transaction is MALFORMED"));
                }

                throw new Exception("CreateAccountOperation transactionResponse is NOT success");
            }

        } catch (final IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }

    private static final String TX_MALFORMED = "AAAAAAAAAGT/////AAAAAQAAAAAAAAAA/////wAAAAA=",
            TX_UNDER_FUNDED = "AAAAAAAAAGT/////AAAAAQAAAAAAAAAA/////gAAAAA=";

    public AccountResponseBag createAndFundUserAccount(final String anchorId, final String startingXLMBalance,
            final String startingFiatBalance, final String fiatLimit) throws Exception {
        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR) + "\uD83D\uDC99 ... ... ... ... createAndFundAgentAccount starting "
                + "....... startingXLMBalance: " + startingXLMBalance + " startingFiatBalance:" + startingFiatBalance
                + " fiatLimit: " + fiatLimit);
        setServerAndNetwork();
        setAnchor(anchorId);

        try {
            final String baseSeed = cryptoService.getDecryptedSeed(anchor.getBaseAccount().getAccountId());
            final KeyPair agentKeyPair = KeyPair.random();
            final KeyPair sourceKeyPair = KeyPair.fromSecretSeed(baseSeed);

            final AccountResponse baseAccount = server.accounts().account(sourceKeyPair.getAccountId());

            final String distributionSeed = cryptoService
                    .getDecryptedSeed(anchor.getDistributionAccount().getAccountId());
            final KeyPair distributionKeyPair = KeyPair.fromSecretSeed(distributionSeed);

            final Transaction transaction = new Transaction.Builder(baseAccount, network)
                    .addOperation(
                            new CreateAccountOperation.Builder(agentKeyPair.getAccountId(), startingXLMBalance).build())
                    .addMemo(Memo.text("CreateAccount Tx")).setTimeout(180).setOperationFee(100).build();

            transaction.sign(sourceKeyPair);
            LOGGER.info(Emoji.RED_CAR + "Submit tx with CreateAccountOperation ");
            final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                // add trustlines and first payment for all fiat tokens
                LOGGER.info(Emoji.LEAF + "Stellar account created: ".concat(Emoji.LEAF).concat(" ")
                        .concat(agentKeyPair.getAccountId()).concat(" ... about to start creating trustlines"));
                final AccountResponseBag agentAccountResponseBag = addTrustlinesAndOriginalBalances(fiatLimit,
                        startingFiatBalance, agentKeyPair, distributionKeyPair);
                LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF.concat(Emoji.LEAF))
                        + "It is indeed possible that everything worked. WTF?");
                final String secret = new String(agentKeyPair.getSecretSeed());
                agentAccountResponseBag.setSecretSeed(secret);
                return agentAccountResponseBag;
            } else {
                LOGGER.warning(Emoji.NOT_OK + "CreateAccountOperation ERROR: \uD83C\uDF45 resultXdr: "
                        + submitTransactionResponse.getResultXdr().get());
                if (submitTransactionResponse.getResultXdr().get()
                        .equalsIgnoreCase("AAAAAAAAAGT/////AAAAAQAAAAAAAAAA/////gAAAAA=")) {
                    throw new AccountUnderfundedException(baseAccount.getAccountId().concat(" is UNDER FUNDED"));
                }

                throw new Exception("CreateAccountOperation transactionResponse is NOT success");
            }

        } catch (final IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }

    private AccountResponseBag addTrustlinesAndOriginalBalances(final String limit, final String startingFiatBalance,
            final KeyPair userKeyPair, final KeyPair distributionKeyPair) throws Exception {

        final List<AssetBag> assetBags = getDefaultAssets(anchor.getIssuingAccount().getAccountId());
        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR)
                .concat(("addTrustlinesAndOriginalBalances: "
                        + "Building transaction with trustline operations ... FIAT ASSETS: " + assetBags.size())
                                .concat(Emoji.RED_DOT)));
        final AccountResponse account = server.accounts().account(userKeyPair.getAccountId());
        final Transaction.Builder trustlineTxBuilder = new Transaction.Builder(account, network);
        for (final AssetBag assetBag : assetBags) {
            trustlineTxBuilder.addOperation(new ChangeTrustOperation.Builder(assetBag.asset, limit).build());
        }
        final Transaction userTrustlineTx = trustlineTxBuilder.addMemo(Memo.text("User Trustline Tx")).setTimeout(180)
                .setOperationFee(100).build();

        userTrustlineTx.sign(userKeyPair);
        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR)
                .concat(("addTrustlinesAndOriginalBalances: " + "Submitting transaction with trustline operations ... ")
                        .concat(Emoji.RED_DOT)));
        final SubmitTransactionResponse trustlineTransactionResponse = server.submitTransaction(userTrustlineTx);
        LOGGER.info(
                Emoji.HAND1.concat(Emoji.HAND2.concat(Emoji.HAND3)) + "User Trustline transaction response; isSuccess: "
                        .concat("" + trustlineTransactionResponse.isSuccess()));
        if (trustlineTransactionResponse.isSuccess()) {
            return sendFiatPayments(startingFiatBalance, userKeyPair, distributionKeyPair, assetBags);
        } else {
            final String xdr = trustlineTransactionResponse.getResultXdr().get();
            String msg = Emoji.NOT_OK
                    .concat(Emoji.NOT_OK.concat(Emoji.ERROR).concat("Trustline Transaction Failed: xdr: ".concat(xdr)));
            if (xdr.contains(TX_BadAuth)) {
                msg = Emoji.NOT_OK.concat(Emoji.NOT_OK.concat(Emoji.ERROR)
                        .concat("Bad Auth for Trustline Transaction Response; xdr: ".concat(xdr)));
            }
            if (xdr.contains(TX_ChangeTrustLowReserve)) {
                msg = Emoji.NOT_OK.concat(
                        Emoji.NOT_OK.concat(Emoji.ERROR).concat("ChangeTrustLowReserve Response; xdr: ".concat(xdr)));
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

    private AccountResponseBag sendFiatPayments(final String amount, final KeyPair destinationKeyPair,
            final KeyPair sourceKeyPair, final List<AssetBag> assetBags) throws Exception {

        LOGGER.info(Emoji.BLUE_BIRD.concat(Emoji.BLUE_BIRD)
                .concat("sendFiatPayments: Creating payment transaction ... " + assetBags.size()
                        + " FIAT assets to be paid; destinationAccount: ".concat(destinationKeyPair.getAccountId())
                                .concat(" sourceAccount: ").concat(sourceKeyPair.getAccountId()).concat(Emoji.FIRE)
                                .concat(Emoji.FIRE))
                .concat(" ----- check AMOUNT: ").concat(amount));

        setServerAndNetwork();
        final AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
        final Transaction.Builder paymentTxBuilder = new Transaction.Builder(sourceAccount, network);
        for (final AssetBag assetBag : assetBags) {
            paymentTxBuilder.addOperation(
                    new PaymentOperation.Builder(destinationKeyPair.getAccountId(), assetBag.asset, amount).build());
        }
        final Transaction paymentTx = paymentTxBuilder.addMemo(Memo.text("User Payment Tx")).setTimeout(180)
                .setOperationFee(100).build();

        paymentTx.sign(sourceKeyPair);

        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR).concat(
                ("sendPayment: " + "Submitting transaction with payment operations ... ").concat(Emoji.RED_DOT)));
        final SubmitTransactionResponse payTransactionResponse = server.submitTransaction(paymentTx);
        if (payTransactionResponse.isSuccess()) {
            final String msg = Emoji.LEAF.concat(Emoji.LEAF.concat(Emoji.LEAF)
                    .concat("Payment Transaction is successful. Check fiat balances on user account"));
            LOGGER.info(msg);
            final AccountResponse userAccountResponse = server.accounts().account(destinationKeyPair.getAccountId());
            final String seed = new String(destinationKeyPair.getSecretSeed());
            final AccountResponseBag bag = new AccountResponseBag(userAccountResponse, seed);
            // add original payments to database
            for (final AssetBag assetBag : assetBags) {
                final PaymentRequest request = new PaymentRequest();
                request.setAmount(amount);
                request.setAnchorId(anchor.getAnchorId());
                request.setDate(new DateTime().toDateTimeISO().toString());
                request.setAssetCode(assetBag.assetCode);
                request.setSourceAccount(sourceKeyPair.getAccountId());
                request.setDestinationAccount(destinationKeyPair.getAccountId());
                request.setLedger(payTransactionResponse.getLedger());
                firebaseService.addPaymentRequest(request);
            }
            return bag;
        } else {
            final String xdr = payTransactionResponse.getResultXdr().get();
            final String msg = Emoji.NOT_OK
                    .concat(Emoji.NOT_OK.concat(Emoji.ERROR).concat("Payment Transaction Failed; xdr: ".concat(xdr)));
            LOGGER.info(msg);

            throw new Exception(msg);
        }
    }

    static class AccountUnderfundedException extends Exception {
        public AccountUnderfundedException(final String message) {
            super(message);
        }
    }

    @Autowired
    private CryptoService cryptoService;

    /*
     * 🍊 🍊 🍊 Anchors: issuing assets Any account can issue assets on the Stellar
     * network. Entities that issue assets are called anchors. Anchors can be run by
     * individuals, small businesses, local communities, nonprofits, organizations,
     * etc. Any type of financial institution–a bank, a payment processor–can be an
     * anchor.
     * 
     * 🍎 Each anchor has an issuing account from which it issues the asset.
     * 
     * 🔺🔺🔺🔺🔺 ChangeTrustOperation Possible errors:
     * 
     * Error Code Description CHANGE_TRUST_MALFORMED -1 The input to this operation
     * is invalid. CHANGE_TRUST_NO_ISSUER -2 The issuer of the asset cannot be
     * found. CHANGE_TRUST_INVALID_LIMIT -3 The limit is not sufficient to hold the
     * current balance of the trustline and still satisfy its buying liabilities.
     * CHANGE_TRUST_LOW_RESERVE -4 This account does not have enough XLM to satisfy
     * the minimum XLM reserve increase caused by adding a subentry and still
     * satisfy its XLM selling liabilities. For every new trustline added to an
     * account, the minimum reserve of XLM that account must hold increases.
     * CHANGE_TRUST_SELF_NOT_ALLOWED -5 The source account attempted to create a
     * trustline for itself, which is not allowed.
     */
    public SubmitTransactionResponse createTrustLine(final String issuingAccount, final String userSeed,
            final String limit, final String assetCode) throws Exception {
        LOGGER.info("\uD83C\uDF40 .......... createTrustLines ........ \uD83C\uDF40 " + " \uD83C\uDF40 code: "
                + assetCode + " \uD83C\uDF40 limit: " + limit + " issuingAccount: " + issuingAccount);
        try {
            setServerAndNetwork();
            final KeyPair userKeyPair = KeyPair.fromSecretSeed(userSeed);
            final Asset asset = Asset.createNonNativeAsset(assetCode, issuingAccount);
            final AccountResponse userAccountResponse = server.accounts().account(userKeyPair.getAccountId());
            final Transaction.Builder transactionBuilder = new Transaction.Builder(userAccountResponse, network);

            transactionBuilder.addOperation(new ChangeTrustOperation.Builder(asset, limit).build());

            transactionBuilder.addMemo(Memo.text("Create Trust Line"));
            transactionBuilder.setOperationFee(100);
            transactionBuilder.setTimeout(360);
            final Transaction transaction = transactionBuilder.build();

            transaction.sign(userKeyPair);

            final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);

            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  "
                        + "Stellar issueAsset: ChangeTrustOperation has been executed OK: "
                        + "\uD83C\uDF4E \uD83C\uDF4E isSuccess: " + submitTransactionResponse.isSuccess()
                        + " \uD83C\uDF4E assetCode: ".concat(assetCode) + " \uD83C\uDF4E User account: "
                        + userAccountResponse.getAccountId());
            } else {
                if (submitTransactionResponse.getResultXdr().get().contains(LIMIT_ERROR)) {
                    final String msg = Emoji.NOT_OK.concat(Emoji.GOLD_BELL.concat(Emoji.GOLD_BELL)).concat(
                            "The limit is not sufficient to hold the current balance of the trustline and still satisfy its buying liabilities.");
                    LOGGER.info(msg);
                    throw new Exception(msg);
                }
                LOGGER.warning("ChangeTrustOperation ERROR: \uD83C\uDF45 resultXdr: "
                        + submitTransactionResponse.getResultXdr().get());
                throw new Exception(
                        "ChangeTrustOperation transactionResponse is \uD83C\uDF45 NOT success \uD83C\uDF45");
            }
            return submitTransactionResponse;
        } catch (final Exception e) {
            throw new Exception("ChangeTrustOperation failed", e);
        }
    }

    public static final String LIMIT_ERROR = "AAAAAAAAAGT/////AAAAAQAAAAAAAAAG/////QAAAAA=";
    private static File TOML_FILE;

    public List<AssetBag> getAnchorCurrencies(final String anchorId) throws Exception {
        Anchor anchor = firebaseService.getAnchor(anchorId);
        return getDefaultAssets(anchor.getIssuingAccount().getAccountId());
    }

    public List<AssetBag> getDefaultAssets(final String issuingAccount) throws Exception {
        final List<AssetBag> mList = new ArrayList<>();
        LOGGER.info(Emoji.PRESCRIPTION.concat(Emoji.PRESCRIPTION)
                + "getDefaultAssets: get stellar.toml file and return to caller...");
        final ClassLoader classLoader = getClass().getClassLoader();
        if (TOML_FILE == null) {
            TOML_FILE = new File(Objects.requireNonNull(classLoader.getResource("_well-known/stellar.toml")).getFile());
        }
        if (TOML_FILE.exists()) {
            LOGGER.info(
                    "\uD83C\uDF3C \uD83C\uDF3C ... stellar.toml File has been found: " + TOML_FILE.getAbsolutePath());
            final Toml toml = new Toml().read(TOML_FILE);
            final List<HashMap> currencies = toml.getList("CURRENCIES");
            for (final HashMap currency : currencies) {
                if (issuingAccount.equalsIgnoreCase(currency.get("issuer").toString())) {
                    final String code = currency.get("code").toString();
                    mList.add(new AssetBag(code, Asset.createNonNativeAsset(code, issuingAccount)));
                }
            }
        } else {
            LOGGER.info(" \uD83C\uDF45 stellar.toml : File NOT found. this is where .toml needs to go;  \uD83C\uDF45 ");
            throw new Exception("stellar.toml not found");
        }

        if (mList.isEmpty()) {
            LOGGER.info(Emoji.RED_DOT.concat(Emoji.RED_DOT.concat(Emoji.RED_DOT)
                    .concat("Currencies missing from STELLAR TOML file. Please add issuing account after creation ")));
            mList.add(new AssetBag("ZAR", Asset.createNonNativeAsset("ZAR", issuingAccount)));
            mList.add(new AssetBag("USD", Asset.createNonNativeAsset("USD", issuingAccount)));
            mList.add(new AssetBag("GBP", Asset.createNonNativeAsset("GBP", issuingAccount)));
            mList.add(new AssetBag("EUR", Asset.createNonNativeAsset("EUR", issuingAccount)));
            // mList.add(new AssetBag("CHF", Asset.createNonNativeAsset("CHF",
            // issuingAccount)));
            // mList.add(new AssetBag("CNY", Asset.createNonNativeAsset("CNY",
            // issuingAccount)));
            // Currencies in Africa
            // mList.add(new AssetBag("BWP", Asset.createNonNativeAsset("BWP",
            // issuingAccount)));
            // mList.add(new AssetBag("ETB", Asset.createNonNativeAsset("ETB",
            // issuingAccount)));
            // mList.add(new AssetBag("NGN", Asset.createNonNativeAsset("NGN",
            // issuingAccount)));
            // mList.add(new AssetBag("KES", Asset.createNonNativeAsset("KES",
            // issuingAccount)));
            // mList.add(new AssetBag("RWF", Asset.createNonNativeAsset("RWF",
            // issuingAccount)));
            // mList.add(new AssetBag("MUR", Asset.createNonNativeAsset("MUR",
            // issuingAccount)));

            // todo - write currencies to stellar.toml
        }

        return mList;
    }

    public static class AssetBag {
        String assetCode;
        Asset asset;

        public AssetBag(final String assetCode, final Asset asset) {
            this.assetCode = assetCode;
            this.asset = asset;
        }

        public String getAssetCode() {
            
            return assetCode;
        }

        public void setAssetCode(final String assetCode) {
            this.assetCode = assetCode;
        }

        public Asset getAsset() {
            return asset;
        }

        public void setAsset(final Asset asset) {
            this.asset = asset;
        }
    }

    public SubmitTransactionResponse createAsset(final String issuingAccountSeed, final String distributionAccount,
            final String assetCode, final String amount) throws Exception {
        LOGGER.info(Emoji.PEACH.concat(Emoji.PEACH) + "  .......... createAsset ........ \uD83C\uDF40 "
                + " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 " + " amount:" + amount
                + "\n \uD83C\uDF51 issuingAccountSeed: " + issuingAccountSeed + " \uD83C\uDF51 distributionAccount: "
                + distributionAccount);
        try {
            setServerAndNetwork();
            final KeyPair issuingKeyPair = KeyPair.fromSecretSeed(issuingAccountSeed);
            final AssetBag asset = new AssetBag(assetCode,
                    Asset.createNonNativeAsset(assetCode, issuingKeyPair.getAccountId()));

            final AccountResponse issuingAccount = server.accounts().account(issuingKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Issuing account: " + issuingAccount.getAccountId()
                    + " \uD83C\uDF51 ... create transaction with payment operation ... starting ...");

            final Transaction.Builder trBuilder = new Transaction.Builder(issuingAccount, network);
            trBuilder.addOperation(new PaymentOperation.Builder(distributionAccount, asset.asset, amount)
                    .setSourceAccount(issuingKeyPair.getAccountId()).build());

            trBuilder.addMemo(Memo.text("Fiat Token ".concat(assetCode)));
            trBuilder.setOperationFee(100);
            trBuilder.setTimeout(360);
            final Transaction transaction = trBuilder.build();

            transaction.sign(issuingKeyPair);
            LOGGER.info(
                    "\uD83C\uDF40 PaymentOperation tx has been signed by issuing KeyPair ... \uD83C\uDF51 on to submission ... ");

            final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  "
                        + "Stellar createAsset: PaymentOperation has been executed OK: \uD83C\uDF4E \uD83C\uDF4E isSuccess: "
                        + submitTransactionResponse.isSuccess());

            } else {
                LOGGER.info("ERROR: \uD83C\uDF45 resultXdr: " + submitTransactionResponse.getResultXdr().get());
                throw new Exception("PaymentOperation transactionResponse is \uD83C\uDF45 NOT success \uD83C\uDF45");
            }
            return submitTransactionResponse;
        } catch (final Exception e) {
            throw new Exception("PaymentOperation failed", e);
        }
    }

    public void listenForTransactions() throws Exception {
        setServerAndNetwork();
        // todo - what do we want to listen for ???
        LOGGER.info(
                "\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 ... Listen For Stellar Accounts ...");
        server.accounts().stream(new EventListener<AccountResponse>() {
            @Override
            public void onEvent(final AccountResponse accountResponse) {
                LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 Accounts Listener fired, "
                        + "accountResponse received, add to Firestore ...");
                try {
                    firebaseService.addAccountResponse(accountResponse);
                } catch (final Exception e) {
                    LOGGER.log(Level.SEVERE, "AccountListener failed", e);
                }
            }

            @Override
            public void onFailure(final Optional<Throwable> optional, final Optional<Integer> optional1) {
                try {
                    LOGGER.info("\uD83C\uDF45 accountListener onFailure: " + optional.get().getMessage());
                    LOGGER.severe("AccountListener failed");
                } catch (final Exception e) {
                    // ignore
                }
            }
        });

        // LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45
        // ... Listen For Stellar Payments ...");
        // SSEStream<OperationResponse> mm = server.payments().stream(new
        // EventListener<OperationResponse>() {
        // @Override
        // public void onEvent(OperationResponse operationResponse) {
        // LOGGER.info("isTransactionSuccessful:
        // ".concat(operationResponse.isTransactionSuccessful().toString()
        // .concat(" SourceAccount: ").concat(operationResponse.getSourceAccount())));
        // try {
        // firebaseService.addOperationResponse(operationResponse);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        //
        // @Override
        // public void onFailure(Optional<Throwable> optional, Optional<Integer>
        // optional1) {
        // try {
        // LOGGER.info("\uD83C\uDF45 operationResponse onFailure: " +
        // optional.get().getMessage());
        // LOGGER.severe("operationResponse failed");
        // } catch (Exception e) {
        // //ignore
        // }
        // }
        // });
        // LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45
        // ... Listen For Stellar Transactions ...");
        // server.transactions().stream(new EventListener<TransactionResponse>() {
        // @Override
        // public void onEvent(TransactionResponse transactionResponse) {
        // LOGGER.info("\uD83C\uDF4E transactionListener:onEvent:TransactionResponse:
        // isSuccessful: "
        // + transactionResponse.isSuccessful()
        // + " \uD83D\uDC99 source account: " + transactionResponse.getSourceAccount());
        // try {
        // firebaseService.addTransactionResponse(transactionResponse);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        //
        // @Override
        // public void onFailure(Optional<Throwable> optional, Optional<Integer>
        // optional1) {
        // try {
        // LOGGER.info("\uD83C\uDF45 transactionListener onFailure: " +
        // optional.get().getMessage());
        // LOGGER.severe("transactionListener failed");
        // } catch (Exception e) {
        // //ignore
        // }
        // }
        // });

    }

    public SubmitTransactionResponse setOptions(final String seed, final int clearFlags, final int highThreshold,
            final int lowThreshold, final String inflationDestination, final int masterKeyWeight) throws Exception {

        setServerAndNetwork();
        final KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        final AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        final SetOptionsOperation operation = new SetOptionsOperation.Builder().setClearFlags(clearFlags)
                .setHighThreshold(highThreshold).setLowThreshold(lowThreshold)
                .setInflationDestination(inflationDestination).setSourceAccount(keyPair.getAccountId())
                .setMasterKeyWeight(masterKeyWeight).setHomeDomain(domain).build();

        final Transaction transaction = new Transaction.Builder(sourceAccount, network).addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS).setOperationFee(100).build();
        try {
            transaction.sign(keyPair);
            final SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("setOptions: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess()
                    + " \uD83D\uDC99 ");
            LOGGER.info(
                    response.isSuccess() ? "setOptions transaction is SUCCESSFUL" : "setOptions transaction failed");
            return response;
        } catch (final Exception e) {
            final String msg = "Failed to setOptions: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    @Autowired
    AnchorSep10Challenge anchorSep10Challenge;

    public String handleChallenge(final String seed) throws Exception {

        setServerAndNetwork();
        final KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        //todo - finish this ...
        final AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());


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
