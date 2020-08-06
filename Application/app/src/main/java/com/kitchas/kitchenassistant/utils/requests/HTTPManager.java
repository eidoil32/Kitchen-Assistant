package com.kitchas.kitchenassistant.utils.requests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kitchas.kitchenassistant.utils.Settings;

import org.json.JSONException;
import org.json.JSONObject;

public class HTTPManager {
    private Context context;

    public HTTPManager(Context context) {
        this.context = context;
    }

    public void POSTRequest(String endpoint, JSONObject parameters, IOnRequest success_callback, IOnRequest error_callback) {
        this.sendHTTPRequest(endpoint, parameters, Request.Method.POST, success_callback, error_callback);
    }

    public void GETRequest(String endpoint, JSONObject parameters, IOnRequest success_callback, IOnRequest error_callback) {
        this.sendHTTPRequest(endpoint, parameters, Request.Method.GET, success_callback, error_callback);
    }

    private void sendHTTPRequest(String endpoint, JSONObject parameters, int method, final IOnRequest success_callback, final IOnRequest error_callback) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        JsonObjectRequest request = new JsonObjectRequest(method, Settings.SERVER_URL + endpoint, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null != response) {
                            success_callback.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject object = new JSONObject(error.getMessage() == null ? "" : error.getMessage());
                    error_callback.onResponse(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        queue.add(request);
    }
}
