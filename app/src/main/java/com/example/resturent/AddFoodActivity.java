
//package com.example.resturent;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class AddFoodActivity extends AppCompatActivity {
//
//    private EditText editTextFoodName, editTextFoodPrice, editTextFoodQuantity;
//
//    // Firestore instance
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_food);
//
//        // Initialize Firestore instance
//        db = FirebaseFirestore.getInstance();
//
//        editTextFoodName = findViewById(R.id.editTextFoodName);
//        editTextFoodPrice = findViewById(R.id.editTextFoodPrice);
//        editTextFoodQuantity = findViewById(R.id.editTextFoodQuantity);
//        Button submitButton = findViewById(R.id.submitButton);
//        Button addMoreButton = findViewById(R.id.addMoreButton);
//        Button adminHomeButton = findViewById(R.id.adminHomeButton); // New Admin Home Button
//
//        submitButton.setOnClickListener(v -> submitFoodItem());
//        addMoreButton.setOnClickListener(v -> clearInputFields());
//
//        // Set click listener for the Admin Home button to start the Manage activity
//        adminHomeButton.setOnClickListener(v -> {
//            Intent intent = new Intent(AddFoodActivity.this, Manage.class);
//            startActivity(intent);
//        });
//    }
//
//    private void submitFoodItem() {
//        String foodName = editTextFoodName.getText().toString().trim();
//        String foodPrice = editTextFoodPrice.getText().toString().trim();
//        String foodQuantity = editTextFoodQuantity.getText().toString().trim();
//
//        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(foodPrice) || TextUtils.isEmpty(foodQuantity)) {
//            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a map for the food item
//        Map<String, Object> foodItem = new HashMap<>();
//        foodItem.put("foodName", foodName);
//        foodItem.put("foodPrice", foodPrice);
//        foodItem.put("foodQuantity", foodQuantity);
//
//        // Add a new document with an auto-generated ID to the "FoodItems" collection
//        db.collection("FoodItems").add(foodItem)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(AddFoodActivity.this, "Food item added successfully", Toast.LENGTH_SHORT).show();
//                    clearInputFields();
//                })
//                .addOnFailureListener(e -> Toast.makeText(AddFoodActivity.this, "Failed to add food item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void clearInputFields() {
//        editTextFoodName.setText("");
//        editTextFoodPrice.setText("");
//        editTextFoodQuantity.setText("");
//    }
//}
//package com.example.resturent;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import java.util.HashMap;
//import java.util.Map;
//
//public class AddFoodActivity extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//
//    private EditText editTextFoodName, editTextFoodPrice, editTextFoodQuantity;
//    private ImageView imageViewFood;
//    private Uri imageUri; // To store the selected image URI
//
//    private FirebaseFirestore db;
//    private StorageReference storageRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_food);
//
//        db = FirebaseFirestore.getInstance();
//        storageRef = FirebaseStorage.getInstance().getReference("FoodImages");
//
//        editTextFoodName = findViewById(R.id.editTextFoodName);
//        editTextFoodPrice = findViewById(R.id.editTextFoodPrice);
//        editTextFoodQuantity = findViewById(R.id.editTextFoodQuantity);
//        imageViewFood = findViewById(R.id.imageViewFood);
//
//        Button selectImageButton = findViewById(R.id.selectImageButton);
//        Button submitButton = findViewById(R.id.submitButton);
//        Button addMoreButton = findViewById(R.id.addMoreButton);
//        Button adminHomeButton = findViewById(R.id.adminHomeButton);
//
//        selectImageButton.setOnClickListener(v -> openImagePicker());
//        submitButton.setOnClickListener(v -> submitFoodItem());
//        addMoreButton.setOnClickListener(v -> clearInputFields());
//
//        adminHomeButton.setOnClickListener(v -> {
//            Intent intent = new Intent(AddFoodActivity.this, Manage.class);
//            startActivity(intent);
//        });
//    }
//
//    private void openImagePicker() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//            imageViewFood.setImageURI(imageUri);
//        }
//    }
//
//    private void submitFoodItem() {
//        String foodName = editTextFoodName.getText().toString().trim();
//        String foodPrice = editTextFoodPrice.getText().toString().trim();
//        String foodQuantity = editTextFoodQuantity.getText().toString().trim();
//
//        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(foodPrice) || TextUtils.isEmpty(foodQuantity) || imageUri == null) {
//            Toast.makeText(this, "All fields and an image are required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Upload image to Firebase Storage
//        StorageReference fileReference = storageRef.child(System.currentTimeMillis() + ".jpg");
//        fileReference.putFile(imageUri)
//                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
//                    String imageUrl = uri.toString();
//                    addFoodItemToFirestore(foodName, foodPrice, foodQuantity, imageUrl);
//                }))
//                .addOnFailureListener(e -> Toast.makeText(AddFoodActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void addFoodItemToFirestore(String foodName, String foodPrice, String foodQuantity, String imageUrl) {
//        Map<String, Object> foodItem = new HashMap<>();
//        foodItem.put("foodName", foodName);
//        foodItem.put("foodPrice", foodPrice);
//        foodItem.put("foodQuantity", foodQuantity);
//        foodItem.put("imageUrl", imageUrl);
//
//        db.collection("FoodItems").add(foodItem)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(AddFoodActivity.this, "Food item added successfully", Toast.LENGTH_SHORT).show();
//                    clearInputFields();
//                })
//                .addOnFailureListener(e -> Toast.makeText(AddFoodActivity.this, "Failed to add food item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void clearInputFields() {
//        editTextFoodName.setText("");
//        editTextFoodPrice.setText("");
//        editTextFoodQuantity.setText("");
//        imageViewFood.setImageResource(0); // Clear image preview
//        imageUri = null; // Reset the image URI
//    }
//}

package com.example.resturent;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class AddFoodActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextFoodName, editTextFoodPrice, editTextFoodQuantity;
    private ImageView imageViewFood;
    private Uri imageUri; // To store the selected image URI

    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("FoodImages");

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextFoodPrice = findViewById(R.id.editTextFoodPrice);
        editTextFoodQuantity = findViewById(R.id.editTextFoodQuantity);
        imageViewFood = findViewById(R.id.imageViewFood);

        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button submitButton = findViewById(R.id.submitButton);
        Button addMoreButton = findViewById(R.id.addMoreButton);
        Button adminHomeButton = findViewById(R.id.adminHomeButton);
        Button updateFood= findViewById(R.id.updateFood);
        Button updateTable= findViewById(R.id.updateTable);
        Button tablecustomer= findViewById(R.id.tablecustomer);
        // Set up the animation for each button
        slideUpAnimation(selectImageButton);
        slideUpAnimation(submitButton);
        slideUpAnimation(addMoreButton);
        slideUpAnimation(adminHomeButton);
        slideUpAnimation(updateFood);
        slideUpAnimation(updateTable);
        slideUpAnimation(tablecustomer);
        selectImageButton.setOnClickListener(v -> openImagePicker());
        submitButton.setOnClickListener(v -> submitFoodItem());
        addMoreButton.setOnClickListener(v -> clearInputFields());

        adminHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddFoodActivity.this, Manage.class);
            startActivity(intent);
        });
        updateFood.setOnClickListener(v -> {
            Intent intent = new Intent(AddFoodActivity.this, UpdateFoodActivity.class);
            startActivity(intent);
        });
        updateTable.setOnClickListener(v -> {
            Intent intent = new Intent(AddFoodActivity.this, ManageTableActivity.class);
            startActivity(intent);
        });
        tablecustomer.setOnClickListener(v -> {
            Intent intent = new Intent(AddFoodActivity.this, CustomerTable.class);
            startActivity(intent);
        });
    }

    private void slideUpAnimation(View view) {
        view.setTranslationY(900);  // Start the button off-screen
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.setDuration(800);  // Adjust the duration as needed
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewFood.setImageURI(imageUri);
        }
    }

    private void submitFoodItem() {
        String foodName = editTextFoodName.getText().toString().trim();
        String foodPrice = editTextFoodPrice.getText().toString().trim();
        String foodQuantity = editTextFoodQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(foodPrice) || TextUtils.isEmpty(foodQuantity) || imageUri == null) {
            Toast.makeText(this, "All fields and an image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Firebase Storage
        StorageReference fileReference = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    addFoodItemToFirestore(foodName, foodPrice, foodQuantity, imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(AddFoodActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addFoodItemToFirestore(String foodName, String foodPrice, String foodQuantity, String imageUrl) {
        Map<String, Object> foodItem = new HashMap<>();
        foodItem.put("foodName", foodName);
        foodItem.put("foodPrice", foodPrice);
        foodItem.put("foodQuantity", foodQuantity);
        foodItem.put("imageUrl", imageUrl);

        db.collection("FoodItems").add(foodItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddFoodActivity.this, "Food item added successfully", Toast.LENGTH_SHORT).show();
                    clearInputFields();
                })
                .addOnFailureListener(e -> Toast.makeText(AddFoodActivity.this, "Failed to add food item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearInputFields() {
        editTextFoodName.setText("");
        editTextFoodPrice.setText("");
        editTextFoodQuantity.setText("");
        imageViewFood.setImageResource(0); // Clear image preview
        imageUri = null; // Reset the image URI
    }
}



