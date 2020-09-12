package com.kitchas.kitchenassistant.utils.requests;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kitchas.kitchenassistant.utils.GeneralException;
import com.kitchas.kitchenassistant.utils.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HTTPManager {
    private static class Token {
        public String _token;
        private static Token instance;

        public static Token getInstance() throws GeneralException {
            if (instance == null) {
                throw new GeneralException("TOKEN_NOT_EXISTS");
            }

            return instance;
        }

        public static Token getInstance(String token) {
            if (instance == null) {
                instance = new Token(token);
            }

            if (token != null && !token.equals(instance.getToken())) {
                return null;
            }

            return instance;
        }

        private Token(String token) {
            this._token = token;
        }

        public String getToken() {
            return _token;
        }
    }
    private static HTTPManager instance;
    private Token token;
    private RequestQueue queue = null;
    private RetryPolicy retry_policy;

    public static HTTPManager getInstance() {
        if (instance == null) {
            instance = new HTTPManager();
        }

        return instance;
    }

    private HTTPManager() {
        this.retry_policy = new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public void setToken(String token) {
        this.token = Token.getInstance(token);
    }

    public void POSTRequest(String endpoint, Map<String, String> parameters, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        this.sendHTTPRequest(endpoint, parameters, Request.Method.POST, success_callback, error_callback, context);
    }

    public void GETRequest(String endpoint, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        this.sendHTTPRequest(endpoint, new HashMap<>(), Request.Method.GET, success_callback, error_callback, context);
    }

    public void PATCHRequest(String endpoint, Map<String, String> parameters, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        this.sendHTTPRequest(endpoint, parameters, Request.Method.PATCH, success_callback, error_callback, context);
    }

    public void POSTMultipartRequest(String endpoint, Map<String, String> parameters,Bitmap image, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        this.sendMultipartRequest(endpoint, parameters, image, Request.Method.POST, success_callback, error_callback, context);
    }

    public void GETMultipartRequest(String endpoint, Map<String, String> parameters,Bitmap image, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        this.sendMultipartRequest(endpoint, parameters, image, Request.Method.GET, success_callback, error_callback, context);
    }

    private void sendMultipartRequest(String endpoint, Map<String, String> parameters, Bitmap image, int method, IOnRequest success_callback, IOnRequest error_callback, Context context) {
        if (this.queue == null) {
            this.queue = Volley.newRequestQueue(context);
        }

        VolleyMultipartRequest request = new VolleyMultipartRequest(method, Settings.SERVER_URL + endpoint,
                response -> {
                    String resultResponse = new String(response.data);
                    JSONObject results;
                    try {
                        results = new JSONObject(resultResponse);
                        success_callback.onResponse(results);
                    } catch (JSONException e) {
                        error_callback.onResponse(Settings.UNKNOWN_ERROR);
                    }
                },
                error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                return parameters;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token.getToken());
                }
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (image != null) {
                    params.put("image", new DataPart("image.png", getFileDataFromDrawable(image)));
                }
                return params;
            }
        };


        request.setRetryPolicy(retry_policy);
        this.queue.add(request);
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void sendHTTPRequest(String endpoint, Map<String, String> parameters, int method, final IOnRequest success_callback, final IOnRequest error_callback, Context context) {
        if (this.queue == null) {
            this.queue = Volley.newRequestQueue(context);
        }
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token.getToken());
                }
                return headers;
            }
        };

        request.setRetryPolicy(retry_policy);
        this.queue.add(request);
    }

    public void request(String endpoint, Map<String, String> parameters, int method, final IOnRequestObject success_callback, final IOnRequest error_callback, Context context) {
        if (this.queue == null) {
            this.queue = Volley.newRequestQueue(context);
        }
        StringRequest request = new StringRequest(method, Settings.SERVER_URL + endpoint,
                response -> {
                    try {
                        Object results;
                        try {
                            results = new JSONArray(response);
                        } catch (JSONException ignored) {
                            try {
                                results = new JSONObject(response);
                            } catch (JSONException _ignored) {
                                throw new Exception("UNKNOWN RESPONSE");
                            }
                        }

                        success_callback.onResponse(results);
                    } catch (Exception e) {

                    }
                }, error -> {
            System.out.println(error.getMessage());
            try {
                if (error.getMessage() != null) {
                    error_callback.onResponse(new JSONObject());
                } else {
                    throw new Exception("");
                }
            } catch (Exception ignored) {
                error_callback.onResponse(Settings.UNKNOWN_ERROR);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return parameters;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + token.getToken());
                return headers;
            }
        };
        request.setRetryPolicy(retry_policy);
        this.queue.add(request);
    }

}
