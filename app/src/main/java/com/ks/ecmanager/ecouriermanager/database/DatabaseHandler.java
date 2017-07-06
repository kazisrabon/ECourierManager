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

import com.ks.ecmanager.ecouriermanager.pojo.AgentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.DOListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ProfileListDatum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kazi Srabon on 6/21/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
//    dbhelper instance for singleton
    private static DatabaseHandler mInstance = null;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "eCourierManager";

    // eCourier Manager table name
    private static final String TABLE_AGENTS = "agents";
    private static final String TABLE_DOS = "dos";
    private static final String TABLE_PROFILE = "profile";
    private static final String TABLE_CONFIG = "config";

    // Table Agents Columns names
    private static final String AGENT_ID = "id";
    private static final String AGENT_API_ID = "agent_id";
    private static final String AGENT_NAME = "agent_name";
    private static final String AGENT_DO_NAME = "do_name";
    private static final String AGENT_DO_ID = "do_id";

    //Table dos Columns names
    private static final String DO_ID = "id";
    private static final String DO_API_ID = "do_id";
    private static final String DO_NAME = "do_name";

    // Table Profile Columns names
    private static final String PROFILE_ID = "id";
    private static final String PROFILE_API_ID = "agent_id";
    private static final String BLOOD_GROUP = "blood_group";
    private static final String NID = "nid";
    private static final String M_NAME = "m_name";
    private static final String DOB = "dob";
    private static final String PROFILE_PIC = "profile_pic";
    private static final String JOIN_DATE = "join_date";
    private static final String DO_LOCATION = "do_location";

    // Table Config Columns names
    private static final String CONFIG_ID = "id";

    public static DatabaseHandler getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx);
        }
        return mInstance;
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

       createAgentTable(db);
       createDoTable(db);
       createProfileTable(db);
    }

    public static boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    private void createProfileTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILE + "("
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
        db.execSQL(CREATE_TABLE);
    }

    private void createDoTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_DOS + "("
                + DO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DO_API_ID + " TEXT NOT NULL,"
                + DO_NAME + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createAgentTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_AGENTS + "("
                + AGENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AGENT_API_ID + " TEXT NOT NULL,"
                + AGENT_NAME + " TEXT NOT NULL,"
                + AGENT_DO_NAME + " TEXT,"
                + AGENT_DO_ID + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);

        // Create tables again
        onCreate(db);
    }

//    TABLE_AGENTS CRUD operations

    // Adding new agent
    public void addAgent(AgentListDatum agentDatum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AGENT_API_ID, agentDatum.getAgent_id()); // AGENT ID
        values.put(AGENT_NAME, agentDatum.getAgent_name()); // AGENT Name
        values.put(AGENT_DO_NAME, agentDatum.getDo_name()); // AGENT Do Name
        values.put(AGENT_DO_ID, agentDatum.getDo_id()); // AGENT Do ID

        // Inserting Row
        db.insert(TABLE_AGENTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single agent
    public AgentListDatum getAgent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_AGENTS, new String[] { AGENT_ID, AGENT_API_ID, AGENT_NAME, AGENT_DO_NAME, AGENT_DO_ID },
                AGENT_API_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        AgentListDatum agentListDatum = new AgentListDatum(cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4));
        // return contact
        return agentListDatum;
    }

    // Getting All Agents
    public List<AgentListDatum> getAllAgents() {
        List<AgentListDatum> agentList = new ArrayList<AgentListDatum>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_AGENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AgentListDatum datum = new AgentListDatum();
                datum.setAgent_id(cursor.getString(1));
                datum.setAgent_name(cursor.getString(2));
                datum.setDo_name(cursor.getString(3));
                datum.setDo_id(cursor.getString(4));
                // Adding contact to list
                agentList.add(datum);
            } while (cursor.moveToNext());
        }

        // return contact list
        return agentList;
    }

    // Getting agents Count
    public int getAgentsCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_AGENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Updating single agent
    public int updateAgent(AgentListDatum agentListDatum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AGENT_API_ID, agentListDatum.getAgent_id());
        values.put(AGENT_NAME, agentListDatum.getAgent_name());
        values.put(AGENT_DO_NAME, agentListDatum.getDo_name());
        values.put(AGENT_DO_ID, agentListDatum.getDo_id());

        // updating row
        return db.update(TABLE_AGENTS, values, AGENT_API_ID + " = ?",
                new String[] { String.valueOf(agentListDatum.getAgent_id()) });
    }

    // Deleting single agent
    public void deleteAgent(AgentListDatum agentListDatum) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AGENTS, AGENT_API_ID + " = ?",
                new String[] { String.valueOf(agentListDatum.getAgent_id()) });
        db.close();
    }

    // Deleting all agents
    public void  deleteAgents(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_AGENTS);
    }

//    TABLE_DOS CRUD operations

    // Adding new do
    public void addDo(DOListDatum doListDatum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DO_NAME, doListDatum.getValue()); // do Name
        values.put(DO_API_ID, doListDatum.getId()); // do api id

        // Inserting Row
        db.insert(TABLE_DOS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Do
    public DOListDatum getDo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DOS, new String[] { DO_ID,
                        DO_API_ID, DO_NAME }, DO_API_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DOListDatum doListDatum = new DOListDatum(cursor.getString(1),
                cursor.getString(2));
        // return contact
        return doListDatum;
    }

    // Getting All Dos
    public List<DOListDatum> getAllDOs() {
        List<DOListDatum> doList = new ArrayList<DOListDatum>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DOS;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DOListDatum datum = new DOListDatum();
                datum.setId(cursor.getString(1));
                datum.setValue(cursor.getString(2));
                // Adding contact to list
                doList.add(datum);
            } while (cursor.moveToNext());
        }

        // return contact list
        return doList;
    }

    // Getting Dos Count
    public int getDOsCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_DOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Updating single Do
    public int updateDO(DOListDatum doListDatum) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DO_API_ID, doListDatum.getId());
        values.put(DO_NAME, doListDatum.getValue());

        // updating row
        return db.update(TABLE_DOS, values, DO_API_ID + " = ?",
                new String[] { String.valueOf(doListDatum.getId()) });
    }

    // Deleting single Do
    public void deleteDO(DOListDatum doListDatum) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOS, DO_API_ID + " = ?",
                new String[] { String.valueOf(doListDatum.getId()) });
        db.close();
    }

    // Deleting all Dos
    public void  deleteDOs(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DOS);
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
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_PROFILE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
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
