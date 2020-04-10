package com.anchor.api.data.transfer.sep26;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferProtocols {
    @SerializedName("sep26")
    @Expose
    private Sep26 sep26;

    public TransferProtocols(Sep26 sep26) {
        this.sep26 = sep26;
    }

    public Sep26 getSep26() {
        return sep26;
    }
}
