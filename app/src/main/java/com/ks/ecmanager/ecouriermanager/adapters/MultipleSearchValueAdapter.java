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

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.secondLayer.ActivityConsignment;
import com.ks.ecmanager.ecouriermanager.pojo.ConsignmentListDatum;

import java.util.ArrayList;
import java.util.List;

import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_CN_DATA;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_CN_POS;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_WHERE_FROM;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.CN_TYPE_SEARCH;

/**
 * Created by Kazi Srabon on 7/17/2017.
 */

public class MultipleSearchValueAdapter  extends BaseAdapter {
    private Context context;
    private List<ConsignmentListDatum> consignmentListDatumList;
    private LayoutInflater inflater;

    public MultipleSearchValueAdapter(Context context, List<ConsignmentListDatum> consignmentListDatumList) {
        this.context = context;
        this.consignmentListDatumList = consignmentListDatumList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_search_item, parent, false);
        } else {
            result = convertView;
        }
        ConsignmentListDatum listDatum = consignmentListDatumList.get(position);
        TextView tvEcrNumber = (TextView) result.findViewById(R.id.tvEcrNumber);
        TextView tvProductPrice = (TextView) result.findViewById(R.id.tvProductPrice);
        TextView tvCompany = (TextView) result.findViewById(R.id.tvCompany);
        TextView tvReciverName = (TextView) result.findViewById(R.id.tvReciverName);
        TextView tvReceiverMobile = (TextView) result.findViewById(R.id.tvReceiverMobile);
        TextView tvStatus = (TextView) result.findViewById(R.id.tvStatus);

        tvEcrNumber.setText(listDatum.getConsignment_no());
        tvProductPrice.setText(listDatum.getProduct_price());
        tvCompany.setText(listDatum.getEso());
        tvReciverName.setText(listDatum.getRecipient_name());
        tvReceiverMobile.setText(listDatum.getRecipient_mobile());
        tvStatus.setText(listDatum.getParcel_status());
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // convert the list to array list and pass via intent
                ArrayList<ConsignmentListDatum> ItemArray = ((ArrayList<ConsignmentListDatum>) consignmentListDatumList);
                Intent intent = new Intent(context, ActivityConsignment.class);
                intent.putExtra(KEY_CN_POS, 0);
                intent.putExtra(KEY_CN_DATA, ItemArray);
                intent.putExtra(KEY_WHERE_FROM, CN_TYPE_SEARCH);
                context.startActivity(intent);

            }
        });
        return result;
    }
}
