package com.anchor.api.data.transfer.sep26;

import com.anchor.api.data.transfer.sep10.Sep10;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
     🍀  🍀  🍀  SEP 0026  🍀  🍀  🍀  Deposit
    A deposit is when a user sends an external token (BTC via Bitcoin, USD via bank transfer, etc...)
    to an address held by an anchor.
    In turn, the anchor sends an equal amount of tokens on the Stellar network (minus fees) to
    the user's Stellar account.

    The deposit endpoint allows a wallet to get deposit information from an anchor,
    so a user has all the information needed to initiate a deposit.
    It also lets the anchor specify additional information (if desired) that the user must submit interactively to be able to deposit.

    If the given account does not exist, or if the account doesn't have a trust line for that specific asset,
    see the Special Cases section below.
    🌺 🌺
    Request
    🛎 POST TRANSFER_SERVER/deposit
    Content-Type: multipart/form-data
    Request Parameters:
    # 🌺 🌺 🌺 🌺 Request Parameters
    Name	        Type	Description
    asset_code	    string	The code of the asset the user wants to deposit with the anchor. E.g. BTC, ETH, USD, INR, etc. This should be the same asset code specified in the stellar.toml file.
    asset_issuer	string	The issuer of the asset the user wants to deposit with the anchor.
    account	G...    string	The stellar account ID of the user that wants to deposit. This is where the asset token will be sent to.
    memo_type	    string	(optional) Type of memo that the anchor should attach to the Stellar payment transaction, one of text, id or hash.
    memo	        string	(optional) Value of memo to attach to transaction, for hash this should be base64-encoded.
    email_address	string	(optional) Email address of depositor. If desired, an anchor can use this to send email updates to the user about the deposit.
    type	        string	(required) Deposit option. If the anchor supports one or multiple deposit methods (e.g. SEPA or SWIFT), the wallet should specify type. The type should be set to one of the deposit options defined above in the SEP-27 json response.
    wallet_name	    string	(optional) In communications / pages about the deposit, anchor should display the wallet name to the user to explain where funds are going.
    wallet_url	    string	(optional) Anchor should link to this when notifying the user that the transaction has completed.
    lang	        string	(optional) Defaults to en. Language code specified using ISO 639-1. error fields in the response should be in this language.

    🛎 Example:

    POST https://api.example.com/deposit
    Content-Type: multipart/form-data

    🛎 Response
    There are several types of responses, depending on whether the anchor needs more information about the user,
    how it should be sent to the anchor, and if there are any errors.

    The first response, the success response, is explained below. The other possible responses are shared with the withdrawal endpoint,
    and are explained in the Deposit and Withdraw shared responses section below.

    1. Success: no additional information needed
    Response code: 200 OK

    This is the correct response if the anchor is able to accept the deposit and needs no additional information about the user.
    It should also be used if the anchor requires information about the user, but the information has previously been submitted and accepted.

    🛎 🛎 🛎 The response body should be a JSON object with the following fields:

    Name	    Type	Description
    how	        string	Markdown formatted instructions on how to deposit the asset. In the case of most cryptocurrencies it is just an address to which the deposit should be sent to. Important: Wallets should make sure markdown inputs are sanitized for security purposes.
    eta	        int	(optional) Estimate of how long the deposit will take to credit in seconds.
    min_amount	float	(optional) Minimum amount of an asset that a user can deposit.
    max_amount	float	(optional) Maximum amount of asset that a user can deposit.
    fee	        float	(optional) Calculated fee (if any). In units of the deposited asset.
    extra_info	array	(optional) JSON array with additional information about the deposit process. Each element in the array is formatted as follows {key: KEY, value: VALUE}. Wallets are encouraged to present extra_info in a tabular manner and enable easy copy to clipboard for each line value.
 */
public class Deposit {
    public Deposit() {
    }

    public Deposit(Boolean enabled, List<Sep10> authenticationProtocols, List<Option> options) {
        this.enabled = enabled;
        this.authenticationProtocols = authenticationProtocols;
        this.options = options;
    }

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("authentication_protocols")
    @Expose
    private List<Sep10> authenticationProtocols;
    @SerializedName("options")
    @Expose
    private List<Option> options = null;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Sep10> getAuthenticationProtocols() {
        return authenticationProtocols;
    }

    public void setAuthenticationProtocols(List<Sep10> authenticationProtocols) {
        this.authenticationProtocols = authenticationProtocols;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

}
