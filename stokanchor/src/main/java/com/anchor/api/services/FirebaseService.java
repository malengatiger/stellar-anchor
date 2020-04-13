package com.anchor.api.services;

import com.anchor.api.data.account.AccountResponseWithDate;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.data.info.Info;
import com.anchor.api.util.Constants;
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
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;

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
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.ANCHORS).get();
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

    public String addClient(Client client) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Client current = getClientByNameAndAnchor(client.getAnchorId(),
                client.getPersonalKYCFields().getFirstName(),
                client.getPersonalKYCFields().getLastName());
        if (current == null) {
            ApiFuture<DocumentReference> future = fs.collection(Constants.CLIENTS).add(client);
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Client added at path: ".concat(future.get().getPath()));
            return "\uD83C\uDF4F Client added";
        } else {
            throw new Exception("Client already exists for this Anchor");
        }
    }
    public String addAgent(Agent agent) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Agent current = getAgentByNameAndAnchor(agent.getAnchorId(),
                agent.getPersonalKYCFields().getFirstName(),
                agent.getPersonalKYCFields().getLastName());
        if (current == null) {
            ApiFuture<DocumentReference> future = fs.collection(Constants.AGENTS).add(agent);
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Agent added at path: ".concat(future.get().getPath()));
            return "\uD83C\uDF4F Agent added";
        } else {
            throw new Exception("Agent already exists for this Anchor");
        }
    }
    public String addAnchorInfo(Info info) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Info current = getAnchorInfo(info.getAnchorId());
        if (current == null) {
            ApiFuture<DocumentReference> future = fs.collection(Constants.INFOS).add(info);
            LOGGER.info("Info added at path: ".concat(future.get().getPath()));
            return "Info added";
        } else {
            ApiFuture<QuerySnapshot> future = fs.collection(Constants.INFOS)
                    .whereEqualTo("anchorId",current.getAnchorId()).get();
            for (QueryDocumentSnapshot document : future.get().getDocuments()) {
                ApiFuture<WriteResult> m = document.getReference().delete();
                LOGGER.info("Info deleted, updateTime: ".concat(m.get().getUpdateTime().toString()));
            }
            ApiFuture<DocumentReference> future2 = fs.collection(Constants.INFOS).add(info);
            LOGGER.info("Info added after delete, at path: ".concat(future2.get().getPath()));
            return "Info Updated";
        }
    }
    public Info getAnchorInfo(String anchorId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Info info;
        List<Info> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.INFOS)
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
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.ANCHORS)
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
    public Agent getAgent(String agentId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Agent agent;
        List<Agent> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.AGENTS)
                .whereEqualTo("agentId",agentId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = gson.toJson(map);
            Agent mInfo = gson.fromJson(object,Agent.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            agent = mList.get(0);
        }

        return agent;
    }
    public Agent getAgentByNameAndAnchor(String anchorId, String firstName, String lastName) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Agent agent;
        List<Agent> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.AGENTS)
                .whereEqualTo("anchorId",anchorId)
                .whereEqualTo("personalKYCFields.first_name",firstName)
                .whereEqualTo("personalKYCFields.last_name", lastName).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = gson.toJson(map);
            Agent mInfo = gson.fromJson(object,Agent.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            agent = mList.get(0);
        }

        return agent;
    }
    public Client getClientByNameAndAnchor(String anchorId, String firstName, String lastName) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Client agent;
        List<Client> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.CLIENTS)
                .whereEqualTo("anchorId",anchorId)
                .whereEqualTo("personalKYCFields.first_name",firstName)
                .whereEqualTo("personalKYCFields.last_name", lastName).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = gson.toJson(map);
            Client mInfo = gson.fromJson(object,Client.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            agent = mList.get(0);
        }

        return agent;
    }
    public String addAccountResponse(AccountResponse accountResponse) throws Exception {
        LOGGER.info("\uD83C\uDFBD Adding accountResponse to Firestore: ".concat(accountResponse.getAccountId()));
//        Firestore fs = FirestoreClient.getFirestore();
//        AccountResponseWithDate withDate = new AccountResponseWithDate(accountResponse,null);
//        ApiFuture<DocumentReference> future2 = fs.collection("accountResponses").add(withDate);
//        LOGGER.info("\uD83C\uDFBD AccountResponseWithDate added, \uD83C\uDFBD at path: ".concat(future2.get().getPath()));
        return "\uD83C\uDF51 AccountResponse added";
    }
    public String addOperationResponse(OperationResponse operationResponse) throws Exception {
        LOGGER.info("\uD83C\uDFBD Adding operationResponse to Firestore: ".concat(operationResponse.getSourceAccount()));
//        Firestore fs = FirestoreClient.getFirestore();
//        operationResponse.getTransaction().get().
//        ApiFuture<DocumentReference> future2 = fs.collection("operationResponses").add(operationResponse);
//        LOGGER.info("\uD83C\uDFBD operationResponse added, \uD83C\uDFBD at path: ".concat(future2.get().getPath()));
        return "\uD83C\uDF51 operationResponse added";
    }
    public String addTransactionResponse(TransactionResponse transactionResponse) throws Exception {
        LOGGER.info("\uD83C\uDFBD Adding transactionResponse to Firestore: createdAt: ".concat(transactionResponse.getCreatedAt()));
//        Firestore fs = FirestoreClient.getFirestore();
//        ApiFuture<DocumentReference> future2 = fs.collection("transactionResponses").add(transactionResponse);
//        LOGGER.info("\uD83C\uDFBD transactionResponse added, \uD83C\uDFBD at path: ".concat(future2.get().getPath()));
        return "\uD83C\uDF51 transactionResponse added";
    }
}
