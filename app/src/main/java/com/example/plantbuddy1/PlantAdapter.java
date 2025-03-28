package com.example.plantbuddy1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PlantAdapter extends ArrayAdapter<Plant> {

    private Context context;
    private List<Plant> plantList;
    private DatabaseHelper dbHelper;

    public PlantAdapter(Context context, List<Plant> plantList) {
        super(context, R.layout.plant_item, plantList);
        this.context = context;
        this.plantList = plantList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.plant_item, null);
        }

        Plant plant = plantList.get(position);

        if (plant != null) {
            TextView textViewPlantName = view.findViewById(R.id.textViewPlantName);
            TextView textViewPlantType = view.findViewById(R.id.textViewPlantType);
            TextView textViewWateringSchedule = view.findViewById(R.id.textViewWateringSchedule);
            MaterialButton buttonEdit = view.findViewById(R.id.buttonEdit);
            MaterialButton buttonDelete = view.findViewById(R.id.buttonDelete);

            textViewPlantName.setText(plant.getName());
            textViewPlantType.setText(plant.getType());
            textViewWateringSchedule.setText(plant.getWateringDay() + " - " + plant.getWateringTime());

            // Edit button
            buttonEdit.setOnClickListener(v -> {
                if (context instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).getIntent().putExtra("PLANT_ID", plant.getId());
                    ((ScheduleActivity) context).recreate();
                } else if (context instanceof MainActivity) {
                    // Navigate to ScheduleActivity with plant ID
                    android.content.Intent intent = new android.content.Intent(context, ScheduleActivity.class);
                    intent.putExtra("PLANT_ID", plant.getId());
                    context.startActivity(intent);
                }
            });

            // Delete button
            buttonDelete.setOnClickListener(v -> {
                // Show confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus Tanaman");
                builder.setMessage("Apakah Anda yakin ingin menghapus " + plant.getName() + "?");
                builder.setPositiveButton("Ya", (dialog, which) -> {
                    // Delete plant from database
                    dbHelper.deletePlant(plant.getId());

                    // Remove from list and update adapter
                    plantList.remove(position);
                    notifyDataSetChanged();

                    // Refresh activity if needed
                    if (context instanceof ScheduleActivity) {
                        ((ScheduleActivity) context).recreate();
                    } else if (context instanceof MainActivity) {
                        ((MainActivity) context).recreate();
                    }
                });
                builder.setNegativeButton("Tidak", null);
                builder.show();
            });
        }

        return view;
    }
}

