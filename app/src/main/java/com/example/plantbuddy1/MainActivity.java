package com.example.plantbuddy1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialButton buttonHome, buttonSchedule, buttonExit;
    private ListView listViewPlants;
    private TextView textViewNoPlants;
    private List<Plant> plantList;
    private PlantAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        buttonHome = findViewById(R.id.buttonHome);
        buttonSchedule = findViewById(R.id.buttonSchedule);
        buttonExit = findViewById(R.id.buttonExit);
        listViewPlants = findViewById(R.id.listViewPlants);
        textViewNoPlants = findViewById(R.id.textViewNoPlants);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set up navigation buttons
        setupButtons();

        // Load plants from database
        loadPlants();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh plant list when returning to this activity
        loadPlants();
    }

    private void setupButtons() {
        // Home button is disabled since we're already on the home screen
        buttonHome.setEnabled(false);

        // Navigate to Schedule Activity
        buttonSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        // Exit the application
        buttonExit.setOnClickListener(v -> {
            // Log out the user
            SessionManager sessionManager = new SessionManager(getApplicationContext());
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
        plantList = dbHelper.getAllPlants();

        // Show "no plants" message if list is empty
        if (plantList.isEmpty()) {
            textViewNoPlants.setVisibility(View.VISIBLE);
            listViewPlants.setVisibility(View.GONE);
        } else {
            textViewNoPlants.setVisibility(View.GONE);
            listViewPlants.setVisibility(View.VISIBLE);

            // Set up adapter for ListView
            adapter = new PlantAdapter(this, plantList);
            listViewPlants.setAdapter(adapter);

            // Set item click listener
            listViewPlants.setOnItemClickListener((parent, view, position, id) -> {
                Plant selectedPlant = plantList.get(position);
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                intent.putExtra("PLANT_ID", selectedPlant.getId());
                startActivity(intent);
            });
        }
    }
}

