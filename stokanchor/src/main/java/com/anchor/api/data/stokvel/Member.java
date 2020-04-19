package com.anchor.api.data.stokvel;

import com.anchor.api.data.transfer.sep9.PersonalKYCFields;

import java.util.List;

public class Member {
    private String date, memberId, stellarAccountId, url, fcmToken,
            externalAccountId, password, secretSeed;
    private boolean active;
    List<String> stokvelIds;
    PersonalKYCFields kycFields;

    public String getSecretSeed() {
        return secretSeed;
    }

    public void setSecretSeed(String secretSeed) {
        this.secretSeed = secretSeed;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        if (kycFields != null) {
            return  kycFields.getFirst_name() + " " + kycFields.getLast_name();
        }
        return null;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getStellarAccountId() {
        return stellarAccountId;
    }

    public void setStellarAccountId(String stellarAccountId) {
        this.stellarAccountId = stellarAccountId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getStokvelIds() {
        return stokvelIds;
    }

    public void setStokvelIds(List<String> stokvelIds) {
        this.stokvelIds = stokvelIds;
    }

    public PersonalKYCFields getKycFields() {
        return kycFields;
    }

    public void setKycFields(PersonalKYCFields kycFields) {
        this.kycFields = kycFields;
    }
}
