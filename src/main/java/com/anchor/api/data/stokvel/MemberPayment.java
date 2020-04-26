package com.anchor.api.data.stokvel;

public class MemberPayment {
    private String fromMemberId, toMemberId;
    private String amount, date, seed, stellarHash, paymentId;

    public String getFromMemberId() {
        return fromMemberId;
    }

    public void setFromMemberId(String fromMemberId) {
        this.fromMemberId = fromMemberId;
    }

    public String getToMemberId() {
        return toMemberId;
    }

    public void setToMemberId(String toMemberId) {
        this.toMemberId = toMemberId;
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
