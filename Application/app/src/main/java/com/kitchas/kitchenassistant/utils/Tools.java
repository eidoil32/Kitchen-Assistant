package com.kitchas.kitchenassistant.utils;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;

public class Tools {
    public static String encrypt(String text) {
        return DigestUtils.sha256Hex(text);
    }

    public static void ShowInputErrors(Map<TextInputLayout, String> errors) {
        for(Map.Entry<TextInputLayout, String> error : errors.entrySet()) {
            error.getKey().setError(error.getValue());
        }
    }

    public static int getCurrentTimeStamp() {
        return (int) ((int)System.currentTimeMillis() / 1000L);
    }

    public static void hideTopBar(AppCompatActivity activity) {
        try {
            activity.getSupportActionBar().hide();
        } catch (NullPointerException ignored) { }
    }
}
