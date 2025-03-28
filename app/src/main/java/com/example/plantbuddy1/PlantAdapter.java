package com.example.plantbuddy1;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Adapter untuk menampilkan daftar tanaman
 */
public class PlantAdapter extends ArrayAdapter<Plant> {

    private static final String TAG = "PlantAdapter";

    private Context context;
    private List<Plant> plantList;
    private DatabaseHelper dbHelper;

    public PlantAdapter(Context context, List<Plant> plantList, DatabaseHelper dbHelper) {
        super(context, R.layout.plant_item, plantList);
        this.context = context;
        this.plantList = plantList;
        this.dbHelper = dbHelper;
        Log.d(TAG, "PlantAdapter initialized with " + plantList.size() + " plants");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.plant_item, null);
            Log.d(TAG, "Creating new view for position: " + position);
        }

        Plant plant = plantList.get(position);

        if (plant != null) {
            ImageView imageViewPlantType = view.findViewById(R.id.imageViewPlantType);
            TextView textViewPlantName = view.findViewById(R.id.textViewPlantName);
            TextView textViewPlantType = view.findViewById(R.id.textViewPlantType);
            TextView textViewWateringSchedule = view.findViewById(R.id.textViewWateringSchedule);
            MaterialButton buttonEdit = view.findViewById(R.id.buttonEdit);
            MaterialButton buttonDelete = view.findViewById(R.id.buttonDelete);

            // Set plant type image based on plant type
            setPlantTypeImage(imageViewPlantType, plant.getType());

            textViewPlantName.setText(plant.getName());
            textViewPlantType.setText(plant.getType());
            textViewWateringSchedule.setText(plant.getWateringDay() + " - " + plant.getWateringTime());

            // Edit button
            buttonEdit.setOnClickListener(v -> {
                Log.d(TAG, "Edit button clicked for plant: " + plant.getName());
                if (context instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).getIntent().putExtra("PLANT_ID", plant.getId());
                    ((ScheduleActivity) context).recreate();
                } else if (context instanceof MainActivity) {
                    // Navigate to ScheduleActivity with plant ID
                    Intent intent = new Intent(context, ScheduleActivity.class);
                    intent.putExtra("PLANT_ID", plant.getId());
                    context.startActivity(intent);
                }
            });

            // Delete button
            buttonDelete.setOnClickListener(v -> {
                Log.d(TAG, "Delete button clicked for plant: " + plant.getName());
                // Show confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus Tanaman");
                builder.setMessage("Apakah Anda yakin ingin menghapus " + plant.getName() + "?");
                builder.setPositiveButton("Ya", (dialog, which) -> {
                    // Delete plant from database
                    boolean success = dbHelper.deletePlant(plant.getId());
                    if (success) {
                        Log.d(TAG, "Plant deleted successfully: " + plant.getName());
                        // Remove from list and update adapter
                        plantList.remove(position);
                        notifyDataSetChanged();

                        // Refresh activity if needed
                        if (context instanceof ScheduleActivity) {
                            ((ScheduleActivity) context).recreate();
                        } else if (context instanceof MainActivity) {
                            ((MainActivity) context).recreate();
                        }
                    } else {
                        Log.d(TAG, "Failed to delete plant: " + plant.getName());
                    }
                });
                builder.setNegativeButton("Tidak", null);
                builder.show();
            });
        }

        return view;
    }

    /**
     * Set the appropriate plant type image based on the plant type
     */
    private void setPlantTypeImage(ImageView imageView, String plantType) {
        if (plantType == null) {
            imageView.setImageResource(R.drawable.ic_plant_hias);
            return;
        }

        // Set image based on plant type
        switch (plantType) {
            case "Tanaman Hias":
                imageView.setImageResource(R.drawable.ic_plant_hias);
                break;
            case "Tanaman Konsumsi":
                imageView.setImageResource(R.drawable.ic_plant_konsumsi);
                break;
            case "Tanaman Peneduh":
                imageView.setImageResource(R.drawable.ic_plant_peneduh);
                break;
            case "Tanaman Obat":
                imageView.setImageResource(R.drawable.ic_plant_obat);
                break;
            default:
                // Default to tanaman hias icon
                imageView.setImageResource(R.drawable.ic_plant_hias);
                break;
        }
    }
}

