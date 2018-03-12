package dk.adamino.locationtracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Adamino.
 */

public class MySensorListener implements SensorEventListener {

    ISensorCallbacks m_view;
    private boolean mjustStartedCounting;
    private float mStartSteps;

    public MySensorListener(ISensorCallbacks view) {
        m_view = view;
        mjustStartedCounting = true;
    }

    /**
     * Get updates from sensor!
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float steps = event.values[0];
        if (mjustStartedCounting) {
            mStartSteps = steps;
            mjustStartedCounting = false;
        }
        // Reasoning behind this math can be found here: https://stackoverflow.com/questions/22649324/step-counter-doesnt-reset-the-step-count
        int stepsTakenSinceStart = (int) (steps - mStartSteps);
        m_view.setSteps(stepsTakenSinceStart);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
