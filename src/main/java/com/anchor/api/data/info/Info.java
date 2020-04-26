package com.anchor.api.data.info;

import shadow.com.google.gson.Gson;
import shadow.com.google.gson.GsonBuilder;

public class Info {
    public static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private DepositInfo deposit;
    private WithdrawInfo withdraw;
    private Fee fee;
    private String anchorId;

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public DepositInfo getDeposit() {
        return deposit;
    }

    public void setDeposit(DepositInfo deposit) {
        this.deposit = deposit;
    }

    public WithdrawInfo getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(WithdrawInfo withdraw) {
        this.withdraw = withdraw;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }
    public String toJson() {
        return G.toJson(this);
    }

}
