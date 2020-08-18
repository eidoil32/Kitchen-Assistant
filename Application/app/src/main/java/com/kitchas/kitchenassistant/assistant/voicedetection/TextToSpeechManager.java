package com.kitchas.kitchenassistant.assistant.voicedetection;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import java.util.Locale;

public class TextToSpeechManager implements TextToSpeech.OnInitListener {
    private static final float speech = 1f;
    private static final float pitch = 1f;
    private static TextToSpeechManager instance = null;
    private TextToSpeech textToSpeech;

    private TextToSpeechManager() {
    }

    public static TextToSpeechManager getInstance() {
        if (instance == null) {
            instance = new TextToSpeechManager();
        }

        return instance;
    }

    public void speak(Context context, String text) {
        this.textToSpeech = new TextToSpeech(context, this);
        textToSpeech.setSpeechRate(pitch);
        textToSpeech.setSpeechRate(speech);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null); //QUEUE_ADD
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //todo error!
            } else {

            }
        } else {
            //todo error!
        }
    }
}