/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.secondLayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.adapters.ListItemAdapter;
import com.ks.ecmanager.ecouriermanager.pojo.AgentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.DOListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ResponseList;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityDelivery extends ActivityBase {
    private TextView okBtn, cancleBtn;
    private ListView listView;
    private Context context;
    private ListItemAdapter listItemAdapter;
    private List<ListDatum> listData;
    private String id = "", group = "", authentication_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        HashMap<String, String> user = SessionUserData.getSFInstance(this).getSessionDetails();
        id = user.get(SessionUserData.KEY_USER_ID);
        group = user.get(SessionUserData.KEY_USER_GROUP).toLowerCase();
        authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        listView = (ListView) findViewById(R.id.listView);
        context = ActivityDelivery.this;
        getIntentValue();
        okBtn = (TextView) findViewById(R.id.footer_1);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String where_from = getIntent().getStringExtra(KEY_DELIVERY);
                String s = accessLevel("3", "41");
                if (s.contains("2") && !where_from.contains("2")){
                    Intent refresh = new Intent(ActivityDelivery.this, ActivityDelivery.class);
                    refresh.putExtra(KEY_DELIVERY, "2");
                    startActivity(refresh);
                    ActivityDelivery.this.finish();
                }
                else if (s.contains("3") && !where_from.contains("3")){
                    Intent refresh = new Intent(ActivityDelivery.this, ActivityDelivery.class);
                    refresh.putExtra(KEY_DELIVERY, "3");
                    startActivity(refresh);
                    ActivityDelivery.this.finish();
                }
                else {
//                    startActivity(new Intent(ActivityDelivery.this, ActivityMultipleScan.class));
                }
            }
        });
        cancleBtn = (TextView) findViewById(R.id.footer_2);
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Cancled...", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    private void getIntentValue(){
        if (getIntent().getExtras() != null){
            String where_from = getIntent().getStringExtra(KEY_DELIVERY);
            if (where_from.contains("1"))
                loadStatus();
            else if (where_from.contains("2"))
                loadAgent();
            else if (where_from.contains("3"))
                loadDO();
            else
                showErrorToast(""+getString(R.string.empty_field), Toast.LENGTH_SHORT, END);
        }
    }

    private void loadDO() {
        listData = new ArrayList<>();
        ListDatum listDatum;
        List<DOListDatum> doListData = db.getAllDOs();
        for (DOListDatum doListDatum : doListData){
            listDatum = new ListDatum(doListDatum.getId(), doListDatum.getValue());
            listData.add(listDatum);
        }
        listItemAdapter = new ListItemAdapter(ActivityDelivery.this, listData);
        listView.setAdapter(listItemAdapter);
    }

    private void loadAgent() {
        listData = new ArrayList<>();
        ListDatum listDatum;
        List<AgentListDatum> agentListData = db.getAllAgents();
        for (AgentListDatum agentListDatum : agentListData){
            listDatum = new ListDatum(agentListDatum.getAgent_id(), agentListDatum.getAgent_name());
            listData.add(listDatum);
        }
        listItemAdapter = new ListItemAdapter(ActivityDelivery.this, listData);
        listView.setAdapter(listItemAdapter);
    }

    private void loadStatus() {
        listData = db.getAllSpecificStatusforGroup(group);
        listItemAdapter = new ListItemAdapter(ActivityDelivery.this, listData);
        listView.setAdapter(listItemAdapter);
    }
}
