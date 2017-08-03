/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.thirdLayer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.adapters.MultipleSearchValueAdapter;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.CN_TYPE_MULTIPLE;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_CN_DATA;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_WHERE_FROM;

public class ActivityMultipleList extends Activity {
    private Context context;
    private Button btnUpdate, btnCancle;
    private TextView tvCS, tvNS, tvAgent, tvDo;
    private ListView listView;
    private MultipleSearchValueAdapter listItemAdapter;
    private List<ConsignmentListDatum> myList;
    private HashMap<String, String> statusDetails = new HashMap<>();
    private HashMap<String, String> userDetails = new HashMap<>();
    private HashMap<String, String> map = new HashMap<>();
    private String id = "", group = "", authentication_key = "", nextStatus = "", currentStatus = "", agentId = "", doId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_list);
        context = ActivityMultipleList.this;

    }

    private void init(Context context){
        tvCS = (TextView) findViewById(R.id.tvCS);
        tvNS = (TextView) findViewById(R.id.tvNS);
        tvAgent = (TextView) findViewById(R.id.tvAgent);
        tvDo = (TextView) findViewById(R.id.tvDo);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancle = (Button) findViewById(R.id.btnCancle);
        listView = (ListView) findViewById(R.id.listView);
    }

    private void receiveDataFromIntent() {
        // receives data from intent and and serialize it back to a list
        if (getIntent().getExtras() != null) {
            String where_from = getIntent().getStringExtra(KEY_WHERE_FROM);
            if(where_from != null && !where_from.isEmpty()){
                if (where_from.equals(CN_TYPE_MULTIPLE)){
                    myList = (ArrayList<ConsignmentListDatum>) getIntent().getSerializableExtra(KEY_CN_DATA);
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

    private void setDetails(ConsignmentListDatum datum) {
        tvCS.setText(currentStatus);
        tvNS.setText(nextStatus);
        tvAgent.setText(agentId);
        tvDo.setText(doId);
        listItemAdapter = new MultipleSearchValueAdapter(context, myList);
        listView.setAdapter(listItemAdapter);
    }
}
