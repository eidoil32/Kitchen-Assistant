package com.kitchas.kitchenassistant.assistant.models.recipe;

import org.json.JSONException;
import org.json.JSONObject;

public class Tag {
    private String title;
    private String id;

    public Tag(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public static Tag loadFromJSON(JSONObject json) throws JSONException {
        return new Tag(json.getString("title"), json.getString("_id"));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
