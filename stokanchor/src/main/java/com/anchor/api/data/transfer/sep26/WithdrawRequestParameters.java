package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.kyc.OrganizationKYCFields;
import com.anchor.api.data.kyc.PersonalKYCFields;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
 * 🍀 🛎 🛎 🛎 SEP 002 🛎 Withdraw Request Parameters
 * 🛎 POST TRANSFER_SERVER/withdraw
 * Content-Type: multipart/form-data
 * Request parameters:
 * <p>
 * 🍀 Name	        Type	Description
 * asset_code	    string	Code of the asset the user wants to withdraw. This must match the asset code issued by the anchor. For example, if a user withdraws MyBTC tokens and receives BTC, the asset_code must be MyBTC.
 * asset_issuer	string	The issuer of the asset the user wants to deposit with the anchor.
 * amount	        number	The amount the user wishes to withdraw. Some anchors may use the amount to withdraw to determine fees.
 * type	        string	Type of withdrawal. Can be: crypto, bank_account, cash, mobile, bill_payment or other custom values.
 * dest	        string	The account that the user wants to withdraw their funds to. This can be a crypto account, a bank account number, IBAN, mobile number, or email address.
 * account	G...    string	The stellar account ID of the user that wants to do the withdrawal. This can be used by the anchor to calculate fees based on the account. It might also be needed if the anchor requires KYC information for withdrawal. The anchor can use account to look up the user's KYC information.
 * memo	        string	(optional) A wallet will send this to uniquely identify a user if the wallet has multiple users sharing one Stellar account. The anchor can use this along with account to look up the user's KYC info.
 * memo_type	    string	(optional) Type of memo. One of text, id or hash.
 * wallet_name	    string	(optional) In communications / pages about the withdrawal, anchor should display the wallet name to the user to explain where funds are coming from.
 * wallet_url	    string	(optional) Anchor can show this to the user when referencing the wallet involved in the withdrawal (ex. in the anchor's transaction history).
 * lang	        string	(optional) Defaults to en. Language code specified using ISO 639-1. error fields in the response should be in this language.
 * <p>
 * 🌼 🌼 🌼 Example:
 * <p>
 * 🍀 POST https://api.example.com/withdraw
 * Content-Type: multipart/form-data
 */
public class WithdrawRequestParameters {
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
    private String memo,
            dest,
            type;
    @SerializedName("wallet_name")
    @Expose
    private String walletName;
    @SerializedName("wallet_url")
    @Expose
    private String walletUrl;
    private String lang;
    private double amount;

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

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
