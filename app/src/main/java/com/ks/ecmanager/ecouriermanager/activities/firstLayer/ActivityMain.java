/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.firstLayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin;
import com.ks.ecmanager.ecouriermanager.activities.secondLayer.ActivityConsignment;
import com.ks.ecmanager.ecouriermanager.adapters.MultipleSearchValueAdapter;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentList;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.utils.FormValidator;
import com.ks.ecmanager.ecouriermanager.utils.FragmentDialogQRScanner;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ConsignmentListInterface;

import java.net.ContentHandler;
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
    private Button mSearch, mQRScan, mProfile;
    private ArrayList<String> searchValuesArray;
    private Spinner searchValueSpinner;
    private ArrayAdapter<String> adapterSpinner;
    private HashMap<String, String> user = new HashMap<String, String>();
    private HashMap<String, String> map = new HashMap<String, String>();
    private List<ConsignmentListDatum> consignmentListDatumList;
    private AlertDialog b;
    private Context context;
    private SessionUserData sessionUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = ActivityMain.this;
//        if (!sessionUserData.getSessionDetails().isEmpty())
//            printHash(sessionUserData.getSessionDetails());
        sessionUserData = SessionUserData.getSFInstance(context);
        user = sessionUserData.getSessionDetails();
        setHashMap();
        initialize();
        Log.e(TAG, "isLooggedIn "+ sessionUserData.isLoggedIn());
//  //      getConfigData();
//        if (!sessionUserData.isLoggedIn()){
//            showProgressDialog(false, "", getResources().getString(R.string.loading));
//            setDoBidiList(map);
//            setAgentBidiList(map);
//            setProfileData(map, user.get(SessionUserData.KEY_USER_GROUP));
//            getConfigData();
//            sessionUserData.setLoggedIn();
//            hideProgressDialog();
//        }
//        Log.e(TAG, "isLooggedIn "+ sessionUserData.isLoggedIn());
//        if (db == null){
//            db = DatabaseHandler.getInstance(this);
//        }
//        else {
//        }

    }

    private void setHashMap() {
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
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
        consignmentListDatumList = new ArrayList<>();
        finalSearchValues[0] = ApiParams.TAG_CONSIGNMENT_NO;
        finalSearchValues[1] = ApiParams.TAG_RECEIVER_NO;
        finalSearchValues[2] = ApiParams.TAG_PRODUCT_ID;
        mSearchValue = (EditText) findViewById(R.id.editSearchValue);
        mSearch = (Button) findViewById(R.id.btnSearch);
        mQRScan = (Button) findViewById(R.id.btnScan);
        mProfile = (Button) findViewById(R.id.btnProfile);

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

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMain.this, ActivityProfile.class));
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
            fm = this.getFragmentManager();
            @SuppressLint("ValidFragment")
            FragmentDialogQRScanner fragmentDialogQRScanner = new FragmentDialogQRScanner() {

                @Override
                public void success(String value, String format_type) {
                    Log.e("QR value", ""+value);
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

        map.put(searchTypeValue, search_value);

//        printHash(TAG, map);
        myApiCallback.getData(ApiParams.TAG_CONSIGNMENT_SEARCH_KEY, map, new Callback<ConsignmentList>() {
            @Override
            public void success(ConsignmentList consignment_list, Response response) {
                hideProgressDialog();

                boolean status = consignment_list.getStatus();

                Log.e(TAG, status+" ");
                if (status) {
//                    showSuccessToast(""+status, Toast.LENGTH_SHORT, MIDDLE);
                    mSearchValue.setText("");
                    consignmentListDatumList = consignment_list.getData();
//                    promptView(consignmentListDatumList);

                    if (!consignmentListDatumList.isEmpty()) {
                        Log.e(TAG, "consignmentListDatumList is not empty");
                        promptView(consignmentListDatumList);
                    }
                    else showErrorToast("No ECR found!!!", Toast.LENGTH_SHORT, MIDDLE);
//                    if (canECRView(consignmentListDatumList.get(0).getStatus_code(),
//                            consignmentListDatumList.get(0).getSource_do(),
//                            consignmentListDatumList.get(0).getDestination_do())) {
//                        ArrayList<ConsignmentListDatum> ItemArray = ((ArrayList<ConsignmentListDatum>) consignmentListDatumList);
//                        Intent intent = new Intent(ActivityMain.this, ActivityConsignment.class);
//                        intent.putExtra(KEY_CN_POS, 0);
//                        intent.putExtra(KEY_CN_DATA, ItemArray);
//                        intent.putExtra(KEY_WHERE_FROM, CN_TYPE_SEARCH);
//                        sessionUserData.initStatus();
//                        startActivity(intent);
//                    }
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

    private void promptView(List<ConsignmentListDatum> consignmentListDatumList) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ActivityMain.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.simple_list, null);
        dialogBuilder.setView(dialogView);
        b = dialogBuilder.create();
        b.setCancelable(true);
        ListView listView = (ListView) dialogView.findViewById(R.id.list);
        listView.setAdapter(new MultipleSearchValueAdapter(ActivityMain.this, consignmentListDatumList, FROM_INDI));
        b.show();
    }

    private void getRequiredPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            showErrorToast("You must provide access to use this app!", Toast.LENGTH_SHORT, MIDDLE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            Intent main2Activity =  new Intent(ActivityMain.this, Main2Activity.class);
            main2Activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main2Activity);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() > 0) {

            AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
            alert_box.setTitle(getResources().getString(R.string.exit_title));
            alert_box.setMessage(getResources().getString(R.string.logout_confirmation));

            alert_box.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            sessionUserData.endSession();
                            Intent loginActivity =  new Intent(ActivityMain.this,ActivityLogin.class);
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

        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                showToast(getString(R.string.refreshing), Toast.LENGTH_LONG, MIDDLE);
                setDoBidiList(map);
                setAgentBidiList(map);
                break;
        }
        return true;
    }
}
