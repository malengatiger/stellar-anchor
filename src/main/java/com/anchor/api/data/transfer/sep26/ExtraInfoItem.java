package com.anchor.api.data.transfer.sep26;

public class ExtraInfoItem {
    String key, value;

    public ExtraInfoItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
