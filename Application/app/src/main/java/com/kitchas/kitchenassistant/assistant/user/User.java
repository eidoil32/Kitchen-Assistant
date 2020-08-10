package com.kitchas.kitchenassistant.assistant.user;

import android.content.Context;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.utils.Tools;
import com.kitchas.kitchenassistant.utils.requests.API;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;
import com.kitchas.kitchenassistant.utils.requests.IOnRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class User extends Base {
    public static User instance;
    private String email, password;

    private User() {}

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }

        return instance;
    }

    public static void login(String email, String password, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        HTTPManager httpManager = new HTTPManager(context);
        if ((email.isEmpty()) || password.isEmpty()) {
            try {
                error_callback.onResponse(new JSONObject(context.getString(R.string.EMPTY_EMAIL_PASSWORD)));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        } else {
            password = Tools.encrypt(password);
            Map<String, String> parameters = new HashMap<>();
            parameters.put(API.API_KEY_PASSWORD, password);
            parameters.put(API.API_KEY_EMAIL, email);
            System.out.println(parameters);
            httpManager.POSTRequest(
                    "user/login",
                    parameters,
                    response -> {
                        createUserInstance(response, error_callback);
                        success_callback.onResponse(response);
                    },
                    error_callback
            );
        }
    }

    public static void register(String email, String password, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        HTTPManager httpManager = new HTTPManager(context);
        if ((email.isEmpty()) || password.isEmpty()) {
            try {
                error_callback.onResponse(new JSONObject(context.getString(R.string.EMPTY_EMAIL_PASSWORD)));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        } else {
            password = Tools.encrypt(password);
            Map<String, String> parameters = new HashMap<>();
            parameters.put(API.API_KEY_PASSWORD, password);
            parameters.put(API.API_KEY_EMAIL, email);
            System.out.println(parameters);
            httpManager.POSTRequest(
                    "user/register",
                    parameters,
                    success_callback,
                    error_callback
            );
        }
    }

    private static void createUserInstance(JSONObject user_data, IOnRequest error_callback) {
        try {
            instance.setData(user_data);
        } catch (JSONException e) {
            try {
                error_callback.onResponse(new JSONObject("SET_USER_DATA_FAILED"));
            } catch (JSONException ex) {
                System.out.println("JSON Object creation failed");
            }
        }
    }

    public void setData(JSONObject user_data) throws JSONException {
        this.email = user_data.getString("email");
    }
}
