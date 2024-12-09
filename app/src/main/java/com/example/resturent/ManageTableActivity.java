package com.example.resturent;







import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManageTableActivity extends AppCompatActivity {

    private EditText editTableName, editTableNumber;
    private Button btnSubmit, btnHome;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_table);

        editTableName = findViewById(R.id.editTableName);
        editTableNumber = findViewById(R.id.editTableNumber);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnHome = findViewById(R.id.btnHome);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = editTableName.getText().toString();
                String tableNumberString = editTableNumber.getText().toString();

                if (tableName.isEmpty() || tableNumberString.isEmpty()) {
                    Toast.makeText(ManageTableActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int tableNumber = Integer.parseInt(tableNumberString); // Convert to integer
                        saveTableInfo(tableName, tableNumber);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ManageTableActivity.this, "Table number must be an integer", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to ManageActivity
                Intent intent = new Intent(ManageTableActivity.this, Manage.class);
                startActivity(intent);
            }
        });
    }

    private void saveTableInfo(String tableName, int tableNumber) {
        // Create a map to hold the data
        Map<String, Object> tableInfo = new HashMap<>();
        tableInfo.put("tableName", tableName);
        tableInfo.put("tableNumber", tableNumber); // Store as an integer

        // Add to Firestore collection "TableInfo"
        db.collection("TableInfo")
                .add(tableInfo)
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ManageTableActivity.this, "Table added successfully!", Toast.LENGTH_SHORT).show();
                            // Clear the input fields
                            editTableName.setText("");
                            editTableNumber.setText("");
                        } else {
                            Toast.makeText(ManageTableActivity.this, "Failed to add table", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
