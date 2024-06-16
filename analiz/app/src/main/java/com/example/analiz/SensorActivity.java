package com.example.analiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class SensorActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private TextView accelerometerTextView;
    private TextView gyroscopeTextView;
    private List<String> accelerometerDataList = new ArrayList<>();
    private List<String> gyroscopeDataList = new ArrayList<>();
    private Timer timer;
    private TimerTask timerTask;
    private Button stopButton;
    private float lastAccelerometerX;
    private float lastAccelerometerY;
    private float lastAccelerometerZ;

    private float lastGyroscopeX;
    private float lastGyroscopeY;
    private float lastGyroscopeZ;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerometerTextView = findViewById(R.id.accelerometerTextView);
        gyroscopeTextView = findViewById(R.id.gyroscopeTextView);
        stopButton = findViewById(R.id.stopButton);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        if (accelerometerSensor != null) {
            sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(sensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameInputDialog();
            }
        });

        startRecording();
    }

    private void saveSensorData() {
        long currentTime = SystemClock.elapsedRealtime();
        float elapsedSeconds = (currentTime - startTime) / 1000f;

        String accelerometerData = "Zaman: " + elapsedSeconds + ", X: " + lastAccelerometerX + ", Y: " + lastAccelerometerY + ", Z: " + lastAccelerometerZ;
        String gyroscopeData = "Zaman: " + elapsedSeconds + ", X: " + lastGyroscopeX + ", Y: " + lastGyroscopeY + ", Z: " + lastGyroscopeZ;

        accelerometerDataList.add(accelerometerData);
        gyroscopeDataList.add(gyroscopeData);
    }

    private void startRecording() {
        startTime = SystemClock.elapsedRealtime();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveSensorData();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void stopRecording() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        Intent intent = new Intent(SensorActivity.this, ResultActivity.class);
        intent.putStringArrayListExtra("accelerometerDataList", (ArrayList<String>) accelerometerDataList);
        intent.putStringArrayListExtra("gyroscopeDataList", (ArrayList<String>) gyroscopeDataList);
        startActivity(intent);
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("İsim Giriniz");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = input.getText().toString();
                if (username.isEmpty()) {
                    username = "Unknown";
                }
                saveSensorDataWithUsername(username);
                stopRecording();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void saveSensorDataWithUsername(String username) {
        for (int i = 0; i < accelerometerDataList.size(); i++) {
            String data = accelerometerDataList.get(i) + ", İsim: " + username;
            accelerometerDataList.set(i, data);
        }

        for (int i = 0; i < gyroscopeDataList.size(); i++) {
            String data = gyroscopeDataList.get(i) + ", İsim: " + username;
            gyroscopeDataList.set(i, data);
        }
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                lastAccelerometerX = event.values[0];
                lastAccelerometerY = event.values[1];
                lastAccelerometerZ = event.values[2];

                accelerometerTextView.setText("xAxis: " + lastAccelerometerX + "\nyAxis: " + lastAccelerometerY + "\nzAxis: " + lastAccelerometerZ);
            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                lastGyroscopeX = event.values[0];
                lastGyroscopeY = event.values[1];
                lastGyroscopeZ = event.values[2];

                gyroscopeTextView.setText("xRR: " + lastGyroscopeX + "\nyRR: " + lastGyroscopeY + "\nzRR: " + lastGyroscopeZ);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    };
}
