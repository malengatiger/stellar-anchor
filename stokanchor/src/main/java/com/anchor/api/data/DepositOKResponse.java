package com.anchor.api.data;

import com.anchor.api.data.transfer.sep26.ExtraInfoItem;

import java.util.List;

/*
    🍎 🍎  Success: no additional information needed
    Response code: 200 OK

    This is the correct response if the anchor is able to accept the deposit and
    needs no additional information about the user. It should also be used if the anchor
    requires information about the user, but the information has previously been submitted and
    accepted.

    The response body should be a JSON object with the following fields:

    🍎 Name	Type	Description
    how	        string	Markdown formatted instructions on how to deposit the asset. In the case of most cryptocurrencies it is just an address to which the deposit should be sent to. Important: Wallets should make sure markdown inputs are sanitized for security purposes.
    eta	        int	    (optional) Estimate of how long the deposit will take to credit in seconds.
    min_amount	float	(optional) Minimum amount of an asset that a user can deposit.
    max_amount	float	(optional) Maximum amount of asset that a user can deposit.
    fee	        float	(optional) Calculated fee (if any). In units of the deposited asset.
    extra_info	array	(optional) JSON array with additional information about the deposit process. Each element in the array is formatted as follows {key: KEY, value: VALUE}. Wallets are encouraged to present extra_info in a tabular manner and enable easy copy to clipboard for each line value.

     🥦 🥦 🥦 🥦 Mexican peso (MXN) response example
        {
          "how" : "![STP logo](https://stpbank.com/logo.png)\n\nMake a payment to Bank: STP\n\nAccount: 646180111803859359",
          "eta": 1800,
          "fee": 100,
          "extra_info": [
            {
              "key" : "Bank",
              "value": "STP"
            },
            {
              "key" : "Swift Code",
              "value": "XYZ"
            },
            {
              "key" : "Account",
              "value": "646180111803859359"
            }
          ]
        }

    🌼 🌼 🌼 🌼 🌼 Bitcoin response example
        {
          "how" : "1Nh7uHdvY6fNwtQtM1G5EZAFPLC33B59rB",
          "fee" : 0.0002
        }
   🥏 🥏 🥏 🥏 🥏 Ripple response example
        {
          "how" : "**Ripple address**: rNXEkKCxvfLcM1h4HJkaj2FtmYuAWrHGbf\n\n**tag:** 88",
          "eta": 60,
          "fee" : 0.1,
          "extra_info": [
            {
              "key" : "Important",
              "value": "Do not forget the tag"
            },
            {
              "key" : "Ripple Address",
              "value": "rNXEkKCxvfLcM1h4HJkaj2FtmYuAWrHGbf"
            },
            {
              "key" : "Tag",
              "value": "88"
            }
          ]
        }
 */
public class DepositOKResponse {
    String how;
    int eta;
    float min_amount, max_amount, fee;
    List<ExtraInfoItem> extra_info;

    public DepositOKResponse() {
    }

    public DepositOKResponse(String how, int eta, float min_amount, float max_amount,
                             float fee, List<ExtraInfoItem> extra_info) {
        this.how = how;
        this.eta = eta;
        this.min_amount = min_amount;
        this.max_amount = max_amount;
        this.fee = fee;
        this.extra_info = extra_info;
    }

    public String getHow() {
        return how;
    }

    public void setHow(String how) {
        this.how = how;
    }


    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public float getMin_amount() {
        return min_amount;
    }

    public void setMin_amount(float min_amount) {
        this.min_amount = min_amount;
    }

    public float getMax_amount() {
        return max_amount;
    }

    public void setMax_amount(float max_amount) {
        this.max_amount = max_amount;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public List<ExtraInfoItem> getExtra_info() {
        return extra_info;
    }

    public void setExtra_info(List<ExtraInfoItem> extra_info) {
        this.extra_info = extra_info;
    }
}
