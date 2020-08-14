package com.kitchas.kitchenassistant.utils.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kitchas.kitchenassistant.utils.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HTTPManager {
    private Context context;

    public HTTPManager(Context context) {
        this.context = context;
    }

    public void POSTRequest(String endpoint, Map<String, String> parameters, IOnRequest success_callback, IOnRequest error_callback) {
        this.sendHTTPRequest(endpoint, parameters, Request.Method.POST, success_callback, error_callback);
    }

    public void GETRequest(String endpoint, Map<String, String> parameters, IOnRequest success_callback, IOnRequest error_callback) {
        this.sendHTTPRequest(endpoint, parameters, Request.Method.GET, success_callback, error_callback);
    }

    private void sendHTTPRequest(String endpoint, Map<String, String> parameters, int method, final IOnRequest success_callback, final IOnRequest error_callback) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        JsonObjectRequest request = new JsonObjectRequest(method, Settings.SERVER_URL + endpoint, new JSONObject(parameters),
                response -> {
                    if (null != response) {
                        success_callback.onResponse(response);
                    }
                },
                error -> {
                    try {
                        String s = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject object = new JSONObject(s);
                        error_callback.onResponse(object);
                    } catch (Exception e) {
                        error_callback.onResponse(Settings.UNKNOWN_ERROR);
                    }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return parameters;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
