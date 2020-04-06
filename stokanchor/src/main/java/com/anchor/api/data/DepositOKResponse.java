package com.anchor.api.data;

public class DepositOKResponse {
    String account_id, memo_type, memo;
    int eta;
    float min_amount, max_amount, fee_fixed, fee_percent, fee_minimum;
    Object extra_info;

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
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

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public float getMin_amount() {
        return min_amount;
    }

    public void setMin_amount(float min_amount) {
        this.min_amount = min_amount;
    }

    public float getMax_amount() {
        return max_amount;
    }

    public void setMax_amount(float max_amount) {
        this.max_amount = max_amount;
    }

    public float getFee_fixed() {
        return fee_fixed;
    }

    public void setFee_fixed(float fee_fixed) {
        this.fee_fixed = fee_fixed;
    }

    public float getFee_percent() {
        return fee_percent;
    }

    public void setFee_percent(float fee_percent) {
        this.fee_percent = fee_percent;
    }

    public float getFee_minimum() {
        return fee_minimum;
    }

    public void setFee_minimum(float fee_minimum) {
        this.fee_minimum = fee_minimum;
    }

    public Object getExtra_info() {
        return extra_info;
    }

    public void setExtra_info(Object extra_info) {
        this.extra_info = extra_info;
    }
}
