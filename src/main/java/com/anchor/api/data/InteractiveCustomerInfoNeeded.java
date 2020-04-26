package com.anchor.api.data;
/*
Adding parameters to the URL
Before the wallet sends the user to the url field received from the anchor, it may add query parameters to the URL.

The basic parameters are summarized in the table below.

Name	Type	Description
callback	string	(optional) A URL that the anchor should POST a JSON message to when the user successfully completes
the interactive flow. Can also be set to postMessage.

callback details

If the wallet wants to be notified that the user has completed the anchor's interactive flow
(either success or failure), it can add this parameter to the URL. If the user abandons the process,
the anchor does not need to report anything to the wallet. If the callback value is a URL, the anchor must POST to it with a JSON message as the body.
 */
public class InteractiveCustomerInfoNeeded {
    final String type = "interactive_customer_info_needed";

    String url, id, callback; //type "interactive_customer_info_needed" ALWAYS

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getType() {
        return type;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
