package dk.adamino.locationtracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Adamino.
 */

public class MySensorListener implements SensorEventListener {

    ISensorCallBack m_view;
    private boolean mJustStartedCounting;
    private float mStartSteps;

    public MySensorListener(ISensorCallBack view) {
        m_view = view;
        mJustStartedCounting = true;
    }

    /**
     * Get updates from sensor!
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float steps = event.values[0];
        if (mJustStartedCounting) {
            mStartSteps = steps;
            mJustStartedCounting = false;
        }
        // Reasoning behind this math can be found here: https://stackoverflow.com/questions/22649324/step-counter-doesnt-reset-the-step-count
        int stepsTakenSinceStart = (int) (steps - mStartSteps);
        m_view.setSteps(stepsTakenSinceStart);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
