package com.kitchas.kitchenassistant.utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.kitchas.kitchenassistant.R;

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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager systemService = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        systemService.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static ProgressDialog showLoading(Context context) {
        return showLoading(context, "Wait while loading...");
    }

    public static ProgressDialog showLoading(Context context, String text) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage(text);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        return progress;
    }
}
