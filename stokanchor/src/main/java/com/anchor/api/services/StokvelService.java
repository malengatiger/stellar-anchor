package com.anchor.api.services;

import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.stokvel.*;
import com.anchor.api.util.Emoji;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class StokvelService {
    public static final Logger LOGGER = Logger.getLogger(AccountService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private AccountService accountService;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private PaymentService paymentService;

    @Value("${sendgrid}")
    private String sendgridAPIKey;

    @Value("${fromMail}")
    private String fromMail;

    @Value("${anchorName}")
    private String anchorName;

    @Value("${agentStartingBalance}")
    private String agentStartingBalance;

    @Value("${clientStartingBalance}")
    private String clientStartingBalance;

    @Value("${fiatLimit}")
    private String fiatLimit;

    public StokvelService() {
        LOGGER.info(Emoji.HAND2.concat(Emoji.HAND2)
        .concat("StokvelService up and ready for gig! ".concat(Emoji.RAIN_DROP)));
    }
    private Anchor anchor;
    public Stokvel createStokvel(Stokvel stokvel) throws Exception {
        LOGGER.info(Emoji.YELLOW_BIRD + Emoji.YELLOW_BIRD + Emoji.YELLOW_BIRD +
                "....... creating Stokvel ....... ");
        if (anchor == null) {
            anchor = firebaseService.getAnchorByName(anchorName);
        }
        Stokvel mStokvel = firebaseService.getStokvelByName(stokvel.getName());
        if (mStokvel != null) {
            throw new Exception("Stokvel already exists");
        }
        AccountResponseBag bag = accountService.createAndFundUserAccount(agentStartingBalance,
                "0.01", fiatLimit);
        LOGGER.info(Emoji.RED_APPLE + Emoji.RED_APPLE +
                "Stokvel Stellar account has been created and funded with ... "
                        .concat(agentStartingBalance).concat(" XLM; secretSeed: ".concat(bag.getSecretSeed())));
        cryptoService.encrypt(bag.getAccountResponse().getAccountId(), bag.getSecretSeed());
        List<AccountService.AssetBag> assetBags = accountService.getDefaultAssets(
                anchor.getIssuingAccount().getAccountId());
        String issuingAccountSeed = cryptoService.getDecryptedSeed(anchor.getIssuingAccount().getAccountId());
        LOGGER.info(Emoji.WARNING.concat(Emoji.WARNING) + "createStokvel:.... Creating Stokvel Fiat Asset Balances .... userSeed: "
                .concat(bag.getSecretSeed()));
        for (AccountService.AssetBag assetBag : assetBags) {
            accountService.createTrustLine(anchor.getIssuingAccount().getAccountId(),bag.getSecretSeed(),fiatLimit,
                    assetBag.assetCode);
            accountService.createAsset(issuingAccountSeed,anchor.getDistributionAccount().getAccountId(),
                    assetBag.assetCode, "0.01");
        }
        //create firebase auth user
        String savedPassword;
        try {
            stokvel.setAccountId(bag.getAccountResponse().getAccountId());
            UserRecord record = firebaseService.createUser(stokvel.getName(),
                    stokvel.getKycFields().getEmail(), stokvel.getPassword());
            stokvel.setStokvelId(record.getUid());
            stokvel.setAccountId(bag.getAccountResponse().getAccountId());
            stokvel.setDate(new DateTime().toDateTimeISO().toString());
            savedPassword = stokvel.getPassword();
            stokvel.setPassword(null);
            firebaseService.addStokvel(stokvel);
            //todo - sendEmail(stokvel ....);
            LOGGER.info((Emoji.LEAF + Emoji.LEAF +
                    "Stokvel has been added to Firestore without seed or password ").concat(G.toJson(stokvel)));
            stokvel.setPassword(savedPassword);
            stokvel.setSecretSeed(bag.getSecretSeed());
        } catch (Exception e) {
            String msg = Emoji.NOT_OK.concat(Emoji.ERROR)
                    .concat("Firebase error: ".concat(e.getMessage()));
            LOGGER.info(msg);
            throw new Exception(msg);
        }
        return stokvel;
    }

    public Member createMember(Member member) throws Exception {
        LOGGER.info(Emoji.BLUE_BIRD + Emoji.BLUE_BIRD + Emoji.BLUE_BIRD +
                "....... creating Member ....... ");
        if (anchor == null) {
            anchor = firebaseService.getAnchorByName(anchorName);
        }
        Member mMember = firebaseService.getMemberByName(member.getKycFields().getFirst_name(), member.getKycFields().getLast_name());
        if (mMember != null) {
            throw new Exception("Member already exists");
        }
        mMember = firebaseService.getMemberByEmail(member.getKycFields().getEmail_address());
        if (mMember != null) {
            throw new Exception("Member already exists");
        }
        AccountResponseBag bag = accountService.createAndFundUserAccount(clientStartingBalance,
                "0.01", fiatLimit);
        LOGGER.info(Emoji.RED_APPLE + Emoji.RED_APPLE +
                "Member Stellar account has been created and funded with ... "
                        .concat(agentStartingBalance).concat(" XLM; secretSeed: ".concat(bag.getSecretSeed())));
        cryptoService.encrypt(bag.getAccountResponse().getAccountId(), bag.getSecretSeed());
        List<AccountService.AssetBag> assetBags = accountService.getDefaultAssets(
                anchor.getIssuingAccount().getAccountId());
        String issuingAccountSeed = cryptoService.getDecryptedSeed(anchor.getIssuingAccount().getAccountId());
        LOGGER.info(Emoji.WARNING.concat(Emoji.WARNING) + "createMember:.... Creating Member Fiat Asset Balances .... userSeed: "
                .concat(bag.getSecretSeed()));

        for (AccountService.AssetBag assetBag : assetBags) {
            accountService.createTrustLine(anchor.getIssuingAccount().getAccountId(),bag.getSecretSeed(),fiatLimit,
                    assetBag.assetCode);
            accountService.createAsset(issuingAccountSeed,anchor.getDistributionAccount().getAccountId(),
                    assetBag.assetCode, "0.01");
        }
        //create firebase auth user
        String savedPassword;
        try {
            member.setStellarAccountId(bag.getAccountResponse().getAccountId());
            UserRecord record = firebaseService.createUser(member.getFullName(),
                    member.getKycFields().getEmail_address(), member.getPassword());
            member.setMemberId(record.getUid());
            member.setDate(new DateTime().toDateTimeISO().toString());
            savedPassword = member.getPassword();
            member.setPassword(null);
            member.setSecretSeed(null);
            firebaseService.addMember(member);
            //sendEmail(agent);
            LOGGER.info((Emoji.LEAF + Emoji.LEAF +
                    "Member has been added to Firestore without seed or password ").concat(G.toJson(member)));
            member.setPassword(savedPassword);
            member.setSecretSeed(bag.getSecretSeed());
        } catch (Exception e) {
            String msg = Emoji.NOT_OK.concat(Emoji.ERROR)
                    .concat("Firebase error: ".concat(e.getMessage()));
            LOGGER.info(msg);
            throw new Exception(msg);
        }


        return member;
    }

    public StokvelPayment addStokvelPayment(StokvelPayment stokvelPayment) throws Exception {

        return null;
    }
    public StokvelGoal addStokvelGoal(StokvelGoal stokvelGoal) throws Exception {

        return null;
    }
}
