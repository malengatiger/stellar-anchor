package com.anchor.api.data;

import com.anchor.api.data.kyc.OrganizationKYCFields;
import com.anchor.api.data.kyc.PersonalKYCFields;

/*
Request
POST TRANSFER_SERVER_SEP0024/transactions/deposit/interactive
Content-Type: multipart/form-data
 */
public class DepositWithdrawRequestParameters {
    private String asset_code, account, memo_type,
            memo, wallet_name,
            wallet_url, lang;

    private PersonalKYCFields personalKYCFields;
    private OrganizationKYCFields organizationKYCFields;

    public DepositWithdrawRequestParameters() {
    }

    public DepositWithdrawRequestParameters(String asset_code, String account,
                                            String memo_type, String memo, String wallet_name,
                                            String wallet_url, String lang) {
        this.asset_code = asset_code;
        this.account = account;
        this.memo_type = memo_type;
        this.memo = memo;
        this.wallet_name = wallet_name;
        this.wallet_url = wallet_url;
        this.lang = lang;
    }

    public PersonalKYCFields getPersonalKYCFields() {
        return personalKYCFields;
    }

    public void setPersonalKYCFields(PersonalKYCFields personalKYCFields) {
        this.personalKYCFields = personalKYCFields;
    }

    public OrganizationKYCFields getOrganizationKYCFields() {
        return organizationKYCFields;
    }

    public void setOrganizationKYCFields(OrganizationKYCFields organizationKYCFields) {
        this.organizationKYCFields = organizationKYCFields;
    }

    public String getAsset_code() {
        return asset_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMemo_type() {
        return memo_type;
    }

    public void setMemo_type(String memo_type) {
        this.memo_type = memo_type;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getWallet_name() {
        return wallet_name;
    }

    public void setWallet_name(String wallet_name) {
        this.wallet_name = wallet_name;
    }

    public String getWallet_url() {
        return wallet_url;
    }

    public void setWallet_url(String wallet_url) {
        this.wallet_url = wallet_url;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
