package com.anchor.api.data.transfer.sep10;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChallengeResponse {
    String transaction;
    @SerializedName("network_passphrase")
    @Expose
    String networkPassphrase;

    public ChallengeResponse(String transaction, String networkPassphrase) {
        this.transaction = transaction;
        this.networkPassphrase = networkPassphrase;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getNetworkPassphrase() {
        return networkPassphrase;
    }
}
