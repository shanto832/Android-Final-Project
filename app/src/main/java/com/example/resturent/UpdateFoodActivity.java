package com.example.resturent;





import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UpdateFoodActivity extends AppCompatActivity {

    private Spinner spinnerFood;
    private EditText editPrice, editQuantity;
    private Button btnUpdate, btnHome;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_food);

        spinnerFood = findViewById(R.id.spinnerFood);
        editPrice = findViewById(R.id.editPrice);
        editQuantity = findViewById(R.id.editQuantity);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnHome = findViewById(R.id.btnHome);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch food names for the spinner
        fetchFoodNames();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedFood = spinnerFood.getSelectedItem().toString();
                String newPrice = editPrice.getText().toString();
                String newQuantity = editQuantity.getText().toString();

                if (selectedFood.isEmpty() || newPrice.isEmpty() || newQuantity.isEmpty()) {
                    Toast.makeText(UpdateFoodActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    updateFoodDetails(selectedFood, newPrice, newQuantity);
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ManageActivity
                Intent intent = new Intent(UpdateFoodActivity.this, Manage.class);
                startActivity(intent);
            }
        });
    }

    private void fetchFoodNames() {
        db.collection("FoodItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> foodNames = new ArrayList<>();
                            for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                                String foodName = document.getString("foodName");
                                foodNames.add(foodName);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateFoodActivity.this,
                                    android.R.layout.simple_spinner_item, foodNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerFood.setAdapter(adapter);
                        } else {
                            Toast.makeText(UpdateFoodActivity.this, "Failed to fetch food names", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateFoodDetails(String foodName, String price, String quantity) {
        db.collection("FoodItems")
                .whereEqualTo("foodName", foodName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String documentId = task.getResult().getDocuments().get(0).getId();

                            db.collection("FoodItems").document(documentId)
                                    .update("foodPrice", price, "foodQuantity", quantity)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(UpdateFoodActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(UpdateFoodActivity.this, "Update Failed", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(UpdateFoodActivity.this, "Food item not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
