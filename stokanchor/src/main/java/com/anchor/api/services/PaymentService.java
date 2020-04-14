package com.anchor.api.services;


import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Anchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;
import java.util.logging.Logger;

@Service
public class PaymentService {
    public static final Logger LOGGER = Logger.getLogger(PaymentService.class.getSimpleName());
    private Server server;
    private Network network;
    private boolean isDevelopment;


    @Autowired
    private FirebaseService firebaseService;

    @Value("${status}")
    private String status;

    @Value("${anchorName}")
    private String anchorName;

    @Value("${stellarUrl}")
    private String stellarUrl;


    public PaymentService() {
        LOGGER.info("\uD83C\uDF0D \uD83C\uDF0D PaymentService Constructor fired ... \uD83C\uDF0D");
    }

    public SubmitTransactionResponse sendPayment(String seed, String assetCode, String amount,
                                                 String destinationAccount) throws Exception {

        setServerAndNetwork();
        Anchor anchor = firebaseService.getAnchorByName(anchorName);
        try {
            KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
            Asset asset = Asset.createNonNativeAsset(
                    assetCode,
                    anchor.getDistributionAccount().getAccountId());

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(new PaymentOperation.Builder(
                            destinationAccount,asset, amount)
                            .build())
                    .addMemo(Memo.text("Payment Tx"))
                    .setTimeout(180)
                    .setOperationFee(100)
                    .build();

            transaction.sign(sourceKeyPair);
            return server.submitTransaction(transaction);

        } catch (IOException e) {
            String msg = "Failed to send Stellar payment";
            LOGGER.severe(msg);
            throw new Exception(msg, e);
        }
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
}
