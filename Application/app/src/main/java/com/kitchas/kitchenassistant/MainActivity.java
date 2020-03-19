package com.kitchas.kitchenassistant;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kitchas.kitchenassistant.motion.motiondetection.motiondetection.MotionDetector;
import com.kitchas.kitchenassistant.motion.motiondetection.motiondetection.MotionDetectorCallback;
import com.kitchas.kitchenassistant.utils.Settings;

public class MainActivity extends AppCompatActivity {
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
