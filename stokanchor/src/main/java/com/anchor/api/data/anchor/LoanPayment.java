package com.anchor.api.data.anchor;

/**
 * 🌼 🌼 LoanPayment  🌼 🌼 🌼
 * 🍎 a payment by a Client against a Loan granted to them by an Agent
 *
 */
public class LoanPayment {
    private String loanId, clientId, agentId, anchorId;
    private String date, amount, clientSeed, agentAccount, assetCode;
    private boolean completed;
    private boolean onTime;
    private int monthOfLoan, yearOfLoan;

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAgentAccount() {
        return agentAccount;
    }

    public void setAgentAccount(String agentAccount) {
        this.agentAccount = agentAccount;
    }

    public String getClientSeed() {
        return clientSeed;
    }

    public void setClientSeed(String clientSeed) {
        this.clientSeed = clientSeed;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isOnTime() {
        return onTime;
    }

    public void setOnTime(boolean onTime) {
        this.onTime = onTime;
    }

    public int getMonthOfLoan() {
        return monthOfLoan;
    }

    public void setMonthOfLoan(int monthOfLoan) {
        this.monthOfLoan = monthOfLoan;
    }

    public int getYearOfLoan() {
        return yearOfLoan;
    }

    public void setYearOfLoan(int yearOfLoan) {
        this.yearOfLoan = yearOfLoan;
    }
}
