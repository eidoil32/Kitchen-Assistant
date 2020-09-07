package com.kitchas.kitchenassistant.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {
    public static String tryString(JSONObject json, String field) {
        return tryString(json, field, "");
    }

    public static String tryString(JSONObject json, String field, String defaultValue) {
        String result = defaultValue;
        try {
            result = json.getString(field);
        } catch (JSONException ignored) { }

        return result;
    }

    public static int tryInt(JSONObject json, String field) {
        return tryInt(json, field, 0);
    }

    public static int tryInt(JSONObject json, String field, int defaultValue) {
        int result = defaultValue;
        try {
            result = json.getInt(field);
        } catch (JSONException ignored) { }

        return result;
    }
}
