/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.firstLayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin;
import com.ks.ecmanager.ecouriermanager.pojo.ProfileList;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ConsignmentListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ProfileListInterface;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin.sessionUserData;

public class ActivityProfile extends ActivityBase {

    private final String TAG = "PROFILE";
    private HashMap<String, String> user = new HashMap<String, String>();
    private TextView txtName, txtJoinDate, txtZone, txtBG, txtDOB, txtNID;
    private CircleImageView imgProfilePic;
    private ProgressDialog mProgressDialog;
    private FloatingActionButton btnSelectImage;
    private Button btnTakePhoto, btnOpenGallery;
    private AlertDialog b;
    private File mPhoto;
    private String path = "";
    private ArrayList<String> image_list=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = sessionUserData.getSessionDetails();
        initialize();
        getProfileData();
    }

    private void initialize() {
        mProgressDialog = new ProgressDialog(ActivityProfile.this);
        mProgressDialog.setMessage("Picture Uploading...");
        mProgressDialog.setCancelable(false);
        txtName = (TextView) findViewById(R.id.txtName);
        txtJoinDate = (TextView) findViewById(R.id.txtJoinDate);
        txtZone = (TextView) findViewById(R.id.txtZone);
        txtBG = (TextView) findViewById(R.id.txtBloodGroup);
        txtDOB = (TextView) findViewById(R.id.txtDOB);
        txtNID = (TextView) findViewById(R.id.txtNID);
        imgProfilePic = (CircleImageView) findViewById(R.id.user_profile_photo);
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptEditPP();
            }
        });
        btnSelectImage = (FloatingActionButton) findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptEditPP();
            }
        });
    }

    private void getProfileData() {
        showProgressDialog(false, "", getResources().getString(R.string.loading));

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        ProfileListInterface myApiCallback = restAdapter.create(ProfileListInterface.class);

        // get user data from session
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        //Lets pass the desired parameters
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
        printHash(TAG+" MAP", map);

        myApiCallback.getData(ApiParams.TAG_PROFILE_KEY, map, new Callback<ProfileList>() {
            @Override
            public void success(ProfileList profileList, Response response) {
                //we get json object from server to our POJO or model class

                hideProgressDialog();

                boolean status = profileList.getStatus();
                Log.e(TAG, profileList.getStatus()+"");
                if (status) {
                    Log.e(TAG, profileList.getData().getName()+"");
                    updateUI(profileList);
                    String url = "";
                    url = profileList.getData().getProfilePic();
                    if (checkText(url)){
                        if(url.contains("test."))
                            url = url.replace("test.", "");
                        url = "http://"+url;
                        Log.e(TAG, url);
                        Picasso.with(ActivityProfile.this)
                                .load(url)
                                .skipMemoryCache()
                                .error(R.drawable.ic_profile_pic)
                                .into(imgProfilePic);
                    }
                } else {
                    showErrorToast(getString(R.string.no_data_found), Toast.LENGTH_SHORT, MIDDLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressDialog();
                showErrorToast("" + error.getMessage() + "!", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(ProfileList profileList) {
        if(checkText(profileList.getData().getName()))
            txtName.setText("Name: " + profileList.getData().getName());

        if(checkText(profileList.getData().getJoinDate()))
            txtJoinDate.setText("Joining Date: " + profileList.getData().getJoinDate());

        if (checkText(profileList.getData().getDeliveryZone()))
            txtZone.setText("Zone: " + profileList.getData().getDeliveryZone());

        if(checkText(profileList.getData().getBlood_group()))
            txtBG.setText("Blood Group: "+ profileList.getData().getBlood_group());

        if(checkText(profileList.getData().getDob()))
            txtDOB.setText("Date of Birth: "+ profileList.getData().getDob());

        if (checkText(profileList.getData().getNid()))
            txtNID.setText("*****************");
    }

    private void promptEditPP() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ActivityProfile.this);
        LayoutInflater inflater = ActivityProfile.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_profile_pic, null);
        dialogBuilder.setView(dialogView);
        b = dialogBuilder.create();
        b.setCancelable(true);
        b.show();

        btnTakePhoto = (Button) dialogView.findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.cancel();
                takePhoto();
            }
        });
        btnOpenGallery = (Button) dialogView.findViewById(R.id.btnOpenGallery);
        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.cancel();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Gallery"), SELECT_PICTURE);
            }
        });
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File folder = new File(Environment.getExternalStorageDirectory() + "/"+ALBUM_NAME);

        if(!folder.exists())
        {
            folder.mkdir();
        }
        final Calendar c = Calendar.getInstance();
        path = String.format(Environment.getExternalStorageDirectory() +"/"+ALBUM_NAME+"_%d.jpg", System.currentTimeMillis());
        mPhoto = new File(path);
        Log.e("URI", Uri.fromFile(mPhoto).toString());
        Uri uri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName()+".provider",
                mPhoto);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    mPhoto = new File(path);
                    Log.e(TAG, "Image file name : " + mPhoto.getName());
                    // Set the image in ImageView
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        imgProfilePic.setImageBitmap(mBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mBitmap == null)
                        imgProfilePic.setImageURI(selectedImageUri);
                    Log.e(TAG, "Image file name : " + mPhoto.getName());
                    sendFile(bitmapToFile(getResizedBitmap(mBitmap, 50)));
//                    sendFile(mPhoto);
                }
            }
            if(requestCode == TAKE_PHOTO)
            {
                image_list.add(path);
                new GetImages().execute();
            }
        }
    }

    public File bitmapToFile(Bitmap bitmap){
        try {
            mPhoto.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(mPhoto);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mPhoto;
    }

    private void sendFile(File file){
        long size = getFolderSize(file)/1024;
        Log.e("File Size ", ""+size);
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        //Lets pass the desired parameters
        HashMap<String, String> map = new HashMap<String, String>();

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ApiParams.PARAM_AUTHENTICATION_KEY, authentication_key)
                .addFormDataPart(ApiParams.PARAM_ADMIN_ID, id)
                .addFormDataPart(ApiParams.PARAM_GROUP, group)
                .addFormDataPart("filename", file.getName(),
                        RequestBody.create(MediaType.parse("jpg"), file))
                .build();
        Log.e(TAG, id+" "+group+" "+authentication_key+" "+file.getName());
        String serverURL = "";
        serverURL = ApiParams.TAG_BASE_URL + "/"+ApiParams.TAG_PROFILE_UPDATE_KEY;
        Log.e("Server Url", serverURL);
        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();
        showProgressDialog(false, "", "Sending...");
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Failed");
                hideProgressDialog();
            }
            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                Log.e(TAG, response.message());
                hideProgressDialog();
            }
        });
    }

    private class GetImages extends AsyncTask<Void, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(Void... params)
        {
            for(int i=0; i<image_list.size(); i++)
            {
                mBitmap = rotateBitmapOrientation(path);
                return mBitmap;
            }
            return null;
        }

        protected void onPostExecute(Bitmap bitmap)
        {
            imgProfilePic.setImageBitmap(bitmap);
            sendFile(bitmapToFile(getResizedBitmap(bitmap, 50)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showErrorToast("Cannot open camera", Toast.LENGTH_SHORT, MIDDLE);
                }
            }
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showErrorToast("Cannot write images to external storage", Toast.LENGTH_SHORT, MIDDLE);
                }
            }
            case REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showErrorToast("Cannot read images to external storage", Toast.LENGTH_SHORT, MIDDLE);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() > 0) {

            AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
            alert_box.setTitle(getResources().getString(R.string.exit_title));
            alert_box.setMessage(getResources().getString(R.string.exit_confirmation));

            alert_box.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            sessionUserData.endSession();
                            Intent loginActivity =  new Intent(ActivityProfile.this,ActivityLogin.class);
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
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent mainActivity =  new Intent(ActivityProfile.this,ActivityMain.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainActivity);

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
