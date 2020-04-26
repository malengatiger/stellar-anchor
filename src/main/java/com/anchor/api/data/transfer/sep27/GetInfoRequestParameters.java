package com.anchor.api.data.transfer.sep27;

/*
    🥬 🥬 🥬 🥬 🥬 Simple Summary
    If you are an anchor or issuer, the info_server endpoint is used to provide a common place where
    the Internet, wallet integrators, and other stellar clients can find out more information about your asset. Asset information obtained via the info_server API endpoint supplements asset information found in stellar.toml, while also providing a more standard way to localize asset information. Anchors are encouraged to provide an info_server endpoint for each of their assets.

    🍎 Specification
    The url for the asset information server will be located inside the field: info_server, which itself will be placed under the relevant [[CURRENCIES]] table in stellar.toml.

        [[CURRENCIES]]
        code = "BTC"
        issuer = "GCKLZLMYTLRBKM6BDZJYAKHDBSNCGBSN7EDF32FMIAYFAITLCB45ZJW4"
        is_asset_anchored = true
        anchor_asset_type="crypto"
        anchor_asset="BTC"
        status="live"

        info_server="https://acme.inc/api/v1/asset_info/"

    🌼 You must enable CORS on the info_server endpoint so people can access this api endpoint from other sites. The following HTTP header must be set from an info_server GET response.

    Access-Control-Allow-Origin: *
    It is also recommended to set an application/json content type so that browsers render the contents, rather than prompting for a download.

    content-type: application/json
    The response from an info_server GET request can have a maximum file size of 100KB.
    All info_server endpoints should be served over a secure channel like https.

    🌼 🌼 🌼 🌼 Request
    GET info_server

    🥏 🥏 🥏 Request Parameters:

    Name	Type	Description
    asset_code	    string (required) The code of the asset, e.g. BTC, ETH, USD, INR, etc. This should not be different from the asset code that the anchor issues.
    asset_issuer	string (required)	The issuer of the asset.
    lang	        string (optional) Defaults to en. Language code specified using ISO 639-1. Fields in the response should be in this language.

    🍎 🍎 🍎 Example:

    GET https://btc.anchor.io/asset_info?asset_code=ETH&asset_issuer=GACW7NONV43MZIFHCOKCQJAKSJSISSICFVUJ2C6EZIW5773OU3HD64VI&lang=fr
    Error Response
    Every HTTP status code other than 200 OK will be considered an error. For example:
        {
           "error": "This anchor doesn't support the given currency code: ETH"
        }
 */
public class GetInfoRequestParameters {
    String asset_code, asset_issuer, lang;

    public GetInfoRequestParameters(String asset_code, String asset_issuer, String lang) {
        this.asset_code = asset_code;
        this.asset_issuer = asset_issuer;
        this.lang = lang;
    }

    public GetInfoRequestParameters() {
    }

    public String getAsset_code() {
        return asset_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getAsset_issuer() {
        return asset_issuer;
    }

    public void setAsset_issuer(String asset_issuer) {
        this.asset_issuer = asset_issuer;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
