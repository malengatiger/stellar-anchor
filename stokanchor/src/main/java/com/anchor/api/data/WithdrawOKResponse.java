package com.anchor.api.data;

import java.util.UUID;

/*

POST TRANSFER_SERVER_SEP0024/transactions/withdraw/interactive
Content-Type: multipart/form-data

asset_code=USD&email_address=myaccount@gmail.com&account=GACW7NONV43MZIFHCOKCQJAKSJSISSICFVUJ2C6EZIW5773OU3HD64VI
1. Success: no additional information needed
Response code: 200 OK

🐳  🐳
This is the correct response if the anchor is able to execute the withdrawal
and needs no additional information about the user.
It should also be used if the anchor requires information about the user,
but the information has previously been submitted and accepted.

The response body should be a JSON object with the following fields:
 */
public class WithdrawOKResponse {
    private String id, account_id, memo_type, memo;
    private int eta;
    private float min_amount, max_amount, fee_fixed, fee_percent, fee_minimum;
    private Object extra_info;

    public WithdrawOKResponse(String account_id, String memo_type, String memo, int eta,
                              float min_amount, float max_amount, float fee_fixed, float fee_percent,
                              float fee_minimum, Object extra_info) {
        this.account_id = account_id;
        this.memo_type = memo_type;
        this.memo = memo;
        this.eta = eta;
        this.min_amount = min_amount;
        this.max_amount = max_amount;
        this.fee_fixed = fee_fixed;
        this.fee_percent = fee_percent;
        this.fee_minimum = fee_minimum;
        this.extra_info = extra_info;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
