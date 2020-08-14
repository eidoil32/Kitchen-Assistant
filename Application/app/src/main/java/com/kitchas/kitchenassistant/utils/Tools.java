package com.kitchas.kitchenassistant.utils;
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
}
