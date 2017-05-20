/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin;
import com.ks.ecmanager.ecouriermanager.activities.firstLayer.ActivityMain;

import java.util.HashMap;

public class SessionUserData {

    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_PASSWORD = "user_password";
    public static final String KEY_USER_GROUP = "user_group";
    public static final String KEY_USER_AUTH_KEY = "user_key";
    public static final String KEY_DO_NAME = "do_name";
    public static final String KEY_DO_MOBILE = "do_mobile";


    // Sharedpref file name
    private static final String PREF_NAME = "userdata";
    // All Shared Preferences Keys
    private static final String IS_AVAILABLE = "IsAvailable";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionUserData(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Create Session
     */
    public void createUserInfo(String type,
                               String id,
                               String password,
                               String group,
                               String key,
                               String do_name,
                               String do_mobile) {
        // Storing job value as TRUE
        editor.putBoolean(IS_AVAILABLE, true);
        editor.putString(KEY_USER_TYPE, type);
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.putString(KEY_USER_GROUP, group);
        editor.putString(KEY_USER_AUTH_KEY, key);
        editor.putString(KEY_DO_NAME, do_name);
        editor.putString(KEY_DO_MOBILE, do_mobile);
        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getSessionDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, ""));
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, ""));
        user.put(KEY_USER_PASSWORD, pref.getString(KEY_USER_PASSWORD, ""));
        user.put(KEY_USER_GROUP, pref.getString(KEY_USER_GROUP, ""));
        user.put(KEY_USER_AUTH_KEY, pref.getString(KEY_USER_AUTH_KEY, ""));
        user.put(KEY_DO_NAME, pref.getString(KEY_DO_NAME, ""));
        user.put(KEY_DO_MOBILE, pref.getString(KEY_DO_MOBILE, ""));
        // return user
        return user;
    }

    /**
     * Quick check for logged in state
     * *
     */
    // Get logged in State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_AVAILABLE, false);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, ActivityLogin.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        } else {
            // user is logged in redirect him to Main Activity
            Intent i = new Intent(_context, ActivityMain.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Clear session details
     */
    public void endSession() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }
}
