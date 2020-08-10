package com.kitchas.kitchenassistant.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.utils.GeneralException;
import com.kitchas.kitchenassistant.utils.PermissionHelper;

abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _init();
        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }

    protected void _init() throws GeneralException {
        getPermissions();
    }

    private void getPermissions() throws GeneralException {
        PermissionHelper permissionHelper = new PermissionHelper(getApplicationContext(), this);
        if (!permissionHelper.checkPermissions()) {
            throw new GeneralException(getString(R.string.PERMISSION_NOT_GRANTED));
        }

    }
}
