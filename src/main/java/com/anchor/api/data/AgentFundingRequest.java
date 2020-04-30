package com.anchor.api.data;

public  class AgentFundingRequest {
    private String agentFundingRequestId,
            assetCode,
            amount,
            date, anchorId, 
            agentId;

    public AgentFundingRequest() {
    }

    public AgentFundingRequest(final String agentFundingRequestId, final String assetCode, final String amount,
            final String date, final String anchorId, 
            final String agentId, final String clientId) {
        this.agentFundingRequestId = agentFundingRequestId;
        this.assetCode = assetCode;
        this.amount = amount;
        this.date = date;
        this.anchorId = anchorId;
        this.agentId = agentId;
    }

    public String getAgentFundingRequestId() {
        return agentFundingRequestId;
    }

    public void setAgentFundingRequestId(String agentFundingRequestId) {
        this.agentFundingRequestId = agentFundingRequestId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
