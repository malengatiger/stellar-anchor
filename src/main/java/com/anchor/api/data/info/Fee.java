package com.anchor.api.data.info;

public class Fee {
    private boolean enabled;

    public Fee(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
