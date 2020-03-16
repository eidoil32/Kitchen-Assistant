package com.kitchas.kitchenassistant.motion.motiondetection.motiondetection;

public interface MotionDetectorCallback {
    void onMotionDetected();
    void onTooDark();
}
