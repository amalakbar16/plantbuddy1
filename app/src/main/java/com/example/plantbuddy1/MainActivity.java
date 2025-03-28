package com.example.plantbuddy1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Activity utama yang menampilkan daftar tanaman
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MaterialButton buttonHome, buttonSchedule, buttonExit;
    private ListView listViewPlants;
    private TextView textViewNoPlants;
    private List<Plant> plantList;
    private PlantAdapter adapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Menghilangkan ActionBar default untuk mengatasi bug header ganda
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate called");

        // Initialize UI components
        buttonHome = findViewById(R.id.buttonHome);
        buttonSchedule = findViewById(R.id.buttonSchedule);
        buttonExit = findViewById(R.id.buttonExit);
        listViewPlants = findViewById(R.id.listViewPlants);
        textViewNoPlants = findViewById(R.id.textViewNoPlants);

        // Initialize database helper and session manager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(getApplicationContext());

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Log.d(TAG, "User not logged in, redirecting to LoginActivity");
            // User is not logged in, redirect to login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Get user ID from session
        userId = sessionManager.getUserId();
        Log.d(TAG, "User ID from session: " + userId);

        // Set up navigation buttons
        setupButtons();

        // Load plants from database
        loadPlants();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        // Refresh plant list when returning to this activity
        loadPlants();
    }

    private void setupButtons() {
        // Home button is disabled since we're already on the home screen
        // buttonHome is already disabled in XML with enabled="false"

        // Navigate to Schedule Activity
        buttonSchedule.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ScheduleActivity");
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
            finish();
        });

        // Exit the application
        buttonExit.setOnClickListener(v -> {
            Log.d(TAG, "Logging out user");
            // Log out the user
            sessionManager.logoutUser();

            // Navigate to login screen
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadPlants() {
        // Get plants from database
        plantList = dbHelper.getAllPlants(userId);
        {
            // Get plants from database
            plantList = dbHelper.getAllPlants(userId);
            Log.d(TAG, "Loaded " + plantList.size() + " plants for user ID: " + userId);

            // Show "no plants" message if list is empty
            if (plantList.isEmpty()) {
                textViewNoPlants.setVisibility(View.VISIBLE);
                listViewPlants.setVisibility(View.GONE);
                Log.d(TAG, "No plants found, showing empty message");
            } else {
                textViewNoPlants.setVisibility(View.GONE);
                listViewPlants.setVisibility(View.VISIBLE);

                // Set up adapter for ListView
                adapter = new PlantAdapter(this, plantList, dbHelper);
                listViewPlants.setAdapter(adapter);

                // Set item click listener
                listViewPlants.setOnItemClickListener((parent, view, position, id) -> {
                    Plant selectedPlant = plantList.get(position);
                    Log.d(TAG, "Plant selected: " + selectedPlant.getName() + " with ID: " + selectedPlant.getId());
                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    intent.putExtra("PLANT_ID", selectedPlant.getId());
                    startActivity(intent);
                    finish();
                });
            }
        }
    }
}

