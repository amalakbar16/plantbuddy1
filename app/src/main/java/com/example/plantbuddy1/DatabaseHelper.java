package com.example.plantbuddy1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class untuk operasi database
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

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

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d(TAG, "DatabaseHelper constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");
        // Create tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PLANTS);
        Log.d(TAG, "Database tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    // User CRUD Operations

    /**
     * Add new user
     */
    public boolean addUser(String username, String email, String password) {
        Log.d(TAG, "Adding new user: " + username);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        // Insert row
        long result = db.insert(TABLE_USERS, null, values);
        Log.d(TAG, "User added with result: " + result);
        db.close();

        return result != -1;
    }

    /**
     * Check if user exists
     */
    public boolean checkUserExists(String username) {
        Log.d(TAG, "Checking if user exists: " + username);
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
        Log.d(TAG, "User exists check result: " + count);
        cursor.close();
        db.close();

        return count > 0;
    }

    /**
     * Check user credentials
     */
    public boolean checkUser(String username, String password) {
        Log.d(TAG, "Checking user credentials for: " + username);
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
        Log.d(TAG, "User credentials check result: " + count);
        cursor.close();
        db.close();

        return count > 0;
    }

    /**
     * Get user ID by username
     */
    public int getUserId(String username) {
        Log.d(TAG, "Getting user ID for: " + username);
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
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        }

        Log.d(TAG, "User ID result: " + userId);
        cursor.close();
        db.close();

        return userId;
    }

    // Plant CRUD Operations

    /**
     * Add new plant
     */
    public boolean addPlant(Plant plant, int userId) {
        Log.d(TAG, "Adding new plant: " + plant.getName() + " for user ID: " + userId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PLANT_NAME, plant.getName());
        values.put(COLUMN_PLANT_TYPE, plant.getType());
        values.put(COLUMN_WATERING_DAY, plant.getWateringDay());
        values.put(COLUMN_WATERING_TIME, plant.getWateringTime());
        values.put(COLUMN_USER_ID, userId);

        // Insert row
        long result = db.insert(TABLE_PLANTS, null, values);
        Log.d(TAG, "Plant added with result: " + result);
        db.close();

        return result != -1;
    }

    /**
     * Get all plants for a specific user
     */
    public List<Plant> getAllPlants(int userId) {
        Log.d(TAG, "Getting all plants for user ID: " + userId);
        List<Plant> plantList = new ArrayList<>();

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
                plant.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                plant.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLANT_NAME)));
                plant.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLANT_TYPE)));
                plant.setWateringDay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WATERING_DAY)));
                plant.setWateringTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WATERING_TIME)));

                // Add to list
                plantList.add(plant);
            } while (cursor.moveToNext());
        }

        Log.d(TAG, "Found " + plantList.size() + " plants");
        cursor.close();
        db.close();

        return plantList;
    }

    /**
     * Get single plant
     */
    public Plant getPlant(int id) {
        Log.d(TAG, "Getting plant with ID: " + id);
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
            plant.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            plant.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLANT_NAME)));
            plant.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLANT_TYPE)));
            plant.setWateringDay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WATERING_DAY)));
            plant.setWateringTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WATERING_TIME)));
            Log.d(TAG, "Plant found: " + plant.getName());
        } else {
            Log.d(TAG, "Plant not found");
        }

        cursor.close();
        db.close();

        return plant;
    }

    /**
     * Update plant
     */
    public boolean updatePlant(Plant plant) {
        Log.d(TAG, "Updating plant with ID: " + plant.getId());
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

        Log.d(TAG, "Plant updated with result: " + result);
        db.close();

        return result > 0;
    }

    /**
     * Delete plant
     */
    public boolean deletePlant(int id) {
        Log.d(TAG, "Deleting plant with ID: " + id);
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete row
        int result = db.delete(
                TABLE_PLANTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});

        Log.d(TAG, "Plant deleted with result: " + result);
        db.close();

        return result > 0;
    }
}

