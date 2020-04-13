package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.transfer.sep10.Sep10;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
    🥦 🥦 🥦 SEP 0026 🥦 🥦 🥦 Simple Summary
    This SEP defines the standard way for wallets to interact with anchors on behalf of users.
    This improves user experience by allowing wallets and other clients to interact with anchors directly without the user needing to
    leave the wallet to go to the anchor's site.

    😡 It is based on, but backwards incompatible with SEP-0006.

    There are cases where the anchor service might wish that all interactions with users happen on a
    different app or web service hosted by the anchor itself.
    For such cases, please refer to the interactive workflow found in SEP-0024.

    💧 Abstract
    This proposal defines a standard protocol enabling the following features directly within a wallet or other Stellar client:

    💧 💧 💧 💧 💧 💧 💧 💧
    Deposit external assets with an anchor
    Withdraw assets from an anchor
    Communicate deposit & withdrawal fee structure for an anchor to the user
    Handle anchor KYC needs
    Check the status of ongoing deposits or withdrawals involving the user
    View history of deposits and withdrawals involving the user

    💧 💧 💧 💧 💧 💧 💧 💧
    To support this protocol, an anchor acts as a server and implements the specified REST API endpoints,
    while a wallet implements a client that consumes the API. The goal is interoperability,
    so a wallet implements a single client according to the protocol,
    and will be able to interact with any compliant anchor. Similarly, an anchor that implements the API endpoints according to the protocol
    will work with any compliant wallet.

    🛎 🛎 🛎 🛎 API Endpoints
        POST /deposit: required
        POST /withdraw: required
        GET /transactions: optional, but recommended
        GET /transaction: optional
    🛎 🛎 🛎 🛎
    🌺 🌺 🌺 🌺 Basic Anchor Implementation
    Decide which endpoints, if any, need to be authenticated.
    Pick your approach to [fees].
    Providing transaction status
    Provide the /transaction endpoint. The wallet may rely on it to complete non-interactive withdrawals.
    Provide the /transactions endpoint. Users like to see transaction histories.
    🌺 🌺 🌺 🌺

    🎽 🎽 🎽  Deposit and Withdraw shared responses
     1. Interactive customer information needed
     Response code: 403 Forbidden

     An anchor that requires the user to fill out one time information on a webpage hosted
     by the anchor should use this response. This can happen in situations where the anchor needs KYC information about a user, or needs a user to agree to Terms of Service. A wallet that receives this response should open a popup browser window or embedded webview to the specified URL. The anchor must take care that the popup page displays well on a mobile device, as many wallets are phone apps.

     🛎  Note that this is for one time information only.
     Please use SEP-24 instead if you need other custom steps interactive to be performed for every deposit and withdrawal. e.g. SMS confirmation before withdrawing.

      🍀  🍀  🍀  🍀 The response body must be a JSON object with the following fields:

     Name	Type	Description
     type	string	Always set to interactive_customer_info_needed.
     url	string	URL hosted by the anchor. The wallet should show this URL to the user either as a popup
                    or an iframe. We encourage anchors to make this url unique for each wallet,
                    and to include a required random short-lived session id for privacy and security reasons.
       🍀  🍀 Example response
        {
          "type": "interactive_customer_info_needed",
          "url" : "https://api.example.com/kycflow?account=GACW7NONV43MZIFHCOKCQJAKSJSISSICFVUJ2C6EZIW5773OU3HD64VI&session_id=short_lived_random_uuid",
        }
 */
public class Sep26 {
    public Sep26() {
    }

    public Sep26(String transferServer, List<Deposit> deposits,
                 List<Withdrawal> withdrawals, TransactionHistory transactionHistory,
                 List<Sep10> authenticationProtocols) {

        this.transferServer = transferServer;
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.transactionHistory = transactionHistory;
        this.authenticationProtocols = authenticationProtocols;
    }

    @SerializedName("transfer_server")
    @Expose
    private String transferServer;

    @SerializedName("deposits")
    @Expose
    private List<Deposit> deposits;

    @SerializedName("withdrawals")
    @Expose
    private List<Withdrawal> withdrawals;

    @SerializedName("transaction_history")
    @Expose
    private TransactionHistory transactionHistory;

    @SerializedName("authentication_protocols")
    @Expose
    private List<Sep10> authenticationProtocols;

    public String getTransferServer() {
        return transferServer;
    }

    public void setTransferServer(String transferServer) {
        this.transferServer = transferServer;
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    public void setDeposits(List<Deposit> deposits) {
        this.deposits = deposits;
    }

    public List<Withdrawal> getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(List<Withdrawal> withdrawals) {
        this.withdrawals = withdrawals;
    }

    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(TransactionHistory transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public List<Sep10> getAuthenticationProtocols() {
        return authenticationProtocols;
    }

    public void setAuthenticationProtocols(List<Sep10> authenticationProtocols) {
        this.authenticationProtocols = authenticationProtocols;
    }
}
