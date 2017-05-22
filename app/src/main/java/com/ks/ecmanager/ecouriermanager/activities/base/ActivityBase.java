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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Kazi Srabon on 5/20/2017.
 */

public class ActivityBase extends AppCompatActivity {

    public static final String ALBUM_NAME = "eCourier";
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
    public static Bitmap mBitmap = null;
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
}
