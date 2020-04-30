package com.anchor.api.services;

import com.anchor.api.data.AgentFundingRequest;
import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.anchor.*;
import com.anchor.api.data.info.Info;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;

import java.util.List;

public interface DatabaseServiceInterface {
    public void initializeDatabase() throws Exception;
    public List<Anchor> getAnchors() throws Exception;
    public Agent getAgent(String agentId) throws Exception;
    public String addLoanApplication(LoanApplication application) throws Exception;
    public String addLoanPayment(LoanPayment loanPayment) throws Exception;
    public String addOrganization(Organization organization) throws Exception;
    public String addAnchor(Anchor anchor) throws Exception;
    public String addAnchorUser(AnchorUser anchorUser) throws Exception;
    public String addClient(Client client) throws Exception;
    public String addAgent(Agent agent) throws Exception;
    public String addStokvel(Stokvel agent) throws Exception;
    public String addMember(Member member) throws Exception;
    public PaymentRequest addPaymentRequest(PaymentRequest paymentRequest) throws Exception;
    public AgentFundingRequest addAgentFundingRequest(AgentFundingRequest agentFundingRequest) throws Exception;
    public String updateLoanApplication(LoanApplication application) throws Exception;
    public String updateClient(Client client) throws Exception;
    public String updateAgent(Agent agent) throws Exception;
    public String addAnchorInfo(Info info) throws Exception;
    public Info getAnchorInfo(String anchorId) throws Exception;
    public Anchor getAnchor(String anchorId) throws Exception;
    public Stokvel getStokvelByName(String name) throws Exception;
    public Member getMemberByName(String firstName, String lastName) throws Exception;
    public Member getMemberByEmail(String email) throws Exception;
    public List<Member> getStokvelMembers(String stokvelId) throws Exception;
    public List<Agent> getAgents(String anchorId) throws Exception;
    public List<PaymentRequest> getPaymentRequests(String anchorId) throws Exception;
    public LoanApplication getLoanApplication(String loanId) throws Exception;
    public Agent getAgentByNameAndAnchor(String anchorId, String firstName, String lastName) throws Exception;
    public List<Client> getAnchorClients(String anchorId) throws Exception;
    public List<Client> getAgentClients(String agentId) throws Exception;
    public List<LoanApplication> getAgentLoans(String agentId) throws Exception;
    public List<LoanPayment> getLoanPayments(String loanId) throws Exception;
    public Client getClientByNameAndAnchor(String anchorId, String firstName, String lastName) throws Exception;
    public Client getClientById(String clientId) throws Exception;
    public String addAccountResponse(AccountResponse accountResponse) throws Exception;
    public String addOperationResponse(OperationResponse operationResponse) throws Exception;
    public String addTransactionResponse(TransactionResponse transactionResponse) throws Exception;
    public Agent getAgentByAccount(String accountId) throws Exception;
    public Client getClientByAccount(String accountId) throws Exception;
}
