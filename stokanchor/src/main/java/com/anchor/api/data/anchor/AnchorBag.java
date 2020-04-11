package com.anchor.api.data.anchor;

public class AnchorBag {
    String assetCode, assetAmount, password, fundingSeed, startingBalance;
    Anchor anchor;

    public AnchorBag(String assetCode, String assetAmount, String password, String fundingSeed, String startingBalance, Anchor anchor) {
        this.assetCode = assetCode;
        this.assetAmount = assetAmount;
        this.password = password;
        this.fundingSeed = fundingSeed;
        this.startingBalance = startingBalance;
        this.anchor = anchor;
    }

    public AnchorBag() {
    }

    public String getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(String startingBalance) {
        this.startingBalance = startingBalance;
    }

    public String getFundingSeed() {
        return fundingSeed;
    }

    public void setFundingSeed(String fundingSeed) {
        this.fundingSeed = fundingSeed;
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
