/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.activities.secondLayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;
import com.ks.ecmanager.ecouriermanager.webservices.ApiParams;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.AgentListInterface;
import com.ks.ecmanager.ecouriermanager.webservices.interfaces.DoListInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin.sessionUserData;

public class ActivityConsignmentDetails extends ActivityBase{

    private final int FROM_AGENT = 2, FROM_DO = 1, FROM_NONE = 0;
    private final String TAG = "CN DETAILS";
    private Button mUpdate, mSelectDO, mSelectDOAgent;
    private LinearLayout mLayoutConsignmentInformation, mLayoutParcelInformation;
    private RelativeLayout altPhoneNumberLayout;
    private TextView mConsignmentId, mSenderGroup, mCompany, mCompanyPhone, editComment, mCallCompany,
            mProductId, mOrderTime, mDeliveryTime, mParcelStatus, mCallRecipient,mCallRecipientAlt, mParcelStatusReason,
            mItemType;
    private EditText editInput, etItem, etCollectedAmount, editProductPrice, editRecipientName, editRecipientMobile,editRecipientMobileAlt, editRecipientAddress;
    private ConsignmentListDatum datum = null;
    private Resources res;
    private String consignment_no = "", current_parcel_status_code = "", agent_name = "", d_do = "";
    private HashMap<String, String> user = new HashMap<String, String>();
    private HashMap<String, String> map = new HashMap<String, String>();
    private HashMap<String, String> statusMap = new HashMap<String, String>();
    private HashMap<String, String> listData = new HashMap<String, String>();
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
    }

    private void setHashMap() {
        String id = user.get(SessionUserData.KEY_USER_ID);
        String group = user.get(SessionUserData.KEY_USER_GROUP);
        String authentication_key = user.get(SessionUserData.KEY_USER_AUTH_KEY);

        //Lets pass the desired parameters
        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
        map.put(ApiParams.PARAM_GROUP, group);
        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);

        map.put(res.getString(R.string.s2), res.getString(R.string.s13));
        map.put(res.getString(R.string.s4), res.getString(R.string.s0));
        map.put(res.getString(R.string.s5), res.getString(R.string.s0));
        map.put(res.getString(R.string.s6), res.getString(R.string.s0));
//        map.put(res.getString(R.string.s7), res.getString(R.string.s));
        map.put(res.getString(R.string.s8), res.getString(R.string.s0));
        map.put(res.getString(R.string.s10), res.getString(R.string.s0));
        map.put(res.getString(R.string.s12), res.getString(R.string.s22));
        map.put(res.getString(R.string.s13), res.getString(R.string.s14));
        map.put(res.getString(R.string.s14), res.getString(R.string.s15));
        map.put(res.getString(R.string.s15), res.getString(R.string.s21));
        map.put(res.getString(R.string.s20), res.getString(R.string.s0));
        map.put(res.getString(R.string.s21), res.getString(R.string.s0));
        map.put(res.getString(R.string.s22), res.getString(R.string.s0));
        map.put(res.getString(R.string.s23), res.getString(R.string.s25));
        map.put(res.getString(R.string.s24), res.getString(R.string.s0));
        map.put(res.getString(R.string.s25), res.getString(R.string.s0));
    }

    private void setHashMap(String s) {
        //Lets pass the desired parameters
//        map.put(ApiParams.PARAM_ADMIN_ID, "" + id);
//        map.put(ApiParams.PARAM_GROUP, group);
//        map.put(ApiParams.PARAM_AUTHENTICATION_KEY, "" + authentication_key);
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

    }

    private void initialize() {
        mUpdate = (Button) findViewById(R.id.btnUpdate);
        mSelectDO = (Button) findViewById(R.id.btnSelectDo);
        mSelectDOAgent = (Button) findViewById(R.id.btnSelectDoAgent);
        mLayoutConsignmentInformation = (LinearLayout) findViewById(R.id.layoutConsignmentInformation);
        mLayoutParcelInformation = (LinearLayout) findViewById(R.id.layoutParcelInformation);
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

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateParcel();
            }
        });

        mSelectDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDO();
            }
        });
        mSelectDOAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDoAgent();
            }
        });
    }

    private void updateParcel() {
        if (changedValue != FROM_NONE){
            printHash(TAG, listData);
            if (changedValue == FROM_DO){
                String s = listData.get(d_do);
                Log.e(TAG, s);
            }
            else if (changedValue == FROM_AGENT){
                String s = listData.get(agent_name);
                Log.e(TAG, s);
            }
        }
        else
            Log.e(TAG, "Nothing Selected");

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

    private void loadDO() {
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

    private void loadDoAgent() {
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
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityConsignmentDetails.this,
                android.R.layout.select_dialog_singlechoice);
        listData = new HashMap<String, String>();
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
}
