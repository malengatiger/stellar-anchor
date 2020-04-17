package com.anchor.api.data.anchor;

import com.anchor.api.data.transfer.sep9.PersonalKYCFields;

/*
    🌼 🌼 AGENT 🌼 🌼 🌼
    The Agent is registered with an Anchor and is enabled, via a Flutter app, to provide micro to small loans
    within a community. The Agent may be funded by the Anchor to provide these loans or to manage group grants
    for some set of people.

    🍎  externalAccountId - optional, may refer to an external app or system
    🍎  organizationId - optional, refers Organization that this Agent belongs in
    🍎  secretSeed - returned to Agent at Registration and NEVER saved in database
 */
public class Agent {
    String anchorId, agentId, anchorName;
    double latitude, longitude;
    String dateRegistered,
            dateUpdated,
            externalAccountId,
            stellarAccountId,
            organizationId, fiatBalance, fiatLimit,
            password, secretSeed;

    PersonalKYCFields personalKYCFields;

    public PersonalKYCFields getPersonalKYCFields() {
        return personalKYCFields;
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

    public String getFiatBalance() {
        return fiatBalance;
    }

    public void setFiatBalance(String fiatBalance) {
        this.fiatBalance = fiatBalance;
    }

    public String getFiatLimit() {
        return fiatLimit;
    }

    public void setFiatLimit(String fiatLimit) {
        this.fiatLimit = fiatLimit;
    }

    public String getSecretSeed() {
        return secretSeed;
    }

    public void setSecretSeed(String secretSeed) {
        this.secretSeed = secretSeed;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
