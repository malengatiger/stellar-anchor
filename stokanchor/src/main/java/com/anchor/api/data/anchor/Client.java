package com.anchor.api.data.anchor;

import com.anchor.api.data.kyc.PersonalKYCFields;

/**
 * Client is created by Agent and will accept loans
 */
public class Client {
    String anchorId, agentId, anchorName, clientId, agentName;
    double latitude, longitude;
    String dateRegistered, dateUpdated, externalAccountId, stellarAccountId, organizationId, password, secretSeed;
    PersonalKYCFields personalKYCFields;

    public PersonalKYCFields getPersonalKYCFields() {
        return personalKYCFields;
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

    public String getStellarAccountId() {
        return stellarAccountId;
    }

    public void setStellarAccountId(String stellarAccountId) {
        this.stellarAccountId = stellarAccountId;
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
