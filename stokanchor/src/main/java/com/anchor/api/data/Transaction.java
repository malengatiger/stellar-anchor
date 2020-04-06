package com.anchor.api.data;
/*
    Shared fields for both deposits and withdrawals
    🌼 🌼 status should be one of:

    incomplete -- there is not yet enough information for this transaction to be initiated. Perhaps the user has not yet entered necessary info in an interactive flow.
    pending_user_transfer_start -- the user has not yet initiated their transfer to the anchor. This is the necessary first step in any deposit or withdrawal flow.
    pending_external -- deposit/withdrawal has been submitted to external network, but is not yet confirmed. This is the status when waiting on Bitcoin or other external crypto network to complete a transaction, or when waiting on a bank transfer.
    pending_anchor -- deposit/withdrawal is being processed internally by anchor.
    pending_stellar -- deposit/withdrawal operation has been submitted to Stellar network, but is not yet confirmed.
    pending_trust -- the user must add a trust-line for the asset for the deposit to complete.
    pending_user -- the user must take additional action before the deposit / withdrawal can complete, for example an email or 2fa confirmation of a withdraw.
    completed -- deposit/withdrawal fully completed.
    no_market -- could not complete deposit because no satisfactory asset/XLM market was available to create the account.
    too_small -- deposit/withdrawal size less than min_amount.
    too_large -- deposit/withdrawal size exceeded max_amount.
    error -- catch-all for any error not enumerated above.
 */
public class Transaction {
    String id,
            kind, status,
            more_info_url,
            amount_in, amount_out,
            amount_fee, started_at,
            completed_at, stellar_transaction_id,
            external_transaction_id, message;
    boolean refunded;
    int status_eta;
    //fields for deposit
    String deposit_memo, deposit_memo_type, from, to;
    //fields for withdrawal
    String withdraw_anchor_account, withdraw_memo_type;

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
