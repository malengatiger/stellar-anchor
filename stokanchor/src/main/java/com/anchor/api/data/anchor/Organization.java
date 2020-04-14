package com.anchor.api.data.anchor;

import com.anchor.api.data.transfer.sep9.OrganizationKYCFields;

public class Organization {
    String anchorId, organizationId, anchorName;
    double latitude, longitude;
    String dateRegistered, dateUpdated;
    OrganizationKYCFields organizationKYCFields;

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public OrganizationKYCFields getOrganizationKYCFields() {
        return organizationKYCFields;
    }

    public void setOrganizationKYCFields(OrganizationKYCFields organizationKYCFields) {
        this.organizationKYCFields = organizationKYCFields;
    }
}
