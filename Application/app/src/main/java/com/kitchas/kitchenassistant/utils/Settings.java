package com.kitchas.kitchenassistant.utils;

import android.Manifest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    public static final String ENV = "DEV";
    public static final int CAMERA_PERMISSION_REQUEST = 0;
    public static final Map<String, String> PERMISSION_ARRAY = new HashMap<>();

    static {
        PERMISSION_ARRAY.put(Manifest.permission.CAMERA, "To wakeup the Assistant");
        PERMISSION_ARRAY.put(Manifest.permission.RECORD_AUDIO, "To talking with the assistant");
        PERMISSION_ARRAY.put(Manifest.permission.INTERNET, "To connect to our database and network");
    }
    public static String SERVER_URL;
    static {
        if (!ENV.equals("DEV")) {
            SERVER_URL = "https://kitchen-assistant.herokuapp.com/api/";
        } else {
            SERVER_URL = "http://77.127.6.12:3000/api/";
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

    public static final String image = "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";
}
