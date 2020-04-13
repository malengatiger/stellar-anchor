package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.kyc.OrganizationKYCFields;
import com.anchor.api.data.kyc.PersonalKYCFields;

/**
    🍀 🛎 🛎 🛎 SEP 002 🛎 Withdraw Request Parameters
    🛎 POST TRANSFER_SERVER/withdraw
    Content-Type: multipart/form-data
        Request parameters:

    🍀 Name	        Type	Description
    asset_code	    string	Code of the asset the user wants to withdraw. This must match the asset code issued by the anchor. For example, if a user withdraws MyBTC tokens and receives BTC, the asset_code must be MyBTC.
    asset_issuer	string	The issuer of the asset the user wants to deposit with the anchor.
    amount	        number	The amount the user wishes to withdraw. Some anchors may use the amount to withdraw to determine fees.
    type	        string	Type of withdrawal. Can be: crypto, bank_account, cash, mobile, bill_payment or other custom values.
    dest	        string	The account that the user wants to withdraw their funds to. This can be a crypto account, a bank account number, IBAN, mobile number, or email address.
    account	G...    string	The stellar account ID of the user that wants to do the withdrawal. This can be used by the anchor to calculate fees based on the account. It might also be needed if the anchor requires KYC information for withdrawal. The anchor can use account to look up the user's KYC information.
    memo	        string	(optional) A wallet will send this to uniquely identify a user if the wallet has multiple users sharing one Stellar account. The anchor can use this along with account to look up the user's KYC info.
    memo_type	    string	(optional) Type of memo. One of text, id or hash.
    wallet_name	    string	(optional) In communications / pages about the withdrawal, anchor should display the wallet name to the user to explain where funds are coming from.
    wallet_url	    string	(optional) Anchor can show this to the user when referencing the wallet involved in the withdrawal (ex. in the anchor's transaction history).
    lang	        string	(optional) Defaults to en. Language code specified using ISO 639-1. error fields in the response should be in this language.

    🌼 🌼 🌼 Example:

    🍀 POST https://api.example.com/withdraw
    Content-Type: multipart/form-data
 */
public class WithdrawRequestParameters {
    private String asset_code,
            asset_issuer,
            account,
            memo_type,
            memo,
            dest,
            type,
            wallet_name,
            wallet_url, lang;
    private double amount;

    public WithdrawRequestParameters() {
    }

    public WithdrawRequestParameters(String asset_code, String asset_issuer, String account, String memo_type, String memo, String dest, String type,
                                     String wallet_name, String wallet_url, String lang,
                                     double amount) {
        this.asset_code = asset_code;
        this.asset_issuer = asset_issuer;
        this.account = account;
        this.memo_type = memo_type;
        this.memo = memo;
        this.dest = dest;
        this.type = type;
        this.wallet_name = wallet_name;
        this.wallet_url = wallet_url;
        this.lang = lang;
        this.amount = amount;
    }

    public String getAsset_issuer() {
        return asset_issuer;
    }

    public void setAsset_issuer(String asset_issuer) {
        this.asset_issuer = asset_issuer;
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
