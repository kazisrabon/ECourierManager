/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.secondLayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.pojo.AgentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.DOListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ParcelList;
import com.ks.ecmanager.ecouriermanager.pojo.ResponseList;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ParcelStatusUpdateInterface;

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
 * Created by Kazi Srabon on 7/10/2017.
 */

public class ActivityConsignment extends ActivityBase {
    private static final String TAG = "ActivityConsignment";
    TextView tvECR, tvStatus, tvAgent, tvDO, tvReceiver, tvReceiverMobile, tvAddress, tvProductPrice;
    Button btnDetails, btnUpdate;
    private Intent intent;
    private HashMap<String, String> user = new HashMap<String, String>();
    private HashMap<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_consignment);
        setHash();
        initView();
        receiveDataFromIntent();
    }

    private void setHash() {
        user = sessionUserData.getSessionDetails();
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        //Lets pass the desired parameters
        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
        map.put(ApiParams.PARAM_COMMENT, getString(R.string.testing));
    }

    private void initView() {
        tvECR = (TextView) findViewById(R.id.tvEcrNumber);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvAgent = (TextView) findViewById(R.id.tvAgent);
        tvDO = (TextView) findViewById(R.id.tvDo);
        tvReceiver = (TextView) findViewById(R.id.tvReceiver);
        tvReceiverMobile = (TextView) findViewById(R.id.tvReceiverMobile);
        tvAddress = (TextView) findViewById(R.id.tvReceiverAddress);
        tvProductPrice = (TextView) findViewById(R.id.tvReceiverPrice);
        btnDetails = (Button) findViewById(R.id.btnDetails);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCN();
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDetails();
            }
        });
    }

    private void gotoDetails() {
        startActivity(intent);
    }

    public String receiveStatusChangeValue(){
        ArrayList<String> keys;
        ArrayList<String> values;
        keys = new ArrayList<>();
        values = new ArrayList<>();
        List<ResponseList> responseListData = db.getAllStatus();
        for (ResponseList responseListDatum : responseListData){
            keys.add(responseListDatum.getStatus());
            values.add(responseListDatum.getReadable_status());
            Log.e("db config", responseListDatum.getStatus()+" "+responseListDatum.getReadable_status());
        }
        showListInPopUp(ActivityConsignment.this, createBidiMap(keys, values));
        String changedStatus = ActivityBase.CHANGED_VALUE;
        Log.e("Changed status", ""+changedStatus);
        return changedStatus;
    }

    public String receiveAgentChangeValue(){
        ArrayList<String> keys;
        ArrayList<String> values;
        keys = new ArrayList<>();
        values = new ArrayList<>();
        List<AgentListDatum> agentListData = db.getAllAgents();
        for (AgentListDatum agentListDatum : agentListData){
            keys.add(agentListDatum.getAgent_id());
            values.add(agentListDatum.getAgent_name());
            Log.e("db agent", agentListDatum.getAgent_id()+" "+agentListDatum.getAgent_name());
        }
        showListInPopUp(ActivityConsignment.this, createBidiMap(keys, values));
        String changedAgent = ActivityBase.CHANGED_VALUE;
        Log.e("Changed agent", ""+changedAgent);
        return changedAgent;
    }

    public String receiveDOChangeValue(){
        ArrayList<String> keys;
        ArrayList<String> values;
        keys = new ArrayList<>();
        values = new ArrayList<>();
        List<DOListDatum> doListData = db.getAllDOs();
        for (DOListDatum doListDatum : doListData){
            keys.add(doListDatum.getId());
            values.add(doListDatum.getValue());
            Log.e("db do", doListDatum.getId()+" "+doListDatum.getValue());
        }
        showListInPopUp(ActivityConsignment.this, createBidiMap(keys, values));
        String changedDO = ActivityBase.CHANGED_VALUE;
        Log.e("Changed do", ""+changedDO);
        return changedDO;
    }

    private void updateCN() {
        String mChangedStatus = "";
        String mChangedAgent = "";
        String mChangedDO = "";
        String accessLevel = accessLevel("3", "41");
//            status can changeable check
        if (accessLevel.contains("1")){
            mChangedStatus = receiveStatusChangeValue();
            Log.e("changed status ", ""+mChangedAgent);
            if (stringNotNullCheck(mChangedStatus)){
                map.put(ApiParams.PARAM_STATUS, "" + mChangedStatus);
//                is status canceled
                if (mChangedStatus.equals(getString(R.string.s12))){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    map.put(ApiParams.PARAM_CANCEL_CALL_TIME, currentDateandTime);
                }
//                agent can changeable for status check
                if (accessLevel.contains("2")){
                    mChangedAgent = receiveAgentChangeValue();
                    if (stringNotNullCheck(mChangedAgent)){
                        map.put(ApiParams.PARAM_RETURN_AGENT, "" + mChangedAgent);
//                        do can changeable for status & agent
                        if (accessLevel.contains("3")){
                            mChangedDO = receiveDOChangeValue();
                            if (stringNotNullCheck(mChangedDO)){
                                map.put(ApiParams.PARAM_D_DO, "" + mChangedDO);
//                                parcelStatusUpdate(map);
                            }
//                            else
//                                parcelStatusUpdate(map);
                        }
//                        else
//                            parcelStatusUpdate(map);
                    }
                }
//                do can changeable for status
                else if (accessLevel.contains("3")){
                    mChangedDO = receiveDOChangeValue();
                    if (stringNotNullCheck(mChangedDO)){
                        map.put(ApiParams.PARAM_D_DO, "" + mChangedDO);
                    }
//                    parcelStatusUpdate(map);
                }

//                else
//                    parcelStatusUpdate(map);
            }
        }

        else if (accessLevel.contains("2")){
            if (!accessLevel.contains("1")){
                mChangedAgent = receiveAgentChangeValue();
                if (stringNotNullCheck(mChangedAgent)){
                    map.put(ApiParams.PARAM_RETURN_AGENT, "" + mChangedAgent);
                    if (accessLevel.contains("3")){
                        mChangedDO = receiveDOChangeValue();
                        if (stringNotNullCheck(mChangedDO)){
                            map.put(ApiParams.PARAM_D_DO, "" + mChangedDO);
                        }
                    }
                }
            }
        }

        else if (accessLevel.equals("3")){
            mChangedDO = receiveDOChangeValue();
            if (stringNotNullCheck(mChangedDO)){
                map.put(ApiParams.PARAM_D_DO, "" + mChangedDO);
            }
        }
        else {
            showErrorToast("Have no access!!!", Toast.LENGTH_SHORT, END);
        }
        Log.e(TAG+"", mChangedStatus +" "+ mChangedAgent +" "+ mChangedDO);
        parcelStatusUpdate(map);
    }

    private void receiveDataFromIntent() {
        // receives data from intent and and serialize it back to a list
        if (getIntent().getExtras() != null) {
            String where_from = getIntent().getStringExtra(KEY_WHERE_FROM);
            if(where_from != null && !where_from.isEmpty()){
                if (where_from.equals("list")){
//                    datum =(ConsignmentListDatum) getIntent().getSerializableExtra("consignment_data");
//                    setConsignmentDetails(datum);
                }
                else if (where_from.equals(CN_TYPE_SEARCH)){
                    int position = getIntent().getIntExtra(KEY_CN_POS, 0);
                    ArrayList<ConsignmentListDatum> myList = (ArrayList<ConsignmentListDatum>) getIntent().getSerializableExtra(KEY_CN_DATA);
                    setConsignmentDetails(myList.get(position));

                    intent = new Intent(ActivityConsignment.this, ActivityConsignmentDetails.class);
                    intent.putExtra(KEY_CN_POS, 0);
                    intent.putExtra(KEY_CN_DATA, myList);
                    intent.putExtra(KEY_WHERE_FROM, CN_TYPE_SEARCH);
                }
            }
        }
    }

    private void setConsignmentDetails(ConsignmentListDatum consignmentListDatum) {
        if (stringNotNullCheck(consignmentListDatum.getConsignment_no())) {
            tvECR.setText(consignmentListDatum.getConsignment_no());
            map.put(ApiParams.PARAM_CONSIGNMENT_NO, "" + consignmentListDatum.getConsignment_no());
        }

//        Log.e("statuscode from service",""+consignmentListDatum.getStatus_code());
//        Log.e("db config status", db.getReadableStatus(consignmentListDatum.getStatus_code()));
//        if (db.getAllStatus() != null){
//            List<ResponseList> responseListData = db.getAllStatus();
//            for (ResponseList responseListDatum : responseListData){
//                Log.e("db config", responseListDatum.getStatus()+" "+responseListDatum.getReadable_status());
//            }
//        }
        if (stringNotNullCheck(consignmentListDatum.getStatus_code())) {
            tvStatus.setText(String.format(getString(R.string.status), db.getReadableStatus(consignmentListDatum.getStatus_code())));
            map.put(ApiParams.PARAM_STATUS, "" + consignmentListDatum.getStatus_code());
        }

        if (stringNotNullCheck(consignmentListDatum.getDelivery_agent())){
            Log.e(""+TAG, consignmentListDatum.getDelivery_agent());
            AgentListDatum agentListDatum = db.getAgent(consignmentListDatum.getDelivery_agent());

            if (agentListDatum != null){
                Log.e(TAG, agentListDatum.getAgent_id()
                        +" "+ agentListDatum.getAgent_name()
                        +" "+ agentListDatum.getDo_name()
                        +" "+ agentListDatum.getDo_id());
            }
            String s = "";
            if (agentListDatum != null) {
                s = agentListDatum.getAgent_name();
            }
            else s= getResources().getString(R.string.no_agent_found);
            tvAgent.setText(String.format(getString(R.string.delivery_agent), s));
        }
        else
            tvAgent.setText(String.format(getString(R.string.delivery_agent), getString(R.string.none)));

        if (stringNotNullCheck(consignmentListDatum.getDestination_do())){
            DOListDatum doListDatum= db.getDo(consignmentListDatum.getDestination_do());

            if (doListDatum != null){
                Log.e(TAG, doListDatum.getId()
                        +" "+ doListDatum.getValue());
            }
            String s = "";
            if (doListDatum != null) {
                s = doListDatum.getValue();
            }
            else s= getResources().getString(R.string.no_do_found);
            tvDO.setText(String.format(getString(R.string.destination_do), s));
        }

        else
            tvDO.setText(String.format(getString(R.string.destination_do), getString(R.string.none)));

        if (stringNotNullCheck(consignmentListDatum.getEso()))
            tvReceiver.setText(String.format(getString(R.string.recipient_name), consignmentListDatum.getEso()));

        if (stringNotNullCheck(consignmentListDatum.getEso_mobile()))
            tvReceiverMobile.setText(String.format(getString(R.string.recipient_mobile), consignmentListDatum.getEso_mobile()));

        if (stringNotNullCheck(consignmentListDatum.getRecipient_address()))
            tvAddress.setText(String.format(getString(R.string.recipient_address), consignmentListDatum.getRecipient_address()));

        if (stringNotNullCheck(consignmentListDatum.getProduct_price()))
            tvProductPrice.setText(String.format(getString(R.string.product_price), consignmentListDatum.getProduct_price()));

    }

    private void parcelStatusUpdate(HashMap<String, String> statusMap){
        showProgressDialog(false, "", getResources().getString(R.string.loading));

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        ParcelStatusUpdateInterface myApiCallback = restAdapter.create(ParcelStatusUpdateInterface.class);

        printHash(TAG, statusMap);
//      parcel update api
//        myApiCallback.getData(ApiParams.TAG_PARCEL_STATUS_UPDATE_KEY, statusMap, new Callback<ParcelList>() {
//            @Override
//            public void success(ParcelList parcelList, Response response) {
//                hideProgressDialog();
//
//                boolean status = parcelList.getStatus();
//                Log.e(TAG, status+" ");
//                if (status) {
//                    showSuccessToast(getString(R.string.sucess), Toast.LENGTH_SHORT, END);
////                    reloadCN(map.get(ApiParams.PARAM_CONSIGNMENT_NO));
//                }
//                else
//                    showErrorToast(getString(R.string.no_data_found), Toast.LENGTH_SHORT, MIDDLE);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                hideProgressDialog();
//                showErrorToast("" + error.getMessage() + "!", Toast.LENGTH_SHORT, MIDDLE);
//            }
//        });
    }
}
