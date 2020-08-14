package com.kitchas.kitchenassistant;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kitchas.kitchenassistant.activities.LoginActivity;
import com.kitchas.kitchenassistant.assistant.Assistant;
import com.kitchas.kitchenassistant.assistant.motiondetection.MotionDetector;

import org.json.JSONObject;

public class MainActivity extends LoginActivity {
    @Override
    protected void OnUserLoggedIn() {
        System.out.println("In main activity");
        try {
            MotionDetector motionDetector = new MotionDetector(this);
            motionDetector.detectMovement((event) -> {
                if (event) {
                    System.out.println("Motion!");
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
