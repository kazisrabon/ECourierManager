/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.thirdLayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.firstLayer.Main2Activity;
import com.ks.ecmanager.ecouriermanager.adapters.MultipleSearchValueAdapter;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ParcelList;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ParcelStatusUpdateInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.CN_TYPE_MULTIPLE;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.FROM_BULK;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_CN_DATA;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_ECRS;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_WHERE_FROM;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.db;

public class ActivityMultipleList extends ActivityBase {
    private Context context;
    private Button btnUpdate, btnCancle;
    private TextView tvCS, tvNS, tvAgent, tvDo;
    private ListView listView;
    private MultipleSearchValueAdapter listItemAdapter;
    private List<ConsignmentListDatum> myList;
    private HashMap<String, String> statusDetails = new HashMap<>();
    private HashMap<String, String> userDetails = new HashMap<>();
    private HashMap<String, String> map = new HashMap<>();
    private String ecrs = "", id = "", group = "", authentication_key = "", nextStatus = "", currentStatus = "", agentId = "", doId = "";
    private final String TAG = "ActivityMultipleList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_list);
        context = ActivityMultipleList.this;

        receiveDataFromIntent();
        setHash();
        init(context);
        setDetails();
    }

    private void init(Context context){
        tvCS = (TextView) findViewById(R.id.tvCS);
        tvNS = (TextView) findViewById(R.id.tvNS);
        tvAgent = (TextView) findViewById(R.id.tvAgent);
        tvDo = (TextView) findViewById(R.id.tvDo);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        btnCancle = (Button) findViewById(R.id.btnCancle);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity =  new Intent(ActivityMultipleList.this,Main2Activity.class);
                loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginActivity);
            }
        });
        listView = (ListView) findViewById(R.id.listView);
    }

    private void sendData() {
        if (stringNotNullCheck(agentId)){
            if (nextStatus.equals(getResources().getString(R.string.s25)))
                map.put(ApiParams.PARAM_RETURN_AGENT, agentId);
            else
                map.put(ApiParams.PARAM_AGENT_ID, agentId);
        }
        if (stringNotNullCheck(doId)){
            if (nextStatus.equals(getResources().getString(R.string.s2))
                    || nextStatus.equals(getResources().getString(R.string.s14))
                    || nextStatus.equals(getResources().getString(R.string.s15))
                    || nextStatus.equals(getResources().getString(R.string.s7))
                    )
                map.put(ApiParams.PARAM_D_DO, agentId);
        }
        map.put(ApiParams.PARAM_STATUS, nextStatus);
        map.put(ApiParams.PARAM_CONSIGNMENTS, ecrs);
        map.put(ApiParams.PARAM_COMMENT, "BULK FROM MOBILE");
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        ParcelStatusUpdateInterface myApiCallback = restAdapter.create(ParcelStatusUpdateInterface.class);

        myApiCallback.getData(ApiParams.TAG_BULK, map, new Callback<ParcelList>() {
            @Override
            public void success(ParcelList parcelList, Response response) {
                hideProgressDialog();

                boolean status = parcelList.getStatus();
                Log.e(TAG, status+" ");
                if (status) {
                    Intent loginActivity =  new Intent(ActivityMultipleList.this,Main2Activity.class);
                    loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginActivity);
                }
                else
                    showErrorToast("Please try again!", Toast.LENGTH_SHORT, MIDDLE);
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressDialog();
                showErrorToast("Please try again!", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    private void receiveDataFromIntent() {
        // receives data from intent and and serialize it back to a list
        if (getIntent().getExtras() != null) {
            String where_from = getIntent().getStringExtra(KEY_WHERE_FROM);
            if(where_from != null && !where_from.isEmpty()){
                if (where_from.equals(CN_TYPE_MULTIPLE)){
                    myList = (ArrayList<ConsignmentListDatum>) getIntent().getSerializableExtra(KEY_CN_DATA);
                    ecrs = getIntent().getStringExtra(KEY_ECRS);
//                    for (ConsignmentListDatum listDatum: myList) {
//                        setConsignmentDetails(listDatum);
//                    }
                }
            }
        }
    }

    private void setHash() {
        statusDetails = SessionUserData.getSFInstance(context).getStatusDetails();
        userDetails = SessionUserData.getSFInstance(context).getSessionDetails();
        id = userDetails.get(SessionUserData.KEY_USER_ID);
        group = userDetails.get(SessionUserData.KEY_USER_GROUP);
        authentication_key = userDetails.get(SessionUserData.KEY_USER_AUTH_KEY);
        currentStatus = statusDetails.get(SessionUserData.KEY_STATUS);
        nextStatus = statusDetails.get(SessionUserData.KEY_NEXT_STATUS);
        agentId = statusDetails.get(SessionUserData.KEY_AGENT_ID);
        doId = statusDetails.get(SessionUserData.KEY_DO_ID);

        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
    }

    private void setDetails() {
        String[] strings;
        StringBuilder status = new StringBuilder();
        String loopDelim = "", delim = ",";
        strings = currentStatus.split(",");
        for (String s : strings){
            status.append(loopDelim);
            status.append(db.getReadableStatus(s));
            loopDelim = delim;
        }
        tvCS.setText(status);
        tvNS.setText(db.getReadableStatus(nextStatus));
        if (agentId.equals(""))
            tvAgent.setVisibility(View.GONE);
        else
            tvAgent.setText(db.getAgentName(agentId));

        if (doId.equals(""))
            tvDo.setVisibility(View.GONE);
        else
            tvDo.setText(db.getDoName(doId));
        listItemAdapter = new MultipleSearchValueAdapter(context, myList, FROM_BULK);
        listView.setAdapter(listItemAdapter);
    }
}
