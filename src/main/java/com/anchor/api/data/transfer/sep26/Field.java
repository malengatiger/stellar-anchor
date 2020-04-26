package com.anchor.api.data.transfer.sep26;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
    sep26 / deposits / options / fields
    An array of form fields supported for each deposit option.

    The following fields are allowed.

    Field	Type	Description
    id	string (required)	Id of the form field. e.g. dest, email, routing_number.
    title	string	Title of the form field e.g. "Your Email", "Routing Number".
    description	string	Description of the form field.
    optional	boolean	Marks the form field as optional or not.
    type	string	One of 'text', 'number', 'email', or 'choice'. The default form field type is text. See below on how to specify choices for the 'choice' type.
 */
public class Field {

    public Field(String id, Boolean optional, String title,
                 String description, String type, List<Choice> choices) {
        this.id = id;
        this.optional = optional;
        this.title = title;
        this.description = description;
        this.type = type;
        this.choices = choices;
    }

    public Field() {
    }

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("optional")
    @Expose
    private Boolean optional;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("choices")
    @Expose
    private List<Choice> choices;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
