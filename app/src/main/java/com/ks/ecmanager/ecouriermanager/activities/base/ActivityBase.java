/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.database.DatabaseHandler;
import com.ks.ecmanager.ecouriermanager.pojo.AgentList;
import com.ks.ecmanager.ecouriermanager.pojo.DoList;
import com.ks.ecmanager.ecouriermanager.pojo.NextStatusandUpdates;
import com.ks.ecmanager.ecouriermanager.pojo.ParcelList;
import com.ks.ecmanager.ecouriermanager.pojo.ProfileList;
import com.ks.ecmanager.ecouriermanager.pojo.ResponseList;
import com.ks.ecmanager.ecouriermanager.pojo.UpdatesListDatum;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.AgentListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.AppConfigInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.DoListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ParcelStatusUpdateInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ProfileListInterface;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin.sessionUserData;

/**
 * Created by Kazi Srabon on 5/20/2017.
 */

public class ActivityBase extends AppCompatActivity {

    public static final String ALBUM_NAME = "eCourier";
    public static final String CN_TYPE_SEARCH = "search";
    public static final String CN_TYPE_MULTIPLE = "multiple";
    public static final String KEY_ECRS = "ecrs";
    public static final String FROM_BULK = "bulk";
    public static final String FROM_INDI = "individual";
//    key_delivery 1 => status 2=> agent 3=> do
    public static final String KEY_DELIVERY = "delivery";
    public static final String KEY_CN_POS = "consignment_data_position";
    public static final String KEY_CN_DATA = "consignment_data_array";
    public static final String KEY_WHERE_FROM = "where_from";
    public static final int MIDDLE = 2;
    public static final int END = 3;
    public static final int CAMERA_PERMISSIONS_REQUEST = 301,
            SELECT_PICTURE = 302,
            TAKE_PHOTO = 303,
            REQUEST_EXTERNAL_STORAGE = 401,
            REQUEST_READ_EXTERNAL_STORAGE = 402,
            calender_type = 110;
    public static String CHANGED_VALUE = "";
    public static Drawable IMG_DRAWABLE = null;
    public static DatabaseHandler db;
    public static Bitmap mBitmap = null;
    public static boolean isAgentRefreshed = false, isDoRefreshed = false;
    private final String TAG = "Base";
    private static ActivityBase activityBase;
    private Activity activity;
    private Context context;
    private ProgressDialog progressDialog;
    public static BidiMap<String, String> agentBidiList;
    public static BidiMap<String, String> doBidiList;
    private HashMap<String, String> nextStatusMap;
    private boolean canAgentChange, canDOChange;
    private String nextStatus = "", nextAgent = "", nextDO = "", current_status = "";

    public void setDoBidiList(HashMap<String, String> map) {
        doBidiList = new DualHashBidiMap();

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        DoListInterface myApiCallback = restAdapter.create(DoListInterface.class);

        myApiCallback.getData(ApiParams.TAG_DO_LIST_KEY, map, new Callback<DoList>() {
            @Override
            public void success(DoList doList, Response response) {
                boolean status = doList.getStatus();
                Log.e(TAG, status+" ");
                if (status) {
                    isDoRefreshed = true;
                    initDB();
                    if (db.getDOsCount() > 0){
                        db.deleteDOs();
                    }
//                    Log.e("DO DB 1"+TAG, db.getDOsCount()+"");
                    for (int i = 0; i < doList.getDo_list().size(); i++) {
                        doBidiList.put(doList.getDo_list().get(i).getId(), doList.getDo_list().get(i).getValue());
//                        Log.e("Do data", doList.getDo_list().get(i).getId()+" "+doList.getDo_list().get(i).getValue());
                        db.addDo(doList.getDo_list().get(i));
                    }
                    Log.e("DO DB 2"+TAG, db.getDOsCount()+"");
//                    for (DOListDatum doListDatum : db.getAllDOs()){
//                        Log.e("DO DB 3"+TAG, doListDatum.getId() + " " + doListDatum.getValue());
//                    }
                    showSuccessToast("DO Loaded", 0, MIDDLE);
                }
                else{
                    showErrorToast(getString(R.string.no_do_found), Toast.LENGTH_SHORT, MIDDLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showErrorToast("" + error.getMessage() + "!", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    public void setAgentDoBidiList(HashMap<String, String> map) {
        doBidiList = new DualHashBidiMap();
        agentBidiList = new DualHashBidiMap();
        showProgressDialog(false, "", getResources().getString(R.string.loading));

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        DoListInterface myApiCallback = restAdapter.create(DoListInterface.class);

        myApiCallback.getData(ApiParams.TAG_DO_LIST_KEY, map, new Callback<DoList>() {
            @Override
            public void success(DoList doList, Response response) {
                boolean status = doList.getStatus();
                Log.e(TAG, status+" ");
                if (status) {
                    isDoRefreshed = true;
                    for (int i = 0; i < doList.getDo_list().size(); i++) {
                        doBidiList.put(doList.getDo_list().get(i).getId(), doList.getDo_list().get(i).getValue());
                    }
                }
                else
                    showErrorToast(getString(R.string.no_data_found), Toast.LENGTH_SHORT, MIDDLE);

            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressDialog();
                showErrorToast("" + error.getMessage() + "!", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    public void setAgentBidiList(HashMap<String, String> map) {
        agentBidiList = new DualHashBidiMap();

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        AgentListInterface myApiCallback = restAdapter.create(AgentListInterface.class);

        myApiCallback.getData(ApiParams.TAG_DO_AGENT_LIST_KEY, map, new Callback<AgentList>() {
            @Override
            public void success(AgentList agentList, Response response) {
                boolean status = agentList.getStatus();
                Log.e(TAG, status+" ");
                if (status) {
                    isAgentRefreshed = true;
                    initDB();
                    if (db.getAgentsCount() > 0){
                        db.deleteAgents();
                    }
//                    Log.e("Agent DB 1"+TAG, db.getAgentsCount()+"");
                    for (int i = 0; i < agentList.getAgent_list().size(); i++) {
                        agentBidiList.put(agentList.getAgent_list().get(i).getAgent_id(), agentList.getAgent_list().get(i).getAgent_name());
                        db.addAgent(agentList.getAgent_list().get(i));
                    }
//                    Log.e("Agent DB 2"+TAG, db.getAgentsCount()+"");
//                    for (AgentListDatum agentListDatum : db.getAllAgents()){
//                        Log.e("Agent DB 3"+TAG,
//                                agentListDatum.getAgent_id()
//                                + " " + agentListDatum.getAgent_name()
//                                + " " + agentListDatum.getDo_id()
//                                + " " + agentListDatum.getDo_name());
//                    }
                    showBlackToast("Config Loaded", 0, MIDDLE);
                }
                else{
                    showErrorToast(getString(R.string.no_agent_found), Toast.LENGTH_SHORT, MIDDLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showErrorToast("" + error.getMessage() + "!", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    public void setProfileData(final HashMap<String, String> map, final String user_group) {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        ProfileListInterface myApiCallback = restAdapter.create(ProfileListInterface.class);

        myApiCallback.getData(ApiParams.TAG_PROFILE_KEY, map, new Callback<ProfileList>() {
            @Override
            public void success(ProfileList profileList, Response response) {
                //we get json object from server to our POJO or model class

                boolean status = profileList.getStatus();
                Log.e(TAG, profileList.getStatus()+"");
                if (status) {
                    initDB();
                    if (db.getProfileCount() > 0){
                        db.deleteProfiles();
                    }
//                    Log.e("Profile 1"+TAG, db.getProfileCount()+"");
//                    Log.e("Profile", profileList.getData().getName()+"");
                    db.addProfile(profileList.getData(), map.get(ApiParams.PARAM_ADMIN_ID), user_group);
                    Log.e("Profile 2"+TAG, db.getProfileCount()+"");
//                    for (ProfileListDatum profileListDatum : db.getAllProfile()){
//                        Log.e("Profile 3"+TAG,
//                                profileListDatum.getName()
//                                +" "+profileListDatum.getProfilePic()
//                                +" "+profileListDatum.getJoinDate());
//                    }
//                    showSuccessToast("Profile Loaded", 0, END);
                } else {
                    showErrorToast(getString(R.string.no_profile_data_found), Toast.LENGTH_SHORT, MIDDLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showErrorToast("" + error.getMessage() + "!", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    public void getConfigData() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        AppConfigInterface myApiCallback = restAdapter.create(AppConfigInterface.class);
//        Log.e(TAG, "getConfigData()");
        myApiCallback.getData(ApiParams.TAG_APP_CONFIG, new Callback<List<ResponseList>>() {

            @Override
            public void success(List<ResponseList> responseLists, Response response) {
                initDB();
                if (db.getConfigCount() > 0){
                    db.deleteConfigs();
                }
                if (db.getViewerCount() > 0){
                    db.deleteViewers();
                }
                if (db.getUpdaterCount() > 0){
                    db.deleteUpdaters();
                }
                HashMap<String, String> viewersMap = new HashMap<>();
                for (ResponseList responseList : responseLists){
                    db.addConfig(responseList.getStatus(), responseList.getReadable_status());
                    viewersMap.put(responseList.getStatus(), responseList.getViewer());
                    db.addViewer(responseList.getStatus(), responseList.getViewer());
                    for (UpdatesListDatum updatesListDatum : responseList.getUpdates()){
//                        Log.e(TAG, updatesListDatum.toString());
                        db.addUpdater(responseList.getStatus(), updatesListDatum.getStatus(), updatesListDatum.getUpdater(), updatesListDatum.getUpdates());
                    }
//                    for (Updates updates : db.getAllUpdaters()){
//                        Log.e("from db ", updates.getCurrent_status()
//                                +" "+updates.getNext_status()
//                                +" "+updates.getUpdaters()
//                                +" "+updates.getUpdates());
//                    }
//                    showSuccessToast("Config Loaded", 0, END);
                }
                showSuccessToast(viewersMap.size()+"", 0, MIDDLE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failure "+error.getMessage());
            }
        });
    }

    public boolean canECRView(String status, String sdo_id, String ddo_id){
        boolean canView = false;
        HashMap<String, String> user = SessionUserData.getSFInstance(this).getSessionDetails();
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP).toLowerCase();
        String dbViewers = db.getViewerforStatus(status).toLowerCase();
        if (!dbViewers.equals("")) {
            Log.e("viewer for status", "" + dbViewers);
//            -1 => no access 0 => has only view access 1 => can change status 2 => can change agent 3 => can change do
            if (dbViewers.contains(group)){
                if (group.equals("ecsz"))
                    canView = specialRuleforECZ(dbViewers, sdo_id, ddo_id, id);
            }
        }
        Log.e("Access code", canView+"");

        return canView;
    }

    public String accessLevel(String status, String sdo_id, String ddo_id){
        String accessCode= "1";
        current_status = status;
        sessionUserData.setKeyStatus(current_status);
        HashMap<String, String> user = SessionUserData.getSFInstance(this).getSessionDetails();
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP).toLowerCase();
        List<NextStatusandUpdates> nextStatusandUpdates = db.getNextStatusandUpdates(status, group);
        nextStatusMap = new HashMap<>();
        for (NextStatusandUpdates updates : nextStatusandUpdates){
            Log.e("NextStatusandUpdates", updates.getNext_status()+" "+updates.getUpdaters()+" "+updates.getDefine_do());
//    who can change define_do => 0 = nothing_special, 1 = sdo, 2 = ddo
            if (updates.getDefine_do() == 1){
                if (sdo_id.equals(id))
                    accessCode = changeAccessCodeforStatus(updates, accessCode);
                else
                    accessCode = "0";
            }
            else if (updates.getDefine_do() == 2){
                if (ddo_id.equals(id))
                    accessCode = changeAccessCodeforStatus(updates, accessCode);
                else
                    accessCode = "0";
            }
            else
                accessCode = changeAccessCodeforStatus(updates, accessCode);
        }

        return accessCode;
    }

    private String changeAccessCodeforStatus(NextStatusandUpdates updates, String accessCode) {
        if (updates.getNext_status().isEmpty()){
            if (accessCode.contains("1"))
                accessCode = accessCode.replace("1","");
        }
        else {
            String next_status = updates.getNext_status();
            next_status = next_status.replace("{", "");
            next_status = next_status.replace("}", "");

            nextStatusMap.put(next_status, db.getReadableStatus(next_status));
        }
        return accessCode;
    }

    public void setCurrentStatus(String nextStatus){

    }

    public void setCheckChangeAgents(String next_status_code) {
        canAgentChange = false;
        HashMap<String, String> user = SessionUserData.getSFInstance(this).getSessionDetails();
        String group = user.get(SessionUserData.KEY_USER_GROUP);

        canAgentChange = db.getNextUpdates(current_status, next_status_code, group, "agent");
        Log.e("testing", ""+canAgentChange);
    }

    public boolean getCheckChangeAgents(){
        return canAgentChange;
    }

    public void setCheckChangeDOs(String next_status_code) {
        canDOChange = false;

        HashMap<String, String> user = SessionUserData.getSFInstance(this).getSessionDetails();
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);

        canDOChange = db.getNextUpdates(current_status, next_status_code, group, "do");
        Log.e("testing", ""+canDOChange);
    }

    public boolean getCheckChangeDOs(){
        return canDOChange;
    }

    public boolean getCheckChangeDOforAgent(){
        HashMap<String, String> user = SessionUserData.getSFInstance(this).getSessionDetails();
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        Log.e("popup value", nextStatus +" "+ nextAgent +" "+ nextDO);
        return db.getNextUpdates(current_status, nextStatus, group, "do", "agent");
    }

    public boolean getCheckChangeDOforAgent(String agent){
        HashMap<String, String> user = SessionUserData.getSFInstance(this).getSessionDetails();
        HashMap<String, String> update = SessionUserData.getSFInstance(this).getStatusDetails();
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        nextStatus = update.get(SessionUserData.KEY_NEXT_STATUS);
        Log.e("popup value", nextStatus +" "+ nextAgent +" "+ nextDO);
        return db.getNextUpdates(current_status, nextStatus, group, "do", "agent");
    }

    public HashMap<String, String> getNextStatusMap() {
        return nextStatusMap;
    }

    private boolean specialRuleforECZ(String dbViewers, String sdo_id, String ddo_id, String id) {
        boolean accessCode = false;
        if (dbViewers.contains("sdo")){
            if (sdo_id.equals(id))
                accessCode = true;
        }
        else if (dbViewers.contains("ddo")){
            if (ddo_id.equals(id))
                accessCode = true;
        }
        else
            accessCode = true;
        return accessCode;
    }

    public ActivityBase() {
        initDB();
        this.activity = ActivityBase.this;
        this.context = ActivityBase.this;
        if (activityBase!=null){
            activityBase = new ActivityBase();
        }
    }

    public String getNextStatus() {
        return nextStatus;
    }

    public String getNextAgent() {
        return nextAgent;
    }

    public String getNextDO() {
        return nextDO;
    }

    public String getCurrent_status() {
        return current_status;
    }
     public String getCurrent_status(String nextStatus, String agentID, String doID, String group){
        return db.getCurrent_status(nextStatus, agentID, doID, group);
     }

    public void initDB() {
        if (db == null){
            db = DatabaseHandler.getInstance(ActivityBase.this);
        }
    }

    public void showToast(String message, int duration, int gravity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        layout.setBackgroundColor(Color.BLACK);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        text.setTextColor(Color.WHITE);

        Toast toast = new Toast(this);
        toast.setDuration(duration);
        if (gravity == MIDDLE)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    public void showErrorToast(String message, int duration, int gravity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        layout.setBackgroundColor(Color.RED);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        text.setTextColor(Color.WHITE);

        Toast toast = new Toast(this);
        toast.setDuration(duration);
        if (gravity == MIDDLE)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    public void showSuccessToast(String message, int duration, int gravity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        layout.setBackgroundColor(Color.GREEN);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        text.setTextColor(Color.WHITE);

        Toast toast = new Toast(this);
        toast.setDuration(duration);
        if (gravity == MIDDLE)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
    }
    public void showBlackToast(String message, int duration, int gravity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        layout.setBackgroundColor(Color.BLACK);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        text.setTextColor(Color.WHITE);

        Toast toast = new Toast(this);
        toast.setDuration(duration);
        if (gravity == MIDDLE)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
    }
    public void showWhiteToast(String message, int duration, int gravity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        layout.setBackgroundColor(Color.WHITE);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        text.setTextColor(Color.BLACK);

        Toast toast = new Toast(this);
        toast.setDuration(duration);
        if (gravity == MIDDLE)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    public void showListInPopUp(final Context context, final HashMap<String, String> mapData, final String where_from){
        if (context!=null && mapData!=null && stringNotNullCheck(where_from)) {
            final String[] returnValue = {""};
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            builderSingle.setIcon(R.mipmap.ic_launcher_new);
            builderSingle.setTitle("Select One:-");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.select_dialog_singlechoice);
            for (String key : mapData.keySet()) {
                if (mapData.get(key).equals(""))
                    arrayAdapter.add(key);
                else
                    arrayAdapter.add(mapData.get(key));
            }
            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
//                if (where_from == FROM_DO)
//                    d_do = arrayAdapter.getItem(which);
//                else if (where_from == FROM_AGENT)
//                    agent_name = arrayAdapter.getItem(which);

                    final String[] s = {""};
                    String key = "";
                    for (String name : mapData.keySet()) {
                        key = name;
                        String value = mapData.get(name);
                        if (value.equals(arrayAdapter.getItem(which)))
                            Log.e("status code " + TAG, "" + key);
                    }
//                String asd= mapData.getKey(arrayAdapter.getItem(which));
//                Log.e("status code "+TAG, ""+asd);
                    s[0] = arrayAdapter.getItem(which);
                    builderInner.setMessage(s[0]);
                    builderInner.setTitle("Your Selection is");
                    final String finalKey = key;
                    builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG, "selected " + s[0]);
                            returnValue[0] = s[0];
                            if (where_from.equals("status")) {
                                setCheckChangeAgents(finalKey);
                                setCheckChangeDOs(finalKey);
                            }
//                        setChangedAgentorDO(s[0], where_from);
                            dialog.dismiss();
                            if (where_from.equals("status")) {
                                sessionUserData.setKeyNextStatus(finalKey);
                                nextStatus = finalKey;
                                if (getCheckChangeAgents()) {
                                    if (getElligibleAgent()!= null)
                                        showListInPopUp(context, getElligibleAgent(), "agent");
                                    else
                                        showErrorToast("Agent List Error!!!", Toast.LENGTH_SHORT, MIDDLE);
                                }
                                else if (getCheckChangeDOs()) {
                                    if (db.getDOs()!=null)
                                        showListInPopUp(context, db.getDOs(), "do");
                                    else
                                        showErrorToast("DO List Error!!!", Toast.LENGTH_SHORT, MIDDLE);
                                }
                                else
                                    commentPopup();
                            }
                            else if (where_from.equals("agent")) {
                                sessionUserData.setKeyAgentId(finalKey);
                                nextAgent = finalKey;
                                if (getCheckChangeDOforAgent()) {
                                    if (db.getDOs() != null)
                                        showListInPopUp(context, db.getDOs(), "do");
                                    else
                                        showErrorToast("DO List Error!!!", Toast.LENGTH_SHORT, MIDDLE);
                                }
                                else
                                    commentPopup();

                            }
                            else {
                                sessionUserData.setKeyDoId(finalKey);
                                nextDO = finalKey;
                                commentPopup();
                            }

//                        checkChangeAgents(finalKey);
                        }
                    });
                    builderInner.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                        if (where_from == FROM_DO)
//                            d_do = "";
//                        else if (where_from == FROM_AGENT)
//                            agent_name = "";
                            s[0] = "";
                            returnValue[0] = s[0];
//                        setChangedAgentorDO(s[0], FROM_NONE);
                            Log.e(TAG, "selected " + s[0]);

                            dialog.dismiss();
                        }
                    });
                    builderInner.show();
                    builderInner.setCancelable(false);
                }
            });
            builderSingle.show();
            builderSingle.setCancelable(false);

            CHANGED_VALUE = returnValue[0];
        }
        else
            showErrorToast("Invalid data to show popup!!!", Toast.LENGTH_SHORT, MIDDLE);
    }

    private HashMap<String, String> getElligibleAgent() {
        HashMap<String, String> user = SessionUserData.getSFInstance(this).getSessionDetails();
        String id = user.get(SessionUserData.KEY_USER_ID);
        return db.getElligibleAgent(id);
    }

    public void showProgressDialog(boolean show_title, String title, String message) {
        progressDialog = new ProgressDialog(this);
        if (show_title) {
            progressDialog.setTitle(title);
        }
        progressDialog.setMessage(message);
        progressDialog.show();

    }

    public BidiMap<String, String> createBidiMap(ArrayList<String> key, ArrayList<String> value){
        BidiMap<String, String> bidiMap = new DualHashBidiMap();

        for (int i =0; i<key.size(); i++){
            bidiMap.put(key.get(i), value.get(i));
        }
        return bidiMap;

    }

    public void printHash(String where_from, HashMap<String, String> map){
        for (String name: map.keySet()){
            String key =name.toString();
            String value = map.get(name).toString();
            Log.e(where_from +" "+TAG ,key + " " + value);
        }
    }

    public void printBidiHash(String where_from, BidiMap<String, String> bidiMap){
        for (String name : bidiMap.keySet()){
            String key =name;
            String value = bidiMap.get(name);
            Log.e(where_from +" "+TAG ,key + " " + value);
        }
    }

    public boolean checkText(String s) {
        boolean isValid;
        if(s != null && !s.isEmpty())
            isValid = true;
        else isValid = false;

        return isValid;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndex(proj[0]);
        res = cursor.getString(column_index);
        cursor.close();
        Log.e(TAG, "Gallery File Path=====>>>"+contentUri);
        return res;
    }

    public long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {

        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    public boolean stringNotNullCheck(String s){
        return s != null && !s.isEmpty() && !s.equals("");
    }

    private void commentPopup(){
        if (sessionUserData.getStatusDetails().get(SessionUserData.KEY_NEXT_STATUS).equals(getResources().getString(R.string.s7))
            || sessionUserData.getStatusDetails().get(SessionUserData.KEY_NEXT_STATUS).equals(getResources().getString(R.string.s12))
            || sessionUserData.getStatusDetails().get(SessionUserData.KEY_NEXT_STATUS).equals(getResources().getString(R.string.s24))
                ){
            final String[] comment = {""};
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogViewComment = inflater.inflate(R.layout.dialog_comment, null);
            dialogBuilder.setView(dialogViewComment);
            final AlertDialog b = dialogBuilder.create();
            b.setCancelable(true);
            b.show();
            final EditText editInput = (EditText) dialogViewComment.findViewById(R.id.editInput);
            final Button btnClear = (Button) dialogViewComment.findViewById(R.id.btnClearComment);
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editInput.setText("");
                }

            });

            final Button btnOk = (Button) dialogViewComment.findViewById(R.id.btnOkComment);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comment[0] = editInput.getText().toString();
                    b.dismiss();
                    parcelUpdate(comment[0]);
                }

            });

            final Button btnCancel = (Button) dialogViewComment.findViewById(R.id.btnCancelComment);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editInput.setText("");
                    b.dismiss();
                    parcelUpdate("");
                }

            });
        }
        else
            parcelUpdate("");

//        return comment[0];
    }

    public void parcelUpdate(String s){
        HashMap<String, String> statusMap = new HashMap<>();
        HashMap<String, String> user = sessionUserData.getSessionDetails();
        HashMap<String, String> status = sessionUserData.getStatusDetails();
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);
        String nextStatus = status.get(SessionUserData.KEY_NEXT_STATUS);
        String agentId = status.get(SessionUserData.KEY_AGENT_ID);
        String doId = status.get(SessionUserData.KEY_DO_ID);
        if (status.get(SessionUserData.KEY_NEXT_STATUS).equals(getResources().getString(R.string.s12))){
            Date time = new Date();
            String fDate = ApiParams.longDateFormat.format(time);
            statusMap.put(ApiParams.PARAM_CANCEL_CALL_TIME, fDate);
        }
        if (stringNotNullCheck(s))
            statusMap.put(ApiParams.PARAM_COMMENT, s);
        else
            statusMap.put(ApiParams.PARAM_COMMENT, "INDIVIDUAL FROM MOBILE");
        statusMap.put(ApiParams.PARAM_GROUP, group);
        statusMap.put(ApiParams.PARAM_ADMIN_ID, id);
        status.put(ApiParams.PARAM_AUTHENTICATION_KEY, authentication_key);
        status.put(ApiParams.PARAM_STATUS, nextStatus);
        if (stringNotNullCheck(agentId)){
            if (nextStatus.equals(getResources().getString(R.string.s25)))
                status.put(ApiParams.PARAM_RETURN_AGENT, agentId);
            else
                status.put(ApiParams.PARAM_AGENT_ID, agentId);
        }
        if (stringNotNullCheck(doId)){
            if (nextStatus.equals(getResources().getString(R.string.s2))
                    || nextStatus.equals(getResources().getString(R.string.s14))
                    || nextStatus.equals(getResources().getString(R.string.s15))
                    || nextStatus.equals(getResources().getString(R.string.s7))
                    )
                status.put(ApiParams.PARAM_D_DO, agentId);
        }
//        showProgressDialog(false, "", getResources().getString(R.string.loading));

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        ParcelStatusUpdateInterface myApiCallback = restAdapter.create(ParcelStatusUpdateInterface.class);

//      parcel update api

        myApiCallback.getData(ApiParams.TAG_PARCEL_STATUS_UPDATE_KEY, statusMap, new Callback<ParcelList>() {
            @Override
            public void success(ParcelList parcelList, Response response) {
                hideProgressDialog();

                boolean status = parcelList.getStatus();
                Log.e(TAG, status+" ");
                if (status) {
                    showSuccessToast(getString(R.string.sucess), Toast.LENGTH_SHORT, MIDDLE);
//                    reloadCN(map.get(ApiParams.PARAM_CONSIGNMENT_NO));
                }
                else
                    showErrorToast(getString(R.string.no_data_found), Toast.LENGTH_SHORT, MIDDLE);
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressDialog();
                showErrorToast("" + error.getMessage() + "!", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
