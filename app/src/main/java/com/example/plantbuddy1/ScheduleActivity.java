package com.example.plantbuddy1;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.List;

/**
 * Activity untuk menambah atau mengedit tanaman
 */
public class ScheduleActivity extends AppCompatActivity {

    private static final String TAG = "ScheduleActivity";

    private EditText editTextPlantName;
    private Spinner spinnerPlantType, spinnerWateringDay;
    private LinearLayout buttonSelectTime;
    private TextView textViewSelectedTime;
    private MaterialButton buttonSave, buttonClear;
    private MaterialButton buttonHome, buttonSchedule, buttonExit;
    private ListView listViewPlants;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<Plant> plantList;
    private PlantAdapter adapter;

    private String selectedTime = "";
    private int editPlantId = -1; // -1 means we're adding a new plant
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Menghilangkan ActionBar default untuk mengatasi bug header ganda
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_schedule);

        Log.d(TAG, "onCreate called");

        // Initialize UI components
        editTextPlantName = findViewById(R.id.editTextPlantName);
        spinnerPlantType = findViewById(R.id.spinnerPlantType);
        spinnerWateringDay = findViewById(R.id.spinnerWateringDay);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);
        textViewSelectedTime = findViewById(R.id.textViewSelectedTime);
        buttonSave = findViewById(R.id.buttonSave);
        buttonClear = findViewById(R.id.buttonClear);
        buttonHome = findViewById(R.id.buttonHome);
        buttonSchedule = findViewById(R.id.buttonSchedule);
        buttonExit = findViewById(R.id.buttonExit);
        listViewPlants = findViewById(R.id.listViewPlants);

        // Initialize database helper and session manager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(getApplicationContext());

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Log.d(TAG, "User not logged in, redirecting to LoginActivity");
            // User is not logged in, redirect to login activity
            Intent intent = new Intent(ScheduleActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Get user ID from session
        userId = sessionManager.getUserId();
        Log.d(TAG, "User ID from session: " + userId);

        // Set up spinners
        setupSpinners();

        // Set up buttons
        setupButtons();

        // Check if we're editing an existing plant
        Intent intent = getIntent();
        if (intent.hasExtra("PLANT_ID")) {
            editPlantId = intent.getIntExtra("PLANT_ID", -1);
            loadPlantData(editPlantId);
            Log.d(TAG, "Editing existing plant with ID: " + editPlantId);
        }

        // Load plants from database
        loadPlants();
    }

    private void setupSpinners() {
        // Set up plant type spinner
        ArrayAdapter<CharSequence> plantTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.plant_types, android.R.layout.simple_spinner_item);
        plantTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantType.setAdapter(plantTypeAdapter);

        // Set up watering day spinner
        ArrayAdapter<CharSequence> wateringDayAdapter = ArrayAdapter.createFromResource(
                this, R.array.watering_days, android.R.layout.simple_spinner_item);
        wateringDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWateringDay.setAdapter(wateringDayAdapter);

        Log.d(TAG, "Spinners set up successfully");
    }

    private void setupButtons() {
        // Time picker button - Using standard TimePickerDialog with improved styling
        buttonSelectTime.setOnClickListener(v -> {
            // Get current time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Parse existing time if available
            if (!TextUtils.isEmpty(selectedTime)) {
                String[] timeParts = selectedTime.split(":");
                if (timeParts.length == 2) {
                    try {
                        hour = Integer.parseInt(timeParts[0]);
                        minute = Integer.parseInt(timeParts[1]);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing time: " + selectedTime, e);
                    }
                }
            }

            // Create time picker dialog with custom theme
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    R.style.TimePickerTheme,
                    (view, hourOfDay, selectedMinute) -> {
                        // Format time as HH:MM
                        selectedTime = String.format("%02d:%02d", hourOfDay, selectedMinute);
                        textViewSelectedTime.setText(selectedTime);
                        textViewSelectedTime.setTextColor(getResources().getColor(android.R.color.black));
                        Log.d(TAG, "Time selected: " + selectedTime);
                    },
                    hour,
                    minute,
                    true); // true for 24-hour format

            timePickerDialog.setTitle("Pilih Waktu Penyiraman");
            timePickerDialog.show();
        });

        // Save button
        buttonSave.setOnClickListener(v -> {
            String plantName = editTextPlantName.getText().toString().trim();
            String plantType = spinnerPlantType.getSelectedItem().toString();
            String wateringDay = spinnerWateringDay.getSelectedItem().toString();

            Log.d(TAG, "Attempting to save plant: " + plantName);

            // Validate input
            if (TextUtils.isEmpty(plantName)) {
                editTextPlantName.setError("Nama tanaman diperlukan");
                return;
            }

            if (TextUtils.isEmpty(selectedTime)) {
                Toast.makeText(this, "Pilih waktu penyiraman", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create plant object
            Plant plant = new Plant();
            plant.setName(plantName);
            plant.setType(plantType);
            plant.setWateringDay(wateringDay);
            plant.setWateringTime(selectedTime);

            boolean success;
            if (editPlantId != -1) {
                // Update existing plant
                plant.setId(editPlantId);
                success = dbHelper.updatePlant(plant);
                if (success) {
                    Log.d(TAG, "Plant updated successfully: " + plantName);
                    Toast.makeText(this, "Tanaman berhasil diperbarui", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Add new plant
                success = dbHelper.addPlant(plant, userId);
                if (success) {
                    Log.d(TAG, "Plant added successfully: " + plantName);
                    Toast.makeText(this, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                }
            }

            if (success) {
                // Clear form and reload plants
                clearForm();
                loadPlants();
            } else {
                Log.d(TAG, "Failed to save plant: " + plantName);
                Toast.makeText(this, "Gagal menyimpan tanaman", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear button
        buttonClear.setOnClickListener(v -> {
            Log.d(TAG, "Clearing form");
            clearForm();
        });

        // Navigation buttons
        buttonHome.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to MainActivity");
            Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Schedule button is disabled since we're already on the schedule screen

        // Exit button
        buttonExit.setOnClickListener(v -> {
            Log.d(TAG, "Logging out user");
            // Log out the user
            sessionManager.logoutUser();

            // Navigate to login screen
            Intent intent = new Intent(ScheduleActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadPlantData(int plantId) {
        Plant plant = dbHelper.getPlant(plantId);
        if (plant != null) {
            Log.d(TAG, "Loading plant data: " + plant.getName());
            // Fill form with plant data
            editTextPlantName.setText(plant.getName());

            // Set plant type spinner
            ArrayAdapter plantTypeAdapter = (ArrayAdapter) spinnerPlantType.getAdapter();
            int plantTypePosition = plantTypeAdapter.getPosition(plant.getType());
            spinnerPlantType.setSelection(plantTypePosition);

            // Set watering day spinner
            ArrayAdapter wateringDayAdapter = (ArrayAdapter) spinnerWateringDay.getAdapter();
            int wateringDayPosition = wateringDayAdapter.getPosition(plant.getWateringDay());
            spinnerWateringDay.setSelection(wateringDayPosition);

            // Set selected time
            selectedTime = plant.getWateringTime();
            textViewSelectedTime.setText(selectedTime);
            textViewSelectedTime.setTextColor(getResources().getColor(android.R.color.black));

            // Update button text
            buttonSave.setText("PERBARUI");
        } else {
            Log.d(TAG, "Plant not found with ID: " + plantId);
        }
    }

    private void clearForm() {
        editTextPlantName.setText("");
        spinnerPlantType.setSelection(0);
        spinnerWateringDay.setSelection(0);
        selectedTime = "";
        textViewSelectedTime.setText("Pilih Waktu");
        textViewSelectedTime.setTextColor(getResources().getColor(android.R.color.darker_gray));
        editPlantId = -1;
        buttonSave.setText("SIMPAN");
    }

    private void loadPlants() {
        // Get plants from database
        plantList = dbHelper.getAllPlants(userId);
        Log.d(TAG, "Loaded " + plantList.size() + " plants for user ID: " + userId);

        // Set up adapter for ListView
        adapter = new PlantAdapter(this, plantList, dbHelper);
        listViewPlants.setAdapter(adapter);

        // Set item click listener for edit
        listViewPlants.setOnItemClickListener((parent, view, position, id) -> {
            Plant selectedPlant = plantList.get(position);
            Log.d(TAG, "Plant selected for editing: " + selectedPlant.getName());
            editPlantId = selectedPlant.getId();
            loadPlantData(editPlantId);
        });
    }
}

