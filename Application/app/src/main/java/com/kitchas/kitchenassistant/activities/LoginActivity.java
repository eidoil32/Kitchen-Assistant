package com.kitchas.kitchenassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.assistant.voicedetection.TextToSpeechManager;
import com.kitchas.kitchenassistant.utils.CustomTextWatcher;
import com.kitchas.kitchenassistant.utils.GeneralException;
import com.kitchas.kitchenassistant.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    protected JSONObject response;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.user = User.getInstance(this);
            init();
        } catch (Exception e) {
            // TODO - show error message
        }
    }

    private void OnUserLoggedIn() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void init() {
        if (this.user.isLoggedIn()) {
            this.OnUserLoggedIn();
        } else {
            Tools.hideTopBar(this);
            setContentView(R.layout.activity_login);
            loginScreen();
        }
    }

    private void loginScreen() {
        final Button login = findViewById(R.id.login_btn_sign_in);
        login.setOnClickListener(this::login);

        final Button register = findViewById(R.id.login_btn_sign_up);
        register.setOnClickListener(this::addName);
    }

    private void onSuccessLoggedIn(JSONObject response) {
        try {
            this.user.setData(response.getJSONObject("user"));
            this.user.saveToLocal(this, response.getString("token"));
            Toast.makeText(this, R.string.TOAST_LOGGED_IN_SUCCESSFULLY, Toast.LENGTH_LONG).show();
            OnUserLoggedIn();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void setErrorMessage(JSONObject response) {
        final TextView error = findViewById(R.id.login_error);
        try {
            int resId = getResources().getIdentifier(response.getString("error"), "string", getPackageName());
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

    private String[] getEmailPassword() {
        Map<TextInputLayout, String> errors = new HashMap<>();
        final TextInputLayout input_email = findViewById(R.id.login_user_email);
        final TextInputLayout input_password = findViewById(R.id.login_user_password);

        EditText email = input_email.getEditText(),
                password = input_password.getEditText();

        if (email != null && email.getText().toString().isEmpty()) {
            errors.put(input_email, getString(R.string.EMPTY_EMAIL));
            email.addTextChangedListener((CustomTextWatcher) () -> input_email.setError(null));
        }

        if (password != null && password.getText().toString().isEmpty()) {
            errors.put(input_password, getString(R.string.EMPTY_PASSWORD));
            password.addTextChangedListener((CustomTextWatcher) () -> input_password.setError(null));
        }

        if (errors.isEmpty()) { // email is not null && password is not null!
            return new String[]{email.getText().toString(), password.getText().toString()};
        } else {
            Tools.ShowInputErrors(errors);
        }

        return null;
    }

    private void login(View view) {
        TextToSpeechManager textToSpeechManager = TextToSpeechManager.getInstance();
        textToSpeechManager.speak(this, "Hello World!");
        String[] values = getEmailPassword();

        if (values != null) { // email is not null && password is not null!
            User.login(values[0],
                    values[1],
                    this::onSuccessLoggedIn,
                    this::setErrorMessage,
                    this);
        }
    }

    private void addName(View view) {
        String[] values = getEmailPassword();
        if (values != null) {
            View inputTextView = LayoutInflater
                    .from(view.getContext())
                    .inflate(R.layout.activity_input_text, (ViewGroup) view.getParent(), false);
            final TextInputLayout inputName = inputTextView.findViewById(R.id.text_input);
            inputName.setHelperText(getString(R.string.SET_NAME_INPUT_HELPER, values[0].split("@")[0]));
            AlertDialog dialog = new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle(R.string.SET_NAME_DIALOG_CONTENT)
                    .setPositiveButton(R.string.NEXT_BTN, (dialogInterface, buttonPressed) -> {
                        String name = values[0].split("@")[0];
                        if (inputName.getEditText() != null && !inputName.getEditText().getText().toString().isEmpty()) {
                            name = inputName.getEditText().getText().toString();
                        }

                        this.register(new String[]{values[0], values[1], name});
                    })
                    .setNegativeButton(R.string.CANCEL_BTN, (dialogInterface, buttonPressed) -> {})
                    .setView(inputTextView)
                    .show();
       }
    }

    private void register(String[] values) {
        if (values != null) { // email is not null && password is not null!
            User.register(values,
                    this::onSuccessLoggedIn,
                    this::setErrorMessage,
                    this
            );
        }
    }
}
