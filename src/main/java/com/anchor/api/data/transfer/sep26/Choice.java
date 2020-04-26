package com.anchor.api.data.transfer.sep26;
/*
    sep26 / deposits / options / fields / choices
    An array of choices for deposit form fields that have type set to choice.

    Field	Type	Description
    value	string (required)	e.g. This is the value that will be sent back to the deposit api endpoint. e.g. "bank001".
    title	string	Human readable title for the choice. E.g. Bank of America.
    See the BTC SEP27 example response above of an asset that uses the choice type on a withdrawal field.
 */
public class Choice {
    String title, value;

    public Choice(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
