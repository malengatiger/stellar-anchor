package com.anchor.api.data.account;

import org.joda.time.DateTime;
import org.stellar.sdk.responses.AccountResponse;

public class AccountResponseWithDate {
    private AccountResponse accountResponse;
    private String date;

    public AccountResponseWithDate(AccountResponse accountResponse, String date) {
        this.accountResponse = accountResponse;
        this.date = date;
        if (date == null) {
            DateTime dateTime = new DateTime();
            this.date = dateTime.toDateTimeISO().toString();
        }
    }

    public AccountResponse getAccountResponse() {
        return accountResponse;
    }

    public String getDate() {
        return date;
    }
}
