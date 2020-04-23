package com.anchor.api.services;

import com.anchor.api.util.Emoji;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Service
public class TOMLService {
    public static final Logger LOGGER = Logger.getLogger(TOMLService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private CryptoService cryptoService;

    @Value("${tomlObject}")
    private String tomlObject;

    @Value("${projectId}")
    private String projectId;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${locationId}")
    private String locationId;

    @Value("${keyRingId}")
    private String keyRingId;

    @Value("${cryptoKeyId}")
    private String cryptoKeyId;

    private Toml toml;

    public TOMLService() {
        LOGGER.info(Emoji.DOG.concat(Emoji.DOG)
        .concat("Service for TOML up and running \uD83D\uDE21"));
    }

    public byte[] encryptAndUploadFile(String anchorId, File file) throws IOException {
        LOGGER.info(Emoji.HASH.concat(Emoji.HASH).concat("encryptAndUploadFile for ".concat(anchorId)));
        Path path = Paths.get(file.getAbsolutePath());
        byte[] bytes = Files.readAllBytes(path);
        byte[] encrypted = cryptoService.encrypt(anchorId, new String(bytes));
        LOGGER.info(Emoji.SKULL.concat(Emoji.SKULL.concat("File has been encrypted")));
        return encrypted;
    }



    public Toml getToml(String anchorId) throws Exception {
        if (toml != null) {
            return toml;
        }
        String data = cryptoService.getDecryptedSeed(anchorId);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat("data: ".concat(data)));
        Path mPath = Files.write(Paths.get(DOWNLOAD_PATH.concat(anchorId)),data.getBytes());
        toml = new Toml().read(mPath.toFile());
        return toml;
    }
    public static final String DOWNLOAD_PATH = "downloaded_toml", UPLOAD_PATH = "upload_toml";


    public void uploadTOMLFile(String anchorId) throws IOException {
        File file = new File(UPLOAD_PATH.concat(anchorId));
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
                .build().getService();
        BlobId blobId = BlobId.of(bucketName, tomlObject +"_".concat(anchorId));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        Files.delete(Paths.get(file.getAbsolutePath()));
        LOGGER.info(
                "... \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Yebo!! \uD83C\uDF4E " +
                        "Encrypted TOML File " + file.getAbsolutePath() + " uploaded to \uD83C\uDF3C " +
                        "bucket " + bucketName + " \uD83C\uDF3C as " + tomlObject);
    }
}
