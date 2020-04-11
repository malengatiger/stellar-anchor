package com.anchor.api.services;

/*
The request object that will be sent.

Values in request have the following properties:

Parameter
name

string

Required. The resource name of the CryptoKey or CryptoKeyVersion to use for encryption.

If a CryptoKey is specified, the server will use its primary version.

plaintext

string

Required. The data to encrypt. Must be no larger than 64KiB.

additionalAuthenticatedData

Optional

string

Optional data that, if specified, must also be provided during decryption through DecryptRequest.additional_authenticated_data. Must be no larger than 64KiB.
 */
public class CryptoRequest {
    private String name, plaintext, additionalAuthenticatedData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(String plaintext) {
        this.plaintext = plaintext;
    }

    public String getAdditionalAuthenticatedData() {
        return additionalAuthenticatedData;
    }

    public void setAdditionalAuthenticatedData(String additionalAuthenticatedData) {
        this.additionalAuthenticatedData = additionalAuthenticatedData;
    }
}
