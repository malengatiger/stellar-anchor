package com.anchor.api.data.account;

public class Options {
    private String seed;
    private int clearFlags;
    private int highThreshold;
    private int lowThreshold;
    private String inflationDestination;
    private int masterKeyWeight;

    public Options(String seed, int clearFlags, int highThreshold, int lowThreshold, String inflationDestination, int masterKeyWeight) {
        this.seed = seed;
        this.clearFlags = clearFlags;
        this.highThreshold = highThreshold;
        this.lowThreshold = lowThreshold;
        this.inflationDestination = inflationDestination;
        this.masterKeyWeight = masterKeyWeight;
    }

    public Options() {
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public int getClearFlags() {
        return clearFlags;
    }

    public void setClearFlags(int clearFlags) {
        this.clearFlags = clearFlags;
    }

    public int getHighThreshold() {
        return highThreshold;
    }

    public void setHighThreshold(int highThreshold) {
        this.highThreshold = highThreshold;
    }

    public int getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(int lowThreshold) {
        this.lowThreshold = lowThreshold;
    }

    public String getInflationDestination() {
        return inflationDestination;
    }

    public void setInflationDestination(String inflationDestination) {
        this.inflationDestination = inflationDestination;
    }

    public int getMasterKeyWeight() {
        return masterKeyWeight;
    }

    public void setMasterKeyWeight(int masterKeyWeight) {
        this.masterKeyWeight = masterKeyWeight;
    }
}
