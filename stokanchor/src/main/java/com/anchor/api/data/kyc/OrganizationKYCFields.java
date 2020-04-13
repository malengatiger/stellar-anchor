package com.anchor.api.data.kyc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
    🛎 🛎 🛎 🛎 🛎 SEP 0009 🛎 Organization KYC fields
    😡 😡 Name	                            Type	Description
    organization.name	                    string	Full organiation name as on the incorporation papers
    organization.VAT_number	                string	Organization VAT number
    organization.registration_number	    string	Organization registration number
    organization.registered_address	        string	Organization registered address
    organization.number_of_shareholders	    number	Organization shareholder number
    organization.shareholder_name	        string	Can be an organization or a person and should be queried recursively up to the ultimate beneficial owners (with KYC information for natural persons such as above)
    organization.photo_incorporation_doc	string	Image of incorporation documents
    organization.photo_proof_adress	        string	Image of a utility bill, bank statement with the organization's name and address
    organization.address_country_code	    country code	country code for current address
    organization.state_or_province	        string	name of state/province/region/prefecture
    organization.city	                    string	name of city/town
    organization.postal_code	            string	Postal or other code identifying organization's locale
    organization.director_name	            string	Organization registered managing director (the rest of the information should be queried as an individual using the fields above)
    organization.website	                string	Organization website
    organization.email	                    string	Organization contact email
    organization.phone	                    string	Organization contact phone

    🎽 🎽 Explanation
    Where possible we use field names from schema.org. Words are separated with underlines as that convention has previously been established in Stellar protocols.

    🥦 Addresses
    Address formatting varies widely from country to country and even within each country.
    See here for details. Rather than attempting to create a field for each possible part of an address in every country,
    this protocol takes a middle of the road approach. Address fields that are fairly universal can be encoded with the country_code,
    state_or_province, city, and postal_code fields.

    Full addresses, however, should be encoded as a single multi-line string in the address field.
    This allows any address in the world to be represented with a limited number of fields.
    If address parsing is necessary, parsing will be easier since the country, city, and postal code are already separate fields.
 */
public class OrganizationKYCFields {
    String name;
    @SerializedName("VAT_number")
    @Expose
    String  VATNumber;
    @SerializedName("registration_number")
    @Expose
    String  registrationNumber;
    @SerializedName("registered_address")
    @Expose
    String  registeredAddress;
    @SerializedName("shareholder_name")
    @Expose
    String  shareholderName;
    String  address;
    @SerializedName("address_country_code")
    @Expose
    String  addressCountryCode;
    String  city;
    @SerializedName("state_or_province")
    @Expose
    String  stateOrProvince;
    String  email;
    String  website;
    String  phone;
    String  director_name;
    @SerializedName("postal_code")
    @Expose
    String  postalCode;

    @SerializedName("number_of_shareholders")
    @Expose
    int numberOfShareholders;

    @SerializedName("photo_incorporation_doc")
    @Expose
    byte[] photoIncorporationDocument;

    @SerializedName("photo_proof_address")
    @Expose
    byte[] photoProofOfAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVATNumber() {
        return VATNumber;
    }

    public void setVATNumber(String VATNumber) {
        this.VATNumber = VATNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public String getShareholderName() {
        return shareholderName;
    }

    public void setShareholderName(String shareholderName) {
        this.shareholderName = shareholderName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressCountryCode() {
        return addressCountryCode;
    }

    public void setAddressCountryCode(String addressCountryCode) {
        this.addressCountryCode = addressCountryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDirector_name() {
        return director_name;
    }

    public void setDirector_name(String director_name) {
        this.director_name = director_name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getNumberOfShareholders() {
        return numberOfShareholders;
    }

    public void setNumberOfShareholders(int numberOfShareholders) {
        this.numberOfShareholders = numberOfShareholders;
    }

    public byte[] getPhotoIncorporationDocument() {
        return photoIncorporationDocument;
    }

    public void setPhotoIncorporationDocument(byte[] photoIncorporationDocument) {
        this.photoIncorporationDocument = photoIncorporationDocument;
    }

    public byte[] getPhotoProofOfAddress() {
        return photoProofOfAddress;
    }

    public void setPhotoProofOfAddress(byte[] photoProofOfAddress) {
        this.photoProofOfAddress = photoProofOfAddress;
    }
}
