package com.anchor.api.services;

import com.anchor.api.AnchorApplication;
import com.anchor.api.controllers.AgentController;
import com.anchor.api.controllers.AnchorController;
import com.anchor.api.data.AgentFundingRequest;
import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.anchor.*;
import com.anchor.api.data.info.Info;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.util.Constants;
import com.anchor.api.util.Emoji;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseService implements DatabaseServiceInterface {
    public static final Logger LOGGER = LoggerFactory.getLogger(FirebaseService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public FirebaseService() {
        LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F FirebaseService constructed \uD83C\uDF4F");
    }

    @Value("${databaseUrl}")
    private String databaseUrl;

    @Override
    public void initializeDatabase() throws Exception {
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD  FirebaseService initializeDatabase ... \uD83C\uDF4F" +
                ".... \uD83D\uDC99 DEV STATUS: " + Emoji.HEART_PURPLE + " " + Emoji.HEART_BLUE + Emoji.HEART_BLUE);

        FirebaseApp app;
        try {
            FirebaseOptions prodOptions = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setDatabaseUrl(databaseUrl)
                    .build();

            app = FirebaseApp.initializeApp(prodOptions);
        } catch (Exception e) {
            String msg = "Unable to initialize Firebase";
            LOGGER.info(msg);
            throw new Exception(msg, e);
        }
        LOGGER.info(Emoji.HEART_BLUE + Emoji.HEART_BLUE + "Firebase has been set up and initialized. " +
                "\uD83D\uDC99 URL: " + app.getOptions().getDatabaseUrl() + Emoji.HAPPY);
        LOGGER.info(Emoji.HEART_BLUE + Emoji.HEART_BLUE + "Firebase has been set up and initialized. " +
                "\uD83E\uDD66 Name: " + app.getName() + Emoji.HEART_ORANGE + Emoji.HEART_GREEN);

        Firestore fs = FirestoreClient.getFirestore();
        int cnt = 0;
        for (CollectionReference listCollection : fs.listCollections()) {
            cnt++;
            LOGGER.info(Emoji.RAIN_DROPS + Emoji.RAIN_DROPS + "Collection: #" + cnt + " \uD83D\uDC99 " + listCollection.getId());
        }
        List<Anchor> list = getAnchors();
        LOGGER.info(Emoji.HEART_BLUE + Emoji.HEART_BLUE +
                "Firebase Initialization complete; ... anchors found: " + list.size());
    }

    @Override
    public List<Anchor> getAnchors() throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<Anchor> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.ANCHORS).get();
        int cnt = 0;
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Anchor anchor = G.fromJson(object, Anchor.class);
            cnt++;
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 ANCHOR: #" + cnt +
                    " \uD83D\uDC99 " + anchor.getName() + "  \uD83E\uDD66 anchorId: "
                    + anchor.getAnchorId());
            mList.add(anchor);
        }
        return mList;
    }

    @Override
    public Agent getAgent(String agentId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Agent agent;
        List<Agent> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.AGENTS)
                .whereEqualTo("agentId", agentId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Agent mInfo = G.fromJson(object, Agent.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            agent = mList.get(0);
        }

        return agent;
    }


    @Override
    public String addLoanApplication(LoanApplication application) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        application.setAgentSeed(null);
        ApiFuture<DocumentReference> future = fs.collection(Constants.LOAN_APPLICATIONS).add(application);
        LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F LoanApplication added at path: "
                + " " + application.getTotalAmountPayable() + " \uD83C\uDF4F rate: "
                + application.getInterestRate() + " % "
                .concat(future.get().getPath()));
        return "\uD83C\uDF4F LoanApplication added";
    }

    @Override
    public String addLoanPayment(LoanPayment loanPayment) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection(Constants.LOAN_PAYMENTS).add(loanPayment);
        LOGGER.info(("\uD83C\uDF4F \uD83C\uDF4F \uD83C\uDF4F LoanPayment amount: " +
                loanPayment.getAmount() + " " + loanPayment.getAssetCode() +
                "" + loanPayment.getDate() +
                " added at path: ").concat(future.get().getPath()));
        return "\uD83C\uDF4F LoanPayment added";
    }

    @Override
    public String addOrganization(Organization organization) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection(Constants.ORGANIZATIONS).add(organization);
        LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Organization added at path: ".concat(future.get().getPath()));
        return "\uD83C\uDF4F Organization added";
    }

    @Override
    public String addAnchor(Anchor anchor) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection(Constants.ANCHORS).add(anchor);
        LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Anchor added at path: ".concat(future.get().getPath()));
        return "\uD83C\uDF4F Anchor added";
    }

    @Override
    public String addAnchorUser(AnchorUser anchorUser) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection(Constants.ANCHOR_USERS).add(anchorUser);
        LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F AnchorUser added at path: ".concat(future.get().getPath()));
        return "\uD83C\uDF4F AnchorUser added";
    }

    @Override
    public String addClient(Client client) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Client current = getClientByNameAndAnchor(client.getAnchorId(),
                client.getPersonalKYCFields().getFirst_name(),
                client.getPersonalKYCFields().getLast_name());
        if (current == null) {
            ApiFuture<DocumentReference> future = fs.collection(Constants.CLIENTS).add(client);
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Client added at path: ".concat(future.get().getPath()));
            return "\uD83C\uDF4F Client added";
        } else {
            throw new Exception("Client already exists for this Anchor");
        }
    }

    @Override
    public String addAgent(Agent agent) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Agent current = getAgentByNameAndAnchor(agent.getAnchorId(),
                agent.getPersonalKYCFields().getFirst_name(),
                agent.getPersonalKYCFields().getLast_name());
        if (current == null) {
            ApiFuture<DocumentReference> future = fs.collection(Constants.AGENTS).add(agent);
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Agent added at path: ".concat(future.get().getPath()));
            return "\uD83C\uDF4F Agent added";
        } else {
            throw new Exception("Agent already exists for this Anchor");
        }
    }

    @Override
    public String addStokvel(Stokvel agent) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Stokvel current = getStokvelByName(agent.getName());
        if (current == null) {
            ApiFuture<DocumentReference> future = fs.collection(Constants.STOKVELS).add(agent);
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Stokvel added at path: ".concat(future.get().getPath()));
            return "\uD83C\uDF4F Stokvel added";
        } else {
            throw new Exception("Stokvel already exists ");
        }
    }

    @Override
    public String addMember(Member member) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Member current = getMemberByName(member.getKycFields().getFirst_name(), member.getKycFields().getLast_name());
        if (current == null) {
            current = getMemberByEmail(member.getKycFields().getEmail_address());
            if (current != null) {
                throw new Exception("Member already exists ");
            }
            ApiFuture<DocumentReference> future = fs.collection(Constants.MEMBERS).add(member);
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Member added at path: ".concat(future.get().getPath()));
            return "\uD83C\uDF4F Member added";
        } else {
            throw new Exception("Member already exists ");
        }
    }

    @Override
    public String addBalances(AnchorController.Balances balances) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection(Constants.BALANCES).add(balances);
        LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F Balances added at path: ".concat(future.get().getPath()));
        return "\uD83C\uDF4F Balances added";

    }

    @Override
    public PaymentRequest addPaymentRequest(PaymentRequest paymentRequest) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();

        //todo - find account holders - 
        Agent agent = getAgentByAccount(paymentRequest.getSourceAccount());
        if (agent == null) {
            agent = getAgentByAccount(paymentRequest.getDestinationAccount());
        }
        Client client = getClientByAccount(paymentRequest.getSourceAccount());
        if (client == null) {
            client = getClientByAccount(paymentRequest.getDestinationAccount());
        }
        if (agent != null) {
            paymentRequest.setAgentId(agent.getAgentId());
        }
        if (client != null) {
            paymentRequest.setClientId(client.getClientId());
        }

        paymentRequest.setSeed(null);
        ApiFuture<DocumentReference> future = fs.collection(Constants.PAYMENT_REQUESTS).add(paymentRequest);
        String msg = Emoji.HAPPY.concat(Emoji.HAPPY.concat(Emoji.HAPPY)) +
                "PaymentRequest added to Database: "
                        .concat(" amount: ").concat(paymentRequest.getAmount())
                        .concat(" ").concat(paymentRequest.getDate())
                        .concat("  \uD83C\uDF51 ledger: ").concat(" " + paymentRequest.getLedger() + " ")
                        .concat(future.get().getPath());
        LOGGER.info(msg);
        return paymentRequest;

    }

    @Override
    public AgentFundingRequest addAgentFundingRequest(AgentFundingRequest agentFundingRequest) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = fs.collection(Constants.AGENT_FUNDING_REQUESTS).add(agentFundingRequest);
        String msg = Emoji.HAPPY.concat(Emoji.HAPPY.concat(Emoji.HAPPY)) +
                "AgentFundingRequest added to Database: "
                        .concat(" amount: ").concat(agentFundingRequest.getAmount())
                        .concat(" ").concat(agentFundingRequest.getDate())
                        + " path: "
                        .concat(future.get().getPath());
        LOGGER.info(msg);
        return agentFundingRequest;
    }

    @Override
    public String updateLoanApplication(LoanApplication application) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = fs.collection(Constants.LOAN_APPLICATIONS)
                .whereEqualTo("loanId", application.getLoanId())
                .limit(1)
                .get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            document.getReference().set(application);
            String msg = Emoji.WINE + "LoanApplication updated";
            return msg;
        }
        throw new Exception("LoanApplication not found for update");

    }

    @Override
    public String updateClient(Client client) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.CLIENTS)
                .whereEqualTo("clientId", client.getClientId())
                .limit(1)
                .get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            document.getReference().set(client);
            String msg = "Client updated";
            LOGGER.info(Emoji.WINE.concat(Emoji.WINE).concat(msg));
            return msg;
        }
        throw new Exception("Client not found for update");
    }

    @Override
    public String updateAgent(Agent agent) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.CLIENTS)
                .whereEqualTo("agentId", agent.getAgentId())
                .limit(1)
                .get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            document.getReference().set(agent);
            String msg = "Agent updated";
            LOGGER.info(Emoji.WINE.concat(Emoji.WINE).concat(msg));
            return msg;
        }
        throw new Exception("Agent not found for update");
    }

    @Override
    public String addAnchorInfo(Info info) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Info current = getAnchorInfo(info.getAnchorId());
        if (current == null) {
            ApiFuture<DocumentReference> future = fs.collection(Constants.INFOS).add(info);
            LOGGER.info("Info added at path: ".concat(future.get().getPath()));
            return "Info added";
        } else {
            ApiFuture<QuerySnapshot> future = fs.collection(Constants.INFOS)
                    .whereEqualTo("anchorId", current.getAnchorId()).get();
            for (QueryDocumentSnapshot document : future.get().getDocuments()) {
                ApiFuture<WriteResult> m = document.getReference().delete();
                LOGGER.info("Info deleted, updateTime: ".concat(m.get().getUpdateTime().toString()));
            }
            ApiFuture<DocumentReference> future2 = fs.collection(Constants.INFOS).add(info);
            LOGGER.info("Info added after delete, at path: ".concat(future2.get().getPath()));
            return "Info Updated";
        }
    }

    @Override
    public Info getAnchorInfo(String anchorId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Info info;
        List<Info> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.INFOS)
                .whereEqualTo("anchorId", anchorId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Info mInfo = G.fromJson(object, Info.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            info = mList.get(0);
        }

        return info;
    }


    @Override
    public Anchor getAnchor(String anchorId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Anchor info;
        List<Anchor> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.ANCHORS)
                .whereEqualTo("anchorId", anchorId)
                .limit(1)
                .get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Anchor mInfo = G.fromJson(object, Anchor.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            info = mList.get(0);
        }

        return info;
    }

    @Override
    public Stokvel getStokvelByName(String name) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Stokvel info;
        List<Stokvel> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.STOKVELS)
                .whereEqualTo("name", name).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Stokvel mInfo = G.fromJson(object, Stokvel.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            info = mList.get(0);
        }

        return info;
    }

    @Override
    public Member getMemberByName(String firstName, String lastName) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Member info;
        List<Member> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.STOKVELS)
                .whereEqualTo("kycFields.first_name", firstName)
                .whereEqualTo("kycFields.last_name", lastName).get();

        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Member mInfo = G.fromJson(object, Member.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            info = mList.get(0);
        }

        return info;
    }

    @Override
    public Member getMemberByEmail(String email) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Member info;
        List<Member> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.STOKVELS)
                .whereEqualTo("kycFields.email_address", email).get();

        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Member mInfo = G.fromJson(object, Member.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            info = mList.get(0);
        }

        return info;
    }

    @Override
    public List<Member> getStokvelMembers(String stokvelId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<Member> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.MEMBERS)
                .whereEqualTo("stokvelId", stokvelId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Member mInfo = G.fromJson(object, Member.class);
            mList.add(mInfo);
        }
        LOGGER.info(Emoji.BLUE_DISC.concat(Emoji.BLUE_DISC) + mList.size() + " Members found");

        return mList;
    }

    @Override
    public List<Agent> getAgents(String anchorId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<Agent> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.AGENTS)
                .whereEqualTo("anchorId", anchorId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Agent mInfo = G.fromJson(object, Agent.class);
            mList.add(mInfo);
        }
        LOGGER.info(Emoji.BLUE_DISC.concat(Emoji.BLUE_DISC) + mList.size() + " agents found");

        return mList;
    }

    @Override
    public List<PaymentRequest> getPaymentRequests(String anchorId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<PaymentRequest> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.PAYMENT_REQUESTS)
                .whereEqualTo("anchorId", anchorId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            PaymentRequest mInfo = G.fromJson(object, PaymentRequest.class);
            mList.add(mInfo);
        }
        LOGGER.info(Emoji.BLUE_DISC.concat(Emoji.BLUE_DISC) + mList.size() + " PaymentRequests found");

        return mList;
    }

    @Override
    public LoanApplication getLoanApplication(String loanId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        LoanApplication loanApplication;
        List<LoanApplication> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.LOAN_APPLICATIONS)
                .whereEqualTo("loanId", loanId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            LoanApplication mInfo = G.fromJson(object, LoanApplication.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            loanApplication = mList.get(0);
        }

        return loanApplication;
    }

    @Override
    public Agent getAgentByNameAndAnchor(String anchorId, String firstName, String lastName) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Agent agent;
        List<Agent> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.AGENTS)
                .whereEqualTo("anchorId", anchorId)
                .whereEqualTo("personalKYCFields.first_name", firstName)
                .whereEqualTo("personalKYCFields.last_name", lastName).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Agent mInfo = G.fromJson(object, Agent.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            agent = mList.get(0);
        }

        return agent;
    }

    @Override
    public List<Client> getAnchorClients(String anchorId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<Client> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.CLIENTS)
                .whereEqualTo("anchorId", anchorId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Client mInfo = G.fromJson(object, Client.class);
            mList.add(mInfo);
        }
        LOGGER.info(Emoji.LEAF + "Found " + mList.size() + " Anchor Clients");
        return mList;
    }

    @Override
    public List<Client> getAgentClients(String agentId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<Client> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.CLIENTS)
                .whereEqualTo("agentId", agentId).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Client mInfo = G.fromJson(object, Client.class);
            mList.add(mInfo);
        }
        LOGGER.info(Emoji.LEAF + "Found " + mList.size() + " Agent Clients");
        return mList;
    }

    @Override
    public List<LoanApplication> getAgentLoans(String agentId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<LoanApplication> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.LOAN_APPLICATIONS)
                .whereEqualTo("agentId", agentId)
                .orderBy("date", Query.Direction.DESCENDING).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            LoanApplication mInfo = G.fromJson(object, LoanApplication.class);
            mList.add(mInfo);
        }
        LOGGER.info(Emoji.LEAF + "Found " + mList.size() + " Agent Loans");
        return mList;
    }

    @Override
    public List<LoanPayment> getLoanPayments(String loanId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        List<LoanPayment> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.LOAN_PAYMENTS)
                .whereEqualTo("loanId", loanId)
                .orderBy("date", Query.Direction.DESCENDING).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            LoanPayment payment = G.fromJson(object, LoanPayment.class);
            mList.add(payment);
        }
        LOGGER.info(Emoji.LEAF + "Found " + mList.size() + " Loan Payments");
        return mList;
    }

    @Override
    public Client getClientByNameAndAnchor(String anchorId, String firstName, String lastName) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Client agent;
        List<Client> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.CLIENTS)
                .whereEqualTo("anchorId", anchorId)
                .whereEqualTo("personalKYCFields.first_name", firstName)
                .whereEqualTo("personalKYCFields.last_name", lastName).get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Client mInfo = G.fromJson(object, Client.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            agent = mList.get(0);
        }

        return agent;
    }

    @Override
    public Client getClientById(String clientId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Client client;
        List<Client> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.CLIENTS)
                .whereEqualTo("clientId", clientId)
                .limit(1)
                .get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Client mInfo = G.fromJson(object, Client.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            client = mList.get(0);
        }

        return client;
    }

    @Override
    public String addAccountResponse(AccountResponse accountResponse) throws Exception {
        LOGGER.info("\uD83C\uDFBD Adding accountResponse to Firestore: ".concat(accountResponse.getAccountId()));
//        Firestore fs = FirestoreClient.getFirestore();
//        AccountResponseWithDate withDate = new AccountResponseWithDate(accountResponse,null);
//        ApiFuture<DocumentReference> future2 = fs.collection("accountResponses").add(withDate);
//        LOGGER.info("\uD83C\uDFBD AccountResponseWithDate added, \uD83C\uDFBD at path: ".concat(future2.get().getPath()));
        return "\uD83C\uDF51 AccountResponse added";
    }

    @Override
    public String addOperationResponse(OperationResponse operationResponse) throws Exception {
        LOGGER.info("\uD83C\uDFBD Adding operationResponse to Firestore: ".concat(operationResponse.getSourceAccount()));
//        Firestore fs = FirestoreClient.getFirestore();
//        operationResponse.getTransaction().get().
//        ApiFuture<DocumentReference> future2 = fs.collection("operationResponses").add(operationResponse);
//        LOGGER.info("\uD83C\uDFBD operationResponse added, \uD83C\uDFBD at path: ".concat(future2.get().getPath()));
        return "\uD83C\uDF51 operationResponse added";
    }

    @Override
    public String addTransactionResponse(TransactionResponse transactionResponse) throws Exception {
        LOGGER.info("\uD83C\uDFBD Adding transactionResponse to Firestore: createdAt: ".concat(transactionResponse.getCreatedAt()));
//        Firestore fs = FirestoreClient.getFirestore();
//        ApiFuture<DocumentReference> future2 = fs.collection("transactionResponses").add(transactionResponse);
//        LOGGER.info("\uD83C\uDFBD transactionResponse added, \uD83C\uDFBD at path: ".concat(future2.get().getPath()));
        return "\uD83C\uDF51 transactionResponse added";
    }

    public UserRecord createUser(String name, String email, String password) throws FirebaseAuthException {
        LOGGER.info(Emoji.LEMON + Emoji.LEMON + "createUser: name: " + name + " email: " + email + " password: " + password);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest();
        createRequest.setEmail(email);
        createRequest.setDisplayName(name);
        createRequest.setPassword(password);
        UserRecord userRecord = firebaseAuth.createUser(createRequest);
        LOGGER.info(Emoji.HEART_ORANGE + Emoji.HEART_ORANGE + "Firebase user record created: ".concat(userRecord.getUid()));
        return userRecord;

    }

    public List<ExportedUserRecord> getAuthUsers() throws FirebaseAuthException {
        // Start listing users from the beginning, 1000 at a time.
        List<ExportedUserRecord> mList = new ArrayList<>();
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
        while (page != null) {
            for (ExportedUserRecord user : page.getValues()) {
                LOGGER.info(Emoji.PIG.concat(Emoji.PIG) + "Auth User: " + user.getDisplayName());
                mList.add(user);
            }
            page = page.getNextPage();
        }

        return mList;
    }

    public void deleteAuthUsers() throws Exception {
        LOGGER.info(Emoji.WARNING.concat(Emoji.WARNING.concat(Emoji.WARNING)
                .concat(" DELETING ALL AUTH USERS from Firebase .... ").concat(Emoji.RED_DOT)));
        List<ExportedUserRecord> list = getAuthUsers();
        for (ExportedUserRecord exportedUserRecord : list) {
            FirebaseAuth.getInstance().deleteUser(exportedUserRecord.getUid());
            LOGGER.info(Emoji.OK.concat(Emoji.RED_APPLE) + "Successfully deleted user: "
                    .concat(exportedUserRecord.getDisplayName()));
        }
    }

    public void deleteCollections() throws Exception {
        LOGGER.info(Emoji.WARNING.concat(Emoji.WARNING.concat(Emoji.WARNING)
                .concat(" DELETING ALL DATA from Firestore .... ").concat(Emoji.RED_DOT)));
        Firestore fs = FirestoreClient.getFirestore();
        CollectionReference ref1 = fs.collection(Constants.ANCHORS);
        deleteCollection(ref1, 1000);
        CollectionReference ref2 = fs.collection(Constants.ANCHOR_USERS);
        deleteCollection(ref2, 1000);
        CollectionReference ref3 = fs.collection(Constants.AGENTS);
        deleteCollection(ref3, 1000);
        CollectionReference ref4 = fs.collection(Constants.LOAN_APPLICATIONS);
        deleteCollection(ref4, 1000);
        CollectionReference ref5 = fs.collection(Constants.LOAN_PAYMENTS);
        deleteCollection(ref5, 1000);
        CollectionReference ref6 = fs.collection(Constants.PAYMENT_REQUESTS);
        deleteCollection(ref6, 1000);
        CollectionReference ref7 = fs.collection(Constants.CLIENTS);
        deleteCollection(ref7, 1000);
        CollectionReference ref8 = fs.collection(Constants.STOKVELS);
        deleteCollection(ref8, 1000);
        CollectionReference ref9 = fs.collection(Constants.MEMBERS);
        deleteCollection(ref9, 1000);
        CollectionReference ref10 = fs.collection(Constants.MEMBER_PAYMENTS);
        deleteCollection(ref10, 1000);
        CollectionReference ref11 = fs.collection(Constants.STOKVEL_PAYMENTS);
        deleteCollection(ref11, 1000);
        CollectionReference ref12 = fs.collection(Constants.STOKVEL_GOALS);
        deleteCollection(ref12, 1000);
        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR.concat(Emoji.PEAR)
                .concat(" DELETED ALL DATA from Firestore .... ").concat(Emoji.RED_TRIANGLE)));
    }

    /**
     * Delete a collection in batches to avoid out-of-memory errors.
     * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
     */
    private void deleteCollection(CollectionReference collection, int batchSize) {
        try {
            // retrieve a small batch of documents to avoid out-of-memory errors
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
                LOGGER.info(Emoji.RECYCLE.concat(document.getReference().getPath()
                        .concat(" deleted")));
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collection, batchSize);
            }
        } catch (Exception e) {
            LOGGER.info(Emoji.NOT_OK.concat(Emoji.ERROR) + "Error deleting collection : " + e.getMessage());
        }
    }

    @Override
    public Agent getAgentByAccount(String accountId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Agent agent;
        List<Agent> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.AGENTS)
                .whereEqualTo("stellarAccountId", accountId)
                .limit(1)
                .get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Agent mInfo = G.fromJson(object, Agent.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            agent = mList.get(0);
        }

        return agent;

    }

    @Override
    public Client getClientByAccount(String accountId) throws Exception {
        Firestore fs = FirestoreClient.getFirestore();
        Client client;
        List<Client> mList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = fs.collection(Constants.CLIENTS)
                .whereEqualTo("account", accountId)
                .limit(1)
                .get();
        for (QueryDocumentSnapshot document : future.get().getDocuments()) {
            Map<String, Object> map = document.getData();
            String object = G.toJson(map);
            Client mInfo = G.fromJson(object, Client.class);
            mList.add(mInfo);
        }
        if (mList.isEmpty()) {
            return null;
        } else {
            client = mList.get(0);
        }

        return client;
    }
}
