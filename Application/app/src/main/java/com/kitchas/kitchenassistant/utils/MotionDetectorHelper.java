package com.kitchas.kitchenassistant.utils;

import android.content.Context;
import android.view.SurfaceView;
import com.kitchas.kitchenassistant.motion.motiondetection.motiondetection.MotionDetector;
import com.kitchas.kitchenassistant.motion.motiondetection.motiondetection.MotionDetectorCallback;

public class MotionDetectorHelper {
    private MotionDetector motionDetector;

    public MotionDetectorHelper(Context context, SurfaceView surfaceView, MotionDetectorCallback callback) {
        motionDetector = new MotionDetector(context, surfaceView);
        motionDetector.setMotionDetectorCallback(callback);

        // configure motion detector
        motionDetector.setCheckInterval(500);
        motionDetector.setLeniency(20);
        motionDetector.setMinLuma(1000);
    }

    public void onResume() {
        motionDetector.onResume();
    }

    public void onPause() {
        motionDetector.onPause();
    }

    public boolean checkHardware() {
        return motionDetector.checkCameraHardware();
    }
}
