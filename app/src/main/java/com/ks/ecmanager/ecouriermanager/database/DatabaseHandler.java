/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ks.ecmanager.ecouriermanager.pojo.AgentDOListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ProfileListDatum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kazi Srabon on 6/21/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "eCourierManager";

    // Contacts table name
    private static final String TABLE_AGENTS = "agents";
    private static final String TABLE_PROFILE = "profile";

    // Contacts Table Agents Columns names
    private static final String AGENT_ID = "id";
    private static final String AGENT_API_ID = "agent_id";
    private static final String AGENT_NAME = "agent_name";

    // Contacts Table Profile Columns names
    private static final String PROFILE_ID = "id";
    private static final String PROFILE_API_ID = "agent_id";
    private static final String BLOOD_GROUP = "blood_group";
    private static final String NID = "nid";
    private static final String M_NAME = "m_name";
    private static final String DOB = "dob";
    private static final String PROFILE_PIC = "profile_pic";
    private static final String JOIN_DATE = "join_date";
    private static final String DO_LOCATION = "do_location";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        + BLOOD_GROUP + " TEXT NOT NULL,"
        String CREATE_AGENTS_TABLE = "CREATE TABLE " + TABLE_AGENTS + "("
                + AGENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AGENT_API_ID + " TEXT NOT NULL,"
                + AGENT_NAME + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_AGENTS_TABLE);

        String CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_PROFILE + "("
                + PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PROFILE_API_ID + " TEXT NOT NULL,"
                + BLOOD_GROUP + " TEXT,"
                + NID + " TEXT,"
                + M_NAME + " TEXT NOT NULL,"
                + DOB + " TEXT,"
                + PROFILE_PIC + " BLOB,"
                + JOIN_DATE + " TEXT,"
                + DO_LOCATION + " TEXT"
                + ")";
        db.execSQL(CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);

        // Create tables again
        onCreate(db);
    }

//    TABLE_AGENTS CRUD operations

    // Adding new agent
    public void addAgent(AgentDOListDatum agentDatum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AGENT_NAME, agentDatum.getValue()); // Contact Name
        values.put(AGENT_API_ID, agentDatum.getId()); // Contact Phone Number

        // Inserting Row
        db.insert(TABLE_AGENTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single agent
    public AgentDOListDatum getAgent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_AGENTS, new String[] { AGENT_ID,
                        AGENT_API_ID, AGENT_NAME }, AGENT_API_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        AgentDOListDatum agentDOListDatum = new AgentDOListDatum(cursor.getString(1),
                cursor.getString(2));
        // return contact
        return agentDOListDatum;
    }

    // Getting All Agents
    public List<AgentDOListDatum> getAllAgents() {
        List<AgentDOListDatum> agentList = new ArrayList<AgentDOListDatum>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_AGENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AgentDOListDatum datum = new AgentDOListDatum();
                datum.setId(cursor.getString(1));
                datum.setValue(cursor.getString(2));
                // Adding contact to list
                agentList.add(datum);
            } while (cursor.moveToNext());
        }

        // return contact list
        return agentList;
    }

    // Getting agents Count
    public int getAgentsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_AGENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single agent
    public int updateAgent(AgentDOListDatum agentDOListDatum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AGENT_API_ID, agentDOListDatum.getId());
        values.put(AGENT_NAME, agentDOListDatum.getValue());

        // updating row
        return db.update(TABLE_AGENTS, values, AGENT_API_ID + " = ?",
                new String[] { String.valueOf(agentDOListDatum.getId()) });
    }

    // Deleting single agent
    public void deleteAgent(AgentDOListDatum agentDOListDatum) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AGENTS, AGENT_API_ID + " = ?",
                new String[] { String.valueOf(agentDOListDatum.getId()) });
        db.close();
    }

    // Deleting all agents
    public void  deleteAgents(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_AGENTS);
    }

//    TABLE_PROFILE CRUD operations

    // Adding new profile
    public void addProfile(ProfileListDatum profileListDatum, String agent_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILE_API_ID, agent_id); // agent id
        values.put(BLOOD_GROUP, profileListDatum.getBlood_group()); // Profile blood_group
        values.put(NID, profileListDatum.getNid()); // Profile nid
        values.put(M_NAME, profileListDatum.getName()); // Profile m_name
        values.put(DOB, profileListDatum.getDob()); // Profile dob
        values.put(PROFILE_PIC, profileListDatum.getProfilePic()); // Profile profile_pic
        values.put(JOIN_DATE, profileListDatum.getJoinDate()); // Profile join_date
        values.put(DO_LOCATION, profileListDatum.getDeliveryZone()); // Profile do_location

        // Inserting Row
        db.insert(TABLE_PROFILE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single profile
    public ProfileListDatum getProfile(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROFILE,
                new String[] { BLOOD_GROUP, NID, M_NAME, DOB, PROFILE_PIC, JOIN_DATE, DO_LOCATION },
                PROFILE_API_ID + "=?",
                new String[] { id },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ProfileListDatum profileListDatum = new ProfileListDatum(cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8));
        // return contact
        return profileListDatum;
    }

    // Getting All Profile Data
    public List<ProfileListDatum> getAllProfile() {
        List<ProfileListDatum> profileListData = new ArrayList<ProfileListDatum>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileListDatum datum = new ProfileListDatum();
                //BLOOD_GROUP, NID, M_NAME, DOB, PROFILE_PIC, JOIN_DATE, DO_LOCATION
                datum.setBlood_group(cursor.getString(2));
                datum.setNid(cursor.getString(3));
                datum.setName(cursor.getString(4));
                datum.setDob(cursor.getString(5));
                datum.setProfilePic(cursor.getString(6));
                datum.setJoinDate(cursor.getString(7));
                datum.setDeliveryZone(cursor.getString(8));
                // Adding contact to list
                profileListData.add(datum);
            } while (cursor.moveToNext());
        }

        // return contact list
        return profileListData;
    }

    // Getting Profile Count
    public int getProfileCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PROFILE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single profile data
    public int updateProfile(ProfileListDatum profileListDatum, String agent_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //BLOOD_GROUP, NID, M_NAME, DOB, PROFILE_PIC, JOIN_DATE, DO_LOCATION
        values.put(PROFILE_API_ID, agent_id);
        values.put(BLOOD_GROUP, profileListDatum.getBlood_group());
        values.put(NID, profileListDatum.getNid());
        values.put(M_NAME, profileListDatum.getName());
        values.put(DOB, profileListDatum.getDob());
        values.put(PROFILE_PIC, profileListDatum.getProfilePic());
        values.put(JOIN_DATE, profileListDatum.getJoinDate());
        values.put(DO_LOCATION, profileListDatum.getDeliveryZone());

        // updating row
        return db.update(TABLE_PROFILE, values, PROFILE_API_ID + " = ?",
                new String[] { agent_id });
    }

    // Deleting single profile data
    public void deleteProfile(ProfileListDatum profileListDatum, String agent_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE, PROFILE_API_ID + " = ?",
                new String[] { agent_id });
        db.close();
    }

    // Deleting all profile data
    public void  deleteProfiles(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_PROFILE);
    }
}
