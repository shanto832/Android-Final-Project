package com.example.resturent;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.content.Intent;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TableAndCustomerActivity extends AppCompatActivity {

    private EditText editTextUserName, editTextUserPhone;
    private LinearLayout tableButtonsLayout;
    private String userName, userPhoneNumber;
    private int selectedTableNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_and_customer);

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextUserPhone = findViewById(R.id.editTextUserPhone);
        tableButtonsLayout = findViewById(R.id.tableButtonsLayout);

        loadTables();
    }

    private void loadTables() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("TableInfo").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                for (int i = 0; i < documents.size(); i++) {
                    DocumentSnapshot document = documents.get(i);
                    String tableName = document.getString("tableName");
                    Long tableNumberLong = document.getLong("tableNumber");
                    if (tableName != null && tableNumberLong != null) {
                        int tableNumber = tableNumberLong.intValue();

                        Button tableButton = new Button(TableAndCustomerActivity.this);
                        tableButton.setText(tableName);
                        tableButton.setTag(tableNumber);
                        tableButton.setBackgroundColor(getResources().getColor(android.R.color.black));
                        tableButton.setTextColor(getResources().getColor(android.R.color.white));
                        tableButton.setTextSize(18);

                        tableButton.setOnClickListener(v -> {
                            selectedTableNumber = (int) v.getTag();
                            userName = editTextUserName.getText().toString();
                            userPhoneNumber = editTextUserPhone.getText().toString();

                            if (userName.isEmpty() || userPhoneNumber.isEmpty()) {
                                Toast.makeText(TableAndCustomerActivity.this, "Please enter your details", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Intent intent = new Intent(TableAndCustomerActivity.this, ShowFoodItemsActivity.class);
                            intent.putExtra("selectedTableNumber", selectedTableNumber);
                            intent.putExtra("userName", userName);
                            intent.putExtra("userPhoneNumber", userPhoneNumber);
                            startActivity(intent);
                        });

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(0, 0, 0, 50);
                        tableButton.setLayoutParams(layoutParams);
                        tableButton.setTranslationY(1000f);
                        tableButtonsLayout.addView(tableButton);

                        animateButton(tableButton, i);
                    }
                }

                // Apply stronger blur to background
                blurBackground();
            } else {
                Toast.makeText(this, "Failed to load tables", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animateButton(Button button, int delayMultiplier) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "translationY", 1000f, 0f);
        animator.setDuration(1000);
        animator.setStartDelay(delayMultiplier * 300);
        animator.start();
    }

    private void blurBackground() {
        // Load the sharp background image
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.your_sharp_background);

        // Create a RenderScript context
        RenderScript rs = RenderScript.create(this);

        // Create an Allocation from the original bitmap
        Allocation input = Allocation.createFromBitmap(rs, bitmap);

        // Create an empty Allocation for the output (blurred) bitmap
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Create a ScriptIntrinsicBlur
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // Set the radius for the blur effect (range 0 to 25)
        blurScript.setRadius(10f); // This value can be adjusted for stronger blur

        // Apply the blur effect
        blurScript.setInput(input);
        blurScript.forEach(output);

        // Copy the blurred image back to a Bitmap
        output.copyTo(bitmap);

        // Set the blurred image as the background
        Drawable blurredBackground = new BitmapDrawable(getResources(), bitmap);
        getWindow().setBackgroundDrawable(blurredBackground);

        // Clean up
        rs.destroy();
    }
}
