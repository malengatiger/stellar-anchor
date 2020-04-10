package com.anchor.api.data;
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
    String id,
            kind, status,
            more_info_url,
            amount_in, amount_out,
            amount_fee, started_at,
            completed_at, stellar_transaction_id,
            external_extra, external_extra_text,
            external_transaction_id, message;
    boolean refunded;
    int status_eta;
    //fields for deposit
    String deposit_memo, deposit_memo_type, from, to;
    //fields for withdrawal
    String withdraw_anchor_account, withdraw_memo_type, withdraw_memo;

    public GetTransactionsResponse() {
    }

    public GetTransactionsResponse(String id, String kind, String status, String more_info_url,
                                   String amount_in, String amount_out, String amount_fee,
                                   String started_at, String completed_at,
                                   String stellar_transaction_id, String external_extra,
                                   String external_extra_text, String external_transaction_id,
                                   String message, boolean refunded,
                                   int status_eta, String deposit_memo, String deposit_memo_type,
                                   String from, String to, String withdraw_anchor_account,
                                   String withdraw_memo_type, String withdraw_memo) {
        this.id = id;
        this.kind = kind;
        this.status = status;
        this.more_info_url = more_info_url;
        this.amount_in = amount_in;
        this.amount_out = amount_out;
        this.amount_fee = amount_fee;
        this.started_at = started_at;
        this.completed_at = completed_at;
        this.stellar_transaction_id = stellar_transaction_id;
        this.external_extra = external_extra;
        this.external_extra_text = external_extra_text;
        this.external_transaction_id = external_transaction_id;
        this.message = message;
        this.refunded = refunded;
        this.status_eta = status_eta;
        this.deposit_memo = deposit_memo;
        this.deposit_memo_type = deposit_memo_type;
        this.from = from;
        this.to = to;
        this.withdraw_anchor_account = withdraw_anchor_account;
        this.withdraw_memo_type = withdraw_memo_type;
        this.withdraw_memo = withdraw_memo;
    }

    public String getDeposit_memo() {
        return deposit_memo;
    }

    public void setDeposit_memo(String deposit_memo) {
        this.deposit_memo = deposit_memo;
    }

    public String getDeposit_memo_type() {
        return deposit_memo_type;
    }

    public void setDeposit_memo_type(String deposit_memo_type) {
        this.deposit_memo_type = deposit_memo_type;
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

    public String getWithdraw_anchor_account() {
        return withdraw_anchor_account;
    }

    public void setWithdraw_anchor_account(String withdraw_anchor_account) {
        this.withdraw_anchor_account = withdraw_anchor_account;
    }

    public String getWithdraw_memo_type() {
        return withdraw_memo_type;
    }

    public void setWithdraw_memo_type(String withdraw_memo_type) {
        this.withdraw_memo_type = withdraw_memo_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMore_info_url() {
        return more_info_url;
    }

    public void setMore_info_url(String more_info_url) {
        this.more_info_url = more_info_url;
    }

    public String getAmount_in() {
        return amount_in;
    }

    public void setAmount_in(String amount_in) {
        this.amount_in = amount_in;
    }

    public String getAmount_out() {
        return amount_out;
    }

    public void setAmount_out(String amount_out) {
        this.amount_out = amount_out;
    }

    public String getAmount_fee() {
        return amount_fee;
    }

    public void setAmount_fee(String amount_fee) {
        this.amount_fee = amount_fee;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public String getStellar_transaction_id() {
        return stellar_transaction_id;
    }

    public void setStellar_transaction_id(String stellar_transaction_id) {
        this.stellar_transaction_id = stellar_transaction_id;
    }

    public String getExternal_transaction_id() {
        return external_transaction_id;
    }

    public void setExternal_transaction_id(String external_transaction_id) {
        this.external_transaction_id = external_transaction_id;
    }

    public String getExternal_extra() {
        return external_extra;
    }

    public void setExternal_extra(String external_extra) {
        this.external_extra = external_extra;
    }

    public String getExternal_extra_text() {
        return external_extra_text;
    }

    public void setExternal_extra_text(String external_extra_text) {
        this.external_extra_text = external_extra_text;
    }

    public String getWithdraw_memo() {
        return withdraw_memo;
    }

    public void setWithdraw_memo(String withdraw_memo) {
        this.withdraw_memo = withdraw_memo;
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

    public int getStatus_eta() {
        return status_eta;
    }

    public void setStatus_eta(int status_eta) {
        this.status_eta = status_eta;
    }
}
