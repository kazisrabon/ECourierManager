/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.firstLayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentList;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.utils.FormValidator;
import com.ks.ecmanager.ecouriermanager.utils.FragmentDialogQRScanner;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ConsignmentListInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin.sessionUserData;

public class ActivityMain extends ActivityBase {

    private final String TAG = "Main";
    private final String[] finalSearchValues = new String[3];
    private String searchTypeValue = "";
    private String[] searchValues;
    private EditText mSearchValue;
    private Button mSearch, mQRScan;
    private ArrayList<String> searchValuesArray;
    private Spinner searchValueSpinner;
    private ArrayAdapter<String> adapterSpinner;
    private HashMap<String, String> user = new HashMap<String, String>();
    private List<ConsignmentListDatum> consignmentListDatumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (!sessionUserData.getSessionDetails().isEmpty())
//            printHash(sessionUserData.getSessionDetails());

        user = sessionUserData.getSessionDetails();

        initialize();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    showErrorToast("You must provide access to use this app!", Toast.LENGTH_SHORT, MIDDLE);
                }
                return;
            }
        }
    }

    private void initialize() {
        finalSearchValues[0] = ApiParams.TAG_CONSIGNMENT_NO;
        finalSearchValues[1] = ApiParams.TAG_RECEIVER_NO;
        finalSearchValues[2] = ApiParams.TAG_PRODUCT_ID;
        mSearchValue = (EditText) findViewById(R.id.editSearchValue);
        mSearch = (Button) findViewById(R.id.btnSearch);
        mQRScan = (Button) findViewById(R.id.btnScan);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonClick();
            }
        });

        mQRScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanButtonClick();
            }
        });

        searchValuesArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.searchValuesArray)));
        searchValues = new String[searchValuesArray.size()];
        searchValues = searchValuesArray.toArray(searchValues);
        searchValueSpinner = (Spinner) findViewById(R.id.spinnerSearchValue);
        adapterSpinner = new ArrayAdapter<String>(this, R.layout.spinner_item, R.id.textSearchValue, searchValues);
        searchValueSpinner.setAdapter(adapterSpinner);
        searchValueSpinner.setSelection(0);
        searchValueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    searchTypeValue = finalSearchValues[position];
                    mSearchValue.setHint(searchTypeValue.toUpperCase());

                } else {
                    searchTypeValue = finalSearchValues[0];
                    mSearchValue.setHint(searchTypeValue.toUpperCase());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchTypeValue = finalSearchValues[0];
                mSearchValue.setHint(searchTypeValue.toUpperCase());
            }
        });
    }

    private void searchButtonClick() {
        String search_value = mSearchValue.getText().toString();

        if (!FormValidator.isValidField(search_value)) {
            mSearchValue.setError(getResources().getString(R.string.empty_field));
        } else {
            searchConsignment(search_value);
        }
    }

    private void scanButtonClick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            FragmentManager fm = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fm = this.getFragmentManager();
            }
            @SuppressLint("ValidFragment")
            FragmentDialogQRScanner fragmentDialogQRScanner = new FragmentDialogQRScanner() {

                @Override
                public void success(String value, String format_type) {
                    searchConsignment(value);
                }

                @Override
                public void error() {

                }
            };
            fragmentDialogQRScanner.setArgs("", "", "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fragmentDialogQRScanner.show(fm, "fragment_qr_scanner");
            }
        } else {
            getRequiredPermission();
        }
    }

    private void searchConsignment(String search_value) {
        showProgressDialog(false, "", getResources().getString(R.string.loading));
//        getSearchTypeValue();

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        ConsignmentListInterface myApiCallback = restAdapter.create(ConsignmentListInterface.class);

        // get user data from session
        String user_type = user.get(SessionUserData.KEY_USER_TYPE);
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        //Lets pass the desired parameters
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
        map.put(searchTypeValue, "" + search_value);

        printHash(map);
        myApiCallback.getData(ApiParams.TAG_CONSIGNMENT_SEARCH_KEY, map, new Callback<ConsignmentList>() {
            @Override
            public void success(ConsignmentList consignment_list, Response response) {
                hideProgressDialog();

                boolean status = consignment_list.getStatus();
                Log.e(TAG, status+" ");
                Log.e(TAG, consignment_list.getMsg());
                if (status == ApiParams.TAG_SUCCESS) {
                    mSearchValue.setText("");
                    consignmentListDatumList = consignment_list.getData();

// convert the list to array list and pass via intent
//                    ArrayList<ConsignmentListDatum> ItemArray = ((ArrayList<ConsignmentListDatum>) consignmentListDatumList);
////                    ConsignmentListDatum datum = ItemArray.get(0);
//                    Intent i = new Intent(ActivityMain.this, ActivityConsignmentDetails.class);
//                    i.putExtra("is_search", true);
//                    i.putExtra("visible_state", "0");
//                    i.putExtra("consignment_data_position", "0");
//                    i.putExtra("consignment_data_array", ItemArray);
////                    i.putExtra("consignment_data", datum);
//                    i.putExtra("where_from", "search");
//                    startActivity(i);

                    showToast("" + consignment_list.getStatus() + "!", Toast.LENGTH_SHORT, MIDDLE);
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

    private void getRequiredPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            showErrorToast("You must provide access to use this app!", Toast.LENGTH_SHORT, MIDDLE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
        }
    }
}
