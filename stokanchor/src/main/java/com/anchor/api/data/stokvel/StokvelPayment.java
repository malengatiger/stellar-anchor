package com.anchor.api.data.stokvel;

public class StokvelPayment {
    private String memberId;
    private String stokvelId;
    private String amount, date, seed, stellarHash, paymentId;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getStokvelId() {
        return stokvelId;
    }

    public void setStokvelId(String stokvelId) {
        this.stokvelId = stokvelId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getStellarHash() {
        return stellarHash;
    }

    public void setStellarHash(String stellarHash) {
        this.stellarHash = stellarHash;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
