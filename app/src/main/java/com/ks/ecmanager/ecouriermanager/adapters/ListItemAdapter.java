/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.secondLayer.ActivityDelivery;
import com.ks.ecmanager.ecouriermanager.pojo.ListDatum;

import org.apache.commons.collections4.BidiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_DELIVERY;

/**
 * Created by Kazi Srabon on 7/24/2017.
 */

public class ListItemAdapter extends BaseAdapter {
    public static String list_clicked_value = "";
    private Context context;
    private List<ListDatum> listData;
//    private String where_from;
    private LayoutInflater inflater;
    private ListDatum listDatum;
    private String where_from = "";

    public ListItemAdapter(Context context, List<ListDatum> listData, String where_from) {
        this.context = context;
        this.listData = listData;
        this.where_from = where_from;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public ListDatum getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View result;
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        } else {
            result = convertView;
        }
        listDatum = listData.get(position);
        TextView value;
        value = ((TextView) result.findViewById(R.id.textView));
        value.setText(listDatum.getValue());
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_clicked_value = getItem(position).getId();
                Toast.makeText(context, getItem(position).getValue()+" selected", Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Confirmation");
                alertDialog.setMessage("Your Selection is "+getItem(position).getValue());
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent refresh = new Intent(context, ActivityDelivery.class);
                        refresh.putExtra(KEY_DELIVERY, "9"+where_from);
//                        context.startActivity(refresh);
//                        ((Activity)context).finish();
                        dialog.dismiss();
                    }
                });
                alertDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        return result;
    }
}
