
package com.example.resturent;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerTable extends AppCompatActivity {

    private int selectedTableNumber = -1; // Variable to store selected table number
    private String userName = "null"; // Default value for name
    private String userPhoneNumber = "null"; // Default value for phone number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_table);

        TextView tvChooseTable = findViewById(R.id.tvChooseTable);
        tvChooseTable.setText("Choose a table:");

        // Initialize and set up buttons
        setupTableButton(R.id.btnTable1, 1);
        setupTableButton(R.id.btnTable2, 2);
        setupTableButton(R.id.btnTable3, 3);
        setupTableButton(R.id.btnTable4, 4);

        // Initialize EditTexts for Name and Phone Number
        EditText etName = findViewById(R.id.etName);
        EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);

        // Handle TextInput and set default values if empty
        etName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                userName = etName.getText().toString().isEmpty() ? "null" : etName.getText().toString();
            }
        });

        // **Fix here:** Capture the phone number input when the user moves out of the field.
        etPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                userPhoneNumber = etPhoneNumber.getText().toString().isEmpty() ? "null" : etPhoneNumber.getText().toString();
            }
        });

        // Background image fade and blur setup
        ImageView bgSharp = findViewById(R.id.bgSharp);
        ImageView bgBlurred = findViewById(R.id.bgBlurred);

        // Create background fade animations
        ObjectAnimator fadeOutSharp = ObjectAnimator.ofFloat(bgSharp, "alpha", 1f, 0f);
        fadeOutSharp.setDuration(2000); // Make the fade out last longer

        ObjectAnimator fadeInBlurred = ObjectAnimator.ofFloat(bgBlurred, "alpha", 0f, 1f);
        fadeInBlurred.setDuration(2000);

        // Animate buttons and background together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                fadeOutSharp, fadeInBlurred,
                slideUpAnimation(etName),
                slideUpAnimation(etPhoneNumber),
                slideUpAnimation(findViewById(R.id.btnTable1)),
                slideUpAnimation(findViewById(R.id.btnTable2)),
                slideUpAnimation(findViewById(R.id.btnTable3)),
                slideUpAnimation(findViewById(R.id.btnTable4))
        );
        animatorSet.start();
    }

    private void setupTableButton(int buttonId, int tableNumber) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            selectedTableNumber = tableNumber; // Save the selected table number
            Toast.makeText(this, "You selected Table " + selectedTableNumber, Toast.LENGTH_SHORT).show();

            // **Fix here:** Capture phone number value when the button is clicked, before passing to next activity
            EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
            userPhoneNumber = etPhoneNumber.getText().toString().trim(); // Capture the phone number input

            if (userPhoneNumber.isEmpty()) {
                userPhoneNumber = "null"; // Ensure it's not empty, set to "null" or a default value
            }

            // Open ShowFoodItemsActivity and pass the selected table number, name, and phone number
            Intent intent = new Intent(CustomerTable.this, ShowFoodItemsActivity.class);
            intent.putExtra("selectedTableNumber", selectedTableNumber); // Pass the selected table number to the next activity
            intent.putExtra("userName", userName); // Pass userName to the next activity
            intent.putExtra("userPhoneNumber", userPhoneNumber); // Pass userPhoneNumber to the next activity
            startActivity(intent);
        });
    }

    private ObjectAnimator slideUpAnimation(View view) {
        // Move the view off-screen and center it horizontally
        view.setTranslationY(1500); // Start from the bottom of the screen
        view.setTranslationX(0); // Center the view horizontally

        // Animate the view to slide up
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.setDuration(1500);  // Adjust the duration as needed
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        return animator;
    }
}



