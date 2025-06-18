package com.example.issah; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.issah.LoginActivity;
import com.example.issah.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private MaterialButton sendResetButton;
    private TextView backToLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailLayout = findViewById(R.id.forgotEmailLayout);
        emailInput = findViewById(R.id.forgotEmailInput);
        sendResetButton = findViewById(R.id.sendResetButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        sendResetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (!isValidEmail(email)) {
                emailLayout.setError("Enter a valid email");
            } else {
                emailLayout.setError(null);
                // TODO: Send reset link using Firebase/Auth backend
                Toast.makeText(this, "Reset link sent to " + email, Toast.LENGTH_SHORT).show();
            }
        });

        backToLoginText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
