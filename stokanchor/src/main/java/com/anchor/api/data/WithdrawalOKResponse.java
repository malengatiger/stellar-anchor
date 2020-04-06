package com.anchor.api.data;

public class WithdrawalOKResponse {
    private String from, to, withdraw_anchor_account, withdraw_memo, withdraw_memo_type;
    private Status status;

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

    public String getWithdraw_memo() {
        return withdraw_memo;
    }

    public void setWithdraw_memo(String withdraw_memo) {
        this.withdraw_memo = withdraw_memo;
    }

    public String getWithdraw_memo_type() {
        return withdraw_memo_type;
    }

    public void setWithdraw_memo_type(String withdraw_memo_type) {
        this.withdraw_memo_type = withdraw_memo_type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
