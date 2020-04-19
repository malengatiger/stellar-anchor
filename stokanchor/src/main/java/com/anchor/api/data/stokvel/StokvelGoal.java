package com.anchor.api.data.stokvel;

import java.util.List;

public class StokvelGoal {
    private String name,
            date,
            targetDate,
            stokvelGoalId,
            targetAmount,
            description;

    private List<StokvelPayment> payments;
    private List<String> imageUrls;
    private Stokvel stokvel;
    private List<Member> beneficiaries;
    private boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getStokvelGoalId() {
        return stokvelGoalId;
    }

    public void setStokvelGoalId(String stokvelGoalId) {
        this.stokvelGoalId = stokvelGoalId;
    }

    public String getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(String targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StokvelPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<StokvelPayment> payments) {
        this.payments = payments;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Stokvel getStokvel() {
        return stokvel;
    }

    public void setStokvel(Stokvel stokvel) {
        this.stokvel = stokvel;
    }

    public List<Member> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<Member> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
