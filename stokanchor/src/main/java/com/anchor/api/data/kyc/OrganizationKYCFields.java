package com.anchor.api.data.kyc;

/**
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
    String name, VAT_number, registration_number, registered_address, shareholder_name, address,
            address_country_code, city, state_or_province, email, website, phone, director_name, postal_code;
    int number_of_shareholders;
    byte[] photo_incorporation_doc, photo_proof_address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVAT_number() {
        return VAT_number;
    }

    public void setVAT_number(String VAT_number) {
        this.VAT_number = VAT_number;
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    public String getRegistered_address() {
        return registered_address;
    }

    public void setRegistered_address(String registered_address) {
        this.registered_address = registered_address;
    }

    public String getShareholder_name() {
        return shareholder_name;
    }

    public void setShareholder_name(String shareholder_name) {
        this.shareholder_name = shareholder_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_country_code() {
        return address_country_code;
    }

    public void setAddress_country_code(String address_country_code) {
        this.address_country_code = address_country_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState_or_province() {
        return state_or_province;
    }

    public void setState_or_province(String state_or_province) {
        this.state_or_province = state_or_province;
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

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public int getNumber_of_shareholders() {
        return number_of_shareholders;
    }

    public void setNumber_of_shareholders(int number_of_shareholders) {
        this.number_of_shareholders = number_of_shareholders;
    }

    public byte[] getPhoto_incorporation_doc() {
        return photo_incorporation_doc;
    }

    public void setPhoto_incorporation_doc(byte[] photo_incorporation_doc) {
        this.photo_incorporation_doc = photo_incorporation_doc;
    }

    public byte[] getPhoto_proof_address() {
        return photo_proof_address;
    }

    public void setPhoto_proof_address(byte[] photo_proof_address) {
        this.photo_proof_address = photo_proof_address;
    }
}
