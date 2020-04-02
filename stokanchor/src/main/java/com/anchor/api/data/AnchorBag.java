package com.anchor.api.data;

public class AnchorBag {
    String assetCode, assetAmount, password;
    Anchor anchor;

    public AnchorBag(String assetCode, String assetAmount, String password, Anchor anchor) {
        this.assetCode = assetCode;
        this.assetAmount = assetAmount;
        this.password = password;
        this.anchor = anchor;
    }

    public AnchorBag() {
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetAmount() {
        return assetAmount;
    }

    public void setAssetAmount(String assetAmount) {
        this.assetAmount = assetAmount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }
}
