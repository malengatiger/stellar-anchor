package com.anchor.api.data.kyc;

public class PersonalKYCFields {
    String last_name, first_name, mobile_number, email_address, birth_date, bank_account_number, bank_number, address,
    bank_phone_number, id_type, id_country_code, id_issue_date, id_number, language_code, tax_id, tax_id_name;
    byte[] photo_proof_residence, photo_id_front, photo_id_back, notary_approval_of_photo_id;

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
