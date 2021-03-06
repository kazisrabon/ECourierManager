/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.secondLayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ks.ecmanager.ecouriermanager.adapters.ListItemAdapter.list_clicked_value;

public class ActivityDelivery extends ActivityBase {
    private Button okBtn, cancleBtn;
    private ListView listView;
    private Context context;
    private ListItemAdapter listItemAdapter;
    private List<ListDatum> listData;
    private String id = "", group = "", authentication_key = "";
    private HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        user = SessionUserData.getSFInstance(this).getSessionDetails();
        id = user.get(SessionUserData.KEY_USER_ID);
        group = user.get(SessionUserData.KEY_USER_GROUP).toLowerCase();
        authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        listView = (ListView) findViewById(R.id.listView);
        context = ActivityDelivery.this;
        getIntentValue();
        okBtn = (Button) findViewById(R.id.footer_1);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String where_from = getIntent().getStringExtra(KEY_DELIVERY);
                if (stringNotNullCheck(list_clicked_value)) {
                    if (where_from.contains("1"))
                        refreshActivityforStatus();
                    else if (where_from.contains("2"))
                        refreshActivityforAgent();
                    else if (where_from.contains("3")) {
                        showBlackToast("ActivityMultipleScan", Toast.LENGTH_SHORT, MIDDLE);
//                    startActivity(new Intent(ActivityDelivery.this, ActivityMultipleScan.class));
                    }
                    else
                        showErrorToast("Something is wrong!!!", Toast.LENGTH_SHORT, MIDDLE);
                }
                else
                    showErrorToast("Please click...", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
        cancleBtn = (Button) findViewById(R.id.footer_2);
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Cancled...", Toast.LENGTH_SHORT, MIDDLE);
            }
        });
    }

    public void refreshActivityforAgent() {
        if (getCheckChangeDOs()) {
            Intent refresh = new Intent(ActivityDelivery.this, ActivityDelivery.class);
            refresh.putExtra(KEY_DELIVERY, "3");
            startActivity(refresh);
            ActivityDelivery.this.finish();
        } else {
            showBlackToast("ActivityMultipleScan", Toast.LENGTH_SHORT, MIDDLE);
//                        startActivity(new Intent(ActivityDelivery.this, ActivityMultipleScan.class));
        }
    }

    public void refreshActivityforStatus() {
        setCheckChangeAgents(list_clicked_value);
        setCheckChangeDOs(list_clicked_value);
        if (getCheckChangeAgents()) {
            Intent refresh = new Intent(ActivityDelivery.this, ActivityDelivery.class);
            refresh.putExtra(KEY_DELIVERY, "2");
            startActivity(refresh);
            ActivityDelivery.this.finish();
        } else if (getCheckChangeDOs()) {
            Intent refresh = new Intent(ActivityDelivery.this, ActivityDelivery.class);
            refresh.putExtra(KEY_DELIVERY, "3");
            startActivity(refresh);
            ActivityDelivery.this.finish();
        } else {
            showBlackToast("ActivityMultipleScan", Toast.LENGTH_SHORT, MIDDLE);
//                                startActivity(new Intent(ActivityDelivery.this, ActivityMultipleScan.class));
        }
    }

    private void getIntentValue(){
        if (getIntent().getExtras() != null){
            String where_from = getIntent().getStringExtra(KEY_DELIVERY);
            if (where_from.contains("1"))
                loadStatus("1");
            else if (where_from.contains("2"))
                loadAgent("2");
            else if (where_from.contains("3"))
                loadDO("3");
            else
                showErrorToast(""+getString(R.string.empty_field), Toast.LENGTH_SHORT, MIDDLE);
        }
    }

    private void loadDO(String from_where) {
        listData = new ArrayList<>();
        ListDatum listDatum;
        List<DOListDatum> doListData = db.getAllDOs();
        for (DOListDatum doListDatum : doListData){
            listDatum = new ListDatum(doListDatum.getId(), doListDatum.getValue());
            listData.add(listDatum);
        }
        listItemAdapter = new ListItemAdapter(ActivityDelivery.this, listData, from_where);
        listView.setAdapter(listItemAdapter);
    }

    private void loadAgent(String from_where) {
        listData = new ArrayList<>();
        ListDatum listDatum;
//        List<AgentListDatum> agentListData = db.getAllAgents();
        HashMap<String, String> mapData = getElligibleAgent();
        for (String key : mapData.keySet()) {
            if (stringNotNullCheck(mapData.get(key))) {
                listDatum = new ListDatum(key, mapData.get(key));
                listData.add(listDatum);
            }
            else {
                listDatum = new ListDatum(key, key);
                listData.add(listDatum);
            }
        }
//        for (AgentListDatum agentListDatum : agentListData){
//            listDatum = new ListDatum(agentListDatum.getAgent_id(), agentListDatum.getAgent_name());
//            listData.add(listDatum);
//        }
        listItemAdapter = new ListItemAdapter(ActivityDelivery.this, listData, from_where);
        listView.setAdapter(listItemAdapter);
    }

    private void loadStatus(String from_where) {
        listData = db.getAllSpecificStatusforGroup(group);
        listItemAdapter = new ListItemAdapter(ActivityDelivery.this, listData, from_where);
        listView.setAdapter(listItemAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                HashMap<String, String> map = new HashMap<>();
                map.put(ApiParams.PARAM_ADMIN_ID, user.get(SessionUserData.KEY_USER_ID));
                map.put(ApiParams.PARAM_GROUP, user.get(SessionUserData.KEY_USER_GROUP));
                map.put(ApiParams.PARAM_AUTHENTICATION_KEY, user.get(SessionUserData.KEY_USER_GROUP));
                setDoBidiList(map);
                setAgentBidiList(map);
                setProfileData(map, user.get(SessionUserData.KEY_USER_GROUP));
                getConfigData();
                break;
        }
        return true;
    }
}
