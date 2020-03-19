package com.kitchas.kitchenassistant.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    private Context context;

    public PermissionHelper(ContextCompat contextCompat, Context context) {
        this.context = context;
    }

    public boolean checkPermissions() {
        for (String permission : Settings.PERMISSION_STRING_ARRAY) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
