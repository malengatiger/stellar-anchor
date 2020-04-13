package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.kyc.OrganizationKYCFields;
import com.anchor.api.data.kyc.PersonalKYCFields;

/**
    🍏 🛎 🛎 🛎 SEP 0026 🛎 Deposit Request Parameters
    🛎 POST TRANSFER_SERVER/deposit
    Content-Type: multipart/form-data

    🍏 🍏 🍏 Request Parameters:
    Name	        Type	Description
    asset_code	    string	The code of the asset the user wants to deposit with the anchor. E.g. BTC, ETH, USD, INR, etc. This should be the same asset code specified in the stellar.toml file.
    asset_issuer	string	The issuer of the asset the user wants to deposit with the anchor.
    account	G...    string	The stellar account ID of the user that wants to deposit. This is where the asset token will be sent to.
    memo_type	    string	(optional) Type of memo that the anchor should attach to the Stellar payment transaction, one of text, id or hash.
    memo	        string	(optional) Value of memo to attach to transaction, for hash this should be base64-encoded.
    email_address	string	(optional) Email address of depositor. If desired, an anchor can use this to send email updates to the user about the deposit.
    type	        string	(required) Deposit option. If the anchor supports one or multiple deposit methods (e.g. SEPA or SWIFT), the wallet should specify type. The type should be set to one of the deposit options defined above in the SEP-27 json response.
    wallet_name	    string	(optional) In communications / pages about the deposit, anchor should display the wallet name to the user to explain where funds are going.
    wallet_url	    string	(optional) Anchor should link to this when notifying the user that the transaction has completed.
    lang	        string	(optional) Defaults to en. Language code specified using ISO 639-1. error fields in the response should be in this language.

    🌺 🌺 Example:

    🍏 POST https://api.example.com/deposit
    Content-Type: multipart/form-data

 */
public class DepositRequestParameters {
    private String asset_code,
            asset_issuer,
            account,
            memo_type,
            memo,
            email_address,
            type,
            wallet_name,
            wallet_url, lang;

    private PersonalKYCFields personalKYCFields;
    private OrganizationKYCFields organizationKYCFields;

    public DepositRequestParameters() {
    }

    public DepositRequestParameters(String asset_code, String asset_issuer,
                                    String account, String memo_type, String memo,
                                    String email_address, String type,
                                    String wallet_name, String wallet_url,
                                    String lang, PersonalKYCFields personalKYCFields,
                                    OrganizationKYCFields organizationKYCFields) {
        this.asset_code = asset_code;
        this.asset_issuer = asset_issuer;
        this.account = account;
        this.memo_type = memo_type;
        this.memo = memo;
        this.email_address = email_address;
        this.type = type;
        this.wallet_name = wallet_name;
        this.wallet_url = wallet_url;
        this.lang = lang;
        this.personalKYCFields = personalKYCFields;
        this.organizationKYCFields = organizationKYCFields;
    }

    public String getAsset_issuer() {
        return asset_issuer;
    }

    public void setAsset_issuer(String asset_issuer) {
        this.asset_issuer = asset_issuer;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
