/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.initLayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.firstLayer.ActivityMain;
import com.ks.ecmanager.ecouriermanager.pojo.Login;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.utils.FormValidator;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.LoginInterface;

import java.io.File;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ActivityLogin extends ActivityBase {

    public static int isLoggedIN;
    private String name, password;
    private Button mSubmit;
    private TextInputEditText mName, mPassword;
    public static SessionUserData sessionUserData;
    private final String TAG = "LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionUserData = new SessionUserData(this);
//        redirect
        if (sessionUserData.isLoggedIn())
//            isLoggedIN = 1;
            startActivity(new Intent(ActivityLogin.this, ActivityMain.class));

        mName = (TextInputEditText) findViewById(R.id.editUserName);
        mPassword = (TextInputEditText) findViewById(R.id.editPassword);

        mSubmit = (Button) findViewById(R.id.btnSubmit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = mName.getText().toString();
                password = mPassword.getText().toString();

                if (!FormValidator.isValidField(name)) {
                    mName.setError(getResources().getString(R.string.empty_field));
                } else if (!FormValidator.isValidField(password)) {
                    mPassword.setError(getResources().getString(R.string.empty_field));
                } else {
                    verifyUser();
                }
            }
        });
    }

    private void verifyUser() {

        showProgressDialog(false, "", getResources().getString(R.string.loading));

        //Retrofit section start from here...
        //create an adapter for retrofit with base url
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        //creating a service for adapter with our ApiCallback class
        LoginInterface myLoginInterface = restAdapter.create(LoginInterface.class);

        //Lets pass the desired parameters
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(ApiParams.PARAM_USER_NAME, name);
        map.put(ApiParams.PARAM_PASSWORD, password);
        map.put(ApiParams.PARAM_USER_TYPE, ApiParams.USER_TYPE_ADMIN);
        printHash(TAG, map);

        //Now, we will to call for response
        //Retrofit using gson for JSON-POJO conversion
        myLoginInterface.getResult(ApiParams.TAG_LOGIN_KEY, map, new Callback<Login>() {
            @Override
            public void success(Login loginModel, Response response) {
                //we get json object from server to our POJO or model class

                hideProgressDialog();

                boolean status = loginModel.getStatus();
                Log.e(TAG, status+"");
                if (status == ApiParams.TAG_SUCCESS) {

                    deleteCache(ActivityLogin.this);

                    sessionUserData.createUserInfo(
                            ApiParams.USER_TYPE_USER,
                            loginModel.getAdmin_id(),
                            password,
                            loginModel.getGroup(),
                            loginModel.getAuthentication_key(),
                            loginModel.getDo_name(),
                            loginModel.getDo_mobile()
                    );
                    isLoggedIN = 0;
                    Intent i = new Intent(ActivityLogin.this, ActivityMain.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
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

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

        AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
        alert_box.setTitle(getResources().getString(R.string.exit_title));
        alert_box.setMessage(getResources().getString(R.string.exit_confirmation));

        alert_box.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
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
}