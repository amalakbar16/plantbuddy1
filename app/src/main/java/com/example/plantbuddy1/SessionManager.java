package com.example.plantbuddy1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String username) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing username in pref
        editor.putString(KEY_USERNAME, username);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Get stored session data
     */
    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }
}

