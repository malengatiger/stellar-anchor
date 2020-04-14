package com.anchor.api.data.anchor;

import com.anchor.api.data.transfer.sep9.PersonalKYCFields;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
    🌼 🌼 SEP 009 🌼 🌼 Customer PUT
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
    String anchorId, agentId, anchorName, clientId, agentName;
    double latitude, longitude;
    String dateRegistered, dateUpdated, externalAccountId, account,
            memo, organizationId, password, secretSeed;
    @SerializedName("memo_type")
    @Expose
    String memoType;
    PersonalKYCFields personalKYCFields;

    public PersonalKYCFields getPersonalKYCFields() {
        return personalKYCFields;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemoType() {
        return memoType;
    }

    public void setMemoType(String memoType) {
        this.memoType = memoType;
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
            return personalKYCFields.getFirstName() + " " + personalKYCFields.getLastName();
        }
        return null;
    }
    public void setPersonalKYCFields(PersonalKYCFields personalKYCFields) {
        this.personalKYCFields = personalKYCFields;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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
