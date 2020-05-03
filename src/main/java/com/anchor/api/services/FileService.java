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
public class FileService {
    public static final Logger LOGGER = Logger.getLogger(FileService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Value("${projectId}")
    private String projectId;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${locationId}")
    private String locationId;

    @Value("${keyRingId}")
    private String keyRingId;

    public FileService() {
        LOGGER.info(Emoji.RED_CAR.concat(Emoji.RED_CAR)
        .concat("FileService for client files up and running \uD83D\uDE21"));
    }
    public static final String
            DOWNLOAD_PATH = "downloaded_file",
            UPLOAD_PATH = "upload_file",
            TYPE_ID_FRONT = "id_front",
            TYPE_ID_BACK = "id_back",
            TYPE_SELFIE = "selfie",
            TYPE_PROOF_OF_RESIDENCE = "proof_residence";

    public void uploadIDFront(String id, File file) throws Exception {
        uploadFile(id, file, TYPE_ID_FRONT);
    }
    public void uploadIDBack(String id, File file) throws Exception {
        uploadFile(id, file, TYPE_ID_BACK);
    }
    public void uploadProofOfResidence(String id, File file) throws Exception {
        uploadFile(id, file, TYPE_PROOF_OF_RESIDENCE);
    }
    public void uploadSelfie(String id, File file) throws Exception {
        uploadFile(id, file, TYPE_SELFIE);
    }
    private void uploadFile(String id, File file, String type) throws IOException {

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
                .build().getService();
        BlobId blobId = BlobId.of(bucketName, type +"_".concat(id));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        Files.delete(Paths.get(file.getAbsolutePath()));
        LOGGER.info(
                "... \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Yebo!! \uD83C\uDF4E " +
                        " File " + file.getAbsolutePath() + " uploaded to \uD83C\uDF3C " +
                        "bucket " + bucketName + " \uD83C\uDF3C as " + type);
    }
    public File downloadIDFront(String id) throws Exception {
        return downloadFile(id, TYPE_ID_FRONT);
    }
    public File downloadIDBack(String id) throws Exception {
        return downloadFile(id, TYPE_ID_BACK);
    }
    public File downloadProofOfResidence(String id) throws Exception {
        return downloadFile(id, TYPE_PROOF_OF_RESIDENCE);
    }
    public File downloadSelfie(String id) throws Exception {
        return downloadFile(id, TYPE_SELFIE);
    }
    private File downloadFile(String id, String type) throws Exception {
        LOGGER.info(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD).concat(" .... about to download file for: "
                .concat(id).concat(" bucket: ").concat(bucketName).concat(Emoji.RED_APPLE)
                .concat(" object: ".concat(type)).concat(" ").concat(Emoji.RED_APPLE)));
        Path destFilePath = Paths.get(DOWNLOAD_PATH.concat(id));
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Blob blob = storage.get(BlobId.of(bucketName, type.concat("_").concat(id)));
        if (blob == null) {
            LOGGER.info(Emoji.NOT_OK.concat(Emoji.NOT_OK).concat("Blob for downloading is fucking NULL? WTF?"));
            throw new Exception(Emoji.NOT_OK + "KMS Blob for downloading is fucking NULL? WTF?");
        }
        blob.downloadTo(destFilePath);
        LOGGER.info(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD)
                .concat(" File downloaded from cloud storage: ")
                .concat(" length: " + destFilePath.toFile().length()));
        return destFilePath.toFile();
    }
}
