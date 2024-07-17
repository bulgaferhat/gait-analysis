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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private DatabaseReference databaseReference;
    private String username;

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

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameInputDialog();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        startRecording();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometerSensor != null) {
            sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (gyroscopeSensor != null) {
            sensorManager.registerListener(sensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void saveSensorData() {
        long currentTime = SystemClock.elapsedRealtime();
        float elapsedSeconds = (currentTime - startTime) / 1000f;

        String accelerometerData = String.format("Zaman: %.6f, X: %.6f, Y: %.6f, Z: %.6f, İsim: %s", elapsedSeconds, lastAccelerometerX, lastAccelerometerY, lastAccelerometerZ, username);
        String gyroscopeData = String.format("Zaman: %.6f, X: %.6f, Y: %.6f, Z: %.6f, İsim: %s", elapsedSeconds, lastGyroscopeX, lastGyroscopeY, lastGyroscopeZ, username);

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

        saveDataToFirebase();

        Intent intent = new Intent(SensorActivity.this, ResultActivity.class);
        intent.putStringArrayListExtra("accelerometerDataList", (ArrayList<String>) accelerometerDataList);
        intent.putStringArrayListExtra("gyroscopeDataList", (ArrayList<String>) gyroscopeDataList);
        startActivity(intent);
    }

    private void saveDataToFirebase() {
        String timestamp = Long.toString(System.currentTimeMillis());
        for (int i = 0; i < accelerometerDataList.size(); i++) {
            databaseReference.child("users").child(username).child("accelerometer").child(timestamp).child(Integer.toString(i)).setValue(accelerometerDataList.get(i));
        }

        for (int i = 0; i < gyroscopeDataList.size(); i++) {
            databaseReference.child("users").child(username).child("gyroscope").child(timestamp).child(Integer.toString(i)).setValue(gyroscopeDataList.get(i));
        }
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("İsim Giriniz");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                username = input.getText().toString();
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
        this.username = username; // Kullanıcı adını sınıf seviyesinde saklayın
        for (int i = 0; i < accelerometerDataList.size(); i++) {
            String data = accelerometerDataList.get(i).replaceAll("İsim: .*", "İsim: " + username);
            accelerometerDataList.set(i, data);
        }

        for (int i = 0; i < gyroscopeDataList.size(); i++) {
            String data = gyroscopeDataList.get(i).replaceAll("İsim: .*", "İsim: " + username);
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

                accelerometerTextView.setText(String.format("xAxis: %.6f\nyAxis: %.6f\nzAxis: %.6f", lastAccelerometerX, lastAccelerometerY, lastAccelerometerZ));
            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                lastGyroscopeX = event.values[0];
                lastGyroscopeY = event.values[1];
                lastGyroscopeZ = event.values[2];

                gyroscopeTextView.setText(String.format("xRR: %.6f\nyRR: %.6f\nzRR: %.6f", lastGyroscopeX, lastGyroscopeY, lastGyroscopeZ));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    };
}
