package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.GetTransactionsResponse;
import com.anchor.api.data.transfer.sep10.Sep10;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
    🌼 🌼 🌼 SEP 0026 🌼 🌼 GET TRANSFER_SERVER/transactions
    🌼 Request parameters:

    Name	Type	Description
    asset_code	    string	The code of the asset of interest. E.g. BTC, ETH, USD, INR, etc.
    asset_issuer	string	The issuer of the asset the user wants to deposit with the anchor.
    account	        string	The stellar account ID involved in the transactions.
    no_older_than	string UTC ISO 8601 string	(optional) The response should contain transactions starting on or after this date & time.
    limit	        int	(optional) The response should contain at most limit transactions.
    kind	        string	(optional) The kind of transaction that is desired. Should be either deposit or withdrawal.
    paging_id	    string	(optional) The response should contain transactions starting prior to this ID (exclusive).

    😡 😡
    On success the endpoint should return 200 OK HTTP status code and a JSON object with the following fields:
    💧💧💧💧💧
    Name	        Type	Description
    transactions	array	List of transactions as requested by the client, sorted in time-descending order.
 */
public class TransactionHistory {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("authentication_protocols")
    @Expose
    private List<Sep10> authenticationProtocols;

    private List<GetTransactionsResponse> transactions;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Sep10> getAuthenticationProtocols() {
        return authenticationProtocols;
    }

    public void setAuthenticationProtocols(List<Sep10> authenticationProtocols) {
        this.authenticationProtocols = authenticationProtocols;
    }

    public List<GetTransactionsResponse> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<GetTransactionsResponse> transactions) {
        this.transactions = transactions;
    }
}
