package com.anchor.api.data;

public  class PaymentRequest {
    private String paymentRequestId, seed,
            assetCode,
            amount,
            date, anchorId,
            destinationAccount, sourceAccount, loanId;
    private Long ledger;

    public PaymentRequest() {
    }

    public PaymentRequest(String paymentRequestId, String seed, String assetCode,
                          String amount, String date, String anchorId, String destinationAccount,
                          String sourceAccount) {
        this.paymentRequestId = paymentRequestId;
        this.seed = seed;
        this.assetCode = assetCode;
        this.amount = amount;
        this.date = date;
        this.anchorId = anchorId;
        this.destinationAccount = destinationAccount;
        this.sourceAccount = sourceAccount;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getLedger() {
        return ledger;
    }

    public void setLedger(Long ledger) {
        this.ledger = ledger;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }
}
