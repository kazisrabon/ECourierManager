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
import com.ks.ecmanager.ecouriermanager.pojo.ResponseList;
import com.ks.ecmanager.ecouriermanager.pojo.Updates;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.File;
import java.text.Bidi;
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
    private static final String TABLE_VIEWER = "viewer";
    private static final String TABLE_UPDATER = "updater";

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
    private static final String USER_GROUP = "user_group";

    // Table Config Columns names
    private static final String CONFIG_ID = "id";
    private static final String STATUS= "status";
    private static final String READABLE_STATUS= "readable_status";

//    viewers table
    private static final String VIEWER_ID = "id";
    private static final String CONFIG_STATUS= "config_status";
    private static final String VIEWERS= "viewers";

//    updaters table
    private static final String UPDATER_ID = "id";
    private static final String CURRENT_STATUS= "current_status";
    private static final String NEXT_STATUS= "next_status";
    private static final String UPDATERS= "updaters";
    private static final String UPDATES= "updates";

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
       createConfigTable(db);
       createViewerTable(db);
       createUpdaterTable(db);
    }

    private void createUpdaterTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_UPDATER + "("
                + UPDATER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CURRENT_STATUS + " TEXT,"
                + NEXT_STATUS + " TEXT,"
                + UPDATERS + " TEXT,"
                + UPDATES + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
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
                + DO_LOCATION + " TEXT,"
                + USER_GROUP + " TEXT NOT NULL"
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

    private void createConfigTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONFIG + "("
                + CONFIG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STATUS + " TEXT NOT NULL,"
                + READABLE_STATUS + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createViewerTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_VIEWER + "("
                + VIEWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CONFIG_STATUS + " TEXT NOT NULL,"
                + VIEWERS + " TEXT"
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIG);

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
    public AgentListDatum getAgent(String id) {
        Log.e("DB Agent", id);
        SQLiteDatabase db = this.getReadableDatabase();

//        Cursor cursor = db.query(TABLE_AGENTS, new String[] { AGENT_ID, AGENT_API_ID, AGENT_NAME, AGENT_DO_NAME, AGENT_DO_ID },
//                AGENT_API_ID + "=?",
//                new String[] { id }, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from "+ TABLE_AGENTS +" where "+ AGENT_API_ID + " = " +id, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()){
            AgentListDatum agentListDatum = new AgentListDatum(cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4));
            cursor.close();
            return agentListDatum;
        }
        else
            return null;
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
    public DOListDatum getDo(String id) {
        Log.e("DB DO", id);
        SQLiteDatabase db = this.getReadableDatabase();

//        Cursor cursor = db.query(TABLE_DOS, new String[] { DO_ID,
//                        DO_API_ID, DO_NAME }, DO_API_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from "+ TABLE_DOS +" where "+ DO_API_ID + " = " +id, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()){
            DOListDatum doListDatum = new DOListDatum(cursor.getString(1),
                    cursor.getString(2));
            cursor.close();
            // return contact
            return doListDatum;
        }
        else
            return null;


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
    public void addProfile(ProfileListDatum profileListDatum, String agent_id, String user_group) {
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
        values.put(USER_GROUP, user_group); // user group

        // Inserting Row
        db.insert(TABLE_PROFILE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single profile
    public ProfileListDatum getProfile(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROFILE,
                new String[] { BLOOD_GROUP, NID, M_NAME, DOB, PROFILE_PIC, JOIN_DATE, DO_LOCATION, USER_GROUP},
                PROFILE_API_ID + "=?",
                new String[] { id },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
//String blood_group, String nid, String name, String dob, String profilePic, String joinDate, String deliveryZone, String user_group
        ProfileListDatum profileListDatum = new ProfileListDatum(cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7));
        cursor.close();
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

//    TABLE_CONFIG CRUD operations

    // Adding new configure
    public void addConfig(String status, String readable_status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, status); // status
        values.put(READABLE_STATUS, readable_status); // readable status

        // Inserting Row
        db.insert(TABLE_CONFIG, null, values);
        db.close(); // Closing database connection
    }

    // Getting single readable status
    public String getReadableStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select readable_status from config where status = \"S2\";";
        Cursor cursor = db.rawQuery("select "+READABLE_STATUS + " from "+ TABLE_CONFIG + " where " + STATUS + " = \"" + status + "\"",
                null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()){
            String s = cursor.getString(0);
            cursor.close();
            return s;
        }
        else return "";
    }

    // Getting All config status Data
    public List<ResponseList> getAllStatus() {
        List<ResponseList> responseListData = new ArrayList<ResponseList>();
        // Select All Query
        String selectQuery = "SELECT "+STATUS+", "+READABLE_STATUS+" FROM " + TABLE_CONFIG;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null){
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    ResponseList datum = new ResponseList(cursor.getString(0), cursor.getString(1));
                    responseListData.add(datum);
                } while (cursor.moveToNext());
                cursor.close();
                return responseListData;
            }
            else return  null;
        }
        else return null;
    }

    // Getting config Count
    public int getConfigCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_CONFIG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Deleting all config table data
    public void  deleteConfigs(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CONFIG);
    }

//    table viewer

//    adding new viewer
    public void addViewer(String status, String viewer){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONFIG_STATUS, status); // status
        values.put(VIEWERS, viewer); // viewer

        // Inserting Row
        db.insert(TABLE_VIEWER, null, values);
        db.close(); // Closing database connection
    }

    // Getting All viewer Data
    public BidiMap<String, String> getAllViewersforStatus() {
        BidiMap<String, String> viewres = new DualHashBidiMap();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_VIEWER;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null){
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    viewres.put(cursor.getString(1), cursor.getString(2));
                } while (cursor.moveToNext());
                cursor.close();
                return viewres;
            }
            else return  null;
        }
        else return null;
    }

    // Getting viewer Count
    public int getViewerCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_VIEWER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Deleting all viewer table data
    public void  deleteViewers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_VIEWER);
    }

//    table updater

//    adding new updater
    public void addUpdater(String c_status, String n_status, String updaters, String updates){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CURRENT_STATUS, c_status); // CURRENT status
        values.put(NEXT_STATUS, n_status); // NEXT STATUS
        values.put(UPDATERS, updaters); // updaters
        values.put(UPDATES, updates); // updates


        // Inserting Row
        db.insert(TABLE_UPDATER, null, values);
        db.close(); // Closing database connection
    }

    // Getting All viewer Data
    public List<Updates> getAllUpdaters() {
        List<Updates> updates = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_UPDATER;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null){
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    Updates update = new Updates();
                    update.setCurrent_status(cursor.getString(1));
                    update.setNext_status(cursor.getString(2));
                    update.setUpdaters(cursor.getString(3));
                    update.setUpdates(cursor.getString(4));
                    updates.add(update);
                } while (cursor.moveToNext());
                cursor.close();
                return updates;
            }
            else return  null;
        }
        else return null;
    }

    // Getting updater Count
    public int getUpdaterCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_UPDATER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Deleting all updater table data
    public void  deleteUpdaters(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_UPDATER);
    }

}
