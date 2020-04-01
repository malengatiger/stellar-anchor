package com.anchor.api.data;
import java.util.Date;

public class Account {
    String accountId, seed;
    String date;

    public Account(AccountResponseBag bag) {
        accountId = bag.accountResponse.getAccountId();
        seed = bag.secretSeed;
        date = new Date().toString();
    }

    public Account(String accountId, String seed, String date) {
        this.accountId = accountId;
        this.seed = seed;
        this.date = date;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
