package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.transfer.sep10.Sep10;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Deposit {
    public Deposit() {
    }

    public Deposit(Boolean enabled, List<Sep10> authenticationProtocols, List<Option> options) {
        this.enabled = enabled;
        this.authenticationProtocols = authenticationProtocols;
        this.options = options;
    }

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("authentication_protocols")
    @Expose
    private List<Sep10> authenticationProtocols;
    @SerializedName("options")
    @Expose
    private List<Option> options = null;

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

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

}
