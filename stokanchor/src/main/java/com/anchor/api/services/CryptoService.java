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
            return createdKey.getName();
        }
    }

    /** Encrypts the given plaintext using the specified crypto key. */
    public byte[] encrypt(String stringToEncrypt)
            throws IOException {
        LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E Encrypt starting ...... \uD83C\uDF4E ".concat(stringToEncrypt));
        byte[] stringToEncryptBytes = stringToEncrypt.getBytes();
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String resourceName = CryptoKeyPathName.format(projectId, locationId, keyRingId, cryptoKeyId);
            LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 resourceName: ".concat(resourceName));
            EncryptResponse response = client.encrypt(resourceName, ByteString.copyFrom(stringToEncryptBytes));
            String content = Arrays.toString(response.getCiphertext().toByteArray());
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E Encrypted string: ".concat(response.getCiphertext().toByteArray().toString()));
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E Encrypted bytes: ".concat(content));
            writeFile(response.getCiphertext().toByteArray());
            uploadSeedFile();

            //todo - remove after test
            LOGGER.info(".................. download the file and check to see if decrypted seed is retrieved from file ..................");
            downloadSeedFile();
            byte[] mBytes = readFile();
            String seed = decrypt(mBytes);
            LOGGER.info(("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C byte[] Decrypted " +
                    "\uD83D\uDD35 seed: ").concat(seed).concat(" \uD83D\uDD35 "));

            return response.getCiphertext().toByteArray();
        }
    }

    /** Decrypts the provided ciphertext with the specified crypto key. */
    public String decrypt(byte[] encryptedStringBytes )
            throws IOException {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 .............. decrypting byte[] ............ \uD83C\uDF4E ");
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String resourceName = CryptoKeyPathName.format(projectId, locationId, keyRingId, cryptoKeyId);
            DecryptResponse response = client.decrypt(resourceName, ByteString.copyFrom(encryptedStringBytes));
            LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 \uD83D\uDD11 BYTE[] DecryptResponse response: \uD83E\uDD4F ".concat(response.getPlaintext().toStringUtf8())
            .concat(" \uD83E\uDD4F "));
            return response.getPlaintext().toStringUtf8();
        }
    }

    @Value("${bucketName}")
    private String bucketName;
    @Value("${objectName}")
    private String objectName;
    public static final String FILE_PATH = "encrypted_seed";


    public void writeFile(byte[] encryptedSeed)
            throws IOException {
        LOGGER.info(("\uD83C\uDF3C \uD83C\uDF3C Writing crypto key to file " +
                ".... \uD83C\uDF3C ").concat(FILE_PATH));
        Path path = Paths.get(FILE_PATH);
        Files.write(path, encryptedSeed);
        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 File written with encryptedSeed: "
                .concat(" path: ").concat(path.toString()));
    }
    public byte[] readFile()
            throws IOException {
        LOGGER.info(("\uD83C\uDF3C \uD83C\uDF3C .... Reading crypto key from file " +
                ".... \uD83C\uDF3C ").concat(DOWNLOAD_PATH));
        Path path = Paths.get(DOWNLOAD_PATH);
        byte[] read = Files.readAllBytes(path);
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C " + read.length + " bytes read from file: " +
                " \uD83D\uDD35 \uD83D\uDD35 read: \n".concat(Arrays.toString(read)));
        return read;
    }

    public  void uploadSeedFile() throws IOException {
        LOGGER.info(("\uD83C\uDF3C \uD83C\uDF3C .................... Uploading crypto key to Cloud Storage " +
                ".... \uD83C\uDF3C path: ").concat(FILE_PATH));

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
                .build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        storage.create(blobInfo, Files.readAllBytes(Paths.get(FILE_PATH)));
        LOGGER.info(
                "... \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Yebo!! \uD83C\uDF4E " +
                        "File " + FILE_PATH + " uploaded to \uD83C\uDF3C " +
                        "bucket " + bucketName + " \uD83C\uDF3C as " + objectName);
    }
    public static final String DOWNLOAD_PATH = "downloaded_seed";
    public void downloadSeedFile() {
        LOGGER.info(("\uD83C\uDF3C \uD83C\uDF3C Uploading crypto key file to Cloud Storage " +
                ".... \uD83C\uDF3C to path: ").concat(DOWNLOAD_PATH));
        Path destFilePath = Paths.get(DOWNLOAD_PATH);
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        blob.downloadTo(destFilePath);

        LOGGER.info(("\uD83C\uDF3C \uD83C\uDF3C " +
                "Downloaded Seed File: \uD83C\uDF4E "
                        + objectName
                        + " from bucket name \uD83E\uDD66 "
                        + bucketName
                        + " to path: "
                        + destFilePath));
    }
}
