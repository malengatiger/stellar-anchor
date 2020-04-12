package com.anchor.api.data.anchor;

import com.anchor.api.data.account.Account;

public class Anchor {
    String anchorId, name, cellphone, email;
    Account baseAccount, issuingAccount, distributionAccount;
    AnchorUser anchorUser;
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AnchorUser getAnchorUser() {
        return anchorUser;
    }

    public void setAnchorUser(AnchorUser anchorUser) {
        this.anchorUser = anchorUser;
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


}
