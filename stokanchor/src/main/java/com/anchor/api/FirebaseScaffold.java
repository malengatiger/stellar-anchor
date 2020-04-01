package com.anchor.api;

import com.anchor.api.data.Anchor;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.errorprone.annotations.Var;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.management.loading.MLet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Service
public class FirebaseScaffold {
    public static final Logger LOGGER = Logger.getLogger(FirebaseScaffold.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private ApplicationContext context;
    @Value("${status}")
    private String status;

    public FirebaseScaffold() {
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 FirebaseScaffold Constructor: \uD83D\uDC99 .......");
    }

    public void initializeFirebase() throws Exception {
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Starting Firebase initialization " +
                ".... \uD83D\uDC99 DEV STATUS: \uD83C\uDF51 " + status + " \uD83C\uDF51");

        FirebaseApp app;
        if (status.equalsIgnoreCase("dev")) {
            Resource resource = new ClassPathResource("anchor.json");
            File file = resource.getFile();
            FileInputStream serviceAccount = new FileInputStream(file);

            FirebaseOptions devOptions = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://stellar-anchor-333.firebaseio.com")
                    .build();
            app = FirebaseApp.initializeApp(devOptions);
        } else {
            FirebaseOptions prodOptions = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setDatabaseUrl("https://stellar-stokvel.firebaseio.com/")
                    .build();

            app = FirebaseApp.initializeApp(prodOptions);
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
    public List<Anchor> getAnchors() throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<Anchor> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection("anchors").get();
        int cnt = 0;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

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
}
