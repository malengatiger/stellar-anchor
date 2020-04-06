package com.anchor.api.controllers;

import com.anchor.api.data.*;
import com.anchor.api.services.FirebaseService;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.info.Info;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
@RestController
public class TransferController {
    public static final Logger LOGGER = Logger.getLogger(TransferController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    /*
    😈 👿 😈 👿
    Every other HTTP status code will be considered an error.
    The body should contain a string indicating the error details.
    This error is in a human readable format in the language indicated in the request and is
    intended to be displayed by the wallet. For example:

        {
           "error": "This anchor doesn't support the given currency code: ETH"
        }
     */
    public TransferController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C TransferController  " +
                "\uD83C\uDF51 constructed and ready to go! \uD83C\uDF45 CORS enabled for the controller");
    }

    @Autowired
    private ApplicationContext context;

    @Value("${anchorName}")
    private String anchorName;

//    //this boolean value decides if anchor wants user to interactively provide information
//    @Value("${requiresCustomerInteraction}")
//    private String requiresCustomerInteraction;

    @Autowired
    private FirebaseService firebaseService;

    /*
    🌺 🌺
    A deposit is when a user sends some non-stellar asset (BTC via Bitcoin network, USD via bank transfer, Cash to a teller, etc...) to an account held by an anchor.
    In turn, the anchor sends an equal amount of tokens on the Stellar network (minus fees) to the user's Stellar account.
    The deposit endpoint allows a wallet to get deposit information from an anchor, so a user has all the information needed to initiate a deposit. It also lets the anchor specify
    additional information that the user must submit interactively via a popup or embedded browser window to be able to deposit.
    🌺
    If the given account does not exist, or if the account doesn't have a trust line for that specific asset, see the Special Cases section below.


    POST TRANSFER_SERVER_SEP0024/transactions/deposit/interactive
    Content-Type: multipart/form-data
    asset_code=USD&email_address=myaccount@gmail.com&account=GACW7NONV43MZIFHCOKCQJAKSJSISSICFVUJ2C6EZIW5773OU3HD64VI

    🔵 🔵 Is this right??
    User deposits cash at a teller; someone/some system at the BANK needs to call this endpoint ....
    User gives cash to an Agent; agent's app must call this endpoint

    the end-result: 🔵 user has new balance for this asset code
     */
    @PostMapping("/transactions/deposit/interactive")
    public DepositOKResponse deposit(@RequestBody DepositWithdrawRequestParameters requestParameters) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:deposit ...");
       
        return null;
    }
    /*
    This operation allows a user to redeem an asset currently on the Stellar network for the real asset
    (BTC, USD, stock, etc...) via the anchor of the Stellar asset.

    The withdraw endpoint allows a wallet to get withdrawal information from an anchor,
    so a user has all the information needed to initiate a withdrawal.
    It also lets the anchor specify the url for the interactive webapp to continue with the anchor's side of the withdraw.

    💛 💛  Is this right??
    User gets cash at a teller; someone/some system at the BANK needs to call this endpoint ....
    User gives cash to an Agent; agent's app must call this endpoint

    the end-result: 🔵 user has new balance for this asset code

    RESPONSE Example, when everything's OK status 200:

    {
      "account_id": "GCIBUCGPOHWMMMFPFTDWBSVHQRT4DIBJ7AD6BZJYDITBK2LCVBYW7HUQ",
      "memo_type": "id",
      "memo": "123"
    }

     */
    @PostMapping("/transactions/withdraw/interactive")
    public WithdrawOKResponse withdraw(@RequestBody DepositWithdrawRequestParameters requestParameters) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:withdraw ...");
        //todo - check

        
        return null;
    }
    @PostMapping("/setAnchorInfo")
    public String setAnchorInfo(@RequestBody Info info) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:withdraw ...");
        return firebaseService.addAnchorInfo(info);
    }
    @GetMapping("/info")
    public Info info() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:info ...");
        if (anchorName == null) {
            throw new Exception("Anchor Name missing in application properties");
        }

        Anchor mAnchor = firebaseService.getAnchorByName(anchorName);
        return firebaseService.getAnchorInfo(mAnchor.getAnchorId());
    }

    /*
    🥬 🥬 🥬
    The fee endpoint allows an anchor to report the fee that would be charged for a given deposit or withdraw operation. This is important to allow an anchor to accurately report fees to a user even when the fee schedule is complex. If a fee can be fully expressed with the fee_fixed, fee_percent or fee_minimum fields in the /info response, then an anchor should not implement this endpoint.

    🥬 GET TRANSFER_SERVER_SEP0024/fee

    Request parameters:

    Name	Type	Description

    operation	string	Kind of operation (deposit or withdraw).
    type	    string	(optional) Type of deposit or withdrawal (SEPA, bank_account, cash, etc...).
    asset_code	string	Asset code.
    amount	    float	Amount of the asset that will be deposited/withdrawn.

    🎽 Example request:

    GET https://api.example.com/fee?operation=withdraw&asset_code=ETH&amount=0.5
    On success the endpoint should return 200 OK HTTP status code and a JSON object with the following fields:

    Name	Type	Description
    fee	float	The total fee (in units of the asset involved) that would be charged to deposit/withdraw the specified amount of asset_code.
    Example response:

    {
      "fee": 0.013
    }
    🥬
    😈 👿 😈 👿 😈 👿
    Every HTTP status code other than 200 OK will be considered an error.
    The body should contain error details. For example:
    {
       "error": "This anchor doesn't support the given currency code: ETH"
    }
     */

    @GetMapping(value = "/fee", produces = MediaType.APPLICATION_JSON_VALUE)
    public Fee fee(@RequestParam String operation, String type, String asset_code, String amount) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:fee ..." +
                " " + operation + " " + asset_code + " " + amount);

        return null;
    }
    /*
        🌼 One of id, stellar_transaction_id or external_transaction_id is required.

        On success the endpoint should return 200 OK HTTP status code and a JSON object with the following fields:

        Name	Type	Description
        transaction	object	The transaction that was requested by the client.

        If the transaction cannot be found, the endpoint should return a 404 NOT FOUND result.

        😈 👿 😈 👿
        Every HTTP status code other than 200 OK will be considered an error. An empty transaction list is not an error.
        The body should contain error details. For example:

            {
               "error": "This anchor doesn't support the given currency code: ETH"
            }
     */
    @GetMapping("/transaction")
    public Transaction transaction(@RequestParam String id, @RequestParam String stellar_transaction_id,
                                   @RequestParam String external_transaction_id) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:transaction ...");

        return null;
    }
    @GetMapping("/transactions")
    public List<Transaction> transactions(@RequestParam String asset_code,
                                          @RequestParam String no_older_than,
                                          @RequestParam String kind,
                                          @RequestParam String paging_id) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:transactions ...");

        return null;
    }

}
