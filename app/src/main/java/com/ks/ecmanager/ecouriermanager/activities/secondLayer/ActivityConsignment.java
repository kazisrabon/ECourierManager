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

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.pojo.AgentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.DOListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ResponseList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kazi Srabon on 7/10/2017.
 */

public class ActivityConsignment extends ActivityBase {
    private static final String TAG = "ActivityConsignment";
    TextView tvECR, tvStatus, tvAgent, tvDO, tvReceiver, tvReceiverMobile, tvAddress, tvProductPrice;
    Button btnDetails, btnUpdate;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_consignment);
        initView();
        receiveDataFromIntent();
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

    private void updateCN() {

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
        if (stringNotNullCheck(consignmentListDatum.getConsignment_no()))
            tvECR.setText(consignmentListDatum.getConsignment_no());

//        Log.e("statuscode from service",""+consignmentListDatum.getStatus_code());
//        Log.e("db config status", db.getReadableStatus(consignmentListDatum.getStatus_code()));
//        if (db.getAllStatus() != null){
//            List<ResponseList> responseListData = db.getAllStatus();
//            for (ResponseList responseListDatum : responseListData){
//                Log.e("db config", responseListDatum.getStatus()+" "+responseListDatum.getReadable_status());
//            }
//        }
        if (stringNotNullCheck(consignmentListDatum.getStatus_code()))
            tvStatus.setText(String.format(getString(R.string.status), db.getReadableStatus(consignmentListDatum.getStatus_code())));

        if (stringNotNullCheck(consignmentListDatum.getDelivery_agent())){
            Log.e(""+TAG, consignmentListDatum.getDelivery_agent());
            AgentListDatum agentListDatum = db.getAgent(consignmentListDatum.getDelivery_agent());

            if (agentListDatum != null){
                Log.e(TAG, agentListDatum.getAgent_id()
                        +" "+ agentListDatum.getAgent_name()
                        +" "+ agentListDatum.getDo_name()
                        +" "+ agentListDatum.getDo_id());
            }
            String s = null;
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
            String s = null;
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
}
