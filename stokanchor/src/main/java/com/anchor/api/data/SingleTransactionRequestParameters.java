package com.anchor.api.data;

/*
        🥏 🥏 🥏 🥏 🥏 🥏 Single Historical Transaction
        The transaction endpoint enables clients to query/validate a specific transaction at an anchor.

        🍎 🍎 GET TRANSFER_SERVER/transaction
        Request parameters:

        Name	Type	Description
        id	                    string	(optional) The id of the transaction.
        stellar_transaction_id	(optional) string	The stellar transaction id of the transaction.
        external_transaction_id	(optional) string	The external transaction id of the transaction.

        🥏 One of id, stellar_transaction_id or external_transaction_id is required.

        🌼 🌼 🌼 🌼 🌼 🌼 🌼 🌼
        On success the endpoint should return 200 OK HTTP status code and a JSON object with the following fields:

        Name	    Type	Description
        transaction	object	The transaction that was requested by the client.

        The transaction object should be of the same form as the objects returned by the TRANSFER_SERVER/transactions endpoint.

  🍎 🍎 🍎 🍎 🍎 🍎 🍎 🍎 Example response
        {
          "transaction": {
              "id": "82fhs729f63dh0v4",
              "kind": "deposit",
              "status": "pending_external",
              "status_eta": 3600,
              "external_transaction_id": "2dd16cb409513026fbe7defc0c6f826c2d2c65c3da993f747d09bf7dafd31093",
              "amount_in": "18.34",
              "amount_out": "18.24",
              "amount_fee": "0.1",
              "started_at": "2017-03-20T17:05:32Z"
            }
        }
        🥬
        If the transaction cannot be found, the endpoint should return a 404 NOT FOUND result.
        Every HTTP status code other than 200 OK will be considered an error. An empty transaction list is not an error. The body should contain error details. For example:

        {
           "error": "This anchor doesn't support the given currency code: ETH"
        }
 */
public class SingleTransactionRequestParameters {
    private String id, stellar_transaction_id, external_transaction_id;

    public SingleTransactionRequestParameters(String id, String stellar_transaction_id, String external_transaction_id) {
        this.id = id;
        this.stellar_transaction_id = stellar_transaction_id;
        this.external_transaction_id = external_transaction_id;
    }

    public SingleTransactionRequestParameters() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
