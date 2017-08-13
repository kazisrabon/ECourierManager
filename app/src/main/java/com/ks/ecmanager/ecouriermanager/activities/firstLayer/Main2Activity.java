/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.firstLayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin;
import com.ks.ecmanager.ecouriermanager.activities.secondLayer.ActivityDelivery;
import com.ks.ecmanager.ecouriermanager.database.DatabaseHandler;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;

import java.util.HashMap;

import static com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin.sessionUserData;

public class Main2Activity extends ActivityBase {
    private final String TAG = "Main2Activity";
    private HashMap<String, String> user = new HashMap<String, String>();
    private HashMap<String, String> map = new HashMap<String, String>();
    private Context context;
    private SessionUserData sessionUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context = Main2Activity.this;
        sessionUserData = SessionUserData.getSFInstance(context);
        user = sessionUserData.getSessionDetails();
        setHashMap();
        initialize();
        Log.e(TAG, "isLooggedIn "+ sessionUserData.isLoggedIn());
//        getConfigData();
        if (!sessionUserData.isLoggedIn()){
            showProgressDialog(false, "", getResources().getString(R.string.loading));
            setDoBidiList(map);
            setAgentBidiList(map);
            setProfileData(map, user.get(SessionUserData.KEY_USER_GROUP));
            getConfigData();
            sessionUserData.setLoggedIn();
            hideProgressDialog();
        }
        Log.e(TAG, "isLooggedIn "+ sessionUserData.isLoggedIn());
        if (db == null){
            db = DatabaseHandler.getInstance(this);
        }
        else {
//            Log.e (""+TAG,""+accessLevel());
        }
    }

    private void initialize() {
        CardView cv_individual = (CardView) findViewById(R.id.cv_individual);
        cv_individual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, ActivityMain.class));
            }
        });
        CardView cv_delivery = (CardView) findViewById(R.id.cv_bulk);
        cv_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, ActivityDelivery.class);
                sessionUserData.initStatus();
                intent.putExtra(KEY_DELIVERY, "1");
                startActivity(intent);
            }
        });
    }

    private void setHashMap() {
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);
        Log.e(TAG, id +" "+group+" "+authentication_key);
        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
            alert_box.setTitle(getResources().getString(R.string.exit_title));
            alert_box.setMessage(getResources().getString(R.string.logout_confirmation));

            alert_box.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            sessionUserData.endSession();
                            Intent loginActivity =  new Intent(Main2Activity.this,ActivityLogin.class);
                            loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginActivity);
                        }
                    });

            alert_box.setNeutralButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });

            alert_box.show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                HashMap<String, String> map = new HashMap<>();
                map.put(ApiParams.PARAM_ADMIN_ID, user.get(SessionUserData.KEY_USER_ID));
                map.put(ApiParams.PARAM_GROUP, user.get(SessionUserData.KEY_USER_GROUP));
                map.put(ApiParams.PARAM_AUTHENTICATION_KEY, user.get(SessionUserData.KEY_USER_GROUP));
                setDoBidiList(map);
                setAgentBidiList(map);
                setProfileData(map, user.get(SessionUserData.KEY_USER_GROUP));
                getConfigData();
                break;
        }
        return true;
    }
}
