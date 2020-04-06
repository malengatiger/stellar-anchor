package com.anchor.api.services;

import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.info.Info;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class FirebaseService {
    public static final Logger LOGGER = Logger.getLogger(FirebaseService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private ApplicationContext context;
    @Value("${status}")
    private String status;

    public FirebaseService() {
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 FirebaseService Constructor: \uD83D\uDC99 .......");
    }

    public void initializeFirebase() throws Exception {
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Starting Firebase initialization " +
                ".... \uD83D\uDC99 DEV STATUS: \uD83C\uDF51 " + status + " \uD83C\uDF51");

        FirebaseApp app;
        try {
            FirebaseOptions prodOptions = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setDatabaseUrl("https://stellar-anchor-333.firebaseio.com/")
                    .build();

            app = FirebaseApp.initializeApp(prodOptions);
        } catch (Exception e) {
            LOGGER.severe("Unable to initialize Firebase");
            throw new Exception("Unable to initialize Firebase", e);
        }
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Firebase has been set up and initialized. " +
                "\uD83D\uDC99 URL: " + app.getOptions().getDatabaseUrl() + " \uD83D\uDC99");
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Firebase has been set up and initialized. " +
                "\uD83E\uDD66 Name: " + app.getName() + " \uD83E\uDD66 \uD83D\uDC99 \uD83D\uDC99");

        Firestore fs = FirestoreClient.getFirestore();
        int cnt = 0;
        for (CollectionReference listCollection : fs.listCollections()) {
            cnt++;
            LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 Collection: #" +cnt + " \uD83D\uDC99 " + listCollection.getId());
        }
        List<Anchor> list = getAnchors();
        LOGGER.info("\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 " +
                "Firebase Initialization complete; ... anchors found: " + list.size());
    }

    public UserRecord createUser(String name, String email, String password) throws FirebaseAuthException {
        LOGGER.info("createUser: \uD83C\uDF51 \uD83C\uDF51 name: " + name + " email: " + email + " password: " + password);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest();
        createRequest.setEmail(email);
        createRequest.setDisplayName(name);
        createRequest.setPassword(password);
        UserRecord userRecord = firebaseAuth.createUser(createRequest);
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Firebase user record created: ".concat(userRecord.getUid()));
        return userRecord;

    }
    static final GsonBuilder gsonBuilder = new GsonBuilder();
    static final Gson gson = gsonBuilder.create();
    public List<Anchor> getAnchors() throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<Anchor> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection("anchors").get();
        int cnt = 0;
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = gson.toJson(map);
            Anchor anchor = gson.fromJson(object,Anchor.class);
            cnt++;
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 ANCHOR: #" + cnt +
                    " \uD83D\uDC99 " + anchor.getName() + "  \uD83E\uDD66 anchorId: "
                    + anchor.getAnchorId());
            mList.add(anchor);
        }
        return mList;
    }

    public String addAnchorInfo(Info info) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Info current = getAnchorInfo(info.getAnchorId());
        if (current == null) {
            ApiFuture<DocumentReference> future = fs.collection("infos").add(info);
            LOGGER.info("Info added at path: ".concat(future.get().getPath()));
            return "Info added";
        } else {
            ApiFuture<QuerySnapshot> future = fs.collection("infos")
                    .whereEqualTo("anchorId",current.getAnchorId()).get();
            for (QueryDocumentSnapshot document : future.get().getDocuments()) {
                ApiFuture<WriteResult> m = document.getReference().delete();
                LOGGER.info("Info deleted, updateTime: ".concat(m.get().getUpdateTime().toString()));
            }
            ApiFuture<DocumentReference> future2 = fs.collection("infos").add(info);
            LOGGER.info("Info added after delete, at path: ".concat(future2.get().getPath()));
            return "Info Updated";
        }
    }
    public Info getAnchorInfo(String anchorId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Info info;
        List<Info> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection("infos")
                .whereEqualTo("anchorId",anchorId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = gson.toJson(map);
            Info mInfo = gson.fromJson(object,Info.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            info = mList.get(0);
        }

        return info;
    }
    public Anchor getAnchorByName(String name) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Anchor info;
        List<Anchor> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection("anchors")
                .whereEqualTo("name",name).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = gson.toJson(map);
            Anchor mInfo = gson.fromJson(object,Anchor.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            info = mList.get(0);
        }

        return info;
    }
}
