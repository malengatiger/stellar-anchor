package com.anchor.api.data.account;
import org.joda.time.DateTime;

public class Account {
    String accountId, seed;
    String date;

    public Account(AccountResponseBag bag) {
        accountId = bag.accountResponse.getAccountId();
        seed = bag.secretSeed;
        DateTime dateTime = new DateTime();
        date = dateTime.toDateTimeISO().toString();
    }

    public Account(String accountId, String seed) {
        this.accountId = accountId;
        this.seed = seed;
        DateTime dateTime = new DateTime();
        date = dateTime.toDateTimeISO().toString();
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
