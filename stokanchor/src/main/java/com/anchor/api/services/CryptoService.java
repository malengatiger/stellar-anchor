package com.anchor.api.services;

import com.google.cloud.kms.v1.*;
import com.google.cloud.storage.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class CryptoService {
    public CryptoService() {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 CryptoService; \uD83D\uDD11 ... to hide shit ... \uD83D\uDD11");
    }

    private KeyManagementServiceClient client;
    public static final Logger LOGGER = LoggerFactory.getLogger(CryptoService.class);
    private void listKeyRings() throws IOException {
        // Create the KeyManagementServiceClient using try-with-resources to manage client cleanup.
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the location to search
            String locationPath = LocationName.format(projectId, locationId);
            // Make the RPC call
            KeyManagementServiceClient.ListKeyRingsPagedResponse response = client.listKeyRings(locationPath);
            // Iterate over all KeyRings (which may cause more result pages to be loaded automatically)
            for (KeyRing keyRing : response.iterateAll()) {
                LOGGER.info("\uD83C\uDF4F \uD83C\uDF4E Found KMS KeyRing: " + keyRing.getName());
            }
        }
    }


    @Value("${locationId}")
    private String locationId;
    @Value("${projectId}")
    private String projectId;
    @Value("${keyRingId}")
    private String keyRingId;
    @Value("${cryptoKeyId}")
    private String cryptoKeyId;
    /**
     *  Creates a new key ring with the given id
     * @return
     * @throws IOException
     */
    public String createKeyRing()
            throws IOException {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 \uD83D\uDD11 createKeyRing: ".concat(keyRingId));
        listKeyRings();
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the location associated with the KeyRing.
            String parent = LocationName.format(projectId, locationId);
            // Create the KeyRing for your project.
            KeyRing keyRing = client.createKeyRing(parent, keyRingId, KeyRing.newBuilder().build());
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E KeyRing created: ".concat(keyRing.getName()));

            return keyRing.getName();
        }
    }

    /** Creates a new crypto key with the given id. */
    public String createCryptoKey()
            throws IOException {
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
            return createdKey.getName();
        }
    }

    /** Encrypts the given plaintext using the specified crypto key. */
    public byte[] encrypt(String accountId, String stringToEncrypt)
            throws IOException {
        byte[] stringToEncryptBytes = stringToEncrypt.getBytes();
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String resourceName = CryptoKeyPathName.format(projectId, locationId, keyRingId, cryptoKeyId);
            EncryptResponse response = client.encrypt(resourceName, ByteString.copyFrom(stringToEncryptBytes));
            writeFile(accountId,response.getCiphertext().toByteArray());
            uploadSeedFile(accountId);

            return response.getCiphertext().toByteArray();
        }
    }

    /** Decrypts the provided ciphertext with the specified crypto key. */
    public String decrypt(byte[] encryptedStringBytes )
            throws IOException {
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String resourceName = CryptoKeyPathName.format(projectId, locationId, keyRingId, cryptoKeyId);
            DecryptResponse response = client.decrypt(resourceName, ByteString.copyFrom(encryptedStringBytes));
            return response.getPlaintext().toStringUtf8();
        }
    }

    @Value("${bucketName}")
    private String bucketName;
    @Value("${objectName}")
    private String objectName;
    public static final String FILE_PATH = "encrypted_seed";


    public void writeFile(String accountId, byte[] encryptedSeed)
            throws IOException {

        Path path = Paths.get(FILE_PATH.concat(accountId));
        Files.write(path, encryptedSeed);

    }
    public byte[] readFile(String accountId)
            throws IOException {

        Path path = Paths.get(DOWNLOAD_PATH.concat(accountId));
        byte[] read = Files.readAllBytes(path);
        Files.delete(path);
        return read;
    }

    public void uploadSeedFile(String accountId) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
                .build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName.concat("_").concat(accountId));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        storage.create(blobInfo, Files.readAllBytes(Paths.get(FILE_PATH.concat(accountId))));
        Files.delete(Paths.get(FILE_PATH.concat(accountId)));
        LOGGER.info(
                "... \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Yebo!! \uD83C\uDF4E " +
                        "File " + FILE_PATH.concat(accountId) + " uploaded to \uD83C\uDF3C " +
                        "bucket " + bucketName + " \uD83C\uDF3C as " + objectName);
    }
    public static final String DOWNLOAD_PATH = "downloaded_seed";

    public String getDecryptedSeed(String accountId) throws IOException {
        downloadSeedFile(accountId);
        byte[] bytes = readFile(accountId);
        return decrypt(bytes);
    }
    private void downloadSeedFile(String accountId) {
        Path destFilePath = Paths.get(DOWNLOAD_PATH.concat(accountId));
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Blob blob = storage.get(BlobId.of(bucketName, objectName.concat("_").concat(accountId)));
        blob.downloadTo(destFilePath);
    }
}
