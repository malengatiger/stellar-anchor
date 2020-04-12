package com.anchor.api.data.account;
import org.joda.time.DateTime;

import java.util.List;

public class Account {
    String accountId;
    String date;
    List<Object> encryptedSeed;

    public List<Object> getEncryptedSeed() {
        return encryptedSeed;
    }

    public void setEncryptedSeed(List<Object> encryptedSeed) {
        this.encryptedSeed = encryptedSeed;
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

}
