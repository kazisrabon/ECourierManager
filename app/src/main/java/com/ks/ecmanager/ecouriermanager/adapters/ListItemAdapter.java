/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.pojo.ListDatum;

import org.apache.commons.collections4.BidiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kazi Srabon on 7/24/2017.
 */

public class ListItemAdapter extends BaseAdapter {
    private Context context;
    private List<ListDatum> listData;
//    private String where_from;
    private LayoutInflater inflater;
    private ListDatum listDatum;

    public ListItemAdapter(Context context, List<ListDatum> listData) {
        this.context = context;
        this.listData = listData;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        } else {
            result = convertView;
        }
        listDatum = listData.get(position);
        ((TextView) result.findViewById(R.id.textView)).setText(listDatum.getValue());
        if (convertView != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, listDatum.getId()+" "+listDatum.getValue(), Toast.LENGTH_SHORT).show();
                }
            });
        }
//        String listDatum = bidiMap.get(position);
//        TextView tvEcrNumber = (TextView) convertView.findViewById(R.id.tvEcrNumber);

        return result;
    }
}