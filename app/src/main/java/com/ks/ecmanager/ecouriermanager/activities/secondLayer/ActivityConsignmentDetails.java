/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.secondLayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.firstLayer.ActivityMain;
import com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin;
import com.ks.ecmanager.ecouriermanager.pojo.AgentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.DOListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.AgentList;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentList;
import com.ks.ecmanager.ecouriermanager.pojo.DoList;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ParcelList;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.AgentListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ConsignmentListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.DoListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ParcelStatusUpdateInterface;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

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

public class ActivityConsignmentDetails extends ActivityBase{

    private final String TAG = "CN DETAILS";
    private Button mBack;
    private RelativeLayout altPhoneNumberLayout, rlCompanyCall, rlRecipientCall;
    private TextView mConsignmentId, mSenderGroup, mCompany, mCompanyPhone, editComment, mCallCompany,
            mProductId, mOrderTime, mDeliveryTime, mParcelStatus, mCallRecipient,mCallRecipientAlt,
            mItemType, mDeliveryAgent, mDDO;
    private EditText editProductPrice, editRecipientName, editRecipientMobile,editRecipientMobileAlt, editRecipientAddress;
    private ConsignmentListDatum datum = null;
    private Resources res;
    private String consignment_no = "", current_parcel_status_code = "", agent_name = "", d_do = "",
            changed_parcel_status_code = "";
    private HashMap<String, String> user = new HashMap<String, String>();
    private HashMap<String, String> map = new HashMap<String, String>();
    private ArrayList<ConsignmentListDatum> myList;
    private String accessLevel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consignment_details);
        res = getResources();
        user = sessionUserData.getSessionDetails();
        setHashMap();
        initialize();
        receiveDataFromIntent();
//        setVisibility(INVISIBLE);
    }

    private void setHashMap() {
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        //Lets pass the desired parameters
        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
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
                    myList = new ArrayList<>();
                    myList = (ArrayList<ConsignmentListDatum>) getIntent().getSerializableExtra(KEY_CN_DATA);
                    accessLevel = accessLevel(myList.get(position).getStatus_code(), myList.get(position).getSource_do(), myList.get(position).getDestination_do());
                    datum = myList.get(position);
                    setConsignmentDetails(datum);
                }
            }
        }
    }

    private void setConsignmentDetails(final ConsignmentListDatum datum) {
        if (stringNotNullCheck(datum.getConsignment_no())){
            consignment_no = datum.getConsignment_no();
            mConsignmentId.setText(String.format(res.getString(R.string.consignment_id), consignment_no));
        }
        else
            mConsignmentId.setText(res.getString(R.string.none));

        if (stringNotNullCheck(datum.getEso()))
            mCompany.setText(String.format(res.getString(R.string.company), datum.getEso()));
        else
            mCompany.setText(String.format(res.getString(R.string.company), res.getString(R.string.none)));

        if (stringNotNullCheck(datum.getEso_mobile())) {
            mCompanyPhone.setText(String.format(res.getString(R.string.contact), datum.getEso_mobile()));
            mCallCompany.setVisibility(View.VISIBLE);
            rlCompanyCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel: " + datum.getEso_mobile()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            mCompanyPhone.setText(String.format(res.getString(R.string.contact), res.getString(R.string.none)));
            mCallCompany.setVisibility(View.GONE);
        }

        if (stringNotNullCheck(datum.getItem_type())) {
            mItemType.setText(datum.getItem_type());
        }
        else
            mItemType.setText(res.getString(R.string.none));

        if (stringNotNullCheck(datum.getAlter_mobile())){
            altPhoneNumberLayout.setVisibility(View.VISIBLE);
            editRecipientMobileAlt.setVisibility(View.VISIBLE);
            mCallRecipientAlt.setVisibility(View.VISIBLE);
            editRecipientMobileAlt.setText(datum.getAlter_mobile());
            altPhoneNumberLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel: " + datum.getAlter_mobile()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            altPhoneNumberLayout.setVisibility(View.GONE);
            editRecipientMobileAlt.setVisibility(View.GONE);
            mCallRecipientAlt.setVisibility(View.GONE);
        }

        if (stringNotNullCheck(datum.getSender_group()))
            mSenderGroup.setText(String.format(res.getString(R.string.sender_group), datum.getSender_group()));
        else
            mSenderGroup.setText(String.format(res.getString(R.string.sender_group), res.getString(R.string.none)));


//        edit from here
        if (stringNotNullCheck(datum.getProduct_id()))
            mProductId.setText(String.format(res.getString(R.string.product_id), datum.getProduct_id()));
        else
            mProductId.setText(String.format(res.getString(R.string.product_id), res.getString(R.string.none)));

        if (stringNotNullCheck(datum.getProduct_price()))
            editProductPrice.setText(datum.getProduct_price());
        else
            editProductPrice.setText(res.getString(R.string.none));

        if (stringNotNullCheck(datum.getRecipient_name()))
            editRecipientName.setText(datum.getRecipient_name());
        else
            editRecipientName.setText(res.getString(R.string.none));

        if (stringNotNullCheck(datum.getRecipient_mobile())) {
            editRecipientMobile.setText(datum.getRecipient_mobile());
            mCallRecipient.setVisibility(View.VISIBLE);
            rlRecipientCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel: " + datum.getRecipient_mobile()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            editRecipientMobile.setText(res.getString(R.string.none));
            mCallRecipient.setVisibility(View.GONE);
        }

        if (stringNotNullCheck(datum.getRecipient_address()))
            editRecipientAddress.setText(datum.getRecipient_address());
        else
            editRecipientAddress.setText(res.getString(R.string.none));
//
        if (stringNotNullCheck(datum.getOrder_time()))
            mOrderTime.setText(String.format(res.getString(R.string.order_time), datum.getOrder_time()));
        else
            mOrderTime.setText(String.format(res.getString(R.string.order_time), res.getString(R.string.none)));

        if (stringNotNullCheck(datum.getParcel_status()))
            mParcelStatus.setText(String.format(res.getString(R.string.parcel_status), datum.getParcel_status()));
        else
            mParcelStatus.setText(res.getString(R.string.none));

        current_parcel_status_code = datum.getStatus_code();
        if (stringNotNullCheck(current_parcel_status_code)){
            if (!current_parcel_status_code.equals(res.getString(R.string.s10))){
                mDeliveryTime.setText(String.format(res.getString(R.string.delivery_time), datum.getActual_delivery_time()));
            }
            else
                mDeliveryTime.setVisibility(View.GONE);
        }
        else
            mDeliveryTime.setText(String.format(res.getString(R.string.delivery_time), res.getString(R.string.none)));

        if (stringNotNullCheck(datum.getDestination_do())){
            String s = db.getDoName(datum.getDestination_do());
            if (!stringNotNullCheck(s)) {
                s= getResources().getString(R.string.no_do_found);
            }
            mDDO.setText(String.format(getString(R.string.destination_do), s));
        }
        else
            mDDO.setText(String.format(res.getString(R.string.destination_do), res.getString(R.string.none)));

        if (stringNotNullCheck(datum.getDelivery_agent())){
            Log.e(""+TAG, datum.getDelivery_agent());
            AgentListDatum agentListDatum = db.getAgent(datum.getDelivery_agent());

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
            mDeliveryAgent.setText(String.format(getString(R.string.delivery_agent), s));
        }
        else
            mDeliveryAgent.setText(String.format(res.getString(R.string.delivery_agent), res.getString(R.string.none)));

    }

    private void updateCN(){
        if (accessLevel.contains("1"))
            showListInPopUp(ActivityConsignmentDetails.this, getNextStatusMap(), "status", consignment_no);
        else
            showErrorToast("ERROR!!!", Toast.LENGTH_SHORT, MIDDLE);
        String current_status = getCurrent_status();
        String next_status = getNextStatus();
        String agent_id = getNextAgent();
        String do_id = getNextDO();
        Log.e("map data", current_status
                +" "+next_status
                +" "+agent_id
                +" "+do_id);
    }


    private void initialize() {
//        mUpdate = (Button) findViewById(R.id.btnUpdate);
//        mSelectDO = (Button) findViewById(R.id.btnSelectDo);
//        mSelectDOAgent = (Button) findViewById(R.id.btnSelectDoAgent);
//        mStatusCancel = (Button) findViewById(R.id.btnStatusCancel);
//        mStatusOTW = (Button) findViewById(R.id.btnStatusOTW);
        mBack = (Button) findViewById(R.id.btnBack);

        altPhoneNumberLayout =  (RelativeLayout) findViewById(R.id.altPhoneNumberLayout);
        rlCompanyCall =  (RelativeLayout) findViewById(R.id.rlCompanyCall);
        rlRecipientCall =  (RelativeLayout) findViewById(R.id.rlRecipientCall);

        mConsignmentId = (TextView) findViewById(R.id.textConsignmentId);
        mSenderGroup = (TextView) findViewById(R.id.textSenderGroup);
        mCompany = (TextView) findViewById(R.id.textCompany);
        mCompanyPhone = (TextView) findViewById(R.id.textCompanyMobile);
        mCallCompany = (TextView) findViewById(R.id.txtCompanyMobile);
        mProductId = (TextView) findViewById(R.id.textProductId);
        mCallRecipient = (TextView) findViewById(R.id.txtRecipientMobile);
        mCallRecipientAlt = (TextView) findViewById(R.id.txtRecipientMobileAlt);
        mOrderTime = (TextView) findViewById(R.id.textOrderTime);
        mDeliveryTime = (TextView) findViewById(R.id.textActualDeliveryTime);
        mParcelStatus = (TextView) findViewById(R.id.textParcelStatus);
        mItemType = (TextView) findViewById(R.id.tvItemType);
        mDDO = (TextView) findViewById(R.id.textDDO);
        mDeliveryAgent = (TextView) findViewById(R.id.textDeliveryAgent);

        editProductPrice = (EditText) findViewById(R.id.editProductPrice);
        editRecipientName = (EditText) findViewById(R.id.editRecipientName);
        editRecipientMobile = (EditText) findViewById(R.id.editRecipientMobile);
        editRecipientMobileAlt = (EditText) findViewById(R.id.editRecipientMobileAlt);
        editRecipientAddress = (EditText) findViewById(R.id.editRecipientAddress);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(ActivityConsignmentDetails.this,ActivityConsignment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(KEY_CN_POS, 0);
                intent.putExtra(KEY_CN_DATA, myList);
                intent.putExtra(KEY_WHERE_FROM, CN_TYPE_SEARCH);
                sessionUserData.initStatus();
                startActivity(intent);
            }
        });

//
//        mUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateParcel();
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() > 0) {

            AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
            alert_box.setTitle(getResources().getString(R.string.exit_title));
            alert_box.setMessage(getResources().getString(R.string.logout_confirmation));

            alert_box.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            sessionUserData.endSession();
                            Intent loginActivity =  new Intent(ActivityConsignmentDetails.this,ActivityLogin.class);
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
            Intent mainActivity =  new Intent(ActivityConsignmentDetails.this,ActivityMain.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainActivity);

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
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
