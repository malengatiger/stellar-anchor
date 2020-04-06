package com.anchor.api.data;

public class TransactionsRequestParameters {
    String asset_code, no_older_than, kind, paging_id;
    int limit;

    public String getAsset_code() {
        return asset_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getNo_older_than() {
        return no_older_than;
    }

    public void setNo_older_than(String no_older_than) {
        this.no_older_than = no_older_than;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPaging_id() {
        return paging_id;
    }

    public void setPaging_id(String paging_id) {
        this.paging_id = paging_id;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
