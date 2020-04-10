package com.anchor.api.data.transfer.sep27;

import com.anchor.api.data.transfer.sep26.TransferProtocols;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/*
    🍎 🍎 🍎 🍎 Root level response fields.
    🍎 Values set here override equivalent values defined in 🍎 stellar.toml.

    Field	Requirements	    Description
    name	                    string (<= 20 char)	A short name for the token
    description	                string	Description of token and what it represents
    conditions	                string	Conditions on token
    logo	                    url	URL to a PNG image on a transparent background representing token
    redemption_instructions	    string	If anchored token, these are instructions to redeem the underlying asset from tokens.
    Organization response fields.
    Optional, but anchors are recommended to provide more information regarding the organization
    anchoring an asset.

    🥏 🥏 Org
    Organization information is placed inside the root-level org field.

    Field	Requirements	            Description
    name	                            string	Legal name of your organization
    dba	                                string	(may not apply) DBA of your organization
    url	                                string  https://	Your organization's official URL. Your stellar.toml must be hosted on the same domain.
    logo	                            string  url	A PNG image of your organization's logo on a transparent background
    description	                        string	Short description of your organization
    physical_address	                string	Physical address for your organization
    physical_address_attestation	    string https:// url	URL on the same domain as your url that contains an image or pdf official document attesting to your physical address.
                                                It must list your name or dba as the party at the address.
                                                Only documents from an official third party are acceptable. E.g. a utility bill, mail from a financial institution, or business license.
    phone_number	                    string	Your organization's phone number in E.164 format, e.g. +14155552671. See also this guide.
    phone_number_attestation	        string  https:// url	URL on the same domain as your url that contains an image or pdf of a phone bill showing both the phone number and your organization's name.
    keybase	                            string	A Keybase account name for your organization. Should contain proof of ownership of any public online accounts you list here, including your organization's domain.
    twitter	                            string	Your organization's Twitter account
    github	                            string	Your organization's Github account
    email	                            string	An email where clients can contact your organization. Must be hosted at your url domain.
    licensing_authority	                string	Name of the authority or agency that licensed your organization, if applicable
    license_type	                    string	Type of financial or other license your organization holds, if applicable
    license_number	                    string	Official license number of your organization, if applicable

    👽 👽 👽 Issuers that list verified information including phone/address attestations and Keybase verifications will be prioritized by Stellar clients.

    🥬 🥬 🥬 🥬 Transfer protocols response fields. 🥬 (Optional)

    Certain anchors may provide users with the option to deposit or withdraw assets in and out
    of the Stellar network, directly from their wallets. For such assets, the transaction_protocols field is an object containing all transfer protocols the anchor supports for that asset.

    Pure stellar assets that only exist within the Stellar network need not define this field.
    Asset-backed tokens on the other hand are encouraged to:
      🍎 either implement SEP24 for interactive anchor/wallet asset transfer,
      🍎 or SEP26 for non-interactive anchor/wallet asset transfer.

 */
public class InfoServerResponse {
    private Org org;
    String name, description, conditions, logo;
    @SerializedName("redemption_instructions")
    @Expose
    String redemptionInstructions;
    @SerializedName("transfer_protocols")
    @Expose
    TransferProtocols transferProtocols;

    public InfoServerResponse() {
    }

    public InfoServerResponse(Org org, String name, String description, String conditions,
                              String logo, String redemptionInstructions,
                              TransferProtocols transferProtocols) {
        this.org = org;
        this.name = name;
        this.description = description;
        this.conditions = conditions;
        this.logo = logo;
        this.redemptionInstructions = redemptionInstructions;
        this.transferProtocols = transferProtocols;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getRedemptionInstructions() {
        return redemptionInstructions;
    }

    public void setRedemptionInstructions(String redemptionInstructions) {
        this.redemptionInstructions = redemptionInstructions;
    }

    public TransferProtocols getTransferProtocols() {
        return transferProtocols;
    }

    public void setTransferProtocols(TransferProtocols transferProtocols) {
        this.transferProtocols = transferProtocols;
    }
}
