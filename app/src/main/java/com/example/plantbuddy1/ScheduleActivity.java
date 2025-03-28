package com.example.plantbuddy1;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private EditText editTextPlantName;
    private Spinner spinnerPlantType, spinnerWateringDay;
    private MaterialButton buttonSelectTime, buttonSave, buttonClear, buttonBack;
    private ListView listViewPlants;

    private DatabaseHelper dbHelper;
    private List<Plant> plantList;
    private PlantAdapter adapter;

    private String selectedTime = "";
    private int editPlantId = -1; // -1 means we're adding a new plant

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Initialize UI components
        editTextPlantName = findViewById(R.id.editTextPlantName);
        spinnerPlantType = findViewById(R.id.spinnerPlantType);
        spinnerWateringDay = findViewById(R.id.spinnerWateringDay);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);
        buttonSave = findViewById(R.id.buttonSave);
        buttonClear = findViewById(R.id.buttonClear);
        buttonBack = findViewById(R.id.buttonBack);
        listViewPlants = findViewById(R.id.listViewPlants);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set up spinners
        setupSpinners();

        // Set up buttons
        setupButtons();

        // Check if we're editing an existing plant
        Intent intent = getIntent();
        if (intent.hasExtra("PLANT_ID")) {
            editPlantId = intent.getIntExtra("PLANT_ID", -1);
            loadPlantData(editPlantId);
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
    }

    private void setupButtons() {
        // Time picker button
        buttonSelectTime.setOnClickListener(v -> {
            // Get current time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create time picker dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, selectedMinute) -> {
                        // Format time as HH:MM
                        selectedTime = String.format("%02d:%02d", hourOfDay, selectedMinute);
                        buttonSelectTime.setText("Waktu Penyiraman: " + selectedTime);
                    },
                    hour,
                    minute,
                    true);
            timePickerDialog.show();
        });

        // Save button
        buttonSave.setOnClickListener(v -> {
            String plantName = editTextPlantName.getText().toString().trim();
            String plantType = spinnerPlantType.getSelectedItem().toString();
            String wateringDay = spinnerWateringDay.getSelectedItem().toString();

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
                    Toast.makeText(this, "Tanaman berhasil diperbarui", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Add new plant
                success = dbHelper.addPlant(plant);
                if (success) {
                    Toast.makeText(this, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                }
            }

            if (success) {
                // Clear form and reload plants
                clearForm();
                loadPlants();
            } else {
                Toast.makeText(this, "Gagal menyimpan tanaman", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear button
        buttonClear.setOnClickListener(v -> clearForm());

        // Back button
        buttonBack.setOnClickListener(v -> finish());
    }

    private void loadPlantData(int plantId) {
        Plant plant = dbHelper.getPlant(plantId);
        if (plant != null) {
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
            buttonSelectTime.setText("Waktu Penyiraman: " + selectedTime);

            // Update button text
            buttonSave.setText("Perbarui");
        }
    }

    private void clearForm() {
        editTextPlantName.setText("");
        spinnerPlantType.setSelection(0);
        spinnerWateringDay.setSelection(0);
        selectedTime = "";
        buttonSelectTime.setText("Pilih Waktu Penyiraman");
        editPlantId = -1;
        buttonSave.setText("Simpan");
    }

    private void loadPlants() {
        // Get plants from database
        plantList = dbHelper.getAllPlants();

        // Set up adapter for ListView
        adapter = new PlantAdapter(this, plantList);
        listViewPlants.setAdapter(adapter);

        // Set item click listener for edit
        listViewPlants.setOnItemClickListener((parent, view, position, id) -> {
            Plant selectedPlant = plantList.get(position);
            editPlantId = selectedPlant.getId();
            loadPlantData(editPlantId);
        });
    }
}

