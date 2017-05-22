/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;

import java.util.HashMap;

/**
 * Created by Kazi Srabon on 5/20/2017.
 */

public class ActivityBase extends AppCompatActivity {

    public static final int MIDDLE = 2;
    public static final int START = 1;
    public static final int END = 3;
    public static final int CAMERA_PERMISSIONS_REQUEST = 301,
            SELECT_PICTURE = 302,
            TAKE_PHOTO = 303,
            REQUEST_EXTERNAL_STORAGE = 401,
            REQUEST_READ_EXTERNAL_STORAGE = 402,
            calender_type = 110;
    private final String TAG = "Base";
    private Activity activity;
    private Context context;
    private ProgressDialog progressDialog;

    public ActivityBase() {
        this.activity = ActivityBase.this;
        this.context = ActivityBase.this;

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

    public void printHash(String where_from ,HashMap<String, String> map){
        for (String name: map.keySet()){
            String key =name.toString();
            String value = map.get(name).toString();
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
}
