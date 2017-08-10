/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.secondLayer.ActivityConsignment;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;

import java.util.ArrayList;
import java.util.List;

import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_CN_DATA;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_CN_POS;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_WHERE_FROM;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.CN_TYPE_SEARCH;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.FROM_BULK;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.MIDDLE;

/**
 * Created by Kazi Srabon on 7/17/2017.
 */

public class MultipleSearchValueAdapter  extends BaseAdapter {
    private Context context;
    private List<ConsignmentListDatum> consignmentListDatumList;
    private LayoutInflater inflater;
    private String where_from = "";
    private ActivityBase activityBase;

    public MultipleSearchValueAdapter(Context context, List<ConsignmentListDatum> consignmentListDatumList, String where_from) {
        this.context = context;
        this.consignmentListDatumList = consignmentListDatumList;
        this.where_from = where_from;
    }

    @Override
    public int getCount() {
        return consignmentListDatumList.size();
    }

    @Override
    public ConsignmentListDatum getItem(int position) {
        return consignmentListDatumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View result;
        activityBase = new ActivityBase();
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_search_item, parent, false);
        } else {
            result = convertView;
        }
        TextView consignment_position = (TextView) result.findViewById(R.id.textPosition);
        TextView consignment_id = (TextView) result.findViewById(R.id.textConsignmentId);
        consignment_id.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        TextView company = (TextView) result.findViewById(R.id.textCompany);
        TextView name = (TextView) result.findViewById(R.id.textName);
        TextView amount = (TextView) result.findViewById(R.id.textAmount);
        TextView mobile_no = (TextView) result.findViewById(R.id.textMobileNo);
        TextView order_time = (TextView) result.findViewById(R.id.textOrderTime);
        TextView parcel_status = (TextView) result.findViewById(R.id.textParcelStatus);

        ConsignmentListDatum listDatum = consignmentListDatumList.get(position);
        consignment_position.setText("" + (position + 1) + ". ");
        consignment_id.setText(listDatum.getConsignment_no());
        company.setText(listDatum.getEso());
        name.setText(listDatum.getRecipient_name());
        amount.setText(listDatum.getProduct_price());
        mobile_no.setText(listDatum.getRecipient_mobile());
        order_time.setText(listDatum.getOrder_time());
        parcel_status.setText(listDatum.getParcel_status());

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // convert the list to array list and pass via intent
                if (where_from.equals(FROM_BULK)){
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (activityBase.canECRView(getItem(position).getStatus_code(),
                            getItem(position).getSource_do(),
                            getItem(position).getDestination_do())) {
                        ArrayList<ConsignmentListDatum> ItemArray = ((ArrayList<ConsignmentListDatum>) consignmentListDatumList);
                        Intent intent = new Intent(context, ActivityConsignment.class);
                        intent.putExtra(KEY_CN_POS, position);
                        intent.putExtra(KEY_CN_DATA, ItemArray);
                        intent.putExtra(KEY_WHERE_FROM, CN_TYPE_SEARCH);
                        context.startActivity(intent);
                    }
                    else
                        Toast.makeText(context, "Have no access!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return result;
    }
}
