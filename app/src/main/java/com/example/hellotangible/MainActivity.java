package com.example.hellotangible;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button fakeButton;
    private View[] colorFields;
    private ImageView smokeImageView;
    private boolean colorFieldsVisible = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final float THRESHOLD = 9.8f; // Tilt threshold for hiding color fields


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fakeButton = findViewById(R.id.fakeButton);
        smokeImageView = findViewById(R.id.smokeImageView);

        colorFields = new View[]{
                findViewById(R.id.colorField1),
                findViewById(R.id.colorField2),
                findViewById(R.id.colorField3),
                findViewById(R.id.colorField4),
                findViewById(R.id.colorField5),
                findViewById(R.id.colorField6),
                findViewById(R.id.colorField7),
                findViewById(R.id.colorField8),
                findViewById(R.id.colorField9),
                findViewById(R.id.colorField10),
                findViewById(R.id.colorField11),
                findViewById(R.id.colorField12),
                findViewById(R.id.colorField13),
                findViewById(R.id.colorField14),
                findViewById(R.id.colorField15),
                findViewById(R.id.colorField16),
                findViewById(R.id.colorField17),
                findViewById(R.id.colorField18),
                findViewById(R.id.colorField19),
                findViewById(R.id.colorField20),
                findViewById(R.id.colorField21)
        };

        // Initialize accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        fakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleColorFieldsVisibility();
            }
        });
    }

    private void toggleColorFieldsVisibility() {
        if (!colorFieldsVisible) {
            showColorFieldsWithDelay();
        } else {
            hideColorFieldsWithDelay();
        }
    }

    private void showColorFieldsWithDelay() {
        for (int i = 0; i < colorFields.length; i++) {
            final int index = i;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    colorFields[index].setVisibility(View.VISIBLE);
                    if (index == colorFields.length - 1) {
                        smokeImageView.setVisibility(View.VISIBLE);
                    }
                }
            }, i * 200);
        }
        colorFieldsVisible = true;
    }

    private void hideColorFieldsWithDelay() {
        for (int i = colorFields.length - 1; i >= 0; i--) {
            final int index = i;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    colorFields[index].setVisibility(View.GONE);
                    if (index == 0) {
                        smokeImageView.setVisibility(View.GONE);
                    }
                }
            }, (colorFields.length - i - 1) * 200);
        }
        colorFieldsVisible = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];

            // Check if device is tilted beyond threshold along x-axis (left or right)
            if (Math.abs(x) > THRESHOLD) {
                if (colorFieldsVisible) {
                    hideColorFieldsWithDelay();
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister sensor listener
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
