package com.anchor.api.data.transfer.sep26;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FundWallet {
    public FundWallet() {
    }

    public FundWallet(float amountNative, Boolean enabled, String fees, List<Sep26> fundingProtocols) {
        this.amountNative = amountNative;
        this.enabled = enabled;
        this.fees = fees;
        this.fundingProtocols = fundingProtocols;
    }

    @SerializedName("amount_native")
    @Expose
    private float amountNative;

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;

    @SerializedName("fees")
    @Expose
    private String fees;

    @SerializedName("funding_protocols")
    @Expose
    private List<Sep26> fundingProtocols;
}
