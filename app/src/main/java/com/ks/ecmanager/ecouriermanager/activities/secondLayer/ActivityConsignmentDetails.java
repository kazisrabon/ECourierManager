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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.firstLayer.ActivityMain;
import com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;

import java.util.ArrayList;

import static com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin.sessionUserData;

public class ActivityConsignmentDetails extends ActivityBase {

    private final String TAG = "CN DETAILS";
    private Button mUpdate;
    private LinearLayout mLayoutConsignmentInformation, mLayoutParcelInformation;
    private RelativeLayout altPhoneNumberLayout;
    private TextView mConsignmentId, mSenderGroup, mCompany, mCompanyPhone, editComment, mCallCompany,
            mProductId, mOrderTime, mDeliveryTime, mParcelStatus, mCallRecipient,mCallRecipientAlt, mParcelStatusReason,
            mItemType;
    private EditText editInput, etItem, etCollectedAmount, editProductPrice, editRecipientName, editRecipientMobile,editRecipientMobileAlt, editRecipientAddress;
    private ConsignmentListDatum datum = null;
    private Resources res;
    private String consignment_no = "", current_parcel_status_code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consignment_details);
        res = getResources();

        initialize();
        receiveDataFromIntent();
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

}
