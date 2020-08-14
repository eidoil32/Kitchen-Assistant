package com.kitchas.kitchenassistant.assistant.motiondetection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MotionDetector {
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener listener;
    private final Context context;

    public MotionDetector(Context context) throws Exception {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (this.proximitySensor == null) {
            throw new Exception("PROXIMITY_SENSOR_NOT_AVAILABLE");
        }
    }

    public void detectMovement(ICallback callBack) {
        this.listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                callBack.function(sensorEvent.values[0] < proximitySensor.getMaximumRange());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) { }
        };

        this.sensorManager.registerListener(listener, proximitySensor, 2 * 1000 * 1000);
    }
}
