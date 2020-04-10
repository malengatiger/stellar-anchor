
package com.anchor.api.data.transfer.sep27;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Org {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("dba")
    @Expose
    private String dba;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("physical_address")
    @Expose
    private String physicalAddress;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("phone_number_attestation")
    @Expose
    private String phoneNumberAttestation;
    @SerializedName("keybase")
    @Expose
    private String keybase;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("email")
    @Expose
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDba() {
        return dba;
    }

    public void setDba(String dba) {
        this.dba = dba;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberAttestation() {
        return phoneNumberAttestation;
    }

    public void setPhoneNumberAttestation(String phoneNumberAttestation) {
        this.phoneNumberAttestation = phoneNumberAttestation;
    }

    public String getKeybase() {
        return keybase;
    }

    public void setKeybase(String keybase) {
        this.keybase = keybase;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
