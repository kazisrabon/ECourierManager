/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.thirdLayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentList;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ConsignmentListInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ActivityMultipleScan  extends ActivityBase implements ZXingScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";
    private HashMap<String, String> map = new HashMap<>();
    private ZXingScannerView mScannerView;
    private Context context;
    private StringBuilder scannedECR;
    private String loopDelim = "", delim = ",", nextStatus = "";
    HashMap<String, String> statusDetails;
    HashMap<String, String> userDetails;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_multiple_scan);
        context = ActivityMultipleScan.this;
        setHash();
        scannedECR = new StringBuilder();

//        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    private void setHash() {
        statusDetails = SessionUserData.getSFInstance(context).getStatusDetails();
        userDetails = SessionUserData.getSFInstance(context).getSessionDetails();
        String id = userDetails.get(SessionUserData.KEY_USER_ID);
        String group = userDetails.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = userDetails.get(SessionUserData.KEY_USER_AUTH_KEY);
        nextStatus = statusDetails.get(SessionUserData.KEY_NEXT_STATUS);
        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        // You can optionally set aspect ratio tolerance level
        // that is used in calculating the optimal Camera preview size
        mScannerView.startCamera();

//        mScannerView.setFlash(false);
        mScannerView.setAutoFocus(true);
        mScannerView.setKeepScreenOn(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, false);
    }

    @Override
    public void handleResult(final Result rawResult) {
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage("Your Selection is "+rawResult.getText());
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkValidity(rawResult.getText().trim())){
                    scannedECR.append(loopDelim);
                    scannedECR.append(rawResult.getText().trim());
                    loopDelim = delim;
                    String status = String.valueOf(scannedECR);
                    dialog.dismiss();
                }
                else {
                    dialog.dismiss();
                }
            }
        });
        alertDialog.show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ActivityMultipleScan.this);
            }
        }, 2000);
    }

    private boolean checkValidity(String ecr) {
        final boolean[] b = {false};
        map.put(ApiParams.PARAM_CONSIGNMENT_NO, ecr);
        showProgressDialog(false, "", getResources().getString(R.string.loading));
//        getSearchTypeValue();

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        ConsignmentListInterface myApiCallback = restAdapter.create(ConsignmentListInterface.class);

        myApiCallback.getData(ApiParams.TAG_CONSIGNMENT_SEARCH_KEY, map, new Callback<ConsignmentList>() {
            @Override
            public void success(ConsignmentList consignment_list, Response response) {
                hideProgressDialog();

                boolean status = consignment_list.getStatus();

                Log.e(FLASH_STATE, status+" ");
                if (status) {
                    showSuccessToast(""+status, Toast.LENGTH_SHORT, END);
                    List<ConsignmentListDatum> consignmentListDatumList;
                    consignmentListDatumList = consignment_list.getData();
                    if (consignmentListDatumList.get(0).getStatus_code().contains(nextStatus))
                        b[0] = true;
                    else
                        showErrorToast("ECR not added", Toast.LENGTH_SHORT, MIDDLE);
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

        return b[0];
    }

    public void gotoList() {

    }
}