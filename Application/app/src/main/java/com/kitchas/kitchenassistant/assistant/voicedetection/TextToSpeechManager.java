package com.kitchas.kitchenassistant.assistant.voicedetection;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public abstract class TextToSpeechManager implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private Context context;

    public TextToSpeechManager(Context context) {
        this.context = context;
    }

    public void speak(AppCompatActivity activity, int ENGINE_REQUEST) {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        activity.startActivityForResult(checkIntent, ENGINE_REQUEST);
    }

    public abstract String getText();

    public void preformSpeak(int resultCode, AppCompatActivity activity) {

        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            this.textToSpeech = new TextToSpeech(activity, this);//this, Manager
        } else {//Ask to install what needed!
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            activity.startActivity(installIntent);
        }

    public void speak(Context context, String text) {
        this.textToSpeech = new
                TextToSpeech(context, this);
        textToSpeech.setSpeechRate(pitch);
        textToSpeech.setSpeechRate(speech);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null); //QUEUE_ADD
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int languageStatus = textToSpeech.setLanguage(Locale.ENGLISH);
            if (languageStatus == TextToSpeech.LANG_MISSING_DATA || languageStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "hey2", Toast.LENGTH_LONG).show();
            } else {
                String text = getText();
                int speechStatus = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                if (speechStatus == TextToSpeech.ERROR) {
                    Toast.makeText(context, "hey3", Toast.LENGTH_LONG).show();
                }
            }

        } else {
            Toast.makeText(context, "hey1", Toast.LENGTH_LONG).show();
        }
    }
}