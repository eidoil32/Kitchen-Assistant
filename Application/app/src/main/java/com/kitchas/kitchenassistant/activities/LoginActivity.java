package com.kitchas.kitchenassistant.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.Tools;
import com.kitchas.kitchenassistant.utils.requests.API;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class LoginActivity extends AppCompatActivity {
    protected User user;
    protected JSONObject response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            init();
        } catch (Exception e) {
            // TODO - show error message
        }
    }

    private void init() {
        this.user = new User();
        if (user.isLoggedIn()) {
            OnUserLoggedIn();
        } else {
            setContentView(R.layout.activity_login);
            LoginScreen();
        }
    }

    private void LoginScreen() {
        final HTTPManager httpManager = new HTTPManager(this.getApplicationContext());
        final Button login = findViewById(R.id.login_btn_sign_in);
        login.setOnClickListener(view -> {
            final TextView error = findViewById(R.id.login_error);
            final TextView email_textView = findViewById(R.id.login_user_email);
            String email = email_textView.getText().toString();
            final TextView password_textView = findViewById(R.id.login_user_password);
            String password = password_textView.getText().toString();

            System.out.println("User email: " + email);
            System.out.println("User password: " + password);
            if ((email.isEmpty()) || password.isEmpty()) {
                error.setText(getString(R.string.EMPTY_EMAIL_PASSWORD));
            } else {
                password = Tools.encrypt(password);
                Map<String, String> parameters = new HashMap<>();
                parameters.put(API.API_KEY_PASSWORD, password);
                parameters.put(API.API_KEY_EMAIL, email);
                httpManager.POSTRequest(
                        "user/login",
                        new JSONObject(parameters),
                        this::onSuccessLoggedIn, (response) -> {
                            error.setText(getString(R.string.EMAIL_OR_PASSWORD_INVALID));
                        });
            }
        });
    }

    private void onSuccessLoggedIn(JSONObject response) {
        this.response = response;
        OnUserLoggedIn();
    }

    abstract protected void OnUserLoggedIn();
}
