package com.example.plantbuddy1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PlantBuddy.db";

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PLANTS = "plants";

    // Common Column Names
    private static final String COLUMN_ID = "id";

    // Users Table Column Names
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Plants Table Column Names
    private static final String COLUMN_PLANT_NAME = "name";
    private static final String COLUMN_PLANT_TYPE = "type";
    private static final String COLUMN_WATERING_DAY = "watering_day";
    private static final String COLUMN_WATERING_TIME = "watering_time";
    private static final String COLUMN_USER_ID = "user_id";

    // Create Users Table
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PASSWORD + " TEXT"
            + ")";

    // Create Plants Table
    private static final String CREATE_TABLE_PLANTS = "CREATE TABLE " + TABLE_PLANTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PLANT_NAME + " TEXT,"
            + COLUMN_PLANT_TYPE + " TEXT,"
            + COLUMN_WATERING_DAY + " TEXT,"
            + COLUMN_WATERING_TIME + " TEXT,"
            + COLUMN_USER_ID + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PLANTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    // User CRUD Operations

    // Add new user
    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        // Insert row
        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    // Check if user exists
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    // Check user credentials
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    // Get user ID by username
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        }

        cursor.close();
        db.close();

        return userId;
    }

    // Plant CRUD Operations

    // Add new plant
    public boolean addPlant(Plant plant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PLANT_NAME, plant.getName());
        values.put(COLUMN_PLANT_TYPE, plant.getType());
        values.put(COLUMN_WATERING_DAY, plant.getWateringDay());
        values.put(COLUMN_WATERING_TIME, plant.getWateringTime());

        // Get current user ID from session
        SessionManager sessionManager = new SessionManager(getContext());
        String username = sessionManager.getUsername();
        int userId = getUserId(username);
        values.put(COLUMN_USER_ID, userId);

        // Insert row
        long result = db.insert(TABLE_PLANTS, null, values);
        db.close();

        return result != -1;
    }

    // Get all plants for current user
    public List<Plant> getAllPlants() {
        List<Plant> plantList = new ArrayList<>();

        // Get current user ID from session
        SessionManager sessionManager = new SessionManager(getContext());
        String username = sessionManager.getUsername();
        int userId = getUserId(username);

        // Select query
        String selectQuery = "SELECT * FROM " + TABLE_PLANTS +
                " WHERE " + COLUMN_USER_ID + " = " + userId +
                " ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Plant plant = new Plant();
                plant.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                plant.setName(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NAME)));
                plant.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_TYPE)));
                plant.setWateringDay(cursor.getString(cursor.getColumnIndex(COLUMN_WATERING_DAY)));
                plant.setWateringTime(cursor.getString(cursor.getColumnIndex(COLUMN_WATERING_TIME)));

                // Add to list
                plantList.add(plant);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return plantList;
    }

    // Get single plant
    public Plant getPlant(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PLANTS,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        Plant plant = null;
        if (cursor.moveToFirst()) {
            plant = new Plant();
            plant.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            plant.setName(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NAME)));
            plant.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_TYPE)));
            plant.setWateringDay(cursor.getString(cursor.getColumnIndex(COLUMN_WATERING_DAY)));
            plant.setWateringTime(cursor.getString(cursor.getColumnIndex(COLUMN_WATERING_TIME)));
        }

        cursor.close();
        db.close();

        return plant;
    }

    // Update plant
    public boolean updatePlant(Plant plant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PLANT_NAME, plant.getName());
        values.put(COLUMN_PLANT_TYPE, plant.getType());
        values.put(COLUMN_WATERING_DAY, plant.getWateringDay());
        values.put(COLUMN_WATERING_TIME, plant.getWateringTime());

        // Update row
        int result = db.update(
                TABLE_PLANTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(plant.getId())});

        db.close();

        return result > 0;
    }

    // Delete plant
    public boolean deletePlant(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete row
        int result = db.delete(
                TABLE_PLANTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();

        return result > 0;
    }

    // Helper method to get context
    private Context getContext() {
        return this.getReadableDatabase().getContext();
    }
}

