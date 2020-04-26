package com.anchor.api.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
    GET TRANSFER_SERVER/transactions
    On success the endpoint should return 200 OK HTTP status code and a JSON object with the following fields:

        🍎 🍎 🍎
        Name	Type	Description
        transactions	array	List of transactions as requested by the client, sorted in time-descending order.

        🥏 🥏 🥏 Each object in the transactions array should have the following fields:

        Name	                    Type	Description
        id	                        string	Unique, anchor-generated id for the deposit/withdrawal.
        kind	                    string	deposit or withdrawal.
        status	                    string	Processing status of deposit/withdrawal.
        status_eta	                number	(optional) Estimated number of seconds until a status change is expected.
        more_info_url	            string	(optional) A URL the user can visit if they want more information about their account / status.
        amount_in	                string	(optional) Amount received by anchor at start of transaction as a string with up to 7 decimals. Excludes any fees charged before the anchor received the funds.
        amount_out	                string	(optional) Amount sent by anchor to user at end of transaction as a string with up to 7 decimals. Excludes amount converted to XLM to fund account and any external fees.
        amount_fee	                string	(optional) Amount of fee charged by anchor.
        from	                    string	(optional) Sent from address (perhaps BTC, IBAN, or bank account in the case of a deposit, Stellar address in the case of a withdrawal).
        to	                        string	(optional) Sent to address (perhaps BTC, IBAN, or bank account in the case of a withdrawal, Stellar address in the case of a deposit).
        external_extra	            string	(optional) Extra information for the external account involved. It could be a bank routing number, BIC, or store number for example.
        external_extra_text	        string	(optional) Text version of external_extra. This is the name of the bank or store.
        deposit_memo	            string	(optional) If this is a deposit, this is the memo (if any) used to transfer the asset to the to Stellar address
        deposit_memo_type	        string	(optional) Type for the deposit_memo.
        withdraw_anchor_account	    string	(optional) If this is a withdrawal, this is the anchor's Stellar account that the user transferred (or will transfer) their issued asset to.
        withdraw_memo	            string	(optional) Memo used when the user transferred to withdraw_anchor_account.
        withdraw_memo_type	        string	(optional) Memo type for withdraw_memo.
        started_at	                UTC ISO 8601 string	(optional) Start date and time of transaction.
        completed_at	            UTC ISO 8601 string	(optional) Completion date and time of transaction.
        stellar_transaction_id	    string	(optional) transaction_id on Stellar network of the transfer that either completed the deposit or started the withdrawal.
        external_transaction_id	    string	(optional) ID of transaction on external network that either started the deposit or completed the withdrawal.
        message	                    string	(optional) Human readable explanation of transaction status, if needed.
        refunded	                    boolean	(optional) Should be true if the transaction was refunded. Not including this field means the transaction was not refunded.

        🍎 🍎 🍎 🍎 🍎 status should be one of:

        completed -- deposit/withdrawal fully completed.
        pending_external -- deposit/withdrawal has been submitted to external network, but is not yet confirmed. This is the status when waiting on Bitcoin or other external crypto network to complete a transaction, or when waiting on a bank transfer.
        pending_anchor -- deposit/withdrawal is being processed internally by anchor.
        pending_stellar -- deposit/withdrawal operation has been submitted to Stellar network, but is not yet confirmed.
        pending_trust -- the user must add a trust-line for the asset for the deposit to complete.
        pending_user -- the user must take additional action before the deposit / withdrawal can complete.
        pending_user_transfer_start -- the user has not yet initiated their transfer to the anchor. This is the necessary first step in any deposit or withdrawal flow.
        incomplete -- there is not yet enough information for this transaction to be initiated. Perhaps the user has not yet entered necessary info in an interactive flow.
        no_market -- could not complete deposit because no satisfactory asset/XLM market was available to create the account.
        too_small -- deposit/withdrawal size less than min_amount.
        too_large -- deposit/withdrawal size exceeded max_amount.
        error -- catch-all for any error not enumerated above.

         🌼 🌼 🌼 🌼 Example response
            {
              "transactions": [
                {
                  "id": "82fhs729f63dh0v4",
                  "kind": "deposit",
                  "status": "pending_external",
                  "status_eta": 3600,
                  "external_transaction_id": "2dd16cb409513026fbe7defc0c6f826c2d2c65c3da993f747d09bf7dafd31093",
                  "amount_in": "18.34",
                  "amount_out": "18.24",
                  "amount_fee": "0.1",
                  "started_at": "2017-03-20T17:05:32Z"
                },
                {
                  "id": "82fhs729f63dh0v4",
                  "kind": "withdrawal",
                  "status": "completed",
                  "amount_in": "500",
                  "amount_out": "495",
                  "amount_fee": "3",
                  "started_at": "2017-03-20T17:00:02Z",
                  "completed_at": "2017-03-20T17:09:58Z",
                  "stellar_transaction_id": "17a670bc424ff5ce3b386dbfaae9990b66a2a37b4fbe51547e8794962a3f9e6a",
                  "external_transaction_id": "2dd16cb409513026fbe7defc0c6f826c2d2c65c3da993f747d09bf7dafd31093"
                }
              ]
            }

       🍎 🍎 🍎 Every HTTP status code other than 200 OK will be considered an error. An empty transaction list is not an error. The body should contain error details. For example:

            {
               "error": "This anchor doesn't support the given currency code: ETH"
            }
 */
public class GetTransactionsResponse {
    private String id;
    @SerializedName("more_info_url")
    @Expose
    private String moreInfoUrl;
    @SerializedName("amount_in")
    @Expose
    private String amountIn;

    @SerializedName("amount_out")
    @Expose
    private String amountOut;

    @SerializedName("amount_fee")
    @Expose
    private String amountFee;

    @SerializedName("started_at")
    @Expose
    private String startedAt;

    @SerializedName("completed_at")
    @Expose
    private String completedAt;

    @SerializedName("stellar_transaction_id")
    @Expose
    private String stellarTransactionId;

    @SerializedName("external_extra")
    @Expose
    private String externalExtra;

    @SerializedName("external_extra_text")
    @Expose
    private String externalExtraText;

    @SerializedName("external_transaction_id")
    @Expose
    private String externalTransactionId;
    private String message;
    boolean refunded;
    @SerializedName("status_eta")
    @Expose
    int statusETA;
    //fields for deposit
    @SerializedName("deposit_memo")
    @Expose
    private String depositMemo;
    @SerializedName("deposit_memo_type")
    @Expose
    private String depositMemoType;
    private String from, to;

    //fields for withdrawal
    @SerializedName("withdraw_anchor_account")
    @Expose
    private String withdrawAnchorAccount;

    @SerializedName("withdraw_memo_type")
    @Expose
    private String withdrawMemoType;

    @SerializedName("withdraw_memo")
    @Expose
    private String withdrawMemo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoreInfoUrl() {
        return moreInfoUrl;
    }

    public void setMoreInfoUrl(String moreInfoUrl) {
        this.moreInfoUrl = moreInfoUrl;
    }

    public String getAmountIn() {
        return amountIn;
    }

    public void setAmountIn(String amountIn) {
        this.amountIn = amountIn;
    }

    public String getAmountOut() {
        return amountOut;
    }

    public void setAmountOut(String amountOut) {
        this.amountOut = amountOut;
    }

    public String getAmountFee() {
        return amountFee;
    }

    public void setAmountFee(String amountFee) {
        this.amountFee = amountFee;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getStellarTransactionId() {
        return stellarTransactionId;
    }

    public void setStellarTransactionId(String stellarTransactionId) {
        this.stellarTransactionId = stellarTransactionId;
    }

    public String getExternalExtra() {
        return externalExtra;
    }

    public void setExternalExtra(String externalExtra) {
        this.externalExtra = externalExtra;
    }

    public String getExternalExtraText() {
        return externalExtraText;
    }

    public void setExternalExtraText(String externalExtraText) {
        this.externalExtraText = externalExtraText;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
    }

    public int getStatusETA() {
        return statusETA;
    }

    public void setStatusETA(int statusETA) {
        this.statusETA = statusETA;
    }

    public String getDepositMemo() {
        return depositMemo;
    }

    public void setDepositMemo(String depositMemo) {
        this.depositMemo = depositMemo;
    }

    public String getDepositMemoType() {
        return depositMemoType;
    }

    public void setDepositMemoType(String depositMemoType) {
        this.depositMemoType = depositMemoType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getWithdrawAnchorAccount() {
        return withdrawAnchorAccount;
    }

    public void setWithdrawAnchorAccount(String withdrawAnchorAccount) {
        this.withdrawAnchorAccount = withdrawAnchorAccount;
    }

    public String getWithdrawMemoType() {
        return withdrawMemoType;
    }

    public void setWithdrawMemoType(String withdrawMemoType) {
        this.withdrawMemoType = withdrawMemoType;
    }

    public String getWithdrawMemo() {
        return withdrawMemo;
    }

    public void setWithdrawMemo(String withdrawMemo) {
        this.withdrawMemo = withdrawMemo;
    }
}
