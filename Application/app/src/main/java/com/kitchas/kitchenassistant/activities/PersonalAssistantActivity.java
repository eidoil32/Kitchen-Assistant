package com.kitchas.kitchenassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.models.recipe.Ingredient;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Step;
import com.kitchas.kitchenassistant.assistant.motiondetection.MotionDetector;
import com.kitchas.kitchenassistant.assistant.voicedetection.TextToSpeechManager;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PersonalAssistantActivity extends BaseActivity {
    private String currentTextToSay = "";
    private boolean doneWithIngredients = false;
    private boolean doneWithSteps = false;
    private TextView shower_text_view;
    private Button next_btn;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private Lock lock = new ReentrantLock();
    private long timeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_assistant);
        shower_text_view = findViewById(R.id.personal_assistant_show);
        next_btn = findViewById(R.id.personal_assistant_next_btn);
        next_btn.setOnClickListener(btn -> activePersonalAssistantInteraction());
        timeCheck = System.currentTimeMillis();

        Intent intent = getIntent();
        Recipe recipe;
        recipe = (Recipe) intent.getSerializableExtra("recipe");
        if (recipe != null) {
            this.ingredients = recipe.getIngredients();
            this.steps = recipe.getInstructions().getSteps();

            MotionDetector.ActiveMotionDetector(this, result -> activePersonalAssistantInteraction()

                    , error -> {
                        Toast.makeText(this, "Problem with motion detector!", Toast.LENGTH_LONG).show();
                    });

            textToSpeechManager = new TextToSpeechManager(this) {
                @Override
                public String getText() {
                    return currentTextToSay;
                }
            };
            sayHello();
        }

    }

    private void sayHello() {
        currentTextToSay = "Hello! let's get start cooking!";
        shower_text_view.setText(currentTextToSay);
        textToSpeechManager.speak(this, ENGINE_REQUEST_TEXT_TO_SPEECH);
    }

    public void activePersonalAssistantInteraction() {
        if (!textToSpeechManager.isSpeaking() && System.currentTimeMillis() - timeCheck > 1000) {
            timeCheck = System.currentTimeMillis();
            if (!doneWithIngredients && ingredients != null && ingredients.size() > 0) {
                currentTextToSay = ingredients.get(0).toString();
                shower_text_view.setText(currentTextToSay);
                ingredients.remove(0);
            } else {
                doneWithIngredients = true;
                currentTextToSay = "";
            }

            if (doneWithIngredients) {
                if (!doneWithSteps && steps != null && steps.size() > 0) {
                    currentTextToSay = steps.get(0).getDescription();
                    shower_text_view.setText(currentTextToSay);
                    steps.remove(0);
                } else {
                    doneWithSteps = true;
                    currentTextToSay = "";
                }
            }

            if (!currentTextToSay.equals("")) {
                currentTextToSay = currentTextToSay.trim().replaceAll("[^A-Za-z0-9 ]", "");
                textToSpeechManager.speak(this, ENGINE_REQUEST_TEXT_TO_SPEECH);
            }

            if (doneWithSteps) {
                next_btn.setText("EXIT");
                next_btn.setOnClickListener(btn -> {
                    finish();
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}