package com.kitchas.kitchenassistant.assistant.models.recipe;

import android.content.Context;

import androidx.annotation.Nullable;

import com.kitchas.kitchenassistant.utils.requests.HTTPManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tag implements Serializable {
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

    public static List<String> convertToStringList(List<Tag> tags) {
        return tags.stream().map(tag -> tag.title).collect(Collectors.toList());
    }

    public static void updateRecipe(Context context, String recipe_id, List<Tag> tags, List<Tag> old_tags) {
        HTTPManager helper = HTTPManager.getInstance();
        Map<String, String> parameters = new HashMap<>();
        JSONArray parameters_array = new JSONArray();
        int i = 0;
        for (Tag tag : tags) {
            try {
                if (!old_tags.contains(tag)) {
                    JSONObject tag_object = new JSONObject();
                    tag_object.put("title", tag.getTitle());
                    parameters_array.put(tag_object);
                } else {
                    old_tags.remove(tag);
                }
            } catch (JSONException ignored) {
            }
        }

        for (Tag tag : old_tags) {
            helper.DELETERequest("user/recipe/" + recipe_id + "/tags/" + tag.getId(),
                    response -> { },
                    error -> { }, context);
        }

        if (parameters_array.length() > 0) {
            parameters.put("tags", parameters_array.toString());
            helper.POSTRequest("user/recipe/" + recipe_id + "/create/tags",
                    parameters, response -> {
                        System.out.println("post in tags");
                    }, error -> {
                        System.out.println("error post in tags");
                    }, context);
        }
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
            Tag tag = (Tag) obj;
            return tag.getTitle().equals(this.title);
        }

        return false;
    }
}
