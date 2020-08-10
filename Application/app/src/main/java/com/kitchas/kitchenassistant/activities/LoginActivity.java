package com.kitchas.kitchenassistant.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.GeneralException;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class LoginActivity extends BaseActivity {
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
        if (User.isLoggedIn()) {
            OnUserLoggedIn();
        } else {
            setContentView(R.layout.activity_login);
            loginScreen();
        }
    }

    private void loginScreen() {
        final HTTPManager httpManager = new HTTPManager(this.getApplicationContext());
        final Button login = findViewById(R.id.login_btn_sign_in);
        login.setOnClickListener(this::login);

        final Button register = findViewById(R.id.login_btn_sign_up);
        register.setOnClickListener(this::register);
    }

    private void onSuccessLoggedIn(JSONObject response) {
        this.response = response;
        OnUserLoggedIn();
    }

    abstract protected void OnUserLoggedIn();
    protected void setErrorMessage(JSONObject response) {
        final TextView error = findViewById(R.id.login_error);
        try {
            int resId = getResources().getIdentifier(response.getString("error") , "string", getPackageName());
            if (resId != 0) {
                error.setText(getString(resId));
            } else {
                System.out.println("string resource not found: " + response.getString("error"));
                throw new GeneralException(getString(R.string.UNKNOWN_ERROR));
            }
        } catch (Exception e) {
            error.setText(getText(R.string.UNKNOWN_ERROR));
        }
    }

    private void login(View view) {
        final TextView email_textView = findViewById(R.id.login_user_email);
        final TextView password_textView = findViewById(R.id.login_user_password);

        User.login(email_textView.getText().toString(),
                password_textView.getText().toString(),
                this::onSuccessLoggedIn,
                this::setErrorMessage,
                this
        );
    }

    private void register(View view) {
        final TextView email_textView = findViewById(R.id.login_user_email);
        final TextView password_textView = findViewById(R.id.login_user_password);

        User.register(email_textView.getText().toString(),
                password_textView.getText().toString(),
                this::onSuccessLoggedIn,
                this::setErrorMessage,
                this
        );
    }
}
