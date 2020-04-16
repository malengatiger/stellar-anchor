package com.anchor.api.data.anchor;

import com.anchor.api.data.transfer.sep9.PersonalKYCFields;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
    🍎 🍎 The Client wraps the functionality of Stellar Protocol SEP 009 and extends it for other Anchor related shit.
         Client is created by Agent and will accept loans and other interaction with the Agent

    🌼 🌼 SEP 009, and 🥦 WHAT THE FUCK!! 🥦 weirdly looks like SEP 012 🌼 🌼 🌼 🌼 🌼 🌼 🌼 🌼
    This SEP defines a standard way for stellar wallets to upload KYC (or other) information to anchors
    that implement non-interactive SEP-6.

    🍎 This SEP was made with these goals in mind:

        interoperability
        Allow a user to enter their KYC information once and use it across many anchors without re-entering information manually
        handle the most common 80% of use cases
        handle image and binary data
        support the set of fields defined in SEP-9
        support authentication via SEP-10
        give users control over their data by supporting complete data erasure

    🍏 🍏
    To support this protocol an anchor acts as a server and implements the specified REST API endpoints,
    while a wallet implements a client that consumes the API. The goal is interoperability,
    so a wallet implements a single client according to the protocol, and will be able to interact with any compliant anchor.
    Similarly, an anchor that implements the API endpoints according to the protocol will work with any compliant wallet.

    🍎 Prerequisites
    An anchor must define the location of their 🌼 KYC_SERVER or TRANSFER_SERVER in their stellar.toml.
    This is how a client app knows where to find the anchor's server. A client app will send KYC requests to the KYC_SERVER if it is specified, otherwise to the TRANSFER_SERVER.
    Anchors and clients must support SEP-10 web authentication and use it for all SEP-12 endpoints.
    API Endpoints
    PUT /customer: Idempotent upload of customer info
    DELETE /customer: Idempotent upload of customer info


    🌼 🌼 SEP 009 🌼  SEP 012 ??? 🌼 Customer PUT
    Upload customer information to an anchor in an authenticated and idempotent fashion.

    PUT [KYC_SERVER || TRANSFER_SERVER]/customer
    Content-Type: multipart/form-data

    🌼 🌼 Request
    The fields below should be placed in the request body using the multipart/form-data encoding.

    Name	        Type	Description
    account	G...    string	The Stellar account ID to upload KYC data for
    memo	        string	(optional) Uniquely identifies individual customer in schemes where multiple wallet users share one Stellar address. If included, the KYC data will only apply to deposit/withdraw requests that include this memo.
    memo_type	    string	(optional) type of memo. One of text, id or hash

    🍎 The wallet should also transmit one or more of the fields listed in SEP-9,
    depending on what the anchor has indicated it needs.

    When uploading data for fields specificed in SEP-9,
    binary type fields (typically files) should be submitted after all other fields.
    The reason for this is that some web servers require binary fields at the end so that they know when
    they can begin processing the request as a stream.

    🍏 🍏 Response
    If the anchor received and stored the data successfully, it should respond with a 202 Accepted HTTP status code
    and an empty body.

    🍎 Every other HTTP status code will be considered an error. The body should contain error details. For example:
        {
           "error": "'photo_id_front' cannot be decoded. Must be jpg or png."
        }
    🌼 🌼 SEP 009 🌼 🌼 Customer DELETE
    🍎 Delete all personal information that the anchor has stored about a given customer. [account] is the Stellar account ID (G...) of the customer to delete. This request must be authenticated (via SEP-10) as coming from the owner of the account that will be deleted.

    🍏 🍏  Request
    DELETE [KYC_SERVER || TRANSFER_SERVER]/customer/[account]

    🍏 🍏 🍏 🍏 DELETE Responses
    🍏 🍏 Situation	                            Response
    Success	                                    200 OK
    User not authenticated properly	            401 Unauthorized
    Anchor has no information on the customer   404 Not Found
 */
/**
 * Client is created by Agent and will accept loans
 */
public class Client {
    private String anchorId,
            agentId,
            anchorName,
            clientId,
            agentName;
    private double latitude, longitude;
    private String dateRegistered,
            dateUpdated,
            externalAccountId,
            account,
            memo,
            password,
            secretSeed;

    private String memo_type;
    private PersonalKYCFields personalKYCFields;

    public PersonalKYCFields getPersonalKYCFields() {
        return personalKYCFields;
    }
    public void setPersonalKYCFields(PersonalKYCFields personalKYCFields) {
        this.personalKYCFields = personalKYCFields;
    }
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo_type() {
        return memo_type;
    }

    public void setMemo_type(String memo_type) {
        this.memo_type = memo_type;
    }

    public String getSecretSeed() {
        return secretSeed;
    }

    public void setSecretSeed(String secretSeed) {
        this.secretSeed = secretSeed;
    }

    public String getClientId() {
        return clientId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getFullName() {
        if (personalKYCFields != null) {
            return personalKYCFields.getFirst_name() + " " + personalKYCFields.getLast_name();
        }
        return null;
    }


    public String getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(String dateRegistered) {
        this.dateRegistered = dateRegistered;
    }
}
