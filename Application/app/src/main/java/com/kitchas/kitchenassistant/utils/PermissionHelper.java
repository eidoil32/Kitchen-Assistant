package com.kitchas.kitchenassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.security.KeyPair;
import java.util.Map;
import java.util.Set;

public class PermissionHelper {
    private Context context;
    private Activity activity;

    public PermissionHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public boolean checkPermissions() {
        for (Map.Entry<String, String> permission : Settings.PERMISSION_ARRAY.entrySet()) {
            if ((ContextCompat.checkSelfPermission(context, permission.getKey()) != PackageManager.PERMISSION_GRANTED)
                 && (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.getValue()))) {
                    return false;
            }
        }

        return true;
    }
}
