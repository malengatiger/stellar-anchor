package com.anchor.api.data.transfer.sep26;

import java.util.List;

import com.anchor.api.data.transfer.sep10.Sep10;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionHistory {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("authentication_protocols")
    @Expose
    private List<Sep10> authenticationProtocols;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Sep10> getAuthenticationProtocols() {
        return authenticationProtocols;
    }

    public void setAuthenticationProtocols(List<Sep10> authenticationProtocols) {
        this.authenticationProtocols = authenticationProtocols;
    }
}
