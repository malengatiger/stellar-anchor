package com.anchor.api.data.kyc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
 * 💧 💧 💧 💧 💧 💧 💧 💧 😡 SEP009 😡 Simple Summary
 * This SEP defines a list of standard KYC and AML fields for use in Stellar ecosystem
 * protocols. Issuers; banks; and other entities on Stellar should use these fields
 * when sending or requesting KYC / AML information with other parties on Stellar.
 * This is an evolving list; so please suggest any missing fields that you use.
 * <p>
 * 🥬 🥬
 * This is a list of possible fields that may be necessary to handle many different use cases;
 * there is no expectation that any particular fields be used for a particular application.
 * The best fields to use in a particular case is determined by the needs of the KYC or AML application.
 * <p>
 * 🎽 Encodings
 * ISO encodings are used for fields wherever possible. The table below lists the encodings used for different types of information.
 * <p>
 * 🎽 Field Type	Number of characters	Format / Encoding
 * language	    2	                    ISO 639-1
 * country	        3	                    ISO 3166-1 alpha-3
 * date	        10	                    ISO 8601 date-only format
 * phone number	varies	                E.164
 * occupation	    3	                    ISCO08
 * <p>
 * 🎽 KYC / AML Fields
 * 🎽 Natural Person KYC fields
 * <p>
 * 🛎 Name	                        Type	Description
 * family_name or last_name	    string	Family or last name
 * given_name or first_name	    string	Given or first name
 * additional_name	                string	Middle name or other additional name
 * address_country_code	        country code	country code for current address
 * state_or_province	            string	name of state/province/region/prefecture
 * city	                        string	name of city/town
 * postal_code	                    string	Postal or other code identifying user's locale
 * address	                        string	Entire address (country; state; postal code; street address; etc...) as a multi-line string
 * mobile_number	                string  phone number	Mobile phone number with country code; in E.164 format
 * email_address	                string	Email address
 * birth_date	                    date	Date of birth; e.g. 1976-07-04
 * birth_place                 	string	Place of birth (city; state; country; as on passport)
 * birth_country_code	            string  country code	ISO Code of country of birth
 * bank_account_number	            string	Number identifying bank account
 * bank_number	                    string	Number identifying bank in national banking system (routing number in US)
 * bank_phone_number	            string	Phone number with country code for bank
 * tax_id	                        string	Tax identifier of user in their country (social security number in US)
 * tax_id_name	                    string	Name of the tax ID (SSN or ITIN in the US)
 * occupation	                    number	Occupation ISCO code
 * employer_name	                string	Name of employer
 * employer_address	            string	Address of employer
 * language_code	                string  language code	primary language
 * id_type	                        string	passport; drivers_license; id_card; etc...
 * id_country_code	                string  country code	country issuing passport or photo ID as ISO 3166-1 alpha-3 code
 * id_issue_date	                date	ID issue date
 * id_expiration_date	            date	ID expiration date
 * id_number	                    string	Passport or ID number
 * photo_id_front	                binary	Image of front of user's photo ID or passport
 * photo_id_back	                binary	Image of back of user's photo ID or passport
 * notary_approval_of_photo_id	    binary	Image of notary's approval of photo ID or passport
 * ip_address	                    string	IP address of customer's computer
 * photo_proof_residence	        binary	Image of a utility bill; bank statement or similar with the user's name and address
 * 💧 💧 💧 💧 💧 💧 💧 💧 💧 💧
 */
public class PersonalKYCFields {
    @SerializedName("last_name")
    @Expose
    String lastName;
    @SerializedName("first_name")
    @Expose
    String firstName;
    @SerializedName("mobile_number")
    @Expose
    String mobileNumber;
    @SerializedName("email_address")
    @Expose
    String emailAddress;
    @SerializedName("birth_date")
    @Expose
    String birthDate;
    @SerializedName("bank_account_number")
    @Expose
    String bankAccountNumber;

    @SerializedName("bank_number")
    @Expose
    String bankNumber;

    @SerializedName("address")
    @Expose
    String address;

    @SerializedName("bank_phone_number")
    @Expose
    String bankPhoneNumber;

    @SerializedName("id_type")
    @Expose
    String idType;

    @SerializedName("id_country_code")
    @Expose
    String idCountryCode;

    @SerializedName("id_issue_date")
    @Expose
    String idIssueDate;

    @SerializedName("id_number")
    @Expose
    String idNumber;

    @SerializedName("language_code")
    @Expose
    String languageCode;


    @SerializedName("tax_id")
    @Expose
    String taxId;

    @SerializedName("tax_id_name")
    @Expose
    String taxIdName;

    //binary fields
    @SerializedName("photo_proof_residence")
    @Expose
    byte[] photoProofOfResidence;

    @SerializedName("photo_id_front")
    @Expose
    byte[] photoIdFront;

    @SerializedName("photo_id_back")
    @Expose
    byte[] photoIdBack;

    @SerializedName("notary_approval_of_photo_id")
    @Expose
    byte[] notaryApprovalOfPhotoId;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankPhoneNumber() {
        return bankPhoneNumber;
    }

    public void setBankPhoneNumber(String bankPhoneNumber) {
        this.bankPhoneNumber = bankPhoneNumber;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdCountryCode() {
        return idCountryCode;
    }

    public void setIdCountryCode(String idCountryCode) {
        this.idCountryCode = idCountryCode;
    }

    public String getIdIssueDate() {
        return idIssueDate;
    }

    public void setIdIssueDate(String idIssueDate) {
        this.idIssueDate = idIssueDate;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTaxIdName() {
        return taxIdName;
    }

    public void setTaxIdName(String taxIdName) {
        this.taxIdName = taxIdName;
    }

    public byte[] getPhotoProofOfResidence() {
        return photoProofOfResidence;
    }

    public void setPhotoProofOfResidence(byte[] photoProofOfResidence) {
        this.photoProofOfResidence = photoProofOfResidence;
    }

    public byte[] getPhotoIdFront() {
        return photoIdFront;
    }

    public void setPhotoIdFront(byte[] photoIdFront) {
        this.photoIdFront = photoIdFront;
    }

    public byte[] getPhotoIdBack() {
        return photoIdBack;
    }

    public void setPhotoIdBack(byte[] photoIdBack) {
        this.photoIdBack = photoIdBack;
    }

    public byte[] getNotaryApprovalOfPhotoId() {
        return notaryApprovalOfPhotoId;
    }

    public void setNotaryApprovalOfPhotoId(byte[] notaryApprovalOfPhotoId) {
        this.notaryApprovalOfPhotoId = notaryApprovalOfPhotoId;
    }
}
