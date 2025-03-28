package com.example.plantbuddy1;

/**
 * Model class untuk tanaman
 */
public class Plant {
    private int id;
    private String name;
    private String type;
    private String wateringDay;
    private String wateringTime;

    // Default constructor
    public Plant() {
    }

    // Constructor with all fields
    public Plant(int id, String name, String type, String wateringDay, String wateringTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.wateringDay = wateringDay;
        this.wateringTime = wateringTime;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWateringDay() {
        return wateringDay;
    }

    public void setWateringDay(String wateringDay) {
        this.wateringDay = wateringDay;
    }

    public String getWateringTime() {
        return wateringTime;
    }

    public void setWateringTime(String wateringTime) {
        this.wateringTime = wateringTime;
    }

    @Override
    public String toString() {
        return name + " - " + type;
    }
}

