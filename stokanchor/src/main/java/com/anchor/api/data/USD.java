package com.anchor.api.data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class USD {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("fee_fixed")
    @Expose
    private Integer feeFixed;
    @SerializedName("fee_percent")
    @Expose
    private Integer feePercent;
    @SerializedName("min_amount")
    @Expose
    private Double minAmount;
    @SerializedName("max_amount")
    @Expose
    private Integer maxAmount;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getFeeFixed() {
        return feeFixed;
    }

    public void setFeeFixed(Integer feeFixed) {
        this.feeFixed = feeFixed;
    }

    public Integer getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(Integer feePercent) {
        this.feePercent = feePercent;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

}
