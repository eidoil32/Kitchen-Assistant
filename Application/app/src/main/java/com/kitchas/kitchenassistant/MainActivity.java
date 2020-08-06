package com.kitchas.kitchenassistant;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kitchas.kitchenassistant.activities.LoginActivity;
import com.kitchas.kitchenassistant.assistant.Assistant;

import org.json.JSONObject;

public class MainActivity extends LoginActivity {
    @Override
    protected void OnUserLoggedIn(JSONObject response) {

    }
}
