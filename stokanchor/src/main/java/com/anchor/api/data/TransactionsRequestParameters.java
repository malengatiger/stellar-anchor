package com.anchor.api.data;

import org.stellar.sdk.responses.TransactionResponse;

/*
    🌼 🌼 🌼 🌼 🌼 Transaction History
    The transaction history endpoint helps anchors enable a better experience for users using an external wallet. With it, wallets can display the status of deposits and withdrawals while they process and a history of past transactions with the anchor. It's only for transactions that are deposits to or withdrawals from the anchor.

    GET TRANSFER_SERVER/transactions
    Request parameters:

    🍎 Name	        Type	Description
    asset_code	    string	The code of the asset of interest. E.g. BTC, ETH, USD, INR, etc.
    asset_issuer	string	The issuer of the asset the user wants to deposit with the anchor.
    account	        string	The stellar account ID involved in the transactions.
    no_older_than	string	UTC ISO 8601 (optional) The response should contain transactions starting on or after this date & time.
    limit	        int	    (optional) The response should contain at most limit transactions.
    kind	        string	(optional) The kind of transaction that is desired. Should be either deposit or withdrawal.
    paging_id	    string	(optional) The response should contain transactions starting prior to this ID (exclusive).
    🍎 🍎 🍎
 */
public class TransactionsRequestParameters {
    String asset_code, no_older_than, kind, paging_id;
    int limit;

    public String getAsset_code() {
        return asset_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getNo_older_than() {
        return no_older_than;
    }

    public void setNo_older_than(String no_older_than) {
        this.no_older_than = no_older_than;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPaging_id() {
        return paging_id;
    }

    public void setPaging_id(String paging_id) {
        this.paging_id = paging_id;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
