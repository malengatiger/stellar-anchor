package com.anchor.api.controllers;

import com.anchor.api.services.FileService;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@RestController
public class ClientController {
    public static final Logger LOGGER = Logger.getLogger(AnchorController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/uploadID", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public String uploadID(@RequestParam("id") String id,
                           @RequestParam("idFront") MultipartFile idFront,
                           @RequestParam("idBack") MultipartFile idBack) throws Exception {

        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "ClientController:uploadID...");
        // Front of ID Card
        byte[] idFrontBytes = idFront.getBytes();
        File idFrontFile = new File("file_" + System.currentTimeMillis());
        Path idFrontPath = Paths.get(idFrontFile.getAbsolutePath());
        Files.write(idFrontPath, idFrontBytes);
        LOGGER.info("....... idFront file received: \uD83C\uDFBD "
                .concat(" length: " + idFrontFile.length() + " idFrontBytes"));
        fileService.uploadIDFront(id, idFrontFile);

        // Back of ID Card
        byte[] idBackBytes = idBack.getBytes();
        File idBackFile = new File("file_" + System.currentTimeMillis());
        Path idBackPath = Paths.get(idBackFile.getAbsolutePath());
        Files.write(idBackPath, idBackBytes);
        LOGGER.info("....... idBack file received: \uD83C\uDFBD "
                .concat(" length: " + idBackFile.length() + " idFrontBytes"));
        fileService.uploadIDBack(id, idBackFile);

        try {
            Files.delete(idFrontPath);
            Files.delete(idBackPath);
        } catch (Exception e) {
           LOGGER.info("Unable to delete uploaded file \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21");
        }
        String msg = Emoji.HAND2.concat("ID Documents have been uploaded");
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!");
        return msg;
    }

    @PostMapping(value = "/uploadProofOfResidence", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public String uploadProofOfResidence(@RequestParam("id") String id,
                           @RequestParam("proofOfResidence") MultipartFile proofOfResidence) throws Exception {

        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "ClientController:uploadProofOfResidence...");
        // Front of ID Card
        byte[] proofOfResidenceBytes = proofOfResidence.getBytes();
        File proofFile = new File("file_" + System.currentTimeMillis());
        Path proofPath = Paths.get(proofFile.getAbsolutePath());
        Files.write(proofPath, proofOfResidenceBytes);
        LOGGER.info("....... proofOfResidence file received: \uD83C\uDFBD "
                .concat(" length: " + proofFile.length() + " proofOfResidenceBytes"));

        fileService.uploadProofOfResidence(id, proofFile);
        Files.delete(proofPath);

        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!");
        return Emoji.HAND2.concat("Proof of Residence document has been uploaded");
    }
    @GetMapping(value = "/downloadProofOfResidence", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadProofOfResidence(@RequestParam("id") String id) throws Exception {

        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "ClientController:downloadProofOfResidence...");
        // Front of ID Card
        File file = fileService.downloadProofOfResidence(id);
        LOGGER.info("....... proofOfResidence file received: \uD83C\uDFBD "
                .concat(" length: " + file.length() + " file"));
        return Files.readAllBytes(file.toPath());
    }
    @GetMapping(value = "/downloadIDFront", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadIDFront(@RequestParam("id") String id) throws Exception {

        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "ClientController:downloadIDFront...");
        // Front of ID Card
        File file = fileService.downloadIDFront(id);
        LOGGER.info("....... idFront file downloaded: \uD83C\uDFBD "
                .concat(" length: " + file.length() + " file"));

        byte[] bytes = Files.readAllBytes(file.toPath());
        Files.delete(file.toPath());
        return bytes;
    }
    @GetMapping(value = "/downloadIDBack", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadIDBack(@RequestParam("id") String id) throws Exception {

        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "ClientController:downloadIDBack...");
        // Back of ID Card
        File file = fileService.downloadIDBack(id);
        LOGGER.info("....... idFront file downloaded: \uD83C\uDFBD "
                .concat(" length: " + file.length() + " file"));

        byte[] bytes = Files.readAllBytes(file.toPath());
        Files.delete(file.toPath());
        return bytes;
    }
}
