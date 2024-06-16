package com.example.analiz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Kaydedilen verileri al
        ArrayList<String> accelerometerDataList = getIntent().getStringArrayListExtra("accelerometerDataList");
        ArrayList<String> gyroscopeDataList = getIntent().getStringArrayListExtra("gyroscopeDataList");

        // Accelerometer tablosunu doldur
        TableLayout accelerometerTable = findViewById(R.id.accelerometerTable);
        for (String data : accelerometerDataList) {
            TableRow row = new TableRow(this);
            String[] values = data.split(", ");

            for (String value : values) {
                TextView textView = new TextView(this);
                textView.setText(value.split(": ")[1]); // Sadece değer kısmını al
                textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                row.addView(textView);
            }
            accelerometerTable.addView(row);
        }

        // Gyroscope tablosunu doldur
        TableLayout gyroscopeTable = findViewById(R.id.gyroscopeTable);
        for (String data : gyroscopeDataList) {
            TableRow row = new TableRow(this);
            String[] values = data.split(", ");

            for (String value : values) {
                TextView textView = new TextView(this);
                textView.setText(value.split(": ")[1]); // Sadece değer kısmını al
                textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                row.addView(textView);
            }
            gyroscopeTable.addView(row);
        }
    }
}
