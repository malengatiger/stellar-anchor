package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.kyc.OrganizationKYCFields;
import com.anchor.api.data.kyc.PersonalKYCFields;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
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
    @SerializedName("asset_code")
    @Expose
    private String assetCode;
    @SerializedName("asset_issuer")
    @Expose
    private String assetIssuer;
    private String account;
    @SerializedName("memo_type")
    @Expose
    private String memoType;
    private String memo;
    @SerializedName("email_address")
    @Expose
    private String emailAddress;
    private String type;
    @SerializedName("wallet_name")
    @Expose
    private String walletName;
    @SerializedName("wallet_url")
    @Expose
    private String walletUrl;
    private String lang;

    private PersonalKYCFields personalKYCFields;
    private OrganizationKYCFields organizationKYCFields;

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetIssuer() {
        return assetIssuer;
    }

    public void setAssetIssuer(String assetIssuer) {
        this.assetIssuer = assetIssuer;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMemoType() {
        return memoType;
    }

    public void setMemoType(String memoType) {
        this.memoType = memoType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletUrl() {
        return walletUrl;
    }

    public void setWalletUrl(String walletUrl) {
        this.walletUrl = walletUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
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
}
