package com.anchor.api.data.transfer.sep9;

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

    String last_name;

    String first_name;

    String mobile_number;

    String email_address;

    String birth_date;

    String bank_account_number;

    String bank_number;

    String address;

    String bank_phone_number;

    String id_type;

    String id_country_code;

    String id_issue_date;

    String id_number;

    String language_code;

    String tax_id;

    String tax_id_name;

    byte[] photo_proof_residence;

    byte[] photo_id_front;

    byte[] photo_id_back;

    byte[] notary_approval_of_photo_id;

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getBank_account_number() {
        return bank_account_number;
    }

    public void setBank_account_number(String bank_account_number) {
        this.bank_account_number = bank_account_number;
    }

    public String getBank_number() {
        return bank_number;
    }

    public void setBank_number(String bank_number) {
        this.bank_number = bank_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBank_phone_number() {
        return bank_phone_number;
    }

    public void setBank_phone_number(String bank_phone_number) {
        this.bank_phone_number = bank_phone_number;
    }

    public String getId_type() {
        return id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public String getId_country_code() {
        return id_country_code;
    }

    public void setId_country_code(String id_country_code) {
        this.id_country_code = id_country_code;
    }

    public String getId_issue_date() {
        return id_issue_date;
    }

    public void setId_issue_date(String id_issue_date) {
        this.id_issue_date = id_issue_date;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getTax_id_name() {
        return tax_id_name;
    }

    public void setTax_id_name(String tax_id_name) {
        this.tax_id_name = tax_id_name;
    }

    public byte[] getPhoto_proof_residence() {
        return photo_proof_residence;
    }

    public void setPhoto_proof_residence(byte[] photo_proof_residence) {
        this.photo_proof_residence = photo_proof_residence;
    }

    public byte[] getPhoto_id_front() {
        return photo_id_front;
    }

    public void setPhoto_id_front(byte[] photo_id_front) {
        this.photo_id_front = photo_id_front;
    }

    public byte[] getPhoto_id_back() {
        return photo_id_back;
    }

    public void setPhoto_id_back(byte[] photo_id_back) {
        this.photo_id_back = photo_id_back;
    }

    public byte[] getNotary_approval_of_photo_id() {
        return notary_approval_of_photo_id;
    }

    public void setNotary_approval_of_photo_id(byte[] notary_approval_of_photo_id) {
        this.notary_approval_of_photo_id = notary_approval_of_photo_id;
    }
}
