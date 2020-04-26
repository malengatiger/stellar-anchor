package com.anchor.api.data.stokvel;

import com.anchor.api.data.transfer.sep9.OrganizationKYCFields;

public class Stokvel {
    private String name, date, stokvelId,
    anchorId,
    accountId, password, secretSeed;
    private boolean active;
    Member adminMember;
    OrganizationKYCFields kycFields;

    public String getSecretSeed() {
        return secretSeed;
    }

    public void setSecretSeed(String secretSeed) {
        this.secretSeed = secretSeed;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public OrganizationKYCFields getKycFields() {
        return kycFields;
    }

    public void setKycFields(OrganizationKYCFields kycFields) {
        this.kycFields = kycFields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStokvelId() {
        return stokvelId;
    }

    public void setStokvelId(String stokvelId) {
        this.stokvelId = stokvelId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Member getAdminMember() {
        return adminMember;
    }

    public void setAdminMember(Member adminMember) {
        this.adminMember = adminMember;
    }
}
