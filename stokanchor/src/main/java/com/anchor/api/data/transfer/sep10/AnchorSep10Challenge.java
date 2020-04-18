package com.anchor.api.data.transfer.sep10;


import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.services.CryptoService;
import com.anchor.api.services.FirebaseService;
import com.anchor.api.util.Emoji;
import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.xdr.DecoratedSignature;
import org.stellar.sdk.xdr.Signature;
import org.stellar.sdk.xdr.SignatureHint;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Logger;

@Service
public class AnchorSep10Challenge {
    public AnchorSep10Challenge() {
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 AnchorSep10Challenge Service constructor: " +
                "\uD83D\uDC99 handles Sep10 Web Authentication");
    }

    private static final Logger LOGGER = Logger.getLogger(AnchorSep10Challenge.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEV_SERVER = "https://horizon-testnet.stellar.org";
    private static final String PROD_SERVER = "https://horizon.stellar.org'";
    @Value("${status}")
    private String status;
    boolean isDevelopment;
    private Server server;
    private Network network;

    @Value("${anchorName}")
    private String anchorName;

    @Value("${jwtIssuer}")
    private String jwtIssuer;

    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private JWTokenService tokenService;

    private static final String em1 = "\uD83E\uDD66 \uD83E\uDD66 ",
    em2 = "\uD83C\uDF3C ", error = "\uD83D\uDE08 ";
    private static final int EXPIRE_AFTER_N_MINUTES = 1000 * 60 * 15;

    /**
     * Returns a valid <a href="https://github.com/stellar/stellar-protocol/blob/master/ecosystem/sep-0010.md#response" target="_blank">SEP 10</a> challenge, for use in web authentication.
     *
     * @param clientAccountId The stellar account belonging to the client.
     */
    public  ChallengeResponse newChallenge(String clientAccountId) throws Exception {
        LOGGER.info(emm + "Executing challenge ... to return XDR transaction to caller ... " + emm);
        Anchor anchor = firebaseService.getAnchorByName(anchorName);
        if (anchor == null) {
            LOGGER.severe("Anchor ".concat(anchorName).concat(" is missing"));
            throw new Exception(error + " Anchor is missing " + error
            .concat(anchorName));
        }
        LOGGER.info(em1 + "Anchor found on Firestore: "
                .concat(anchorName));

        String seed = cryptoService.getDecryptedSeed(anchor.getBaseAccount().getAccountId());
        LOGGER.info(em1 + " " + em1 +"Decrypted seed: "
                .concat(seed).concat(" ").concat(em1));
        setServerAndNetwork();
        KeyPair signer = KeyPair.fromSecretSeed(seed);
        TimeBounds bounds = new TimeBounds(new Date().getTime(), new Date().getTime() + EXPIRE_AFTER_N_MINUTES);

        byte[] nonce = new byte[48];
        SecureRandom random = new SecureRandom();
        random.nextBytes(nonce);
        BaseEncoding base64Encoding = BaseEncoding.base64();
        byte[] encodedNonce = base64Encoding.encode(nonce).getBytes();

        AccountResponse clientAccount = null;
        try {
            clientAccount = server.accounts().account(clientAccountId);
        } catch (Exception e) {
            LOGGER.severe("Client Account not found on Stellar");
        }
        if (clientAccount == null) {
            throw new Exception(error + "Client Account missing ".concat(error));
        }
        LOGGER.info(em2 +" Client has an account on Stellar. We good! Starting ManageDataOperation in transaction ..."
                +em2+" anchor: " + anchorName);

        int maxSize = 50;
        String key = "";
        if(anchorName.length() > maxSize ){
            key = anchorName.substring(0, maxSize);
        } else {
            key = anchorName;
        }
        key += " auth";
        Account sourceAccount = new Account(signer.getAccountId(), -1L);
        ManageDataOperation operation = new ManageDataOperation.Builder(key, encodedNonce)
                .setSourceAccount(clientAccountId)
                .build();

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addTimeBounds(bounds)
                .setOperationFee(100)
                .addOperation(operation)
                .build();

        transaction.sign(signer);

        ChallengeResponse challengeResponse = new ChallengeResponse(
                transaction.toEnvelopeXdrBase64(),
                network.getNetworkPassphrase());

        LOGGER.info("\n" + emm + "Challenge Transaction created, "+em2+" signed by anchor base account and converted to "
                +em2+"XDR "+em2+"... we done good, Boss! " + em1);
        LOGGER.info(Emoji.HAND1.concat(Emoji.HAND2.concat(Emoji.HAND3))
                .concat("ChallengeResponse returned to caller")
        .concat(G.toJson(challengeResponse)));
        return challengeResponse;
    }

    public static final String emm = Emoji.BLUE_DOT + Emoji.BLUE_DOT + Emoji.BLUE_DOT;

    public String getToken(String transaction) throws Exception {
        LOGGER.info("\n\n" + emm + Emoji.ICE_CREAM + Emoji.ICE_CREAM +
                " getToken: ... Getting JWT token from XDR transaction string ....".concat(Emoji.DIAMOND).concat(emm));

        setServerAndNetwork();
        AnchorSep10Challenge.ChallengeTransaction challengeTransaction;
        try {
            challengeTransaction = readChallengeTransaction(transaction);
        } catch (Exception e) {
            String msg = "readChallengeTransaction failed: ".concat(Emoji.ERROR) + e.getMessage();
            LOGGER.info(Emoji.ERROR + msg);
            e.printStackTrace();
            throw new Exception(msg);
        }
        String clientAccountId = challengeTransaction.getClientAccountId();
        LOGGER.info(Emoji.ICE_CREAM + Emoji.ICE_CREAM + "clientAccountId from challenge transaction: ".concat(clientAccountId));
        AccountResponse accountResponse = null;
        try {
            accountResponse = server.accounts().account(clientAccountId);
            LOGGER.info(Emoji.PEACH + "Stellar returns client accountResponse: "
                    .concat(accountResponse.getAccountId()).concat(" ")
                    .concat(Emoji.PEACH).concat(Emoji.PEACH));
        } catch (Exception e) {
            LOGGER.info(Emoji.ERROR + "Failed to get Stellar accountResponse ".concat(Emoji.ERROR));
            e.printStackTrace();
            LOGGER.severe(Emoji.ERROR + "getToken: Stellar Client Account not found");
        }
        if (accountResponse == null) {
            throw new Exception(Emoji.ERROR + "getToken: Stellar account not found");
        }
        Set<String> signers = new HashSet<>();
        Set<AccountResponse.Signer> mSigners = new HashSet<>();
        for (AccountResponse.Signer signer : accountResponse.getSigners()) {
            signers.add(signer.getKey());
            mSigners.add(signer);
        }
        LOGGER.info(Emoji.BLUE_DOT + "Signers acquired - start verification ...".concat(Emoji.BLUE_BIRD + Emoji.BLUE_BIRD));
        try {
            LOGGER.info(Emoji.PIG + Emoji.PIG + " ....... verifyChallengeTransactionSigners .... "
            + Emoji.PANDA);
            verifyChallengeTransactionSigners(transaction, signers);
        } catch (Exception e) {
            LOGGER.info(Emoji.ERROR + "Failed to verify ChallengeTransactionSigners ".concat(Emoji.ERROR));
            e.printStackTrace();
            throw new Exception(Emoji.ERROR + "getToken: verifyChallengeTransactionSigners failed");
        }

        try {
            verifyChallengeTransactionThreshold(transaction,0, mSigners);
        } catch (Exception e) {
            LOGGER.info(Emoji.ERROR + "Failed to verifyChallengeTransactionThreshold ".concat(Emoji.ERROR));
            e.printStackTrace();
            throw new Exception(Emoji.ERROR + "getToken: verifyChallengeTransactionThreshold failed");
        }
        String token;
        //🌼
        try {
            token = tokenService.createJWToken(UUID.randomUUID().toString(),
                    jwtIssuer, accountResponse.getAccountId(),EXPIRE_AFTER_N_MINUTES);

            //todo - check token claims ...
            Claims claims = tokenService.decodeJWT(token);
            LOGGER.info("\n " + Emoji.FLOWER_YELLOW +
                    "Claims: JWT issuer: " + claims.getIssuer() + "\n " +
                    Emoji.RED_APPLE + "subject: " + claims.getSubject() + "\n \uD83C\uDF3C iat: "
                    + claims.getIssuedAt().toString() + "\n " + Emoji.PEAR +
                    "exp: " + claims.getExpiration().toString());
        } catch (Exception exception){
            LOGGER.info(Emoji.ERROR + "Failed to create TOKEN ".concat(Emoji.ERROR));
            exception.printStackTrace();
            throw new Exception("JWT token creation failed: " + exception.getMessage());
        }

        return token;
    }

    private Anchor anchor;
    /**
     * Reads a SEP 10 challenge transaction and returns the decoded transaction envelope and client account ID contained within.
     * <p>
     * It also verifies that transaction is signed by the server.
     * <p>
     * It does not verify that the transaction has been signed by the client or
     * that any signatures other than the servers on the transaction are valid. Use
     * one of the following functions to completely verify the transaction:
     * {@link AnchorSep10Challenge#verifyChallengeTransactionThreshold(String, int, Set)}
     *
     * @param challengeXdr    SEP-0010 transaction challenge transaction in base64.
     * @return {@link ChallengeTransaction}, the decoded transaction envelope and client account ID contained within.
     * @throws InvalidSep10ChallengeException If the SEP-0010 validation fails, the exception will be thrown.
     * @throws IOException                    If read XDR string fails, the exception will be thrown.
     */
    private ChallengeTransaction readChallengeTransaction(String challengeXdr) throws Exception {
        setServerAndNetwork();
        if (anchor == null) {
            anchor = firebaseService.getAnchorByName(anchorName);
        }
        LOGGER.info(Emoji.FIRE + "readChallengeTransaction: Anchor is ".concat(anchor.getName().concat(" ")).concat(Emoji.FIRE));
        String serverAccountId = anchor.getBaseAccount().getAccountId();
        // decode the received input as a base64-urlencoded XDR representation of Stellar transaction envelope
        Transaction transaction = Transaction.fromEnvelopeXdr(challengeXdr, network);
        LOGGER.info(Emoji.HAPPY + Emoji.HAPPY + "readChallengeTransaction: We have a decoded Transaction, Yay! ".concat(Emoji.FIRE));
        // verify that transaction source account is equal to the server's signing key
        if (!serverAccountId.equals(transaction.getSourceAccount())) {
            throw new InvalidSep10ChallengeException("Transaction source account is not equal to server's account.");
        }

        // verify that transaction sequenceNumber is equal to zero
        if (transaction.getSequenceNumber() != 0L) {
            throw new InvalidSep10ChallengeException("The transaction sequence number should be zero.");
        }

        // verify that transaction has time bounds set, and that current time is between the minimum and maximum bounds.
        if (transaction.getTimeBounds() == null) {
            throw new InvalidSep10ChallengeException("Transaction requires timeBounds.");
        }

        long maxTime = transaction.getTimeBounds().getMaxTime();
        long minTime = transaction.getTimeBounds().getMinTime();
        LOGGER.info(Emoji.FIRE + "readChallengeTransaction: Checking timeBounds, minTime: " + minTime + " maxTime: " + maxTime + " ".concat(Emoji.FIRE));
        if (maxTime == 0L) {
            throw new InvalidSep10ChallengeException("Transaction requires non-infinite timeBounds.");
        }

        long currentTime = System.currentTimeMillis();
        LOGGER.info(Emoji.FIRE + "readChallengeTransaction: Checking timeBounds, currentTime: " + currentTime + " ".concat(Emoji.FIRE));
        if (currentTime < minTime || currentTime > maxTime) {
            throw new InvalidSep10ChallengeException("Transaction is not within range of the specified timeBounds.");
        }

        // verify that transaction contains a single Manage Data operation and its source account is not null
        if (transaction.getOperations().length != 1) {
            throw new InvalidSep10ChallengeException("Transaction requires a single ManageData operation.");
        }
        Operation operation = transaction.getOperations()[0];
        if (!(operation instanceof ManageDataOperation)) {
            throw new InvalidSep10ChallengeException("Operation type should be ManageData.");
        }
        ManageDataOperation manageDataOperation = (ManageDataOperation) operation;

        // verify that transaction envelope has a correct signature by server's signing key
        String clientAccountId = manageDataOperation.getSourceAccount();
        if (clientAccountId == null) {
            throw new InvalidSep10ChallengeException("Operation should have a source account.");
        }

        // verify manage data value
        if (manageDataOperation.getValue().length != 64) {
            throw new InvalidSep10ChallengeException("Random nonce encoded as base64 should be 64 bytes long.");
        }

        BaseEncoding base64Encoding = BaseEncoding.base64();
        byte[] nonce;
        try {
            nonce = base64Encoding.decode(new String(manageDataOperation.getValue()));
        } catch (IllegalArgumentException e) {
            throw new InvalidSep10ChallengeException("Failed to decode random nonce provided in ManageData operation.", e);
        }

        if (nonce.length != 48) {
            throw new InvalidSep10ChallengeException("Random nonce before encoding as base64 should be 48 bytes long.");
        }

        if (!verifyTransactionSignature(transaction, serverAccountId)) {
            throw new InvalidSep10ChallengeException(String.format("Transaction not signed by server: %s.", serverAccountId));
        }
        ChallengeTransaction challengeTransaction = new ChallengeTransaction(transaction,clientAccountId);
        String mm = Emoji.PANDA + Emoji.PANDA + Emoji.BLUE_DISC + Emoji.BLUE_DISC;
        LOGGER.info(mm + "readChallengeTransaction completed. "
                .concat(challengeTransaction.getClientAccountId()));
        return new ChallengeTransaction(transaction, clientAccountId);
    }

    /**
     * Verifies that for a SEP 10 challenge transaction
     * all signatures on the transaction are accounted for. A transaction is
     * verified if it is signed by the server account, and all other signatures
     * match a signer that has been provided as an argument. Additional signers can
     * be provided that do not have a signature, but all signatures must be matched
     * to a signer for verification to succeed. If verification succeeds a list of
     * signers that were found is returned, excluding the server account ID.
     *
     * @param challengeXdr    SEP-0010 transaction challenge transaction in base64.
     * @param signers         The signers of client account.
     * @return a list of signers that were found is returned, excluding the server account ID.
     * @throws InvalidSep10ChallengeException If the SEP-0010 validation fails, the exception will be thrown.
     * @throws IOException                    If read XDR string fails, the exception will be thrown.
     */
    private Set<String> verifyChallengeTransactionSigners(
            String challengeXdr, Set<String> signers) throws Exception {
        if (signers == null || signers.isEmpty()) {
            throw new InvalidSep10ChallengeException("No verifiable signers provided, at least one G... address must be provided.");
        }
        LOGGER.info(Emoji.FIRE + ".... verify ChallengeTransactionSigners ...".concat(Emoji.FIRE));
        if (anchor == null) {
             anchor = firebaseService.getAnchorByName(anchorName);
        }
        String serverAccountId = anchor.getBaseAccount().getAccountId();
        // Read the transaction which validates its structure.
        ChallengeTransaction parsedChallengeTransaction = readChallengeTransaction(challengeXdr);
        Transaction transaction = parsedChallengeTransaction.getTransaction();
        // Ensure the server account ID is an address and not a seed.
        KeyPair serverKeyPair = KeyPair.fromAccountId(serverAccountId);

        // Deduplicate the client signers and ensure the server is not included
        // anywhere we check or output the list of signers.
        Set<String> clientSigners = new HashSet<>();
        for (String signer : signers) {
            // Ignore non-G... account/address signers.
            StrKey.VersionByte versionByte;
            try {
                versionByte = StrKey.decodeVersionByte(signer);
            } catch (Exception e) {
                continue;
            }

            if (!StrKey.VersionByte.ACCOUNT_ID.equals(versionByte)) {
                continue;
            }

            // Ignore the server signer if it is in the signers list. It's
            // important when verifying signers of a challenge transaction that we
            // only verify and return client signers. If an account has the server
            // as a signer the server should not play a part in the authentication
            // of the client.
            if (serverKeyPair.getAccountId().equals(signer)) {
                continue;
            }
            clientSigners.add(signer);
        }

        // Don't continue if none of the signers provided are in the final list.
        if (clientSigners.isEmpty()) {
            throw new InvalidSep10ChallengeException("No verifiable signers provided, at least one G... address must be provided.");
        }

        // Verify all the transaction's signers (server and client) in one
        // hit. We do this in one hit here even though the server signature was
        // checked in the readChallengeTx to ensure that every signature and signer
        // are consumed only once on the transaction.
        Set<String> allSigners = new HashSet<>(clientSigners);
        allSigners.add(serverKeyPair.getAccountId());
        Set<String> signersFound = verifyTransactionSignatures(transaction, allSigners);

        // Confirm the server is in the list of signers found and remove it.
        boolean serverSignerFound = signersFound.remove(serverKeyPair.getAccountId());

        // Confirm we matched a signature to the server signer.
        if (!serverSignerFound) {
            throw new InvalidSep10ChallengeException(String.format("Transaction not signed by server: %s.", serverAccountId));
        }
        LOGGER.info(Emoji.LEMON + Emoji.LEMON + "Transaction signed by server ... cool! ".concat(Emoji.FIRE));
        //todo - un-comment code to check for client signature ....
        // Confirm we matched signatures to the client signers.
//        if (signersFound.isEmpty()) {
//            throw new InvalidSep10ChallengeException(Emoji.ERROR + "Transaction not signed by any client signer.");
//        }
//
//        // Confirm all signatures were consumed by a signer.
//        if (signersFound.size() != transaction.getSignatures().size() - 1) {
//            throw new InvalidSep10ChallengeException("Transaction has unrecognized signatures.");
//        }
        String em = Emoji.LEAF + Emoji.LEAF + Emoji.LEAF + Emoji.LEAF;
                LOGGER.info(em + "..... verifyChallengeTransactionSigners completed. Signers: "
                .concat("" +signers.size()));
        return signersFound;
    }

    /**
     * Verifies that for a SEP-0010 challenge transaction
     * all signatures on the transaction are accounted for and that the signatures
     * meet a threshold on an account. A transaction is verified if it is signed by
     * the server account, and all other signatures match a signer that has been
     * provided as an argument, and those signatures meet a threshold on the
     * account.
     *
     * @param challengeXdr    SEP-0010 transaction challenge transaction in base64.
     * @param threshold       The threshold on the client account.
     * @param signers         The signers of client account.
     * @return a list of signers that were found is returned, excluding the server account ID.
     * @throws InvalidSep10ChallengeException If the SEP-0010 validation fails, the exception will be thrown.
     * @throws IOException                    If read XDR string fails, the exception will be thrown.
     */
    private Set<String> verifyChallengeTransactionThreshold(
            String challengeXdr,
            int threshold, Set<AccountResponse.Signer> signers) throws Exception {
        setServerAndNetwork();
        LOGGER.info(Emoji.PANDA + Emoji.PANDA +
                " ....... verifyChallengeTransactionThreshold: ... ".concat(Emoji.PANDA));
        if (signers == null || signers.isEmpty()) {
            throw new InvalidSep10ChallengeException("No verifiable signers provided, at least one G... address must be provided.");
        }

        Map<String, Integer> weightsForSigner = new HashMap<>();
        for (AccountResponse.Signer signer : signers) {
            weightsForSigner.put(signer.getKey(), signer.getWeight());
        }

        Set<String> signersFound = verifyChallengeTransactionSigners(challengeXdr,  weightsForSigner.keySet());

        int sum = 0;
        for (String signer : signersFound) {
            Integer weight = weightsForSigner.get(signer);
            if (weight != null) {
                sum += weight;
            }
        }

        if (sum < threshold) {
            throw new InvalidSep10ChallengeException(String.format("Signers with weight %d do not meet threshold %d.", sum, threshold));
        }
        LOGGER.info(Emoji.PANDA + Emoji.PANDA +" ... verifyChallengeTransactionThreshold completed. Signers: "
                .concat("" +signersFound.size()));
        return signersFound;
    }

    private Set<String> verifyTransactionSignatures(
            Transaction transaction, Set<String> signers) throws Exception {
        if (transaction.getSignatures().isEmpty()) {
            throw new InvalidSep10ChallengeException("Transaction has no signatures.");
        }

        byte[] txHash = transaction.hash();

        // find and verify signatures
        Set<String> signersFound = new HashSet<String>();
        Multimap<SignatureHint, Signature> signatures = HashMultimap.create();
        for (DecoratedSignature decoratedSignature : transaction.getSignatures()) {
            signatures.put(decoratedSignature.getHint(), decoratedSignature.getSignature());
        }

        for (String signer : signers) {
            KeyPair keyPair = KeyPair.fromAccountId(signer);
            SignatureHint hint = keyPair.getSignatureHint();

            for (Signature signature : signatures.get(hint)) {
                if (keyPair.verify(txHash, signature.getSignature())) {
                    signersFound.add(signer);
                    // explicitly ensure that a transaction signature cannot be
                    // mapped to more than one signer
                    signatures.remove(hint, signature);
                    break;
                }
            }
        }
        String mm = Emoji.ALIEN + Emoji.ALIEN + Emoji.ALIEN + emm;
        LOGGER.info(mm +"verifyTransactionSignatures completed. Signers: "
                .concat("" +signers.size()));
        return signersFound;
    }

    private boolean verifyTransactionSignature(Transaction transaction, String accountId) throws Exception {
        return !verifyTransactionSignatures(transaction, Collections.singleton(accountId)).isEmpty();
    }

    private void setServerAndNetwork() {
        if (server != null) {
            return;
        }
        if (status == null) {
            LOGGER.info("\uD83D\uDE08 \uD83D\uDC7F Set status to dev because status is NULL");
            status = "dev";
        }
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
    //static classes .....................
    /**
     * Used to store the results produced by {@link AnchorSep10Challenge#readChallengeTransaction(String)}.
     */
    public static class ChallengeTransaction {
        private final Transaction transaction;
        private final String clientAccountId;

        public ChallengeTransaction(Transaction transaction, String clientAccountId) {
            this.transaction = transaction;
            this.clientAccountId = clientAccountId;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public String getClientAccountId() {
            return clientAccountId;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.transaction, this.clientAccountId);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }

            if (!(object instanceof ChallengeTransaction)) {
                return false;
            }

            ChallengeTransaction other = (ChallengeTransaction) object;
            return Objects.equal(this.transaction, other.transaction) &&
                    Objects.equal(this.clientAccountId, other.clientAccountId);
        }
    }

    /**
     * Represents a transaction signer.
     */
    public static class Signer {
        private final String key;
        private final int weight;

        public Signer(String key, int weight) {
            this.key = key;
            this.weight = weight;
        }

        public String getKey() {
            return key;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.key, this.weight);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }

            if (!(object instanceof Signer)) {
                return false;
            }

            Signer other = (Signer) object;
            return Objects.equal(this.key, other.key) &&
                    Objects.equal(this.weight, other.weight);
        }
    }
}
