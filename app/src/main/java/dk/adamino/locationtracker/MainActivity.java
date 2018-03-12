package dk.adamino.locationtracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ILocationCallBack, ISensorCallbacks {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    ToggleButton btnListening;
    Button btnHomeLocation;

    TextView txtHomeLocation, txtDistanceFromHome, txtDeviceVelocityValue, txtStepsFromHome;

    private LocationListener mLocationListener;
    private SensorEventListener mSensorEventListener;
    private Location mHomeLocation;
    private NumberFormat mDecimalFormat = new DecimalFormat("#.##");
    private LocationManager mLocationManager;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtHomeLocation = findViewById(R.id.currentLoc);
        btnListening = findViewById(R.id.startListening);
        txtDistanceFromHome = findViewById(R.id.txtDistanceFromHome);
        txtDeviceVelocityValue = findViewById(R.id.txtDeviceVelocityValue);
        txtStepsFromHome = findViewById(R.id.txtStepsFromHomeValue);
        btnHomeLocation = findViewById(R.id.set_home);

        btnListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnListening.isChecked())
                    MainActivity.this.startListening();
                else
                    MainActivity.this.stopListening();
            }
        });
        btnHomeLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.this.setHomeLocation();
            }
        });

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mLocationListener = null;
    }

    private void registerStepCounterSensor() {
        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            mSensorManager.registerListener(mSensorEventListener, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Couldn't register sensor!", Toast.LENGTH_SHORT).show();
        }
    }

    private void unregisterStepCounterSensor() {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListening();
        if (btnListening.isChecked())
            btnListening.setChecked(false);
    }

    protected void setHomeLocation() {
        mHomeLocation = getHomeLocation();
        if (mHomeLocation == null) {
            Toast.makeText(getApplicationContext(), "Last known location is null",
                    Toast.LENGTH_LONG).show();
            return;
        }
        double latitude = mHomeLocation.getLatitude();
        double longitude = mHomeLocation.getLongitude();
        String msg = "Latitude: " + latitude + "\n" +
                     "Longitude: "+ longitude;

        txtHomeLocation.setText(msg);

    }

    private Location getHomeLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.e("GPS", "NEED PERMISSION TO KNOW LASTKNOWN!");

            checkPermissions();
        }
        Location location = mLocationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        return location;

    }

    protected void startListening() {

        mLocationListener = new MyLocationListener(this);
        mSensorEventListener = new MySensorListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("GPS", "NEED PERMISSION TO LISTEN!");
            checkPermissions();
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, // the minimal time (ms) between notifications
                0, // the minimal distance (meter) between notifications
                mLocationListener);


        registerStepCounterSensor();
    }

    private void stopListening() {
        if (mLocationListener == null) return;
        mLocationManager.removeUpdates(mLocationListener);
        unregisterStepCounterSensor();
    }

    // ILocationCallBack

    @Override
    public void setVelocity(double speed) {
        String speedAsText = mDecimalFormat.format(speed) + "m/s";
        txtDeviceVelocityValue.setText(speedAsText);
    }

    public void setDistanceFromHome(Location location) {
        String distanceToHomeAsText = mDecimalFormat.format(location.distanceTo(mHomeLocation)) + "m";
        txtDistanceFromHome.setText(distanceToHomeAsText);
    }

    /**
     * Verify that user allowed usage of camera
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }

    /**
     * checking  permissions at Runtime.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final String[] requiredPermissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }

    @Override
    public void setSteps(int steps) {
        txtStepsFromHome.setText(steps + "");
    }
}
