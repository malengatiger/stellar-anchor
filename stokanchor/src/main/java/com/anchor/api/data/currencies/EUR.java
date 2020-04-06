package com.anchor.api.data.currencies;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EUR {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;

    @SerializedName("fee_fixed")
    @Expose
    private Double feeFixed;

    @SerializedName("fee_percent")
    @Expose
    private Double feePercent;

    @SerializedName("min_amount")
    @Expose
    private Double minAmount;

    @SerializedName("max_amount")
    @Expose
    private Double maxAmount;

    public EUR(Boolean enabled, Double feeFixed, Double feePercent, Double minAmount, Double maxAmount) {
        this.enabled = enabled;
        this.feeFixed = feeFixed;
        this.feePercent = feePercent;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Double getFeeFixed() {
        return feeFixed;
    }

    public void setFeeFixed(Double feeFixed) {
        this.feeFixed = feeFixed;
    }

    public Double getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(Double feePercent) {
        this.feePercent = feePercent;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

}
