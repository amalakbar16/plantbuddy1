package com.example.plantbuddy1;

import java.util.ArrayList;

/**
 * Singleton class to manage plant data across activities
 */
public class PlantDataManager {
    private static PlantDataManager instance;
    private ArrayList<Plant> plants;

    private PlantDataManager() {
        plants = new ArrayList<>();
    }

    public static synchronized PlantDataManager getInstance() {
        if (instance == null) {
            instance = new PlantDataManager();
        }
        return instance;
    }

    public ArrayList<Plant> getPlants() {
        return plants;
    }

    public void setPlants(ArrayList<Plant> plants) {
        this.plants = plants;
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public void updatePlant(int position, Plant plant) {
        if (position >= 0 && position < plants.size()) {
            plants.set(position, plant);
        }
    }

    public void deletePlant(int position) {
        if (position >= 0 && position < plants.size()) {
            plants.remove(position);
        }
    }
}

