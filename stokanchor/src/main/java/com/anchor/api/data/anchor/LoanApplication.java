package com.anchor.api.data.anchor;

/**
 * 🌼 🌼 LoanApplication  🌼 🌼 🌼
 * 🍎 🍎 a micro to small loan from an Agent to a Client
 *
 * A LoanApplication is made by a Client and approved by both the Agent and Anchor.
 * The interestRate may be set by the Anchor or negotiated by the Agent with the Client
 */
public class LoanApplication {
    private String loanId, clientId, agentId, anchorId;
    private String date, amount;
    private int loanPeriodInMonths;
    private int startMonth, endMonth;
    private double interestRate;
    private boolean approved;

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

    public int getLoanPeriodInMonths() {
        return loanPeriodInMonths;
    }

    public void setLoanPeriodInMonths(int loanPeriodInMonths) {
        this.loanPeriodInMonths = loanPeriodInMonths;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
