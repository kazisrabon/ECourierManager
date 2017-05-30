/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.secondLayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import com.ks.ecmanager.ecouriermanager.pojo.AgentDOListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.AgentList;
import com.ks.ecmanager.ecouriermanager.pojo.DoList;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;
import com.ks.ecmanager.ecouriermanager.pojo.ParcelList;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.AgentListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.DoListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.ParcelStatusUpdateInterface;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin.sessionUserData;

public class ActivityConsignmentDetails extends ActivityBase{

    private final int FROM_AGENT = 2, FROM_DO = 1, FROM_NONE = 0, AGENT_VISIBLE = 1, DO_VISIBLE = 2,
            INVISIBLE = 0;
    private final String TAG = "CN DETAILS";
    private Button mUpdate, mSelectDO, mSelectDOAgent, mStatusCancel, mStatusOTW;
    private LinearLayout mLayoutConsignmentInformation, mLayoutParcelInformation, mLLHold;
    private RelativeLayout altPhoneNumberLayout;
    private TextView mConsignmentId, mSenderGroup, mCompany, mCompanyPhone, editComment, mCallCompany,
            mProductId, mOrderTime, mDeliveryTime, mParcelStatus, mCallRecipient,mCallRecipientAlt, mParcelStatusReason,
            mItemType, mDeliveryAgent, mDDO;
    private EditText editInput, etItem, etCollectedAmount, editProductPrice, editRecipientName, editRecipientMobile,editRecipientMobileAlt, editRecipientAddress;
    private ConsignmentListDatum datum = null;
    private Resources res;
    private String consignment_no = "", current_parcel_status_code = "", agent_name = "", d_do = "",
            changed_parcel_status_code = "";
    private HashMap<String, String> user = new HashMap<String, String>();
    private HashMap<String, String> map = new HashMap<String, String>();
    private HashMap<String, String> statusMap = new HashMap<String, String>();
    private BidiMap<String, String> listData = new DualHashBidiMap();
    private HashMap<String, Integer> visibility = new HashMap<String, Integer>();
    private String changedAgentorDO;
    private int changedValue = FROM_NONE;

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

    private void setVisibility(int visibility) {
        if (visibility == INVISIBLE){
            mSelectDO.setVisibility(View.GONE);
            mSelectDOAgent.setVisibility(View.GONE);
        }
        else if (visibility == AGENT_VISIBLE){
            mSelectDO.setVisibility(View.GONE);
            mSelectDOAgent.setVisibility(View.VISIBLE);
        }
        else if (visibility == DO_VISIBLE){
            mSelectDO.setVisibility(View.VISIBLE);
            mSelectDOAgent.setVisibility(View.GONE);
        }

    }

    private void setHashMap() {
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        //Lets pass the desired parameters
        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);

//        status changing hashMap
        statusMap.put(res.getString(R.string.s2), res.getString(R.string.s13));
        statusMap.put(res.getString(R.string.s4), res.getString(R.string.s0));
        statusMap.put(res.getString(R.string.s5), res.getString(R.string.s0));
        statusMap.put(res.getString(R.string.s6), res.getString(R.string.s0));
//      statusMap.put(res.getString(R.string.s7), res.getString(R.string.s));
        statusMap.put(res.getString(R.string.s8), res.getString(R.string.s0));
        statusMap.put(res.getString(R.string.s10), res.getString(R.string.s0));
        statusMap.put(res.getString(R.string.s12), res.getString(R.string.s22));
        statusMap.put(res.getString(R.string.s13), res.getString(R.string.s14));
        statusMap.put(res.getString(R.string.s14), res.getString(R.string.s15));
        statusMap.put(res.getString(R.string.s15), res.getString(R.string.s21));
        statusMap.put(res.getString(R.string.s20), res.getString(R.string.s0));
        statusMap.put(res.getString(R.string.s21), res.getString(R.string.s0));
        statusMap.put(res.getString(R.string.s22), res.getString(R.string.s0));
        statusMap.put(res.getString(R.string.s23), res.getString(R.string.s25));
        statusMap.put(res.getString(R.string.s24), res.getString(R.string.s0));
        statusMap.put(res.getString(R.string.s25), res.getString(R.string.s0));

//        visibility hashMap
        visibility.put(res.getString(R.string.s0), INVISIBLE);
        visibility.put(res.getString(R.string.s13), INVISIBLE);
        visibility.put(res.getString(R.string.s14), DO_VISIBLE);
        visibility.put(res.getString(R.string.s15), INVISIBLE);
        visibility.put(res.getString(R.string.s21), AGENT_VISIBLE);
        visibility.put(res.getString(R.string.s22), INVISIBLE);
        visibility.put(res.getString(R.string.s25), AGENT_VISIBLE);
        visibility.put(res.getString(R.string.s12), INVISIBLE);
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
//                    int position = Integer.parseInt(getIntent().getStringExtra("consignment_data_position"));
                    int position = getIntent().getIntExtra(KEY_CN_POS, 0);
                    ArrayList<ConsignmentListDatum> myList = (ArrayList<ConsignmentListDatum>) getIntent().getSerializableExtra(KEY_CN_DATA);
                    datum = myList.get(position);
                    setConsignmentDetails(datum);
                }
            }
        }
    }

    private void setConsignmentDetails(ConsignmentListDatum datum) {
        if (!datum.getStatus_code().equals(res.getString(R.string.s7))){
            changed_parcel_status_code = statusMap.get(datum.getStatus_code());
            setVisibility(visibility.get(changed_parcel_status_code));
            mLLHold.setVisibility(View.GONE);
        }
        else if (datum.getStatus_code().equals(res.getString(R.string.s7))){
            mLLHold.setVisibility(View.VISIBLE);
            setVisibility(INVISIBLE);
        }

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

        if (stringNotNullCheck(datum.getEso_mobile()))
            mCompany.setText(String.format(res.getString(R.string.contact), datum.getEso_mobile()));
        else
            mCompany.setText(String.format(res.getString(R.string.contact), res.getString(R.string.none)));


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

        if (stringNotNullCheck(datum.getRecipient_mobile()))
            editRecipientMobile.setText(datum.getRecipient_mobile());
        else
            editRecipientMobile.setText(res.getString(R.string.none));

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
//            printBidiHash(TAG, doBidiList);
            String s = doBidiList.get(datum.getDestination_do());
            if (stringNotNullCheck(s))
                mDDO.setText(String.format(res.getString(R.string.destination_do), s));
            else
                mDDO.setText(String.format(res.getString(R.string.destination_do), datum.getDestination_do()));
        }

        else
            mDDO.setText(String.format(res.getString(R.string.destination_do), res.getString(R.string.none)));

        if (stringNotNullCheck(datum.getDelivery_agent())){
            printBidiHash(TAG, agentBidiList);
            Log.e(TAG, datum.getDelivery_agent());
            String s = agentBidiList.get(datum.getDelivery_agent());
            if (stringNotNullCheck(s))
                mDeliveryAgent.setText(String.format(res.getString(R.string.delivery_agent), s));
            else
                mDeliveryAgent.setText(String.format(res.getString(R.string.delivery_agent), datum.getDelivery_agent()));
        }
        else
            mDeliveryAgent.setText(String.format(res.getString(R.string.delivery_agent), res.getString(R.string.none)));

    }

    private void initialize() {
        mUpdate = (Button) findViewById(R.id.btnUpdate);
        mSelectDO = (Button) findViewById(R.id.btnSelectDo);
        mSelectDOAgent = (Button) findViewById(R.id.btnSelectDoAgent);
        mStatusCancel = (Button) findViewById(R.id.btnStatusCancel);
        mStatusOTW = (Button) findViewById(R.id.btnStatusOTW);
        mLayoutConsignmentInformation = (LinearLayout) findViewById(R.id.layoutConsignmentInformation);
        mLayoutParcelInformation = (LinearLayout) findViewById(R.id.layoutParcelInformation);
        mLLHold = (LinearLayout) findViewById(R.id.llHold);
        altPhoneNumberLayout =  (RelativeLayout) findViewById(R.id.altPhoneNumberLayout);
        mConsignmentId = (TextView) findViewById(R.id.textConsignmentId);
        mSenderGroup = (TextView) findViewById(R.id.textSenderGroup);
        mCompany = (TextView) findViewById(R.id.textCompany);
        mCompanyPhone = (TextView) findViewById(R.id.textCompanyMobile);
        mCallCompany = (TextView) findViewById(R.id.txtCompanyMobile);
        mProductId = (TextView) findViewById(R.id.textProductId);
        mCallRecipient = (TextView) findViewById(R.id.txtRecipientMobile);
        mCallRecipientAlt = (TextView) findViewById(R.id.txtRecipientMobileAlt);
        editProductPrice = (EditText) findViewById(R.id.editProductPrice);
        editRecipientName = (EditText) findViewById(R.id.editRecipientName);
        editRecipientMobile = (EditText) findViewById(R.id.editRecipientMobile);
        editRecipientMobileAlt = (EditText) findViewById(R.id.editRecipientMobileAlt);
        editRecipientAddress = (EditText) findViewById(R.id.editRecipientAddress);
        mOrderTime = (TextView) findViewById(R.id.textOrderTime);
        mDeliveryTime = (TextView) findViewById(R.id.textActualDeliveryTime);
        mParcelStatus = (TextView) findViewById(R.id.textParcelStatus);
        mParcelStatusReason = (TextView) findViewById(R.id.textParcelStatusReason);
        mItemType = (TextView) findViewById(R.id.tvItemType);
        mDDO = (TextView) findViewById(R.id.textDDO);
        mDeliveryAgent = (TextView) findViewById(R.id.textDeliveryAgent);

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateParcel();
            }
        });

        mSelectDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHashMap();
                loadDO(map);
            }
        });
        mSelectDOAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHashMap();
                loadDoAgent(map);
            }
        });

        mStatusCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changed_parcel_status_code = res.getString(R.string.s12);
                setVisibility(INVISIBLE);
                showToast("Parcel Status : Cancle", Toast.LENGTH_SHORT, END);
            }
        });
        mStatusOTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changed_parcel_status_code = res.getString(R.string.s21);
                setVisibility(AGENT_VISIBLE);
                showToast("Parcel Status : On the Way", Toast.LENGTH_SHORT, END);
            }
        });
    }

    private void updateParcel() {
        if (changed_parcel_status_code.equals(res.getString(R.string.s0))){
            showErrorToast(res.getString(R.string.not_allowed), Toast.LENGTH_LONG, MIDDLE);
        }
        else {
            if (stringNotNullCheck(consignment_no)) {
//            consignment_no
                map.put(ApiParams.PARAM_CONSIGNMENT_NO, consignment_no);
                if (changedValue != FROM_NONE) {
                    String s = "";
//            printHash(TAG, listData);
                    if (changedValue == FROM_DO) {
                        s = listData.get(d_do);
                        //Lets pass the desired parameters
                        map.put(ApiParams.PARAM_D_DO, s);
                        map.put(ApiParams.PARAM_STATUS, changed_parcel_status_code);
                        map.put(ApiParams.PARAM_COMMENT, res.getString(R.string.testing));
                        parcelStatusUpdate(map);
                        Log.e(TAG, s);
                    } else if (changedValue == FROM_AGENT) {
                        s = listData.get(agent_name);
                        map.put(ApiParams.PARAM_STATUS, changed_parcel_status_code);
                        map.put(ApiParams.PARAM_COMMENT, res.getString(R.string.testing));
                        if (changed_parcel_status_code.equals(res.getString(R.string.s21))) {
                            map.put(ApiParams.PARAM_AGENT_ID, "" + s);
                        } else if (changed_parcel_status_code.equals(res.getString(R.string.s25))) {
                            map.put(ApiParams.PARAM_RETURN_AGENT, "" + s);
                        }
                        parcelStatusUpdate(map);
                        Log.e(TAG, s);
                    }
                    Log.e(TAG, s + " " + current_parcel_status_code + " " + changed_parcel_status_code + " " +
                            visibility.get(changed_parcel_status_code));
                } else {
                    if (changed_parcel_status_code.equals(res.getString(R.string.s12))) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        map.put(ApiParams.PARAM_STATUS, changed_parcel_status_code);
                        map.put(ApiParams.PARAM_COMMENT, res.getString(R.string.testing));
                        map.put(ApiParams.PARAM_CANCEL_CALL_TIME, currentDateandTime);
                        parcelStatusUpdate(map);
                        Log.e(TAG, currentDateandTime);
                    } else {
                        map.put(ApiParams.PARAM_STATUS, changed_parcel_status_code);
                        map.put(ApiParams.PARAM_COMMENT, res.getString(R.string.testing));
                        parcelStatusUpdate(map);
                        Log.e(TAG, "Nothing Selected" + " " + current_parcel_status_code + " "
                                + changed_parcel_status_code + " " + visibility.get(changed_parcel_status_code));
                    }
                }
            }
            else
                showErrorToast("PLEASE SEARCH AGAIN!!!", Toast.LENGTH_SHORT, MIDDLE);
        }
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

    private void loadDO(HashMap<String, String> map) {
        showProgressDialog(false, "", getResources().getString(R.string.loading));

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        DoListInterface myApiCallback = restAdapter.create(DoListInterface.class);

        printHash(TAG, map);
        myApiCallback.getData(ApiParams.TAG_DO_LIST_KEY, map, new Callback<DoList>() {
            @Override
            public void success(DoList doList, Response response) {
                hideProgressDialog();

                boolean status = doList.getStatus();
                Log.e(TAG, status+" ");
                if (status) {
                    showToast("Total Do : " + doList.getTotal_dos() + "!", Toast.LENGTH_SHORT, MIDDLE);
                    showList(doList.getDo_list(), FROM_DO);
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

    private void loadDoAgent(HashMap<String, String> map) {
        showProgressDialog(false, "", getResources().getString(R.string.loading));

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        AgentListInterface myApiCallback = restAdapter.create(AgentListInterface.class);

        printHash(TAG, map);
        myApiCallback.getData(ApiParams.TAG_DO_AGENT_LIST_KEY, map, new Callback<AgentList>() {
            @Override
            public void success(AgentList agentList, Response response) {
                hideProgressDialog();

                boolean status = agentList.getStatus();
                Log.e(TAG, status+" ");
                if (status) {
                    showToast("Total Agent : " + agentList.getTotal_agents() + "!", Toast.LENGTH_SHORT, MIDDLE);
                    showList(agentList.getAgent_list(), FROM_AGENT);
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

    private void showList(List<AgentDOListDatum> list, final int where_from) {
        final String[] s = {""};
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityConsignmentDetails.this);
        builderSingle.setIcon(R.mipmap.ic_launcher_new);
        builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityConsignmentDetails.this,
                android.R.layout.select_dialog_singlechoice);
        listData = new DualHashBidiMap();
        for (int i = 0; i < list.size(); i++){
            listData.put(list.get(i).getValue(), list.get(i).getId());
            arrayAdapter.add(list.get(i).getValue());
        }
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builderInner = new AlertDialog.Builder(ActivityConsignmentDetails.this);
                if (where_from == FROM_DO)
                    d_do = arrayAdapter.getItem(which);
                else if (where_from == FROM_AGENT)
                    agent_name = arrayAdapter.getItem(which);

                s[0] = arrayAdapter.getItem(which);
                builderInner.setMessage(s[0]);
                builderInner.setTitle("Your Selection is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        Log.e(TAG, "selected "+s[0]+" Agent "+agent_name+" D_Do "+d_do);
                        setChangedAgentorDO(s[0], where_from);
                        dialog.dismiss();
                    }
                });
                builderInner.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        if (where_from == FROM_DO)
                            d_do = "";
                        else if (where_from == FROM_AGENT)
                            agent_name = "";
                        s[0]="";
                        setChangedAgentorDO(s[0], FROM_NONE);
                        Log.e(TAG, "selected "+s[0]+" Agent "+agent_name+" D_Do "+d_do);
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    public void setChangedAgentorDO(String s, int i){
        changedAgentorDO = s;
        changedValue = i;
    }

    private void parcelStatusUpdate (HashMap<String, String> statusMap){
        showProgressDialog(false, "", getResources().getString(R.string.loading));

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiParams.TAG_BASE_URL).build();
        ParcelStatusUpdateInterface myApiCallback = restAdapter.create(ParcelStatusUpdateInterface.class);

        printHash(TAG, statusMap);

        myApiCallback.getData(ApiParams.TAG_PARCEL_STATUS_UPDATE_KEY, map, new Callback<ParcelList>() {
            @Override
            public void success(ParcelList parcelList, Response response) {
                hideProgressDialog();

                boolean status = parcelList.getStatus();
                Log.e(TAG, status+" ");
                if (status) {
                    showSuccessToast("Status Updated", Toast.LENGTH_LONG, MIDDLE);
                    startActivity(new Intent(ActivityConsignmentDetails.this, ActivityMain.class));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                showToast(getString(R.string.refreshing), Toast.LENGTH_LONG, END);
                setDoBidiList(map);
                setAgentBidiList(map);
                break;
        }
        return true;
    }
}
