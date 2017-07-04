/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.database.DatabaseHandler;
import com.ks.ecmanager.ecouriermanager.pojo.AgentList;
import com.ks.ecmanager.ecouriermanager.pojo.AgentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.DOListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.DoList;
import com.ks.ecmanager.ecouriermanager.pojo.ProfileList;
import com.ks.ecmanager.ecouriermanager.pojo.ProfileListDatum;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.AgentListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.DoListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ProfileListInterface;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.ks.ecmanager.ecouriermanager.database.DatabaseHandler.getInstance;

/**
 * Created by Kazi Srabon on 5/20/2017.
 */

public class ActivityBase extends AppCompatActivity {

    public static final String ALBUM_NAME = "eCourier";
    public static final String CN_TYPE_SEARCH = "search";
    public static final String KEY_CN_POS = "consignment_data_position";
    public static final String KEY_CN_DATA = "consignment_data_array";
    public static final String KEY_WHERE_FROM = "where_from";
    public static final int MIDDLE = 2;
    public static final int START = 1;
    public static final int END = 3;
    public static final int CAMERA_PERMISSIONS_REQUEST = 301,
            SELECT_PICTURE = 302,
            TAKE_PHOTO = 303,
            REQUEST_EXTERNAL_STORAGE = 401,
            REQUEST_READ_EXTERNAL_STORAGE = 402,
            calender_type = 110;
    public static Drawable IMG_DRAWABLE = null;
    public static DatabaseHandler db;
    public static Bitmap mBitmap = null;
    public static boolean isAgentRefreshed = false, isDoRefreshed = false;
    private final String TAG = "Base";
    private Activity activity;
    private Context context;
    private ProgressDialog progressDialog;
    public static BidiMap<String, String> agentBidiList;
    public static BidiMap<String, String> doBidiList;

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
                    Log.e("DO DB 1"+TAG, db.getDOsCount()+"");
                    for (int i = 0; i < doList.getDo_list().size(); i++) {
                        doBidiList.put(doList.getDo_list().get(i).getId(), doList.getDo_list().get(i).getValue());
//                        Log.e("Do data", doList.getDo_list().get(i).getId()+" "+doList.getDo_list().get(i).getValue());
                        db.addDo(doList.getDo_list().get(i));
                    }
                    Log.e("DO DB 2"+TAG, db.getDOsCount()+"");
                    for (DOListDatum doListDatum : db.getAllDOs()){
                        Log.e("DO DB 3"+TAG, doListDatum.getId() + " " + doListDatum.getValue());
                    }
                    showSuccessToast("DO Loaded", 0, END);
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
                    Log.e("Agent DB 1"+TAG, db.getAgentsCount()+"");
                    for (int i = 0; i < agentList.getAgent_list().size(); i++) {
                        agentBidiList.put(agentList.getAgent_list().get(i).getAgent_id(), agentList.getAgent_list().get(i).getAgent_name());
                        db.addAgent(agentList.getAgent_list().get(i));
                    }
                    Log.e("Agent DB 2"+TAG, db.getAgentsCount()+"");
                    for (AgentListDatum agentListDatum : db.getAllAgents()){
                        Log.e("Agent DB 3"+TAG,
                                agentListDatum.getAgent_id()
                                + " " + agentListDatum.getAgent_name()
                                + " " + agentListDatum.getDo_id()
                                + " " + agentListDatum.getDo_name());
                    }
                    showSuccessToast("Agent Loaded", 0, END);
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

    public void setProfileData(final HashMap<String, String> map) {
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
                    Log.e("Profile 1"+TAG, db.getProfileCount()+"");
//                    Log.e("Profile", profileList.getData().getName()+"");
                    db.addProfile(profileList.getData(), map.get(ApiParams.PARAM_ADMIN_ID));
                    Log.e("Profile 2"+TAG, db.getProfileCount()+"");
                    for (ProfileListDatum profileListDatum : db.getAllProfile()){
                        Log.e("Profile 3"+TAG,
                                profileListDatum.getName()
                                +" "+profileListDatum.getProfilePic()
                                +" "+profileListDatum.getJoinDate());
                    }
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

    public ActivityBase() {
        initDB();
        this.activity = ActivityBase.this;
        this.context = ActivityBase.this;

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

    public void hideProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    public void showProgressDialog(boolean show_title, String title, String message) {
        progressDialog = new ProgressDialog(this);
        if (show_title) {
            progressDialog.setTitle(title);
        }
        progressDialog.setMessage(message);
        progressDialog.show();

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
            String value = bidiMap.get(name).toString();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
