package com.anchor.api.util;

import com.google.cloud.kms.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

@Service
public class Crypto {

    public Crypto() {
        LOGGER.info("\uD83C\uDFB2 \uD83C\uDFB2 Crypto Service, constructor fired ... \uD83C\uDFB2");
    }

    public static final Logger LOGGER = Logger.getLogger(Crypto.class.getSimpleName());
    public static final String PROJECT_ID = "stellar-stokvel", LOCATION_ID = "global";
    public static final String KEYRING_NAME = "keyring33", CRYPTO_KEY_NAME = "key33";

    public void createDefaults() throws IOException {
//        createKeyRing(KEYRING_NAME);
//        createCryptoKey(KEYRING_NAME,CRYPTO_KEY_NAME);
        LOGGER.info("Crypto: \uD83C\uDF3C \uD83C\uDF3C createDefaults starting ....");
        String testString = "This is the string that I need to encrypt";
        byte[] bytes1 = encrypt(KEYRING_NAME,CRYPTO_KEY_NAME,testString.getBytes());
        byte[] bytes2 = decrypt(KEYRING_NAME,CRYPTO_KEY_NAME,bytes1);
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Decrypted bytes: \uD83C\uDF3C " + Arrays.toString(bytes2));
    }
    /** Creates a new key ring with the given id. */
    public  KeyRing createKeyRing(String keyRingName)
            throws IOException {
        LOGGER.info("\uD83C\uDFBD Create the Cloud KMS client ..... keyRingName: " + keyRingName);
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String parent = LocationName.format(PROJECT_ID, LOCATION_ID);
            LOGGER.info("The resource name of the location associated with the KeyRing." + parent);
            KeyRing keyRing = client.createKeyRing(parent, keyRingName, KeyRing.newBuilder().build());
            LOGGER.info("KeyRing Name: " +keyRing.getName());
            return keyRing;
        }
    }

    /** Creates a new crypto key with the given id. */
    public  CryptoKey createCryptoKey(
            String keyRingName, String cryptoKeyName) throws IOException {

        LOGGER.info("\uD83C\uDFBD Create the Cloud KMS client .....  \uD83C\uDF51 keyRingName: "
                + keyRingName + " cryptoKeyName: " + cryptoKeyName);
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String parent = KeyRingName.format(PROJECT_ID, LOCATION_ID, keyRingName);
            LOGGER.info("\uD83C\uDFBD The resource name of the location associated with the KeyRing." + parent);
            // This will allow the API access to the key for encryption and decryption.
            CryptoKey cryptoKey =
                    CryptoKey.newBuilder().setPurpose(CryptoKey.CryptoKeyPurpose.ENCRYPT_DECRYPT).build();

            // Create the CryptoKey for your project.
            CryptoKey createdKey = client.createCryptoKey(parent, cryptoKeyName, cryptoKey);
            LOGGER.info("\uD83C\uDFBD CryptoKey: createdKey: " + createdKey.getName()
                    + " \uD83D\uDC99 rotationPeriod(seconds): " + createdKey.getRotationPeriod().getSeconds());

            return createdKey;
        }
    }
    /** Encrypts the given plaintext using the specified crypto key. */
    public  byte[] encrypt(
            String keyRingName, String cryptoKeyName, byte[] plaintext) {

        byte[] bytes = null;
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String resourceName = CryptoKeyPathName.format(PROJECT_ID, LOCATION_ID, keyRingName, cryptoKeyName);
            LOGGER.info("\uD83E\uDD4F Encrypt the plaintext with Cloud KMS:resourceName: \uD83E\uDD4F " + resourceName);
            EncryptResponse response = client.encrypt(resourceName, ByteString.copyFrom(plaintext));
            LOGGER.info("\uD83E\uDD4F Encrypt the plaintext with Cloud KMS:resourceName: \uD83E\uDD4F Do we get here?");
            // Extract the cipherText from the response.
            bytes = response.getCiphertext().toByteArray();
            LOGGER.info("\uD83E\uDD4F Encrypted bytes: " + bytes.toString());
            return  bytes;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("\uD83D\uDC67\uD83C\uDFFE \uD83D\uDC67\uD83C\uDFFE \uD83D\uDC7F We fucked, Joe! \uD83D\uDC7F");
        }
        return bytes;
    }

    /** Decrypts the provided cipherText with the specified crypto key. */
    public  byte[] decrypt(
            String keyRingId, String cryptoKeyId, byte[] cipherText)
            throws IOException {

        // Create the KeyManagementServiceClient using try-with-resources to manage client cleanup.
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String resourceName = CryptoKeyPathName.format(PROJECT_ID, LOCATION_ID, keyRingId, cryptoKeyId);
            LOGGER.info("\uD83E\uDD4F Decrypt the plaintext with Cloud KMS:resourceName: \uD83E\uDD4F " + resourceName);
            DecryptResponse response = client.decrypt(resourceName, ByteString.copyFrom(cipherText));
            byte[] bytes = response.getPlaintext().toByteArray();
            LOGGER.info("\uD83E\uDD4F Decrypted bytes: " + bytes.toString());
            return  bytes;
        }
    }
}
