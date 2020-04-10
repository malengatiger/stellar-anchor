package com.anchor.api.data.transfer.sep10;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
    🌺 🌺 🌺 Sep10 🌺 Simple Summary
    This SEP defines the standard way for clients such as wallets or exchanges to create authenticated web sessions on behalf of a user who holds a Stellar account. A wallet may want to authenticate with any web service which requires a Stellar account ownership verification, for example, to upload KYC information to an anchor in an authenticated way as described in SEP-12.

    🌺 Abstract
    This protocol is a variation of mutual challenge-response, which uses Stellar transactions to encode challenges and responses.

    🌼 🌼 The authentication flow is as follows:

    The client obtains a unique challenge, which is represented as specially formed Stellar transaction
    The client verifies that the transaction has an invalid sequence number 0. This is extremely important to ensure the transaction isn't malicious.
    The client signs the transaction using the secret key(s) of signers for the user's Stellar account
    The client submits the signed challenge back to the server using token endpoint
    The server checks that the user's account exists
    If the user's account exists:
    The server gets the signers of the user's account
    The server verifies the client signatures count is one or more;
    The server verifies the client signatures on the transaction are signers of the user's account;
    The server verifies the weight provided by the signers meets the required threshold(s), if any
    If the signatures check out, the server responds with a JWT that represents the user's session
    If the user's account does not exist (optional):
    The server verifies the client signature count is one
    The server verifies the client signature is correct for the master key of the account
    Any future calls to the server can be authenticated by including the JWT as a parameter

    🌼 🌼 The flow achieves several things:

    Both client and server part can be implemented using well-established Stellar libraries
    The client can verify that the server holds the secret key to a particular account
    The server can verify that the client holds the secret key(s) to signers of their account
    The client is able to prove their identity using a Ledger or other hardware wallet as well as by having direct access to the secret key(s)
    The server can choose its own timeout for the user's session
    The server can choose required threshold(s) that the user needs on the account, if any
    The server can choose to include other application specific claims
    The server can choose to authenticate accounts that do not yet exist

    🌼 🌼
 */
public class Sep10 {

    @SerializedName("authentication_server")
    @Expose
    private String authenticationServer;
    @SerializedName("authentication_signing_key")
    @Expose
    private String authenticationSigningKey;

    public String getAuthenticationServer() {
        return authenticationServer;
    }

    public void setAuthenticationServer(String authenticationServer) {
        this.authenticationServer = authenticationServer;
    }

    public String getAuthenticationSigningKey() {
        return authenticationSigningKey;
    }

    public void setAuthenticationSigningKey(String authenticationSigningKey) {
        this.authenticationSigningKey = authenticationSigningKey;
    }

}
