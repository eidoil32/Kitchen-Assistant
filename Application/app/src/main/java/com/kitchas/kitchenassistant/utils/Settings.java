package com.kitchas.kitchenassistant.utils;

import android.Manifest;

public class Settings {
    public static final int CAMERA_PERMISSION_REQUEST = 0;
    public static final String[] PERMISSION_STRING_ARRAY = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO};
}
