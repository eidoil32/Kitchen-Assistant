package com.kitchas.kitchenassistant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kitchas.kitchenassistant.activities.BaseActivity;
import com.kitchas.kitchenassistant.assistant.models.recipe.Ingredient;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Step;
import com.kitchas.kitchenassistant.assistant.motiondetection.MotionDetector;
import com.kitchas.kitchenassistant.assistant.voicedetection.TextToSpeechManager;

import java.util.List;

public class PersonalAssistantActivity extends BaseActivity {
    private Recipe recipe;
    private String currentTextToSay = "";
    private boolean doneWithIngredients = false;
    private boolean doneWithSteps = false;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private TextView textView;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_assistant);
        textView = findViewById(R.id.textView);
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(btn -> activePersonalAssistantInteraction());

        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("recipeId");
        //recipe1 = (Recipe) intent.getSerializableExtra("recipe");
        //ingredients = recipe.getIngredients();
        //steps = recipe.getInstructions().steps;
        //sayHello();

        MotionDetector.ActiveMotionDetector(this, result -> {
            if (result) {
                activePersonalAssistantInteraction();
            }
        }, error -> {
            Toast.makeText(this, "Problem with motion detector!", Toast.LENGTH_LONG).show();
        });

        Recipe.loadRecipeByID(recipeId, this, _recipe -> {
            if (_recipe instanceof Recipe) {
                this.recipe = (Recipe) _recipe;
                ingredients = recipe.getIngredients();
                steps = recipe.getInstructions().steps;
                sayHello();
            }
        }, error -> {
            System.out.println("Error loading recipe");
            System.out.println(error);
        });

        textToSpeechManager = new TextToSpeechManager(this) {
            @Override
            public String getText() {
                return currentTextToSay;
            }
        };
    }

    private void sayHello() {
        currentTextToSay = "Hello! let's get start cooking!";
        textView.setText(currentTextToSay);
        textToSpeechManager.speak(this, ENGINE_REQUEST_TEXT_TO_SPEECH);
    }

    private void activePersonalAssistantInteraction() {

        if (!doneWithIngredients && ingredients != null && ingredients.size() > 0) {
            currentTextToSay = ingredients.get(0).getDescription() + " " + ingredients.get(0).getTitle();
            textView.setText(currentTextToSay);
            ingredients.remove(0);
        } else {
            doneWithIngredients = true;
            currentTextToSay = "";
        }

        if (doneWithIngredients) {
            if (!doneWithSteps && steps != null && steps.size() > 0) {
                currentTextToSay = steps.get(0).getDescription();
                textView.setText(currentTextToSay);
                steps.remove(0);
            } else {
                doneWithSteps = true;
                currentTextToSay = "";
            }
        }

        if (currentTextToSay != "") {
            textToSpeechManager.speak(this, ENGINE_REQUEST_TEXT_TO_SPEECH);
        }

        if (doneWithSteps) {
            //todo!!
        }

    }
}