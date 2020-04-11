package com.anchor.api.services;

import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentService {
    public AgentService() {
        LOGGER.info("\uD83E\uDD4F \uD83E\uDD4F AgentService - not quite the Secret Service \uD83E\uDD4F \uD83E\uDD4F ");
    }
    public static final Logger LOGGER = LoggerFactory.getLogger(CryptoService.class);
    @Autowired
    private AccountService accountService;
    @Autowired
    private FirebaseService firebaseService;

    public Agent createAgent(Agent agent) throws Exception {
        //todo - create Stellar account; add to Firestore;

        AccountResponseBag bag = accountService.createAndFundStellarAccount("seed","startingBalance");
        //todo - encrypt secret seed and write agent to Firestore
        agent.setAccount(bag.getAccountResponse().getAccountId());
        agent.setEncryptedSeed("");

        return null;
    }
    public Agent getAgent(String agentId) throws Exception {

        return null;
    }
    public Agent getAgentByNameAndAnchor(String anchorId, String firstName, String lastName) throws Exception {

        return null;
    }
}
