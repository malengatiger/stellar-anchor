
package com.anchor.api.data.transfer.sep27;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/*
    🍅 🍅 Organization response fields.
    Optional, but anchors are recommended to provide more information regarding the organization anchoring an asset.

    🍅 Organization information is placed inside the root-level org field.

    Field Requirements	            Description
    name	                        string	Legal name of your organization
    dba	                            string	(may not apply) DBA of your organization
    url                             string	uses https://	Your organization's official URL. Your stellar.toml must be hosted on the same domain.
    logo	                        string  url	A PNG image of your organization's logo on a transparent background
    description	                    string	Short description of your organization
    physical_address	            string	Physical address for your organization
    physical_address_attestation	string  https:// url	URL on the same domain as your url that contains an image or pdf official document attesting to your physical address. It must list your name or dba as the party at the address. Only documents from an official third party are acceptable. E.g. a utility bill, mail from a financial institution, or business license.
    phone_number	                string	Your organization's phone number in E.164 format, e.g. +14155552671. See also this guide.
    phone_number_attestation	    string  https:// url	URL on the same domain as your url that contains an image or pdf of a phone bill showing both the phone number and your organization's name.
    keybase	                        string	A Keybase account name for your organization. Should contain proof of ownership of any public online accounts you list here, including your organization's domain.
    twitter	                        string	Your organization's Twitter account
    github	                        string	Your organization's Github account
    email	                        string	An email where clients can contact your organization. Must be hosted at your url domain.
    licensing_authority	            string	Name of the authority or agency that licensed your organization, if applicable
    license_type	                string	Type of financial or other license your organization holds, if applicable
    license_number	                string	Official license number of your organization, if applicable

    🌼 🌼 Issuers that list verified information including phone/address attestations and Keybase verifications will be prioritized by Stellar clients.

    🍅 🍅 Transfer protocols response fields.(Optional)

    Certain anchors may provide users with the option to deposit or withdraw assets in and out of
    the Stellar network, directly from their wallets. For such assets, the transaction_protocols field is an
    object containing all transfer protocols the anchor supports for that asset.

    Pure stellar assets that only exist within the Stellar network need not define this field.
 */
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

    @SerializedName("licensing_authority")
    @Expose
    private String licensingAuthority;

    @SerializedName("license_type")
    @Expose
    private String licenseType;

    @SerializedName("license_number")
    @Expose
    private String licenseNumber;

    @SerializedName("github")
    @Expose
    private String github;

    public String getLicensingAuthority() {
        return licensingAuthority;
    }

    public void setLicensingAuthority(String licensingAuthority) {
        this.licensingAuthority = licensingAuthority;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

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
