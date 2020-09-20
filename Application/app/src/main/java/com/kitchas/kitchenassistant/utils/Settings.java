package com.kitchas.kitchenassistant.utils;

import android.Manifest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    public static final int ADD_RECIPE_FINISH_OK = 10001;
    public static final String ENV = "PROD";
    public static final int CAMERA_PERMISSION_REQUEST = 0;
    public static final Map<String, String> PERMISSION_ARRAY = new HashMap<>();

    static {
        PERMISSION_ARRAY.put(Manifest.permission.CAMERA, "To wakeup the Assistant");
        PERMISSION_ARRAY.put(Manifest.permission.RECORD_AUDIO, "To talking with the assistant");
        PERMISSION_ARRAY.put(Manifest.permission.INTERNET, "To connect to our database and network");
        PERMISSION_ARRAY.put(Manifest.permission.READ_EXTERNAL_STORAGE, "To load images to local storage");
    }
    public static String SERVER_URL;
    static {
        if (!ENV.equals("DEV")) {
            SERVER_URL = "https://kitchen-assistant.herokuapp.com/api/";
        } else {
            SERVER_URL = "http://77.127.44.115:3000/api/";
        }
    }
    public static JSONObject UNKNOWN_ERROR;
    static {
        try {
            UNKNOWN_ERROR = new JSONObject("{'error': 'UNKNOWN_ERROR'}");
        } catch (JSONException e) {
            // hope this exception will not appeared, if it's does - WE ARE DEAD!
            UNKNOWN_ERROR = null;
            System.out.println("WTF - Simple json object parsing is failed!");
        }
    }

    public static JSONObject CONVERT_TO_JSON_FAILED;
    static {
        try {
            CONVERT_TO_JSON_FAILED = new JSONObject("{'error': 'CONVERT_TO_JSON_FAILED'}");
        } catch (JSONException e) {
            // hope this exception will not appeared, if it's does - WE ARE DEAD!
            CONVERT_TO_JSON_FAILED = null;
            System.out.println("WTF - Simple json object parsing is failed!");
        }
    }

    public static final String image = "";
}
