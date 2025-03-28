package com.example.plantbuddy1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private MaterialButton buttonLogin;
    private TextView textViewRegister;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Initialize database helper and session manager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(getApplicationContext());

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            // User is already logged in, redirect to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Set up login button
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(username)) {
                editTextUsername.setError("Username diperlukan");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Password diperlukan");
                return;
            }

            // Attempt login
            boolean success = dbHelper.checkUser(username, password);
            if (success) {
                // Create session
                sessionManager.createLoginSession(username);

                // Navigate to main activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login gagal. Periksa username dan password.", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to register screen
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}

