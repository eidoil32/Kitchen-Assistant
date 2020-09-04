package com.kitchas.kitchenassistant.assistant.user;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.Base;
import com.kitchas.kitchenassistant.utils.Tools;
import com.kitchas.kitchenassistant.utils.database.SQLHelper;
import com.kitchas.kitchenassistant.utils.requests.API;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;
import com.kitchas.kitchenassistant.utils.requests.IOnRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class User extends Base {
    public static String DB_IDENTIFY = "USER_DATA";

    public static User instance;
    private String email;
    private boolean logged_in = false;

    private User(Context context) {
        this.logged_in = readFromLocal(context);
    }

    private boolean readFromLocal(Context context) {
        SQLHelper database = new SQLHelper(context);
        String user_data = database.getData(DB_IDENTIFY);
        if (user_data != null) {
            this.setData(user_data);
            return true;
        }
        return false;
    }

    public static User getInstance(Context context) {
        if (instance == null) {
            instance = new User(context);
        }

        return instance;
    }

    public boolean isLoggedIn() {
        return this.logged_in;
    }

    public static void login(String email, String password, IOnRequest success_callback, IOnRequest error_callback, Context context) {
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
            HTTPManager.getInstance().POSTRequest(
                    "users/login",
                    parameters,
                    success_callback,
                    error_callback,
                    context
            );
        }
    }

    public static void register(String[] params, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        if ((params[0].isEmpty()) || params[1].isEmpty()) {
            try {
                error_callback.onResponse(new JSONObject(context.getString(R.string.EMPTY_EMAIL_PASSWORD)));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        } else {
            String password = params[1];
            String name = params[2];
            String email = params[0];
            password = Tools.encrypt(password);
            Map<String, String> parameters = new HashMap<>();
            parameters.put(API.API_KEY_PASSWORD, password);
            parameters.put(API.API_KEY_EMAIL, email);
            parameters.put(API.API_KEY_NAME, name);
            HTTPManager.getInstance().POSTRequest(
                    "users",
                    parameters,
                    success_callback,
                    error_callback,
                    context
            );
        }
    }

    public void setData(JSONObject user_data) throws JSONException {
        this.email = user_data.getString("email");
    }

    public void setData(String user_data_json) {
        Type empMapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> user_data = new Gson().fromJson(user_data_json, empMapType);
        this.email = user_data.get("email");
        HTTPManager.getInstance().setToken(user_data.get("TOKEN"));
    }

    public void saveToLocal(Context context, String token) {
        Map<String, String> user_data = new HashMap<>();
        user_data.put("email", this.email);
        user_data.put("TOKEN", token);

        SQLHelper database = new SQLHelper(context);
        long newRowId = database.insertData(user_data, DB_IDENTIFY);
        if (newRowId != 0) {
            this.logged_in = true;
            HTTPManager.getInstance().setToken(token);
        }
    }
}
