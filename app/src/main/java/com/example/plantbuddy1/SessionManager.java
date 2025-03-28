package com.example.plantbuddy1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Class untuk mengelola session pengguna
 */
public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private Editor editor;

    // Context
    private Context context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "PlantBuddyPref";

    // Shared preferences keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String username, int userId) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing username in pref
        editor.putString(KEY_USERNAME, username);

        // Storing user ID in pref
        editor.putInt(KEY_USER_ID, userId);

        // commit changes
        editor.commit();

        Log.d("SessionManager", "Session created for user: " + username + " with ID: " + userId);
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Get stored session data - username
     */
    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    /**
     * Get stored session data - user ID
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        Log.d("SessionManager", "User logged out");
    }
}

