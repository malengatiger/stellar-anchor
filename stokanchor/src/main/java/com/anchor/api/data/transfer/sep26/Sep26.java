package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.transfer.sep10.Sep10;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sep26 {
    public Sep26() {
    }

    public Sep26(String transferServer, List<Deposit> deposits,
                 List<Withdrawal> withdrawals, TransactionHistory transactionHistory,
                 List<Sep10> authenticationProtocols) {

        this.transferServer = transferServer;
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.transactionHistory = transactionHistory;
        this.authenticationProtocols = authenticationProtocols;
    }

    @SerializedName("transfer_server")
    @Expose
    private String transferServer;

    @SerializedName("deposits")
    @Expose
    private List<Deposit> deposits;

    @SerializedName("withdrawals")
    @Expose
    private List<Withdrawal> withdrawals;

    @SerializedName("transaction_history")
    @Expose
    private TransactionHistory transactionHistory;

    @SerializedName("authentication_protocols")
    @Expose
    private List<Sep10> authenticationProtocols;

    public String getTransferServer() {
        return transferServer;
    }

    public void setTransferServer(String transferServer) {
        this.transferServer = transferServer;
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    public void setDeposits(List<Deposit> deposits) {
        this.deposits = deposits;
    }

    public List<Withdrawal> getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(List<Withdrawal> withdrawals) {
        this.withdrawals = withdrawals;
    }

    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(TransactionHistory transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public List<Sep10> getAuthenticationProtocols() {
        return authenticationProtocols;
    }

    public void setAuthenticationProtocols(List<Sep10> authenticationProtocols) {
        this.authenticationProtocols = authenticationProtocols;
    }
}
