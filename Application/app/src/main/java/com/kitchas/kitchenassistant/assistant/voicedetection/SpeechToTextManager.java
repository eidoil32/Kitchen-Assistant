package com.kitchas.kitchenassistant.assistant.voicedetection;

import android.content.Intent;
import android.speech.RecognizerIntent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SpeechToTextManager {
    private static SpeechToTextManager instance = null;

    private SpeechToTextManager() { }

    public static SpeechToTextManager getInstance() {
        if (instance == null) {
            instance = new SpeechToTextManager();
        }

        return instance;
    }

    public void listen(AppCompatActivity activity, int request_code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say some thing now!");

        try {
            activity.startActivityForResult(intent, request_code);
        } catch (Exception e) {
            // TODO error
        }
    }
}
