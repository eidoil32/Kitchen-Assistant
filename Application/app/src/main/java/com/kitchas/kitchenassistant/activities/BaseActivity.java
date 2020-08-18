package com.kitchas.kitchenassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.voicedetection.SpeechToTextManager;
import com.kitchas.kitchenassistant.utils.GeneralException;
import com.kitchas.kitchenassistant.utils.PermissionHelper;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity {
    protected SpeechToTextManager speechToTextManager;
    protected int SPEECH_CODE = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _init();
        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }

    protected int getSpeechCode() {
        return this.SPEECH_CODE;
    }

    protected void setSpeechCode(int code) {
        this.SPEECH_CODE = code;
    }

    protected void _init() throws GeneralException {
        getPermissions();
        speechToTextManager = SpeechToTextManager.getInstance();
    }

    private void getPermissions() throws GeneralException {
        PermissionHelper permissionHelper = new PermissionHelper(getApplicationContext(), this);
        if (!permissionHelper.checkPermissions()) {
            throw new GeneralException(getString(R.string.PERMISSION_NOT_GRANTED));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.getSpeechCode() && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            this.speechResult(result != null ? result.get(0) : "");
        }
    }

    protected void speechResult(String data) {
        // do nothing
    }
}
