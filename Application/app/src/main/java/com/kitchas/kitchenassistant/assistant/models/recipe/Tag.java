package com.kitchas.kitchenassistant.assistant.models.recipe;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

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

    public static List<Tag> convertString(List<String> tags_string) {
        List<Tag> tags = new LinkedList<>();
        for (String tag_string : tags_string) {
            Tag tag = new Tag(tag_string, null);
            tags.add(tag);
        }

        return tags;
    }

    public static JSONArray convertToJSON(List<Tag> tags) throws JSONException {
        JSONArray json = new JSONArray();
        for (int i = 0; i < tags.size(); i++) {
            json.put(tags.get(i).title);
        }

        return json;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Tag) {
            Tag tag = (Tag)obj;
            return !tag.getTitle().equals(this.title);
        }

        return false;
    }
}
