package com.anchor.api.data.transfer.sep10;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sep10 {

    @SerializedName("authentication_server")
    @Expose
    private String authenticationServer;
    @SerializedName("authentication_signing_key")
    @Expose
    private String authenticationSigningKey;

    public String getAuthenticationServer() {
        return authenticationServer;
    }

    public void setAuthenticationServer(String authenticationServer) {
        this.authenticationServer = authenticationServer;
    }

    public String getAuthenticationSigningKey() {
        return authenticationSigningKey;
    }

    public void setAuthenticationSigningKey(String authenticationSigningKey) {
        this.authenticationSigningKey = authenticationSigningKey;
    }

}
