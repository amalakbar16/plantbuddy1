package com.example.plantbuddy1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegUsername, editTextRegEmail, editTextRegPassword, editTextRegConfirmPassword;
    private MaterialButton buttonRegister;
    private TextView textViewLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        editTextRegUsername = findViewById(R.id.editTextRegUsername);
        editTextRegEmail = findViewById(R.id.editTextRegEmail);
        editTextRegPassword = findViewById(R.id.editTextRegPassword);
        editTextRegConfirmPassword = findViewById(R.id.editTextRegConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set up register button
        buttonRegister.setOnClickListener(v -> {
            String username = editTextRegUsername.getText().toString().trim();
            String email = editTextRegEmail.getText().toString().trim();
            String password = editTextRegPassword.getText().toString().trim();
            String confirmPassword = editTextRegConfirmPassword.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(username)) {
                editTextRegUsername.setError("Username diperlukan");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                editTextRegEmail.setError("Email diperlukan");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextRegPassword.setError("Password diperlukan");
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                editTextRegConfirmPassword.setError("Konfirmasi password diperlukan");
                return;
            }

            if (!password.equals(confirmPassword)) {
                editTextRegConfirmPassword.setError("Password tidak cocok");
                return;
            }

            // Check if username already exists
            if (dbHelper.checkUserExists(username)) {
                editTextRegUsername.setError("Username sudah digunakan");
                return;
            }

            // Register user
            boolean success = dbHelper.addUser(username, email, password);
            if (success) {
                Toast.makeText(RegisterActivity.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();

                // Navigate to login screen
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to login screen
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}

