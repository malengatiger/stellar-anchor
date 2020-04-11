package com.anchor.api.services;

import com.google.cloud.kms.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CryptoService {
    public CryptoService() {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 CryptoService; \uD83D\uDD11 ... to hide shit ... \uD83D\uDD11");
    }

    private KeyManagementServiceClient client;
    public static final Logger LOGGER = LoggerFactory.getLogger(CryptoService.class);
    private void listKeyRings() throws IOException {
        LOGGER.info("Running setup .... \uD83E\uDD4F \uD83E\uDD4F \uD83E\uDD4F");
        // Create the KeyManagementServiceClient using try-with-resources to manage client cleanup.
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the location to search
            String locationPath = LocationName.format(projectId, locationId);
            // Make the RPC call
            KeyManagementServiceClient.ListKeyRingsPagedResponse response = client.listKeyRings(locationPath);
            // Iterate over all KeyRings (which may cause more result pages to be loaded automatically)
            for (KeyRing keyRing : response.iterateAll()) {
                LOGGER.info("\uD83C\uDF4F \uD83C\uDF4E Found KeyRing: " + keyRing.getName());
            }
        }
    }


    @Value("${locationId}")
    private String locationId;
    @Value("${projectId}")
    private String projectId;

    /**
     *  Creates a new key ring with the given id
      * @param keyRingId
     * @return
     * @throws IOException
     */
    public KeyRing createKeyRing(String keyRingId)
            throws IOException {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 \uD83D\uDD11 createKeyRing: ".concat(keyRingId));
        listKeyRings();
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the location associated with the KeyRing.
            String parent = LocationName.format(projectId, locationId);
            // Create the KeyRing for your project.
            KeyRing keyRing = client.createKeyRing(parent, keyRingId, KeyRing.newBuilder().build());
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E KeyRing created: ".concat(keyRing.getName()));

            return keyRing;
        }
    }

    /** Creates a new crypto key with the given id. */
    public  CryptoKey createCryptoKey(String keyRingId, String cryptoKeyId)
            throws IOException {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 \uD83D\uDD11 createCryptoKey: ".concat(keyRingId));
        // Create the Cloud KMS client.
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the location associated with the KeyRing.
            String parent = KeyRingName.format(projectId, locationId, keyRingId);
            // This will allow the API access to the key for encryption and decryption.
            CryptoKey cryptoKey =
                    CryptoKey.newBuilder().setPurpose(CryptoKey.CryptoKeyPurpose.ENCRYPT_DECRYPT).build();
            // Create the CryptoKey for your project.
            CryptoKey createdKey = client.createCryptoKey(parent, cryptoKeyId, cryptoKey);
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E CryptoKey created: ".concat(createdKey.getName()));
            return createdKey;
        }
    }
    /*
        gcloud kms encrypt --location global \
      --keyring test --key quickstart \
      --plaintext-file mysecret.txt \
      --ciphertext-file mysecret.txt.encrypted
     */

    /** Encrypts the given plaintext using the specified crypto key. */
    public byte[] encrypt(
            String keyRingId, String cryptoKeyId, String stringToEncrypt)
            throws IOException {
        LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E Encrypt starting ...... \uD83C\uDF4E ".concat(stringToEncrypt));
        byte[] plaintext = stringToEncrypt.getBytes();
        // Create the KeyManagementServiceClient using try-with-resources to manage client cleanup.
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the cryptoKey
            String resourceName = CryptoKeyPathName.format(projectId, locationId, keyRingId, cryptoKeyId);
            // Encrypt the plaintext with Cloud KMS.
            EncryptResponse response = client.encrypt(resourceName, ByteString.copyFrom(plaintext));
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E Encrypted: ".concat(response.getCiphertext().toString()));
            //todo - remove after test
            decrypt(keyRingId,cryptoKeyId,response.getCiphertext().toString());
            return response.getCiphertext().toByteArray();
        }
    }
    /*
    gcloud kms decrypt \
  --location global \
  --keyring test \
  --key quickstart \
  --ciphertext-file mysecret.txt.encrypted \
  --plaintext-file mysecret.txt.decrypted
     */

    /** Decrypts the provided ciphertext with the specified crypto key. */
    public  byte[] decrypt(
            String keyRingId, String cryptoKeyId, String encryptedString)
            throws IOException {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 decrypting ... : \uD83C\uDF4E ".concat(encryptedString));
        byte[] encryptedStringBytes = encryptedString.getBytes();
        // Create the KeyManagementServiceClient using try-with-resources to manage client cleanup.
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the cryptoKey
            String resourceName = CryptoKeyPathName.format(projectId, locationId, keyRingId, cryptoKeyId);
            // Decrypt the encryptedStringBytes with Cloud KMS.
            DecryptResponse response = client.decrypt(resourceName, ByteString.copyFrom(encryptedStringBytes));
            LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 \uD83D\uDD11 DecryptResponse response: ".concat(response.getPlaintext().toString()));
            // Extract the plaintext from the response.
            return response.getPlaintext().toByteArray();
        }
    }
}
