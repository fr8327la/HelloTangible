package com.example.hellotangible;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //private Button fakeButton;
    private View[] colorFields;
    private ImageView smokeImageView;
    private boolean colorFieldsVisible = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final float THRESHOLD = 9.8f; // Tilt threshold for hiding color fields
    private MqttAndroidClient client;
    final String MQTT_HOST = "tcp://broker.hivemq.com:1883";
    final String sub_topic = "mamn01/example/arduino";
    final String pub_topic = "mamn01/example/android";
    final String pub_message = "Hello World!";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fakeButton = findViewById(R.id.fakeButton);
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
/*
        fakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleColorFieldsVisibility();
            }
        });
*/
        connect();
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
            }, i * 150);
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
            }, (colorFields.length - i - 1) * 150);
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

    String inTopic = "mamn01/HelloTangibleGang/arduinoSensor";
    String outTopic = "mamn01/HelloTangibleGang/androidSensor";

    public void connect() {
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);

        /*MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setCleanSession(false);
        options.setUserName("VR-Lab");
        options.setPassword("wifivrlab1".toCharArray());*/
        try {
            //IMqttToken token = client.connect(options);
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("file", "onSuccess");
                    //publish(client,"ourMessageToArduino");
                    subscribe(client,inTopic);
                    client.setCallback(new MqttCallback() {

                        @Override
                        public void connectionLost(Throwable cause) {

                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.d("file", Arrays.toString(message.getPayload()));
                            Log.d("messageArrived", "message arrived!");
                            //if (message.toString().equals("start")) {
                                showColorFieldsWithDelay();
                            //}

                        }
                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("file", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void publish(MqttAndroidClient client, String payload) {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(outTopic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(MqttAndroidClient client, String topic) {
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Subscribe", "SÖCCESSFÖLLY SÖBSCRIBED");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



}
