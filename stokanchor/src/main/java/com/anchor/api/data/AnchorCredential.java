package com.anchor.api.data;


public class AnchorCredential {

    String accountId, date, seed, cryptKey, fortunaKey, anchorId;
    AccountType accountType;

    public AnchorCredential(String accountId, String date, String seed, String cryptKey, String fortunaKey, String anchorId, AccountType accountType) {
        this.accountId = accountId;
        this.date = date;
        this.seed = seed;
        this.cryptKey = cryptKey;
        this.fortunaKey = fortunaKey;
        this.anchorId = anchorId;
        this.accountType = accountType;
    }

    public AnchorCredential() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getCryptKey() {
        return cryptKey;
    }

    public void setCryptKey(String cryptKey) {
        this.cryptKey = cryptKey;
    }

    public String getFortunaKey() {
        return fortunaKey;
    }

    public void setFortunaKey(String fortunaKey) {
        this.fortunaKey = fortunaKey;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
