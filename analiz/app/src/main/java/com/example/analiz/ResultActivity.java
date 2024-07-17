package com.example.analiz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.View;

public class ResultActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private String username = "your_username"; // Bu, geçerli kullanıcı adınız olmalıdır
    private ArrayList<String> accelerometerDataList;
    private ArrayList<String> gyroscopeDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Intent'ten verileri al
        accelerometerDataList = getIntent().getStringArrayListExtra("accelerometerDataList");
        gyroscopeDataList = getIntent().getStringArrayListExtra("gyroscopeDataList");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(username);

        TableLayout accelerometerTable = findViewById(R.id.accelerometerTable);
        TableLayout gyroscopeTable = findViewById(R.id.gyroscopeTable);

        // Firebase'den verileri çek
        getDataFromFirebase(accelerometerTable, "accelerometer");
        getDataFromFirebase(gyroscopeTable, "gyroscope");

        // Intent'ten gelen verileri listele
        populateTableWithIntentData(accelerometerTable, accelerometerDataList);
        populateTableWithIntentData(gyroscopeTable, gyroscopeDataList);
    }

    private void getDataFromFirebase(final TableLayout tableLayout, String dataType) {
        databaseReference.child(dataType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot timestampSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot : timestampSnapshot.getChildren()) {
                        TableRow row = new TableRow(ResultActivity.this);
                        String data = snapshot.getValue(String.class);
                        String[] values = data.split(", ");

                        for (String value : values) {
                            TextView textView = new TextView(ResultActivity.this);
                            String numericValue = value.split(": ")[1];
                            textView.setText(numericValue); // Sadece değer kısmını al
                            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
                            textView.setLayoutParams(params);
                            textView.setPadding(16, 8, 16, 8);
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            row.addView(textView);
                        }
                        tableLayout.addView(row);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata durumunda yapılacak işlemler
            }
        });
    }

    private void populateTableWithIntentData(TableLayout tableLayout, ArrayList<String> dataList) {
        for (String data : dataList) {
            TableRow row = new TableRow(this);
            String[] values = data.split(", ");

            for (String value : values) {
                TextView textView = new TextView(this);
                String numericValue = value.split(": ")[1];
                textView.setText(numericValue); // Sadece değer kısmını al
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
                textView.setLayoutParams(params);
                textView.setPadding(16, 8, 16, 8);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(textView);
            }
            tableLayout.addView(row);
        }
    }
}
