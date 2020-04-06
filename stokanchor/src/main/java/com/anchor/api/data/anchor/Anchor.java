package com.anchor.api.data.anchor;

import com.anchor.api.data.account.Account;
import com.anchor.api.data.User;

import java.util.List;

public class Anchor {
    String anchorId, name, cellphone, email;
    List<AnchorCredential> anchorCredentials;
    Account baseAccount, issuingAccount, distributionAccount;
    User user;
    String date;

    public Anchor(String anchorId, String name, String cellphone,
                  String email, List<AnchorCredential> anchorCredentials,
                  Account baseAccount, Account issuingAccount,
                  Account distributionAccount, User user) {
        this.anchorId = anchorId;
        this.name = name;
        this.cellphone = cellphone;
        this.email = email;
        this.anchorCredentials = anchorCredentials;
        this.baseAccount = baseAccount;
        this.issuingAccount = issuingAccount;
        this.distributionAccount = distributionAccount;
        this.user = user;
    }

    public Anchor() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getBaseAccount() {
        return baseAccount;
    }

    public void setBaseAccount(Account baseAccount) {
        this.baseAccount = baseAccount;
    }

    public Account getIssuingAccount() {
        return issuingAccount;
    }

    public void setIssuingAccount(Account issuingAccount) {
        this.issuingAccount = issuingAccount;
    }

    public Account getDistributionAccount() {
        return distributionAccount;
    }

    public void setDistributionAccount(Account distributionAccount) {
        this.distributionAccount = distributionAccount;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AnchorCredential> getAnchorCredentials() {
        return anchorCredentials;
    }

    public void setAnchorCredentials(List<AnchorCredential> anchorCredentials) {
        this.anchorCredentials = anchorCredentials;
    }
}
