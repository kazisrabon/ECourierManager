/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.ecmanager.ecouriermanager.R;
import com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase;
import com.ks.ecmanager.ecouriermanager.activities.secondLayer.ActivityDelivery;
import com.ks.ecmanager.ecouriermanager.activities.thirdLayer.ActivityMultipleScan;
import com.ks.ecmanager.ecouriermanager.pojo.ListDatum;
import com.ks.ecmanager.ecouriermanager.session.SessionUserData;

import java.util.HashMap;
import java.util.List;

import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.KEY_DELIVERY;
import static com.ks.ecmanager.ecouriermanager.activities.base.ActivityBase.db;
import static com.ks.ecmanager.ecouriermanager.activities.initLayer.ActivityLogin.sessionUserData;

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
    private ActivityBase activityBase;

    public ListItemAdapter(Context context, List<ListDatum> listData, String where_from) {
        this.context = context;
        this.listData = listData;
        this.where_from = where_from;
        activityBase = new ActivityBase();
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
//                Toast.makeText(context, getItem(position).getValue()+" selected", Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Confirmation");
                alertDialog.setMessage("Your Selection is "+getItem(position).getValue());
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, String> user = SessionUserData.getSFInstance(context).getSessionDetails();
                        String group = user.get(SessionUserData.KEY_USER_GROUP);
                        if (activityBase.stringNotNullCheck(list_clicked_value)) {
                            if (where_from.contains("1")) {
                                sessionUserData.setKeyNextStatus(list_clicked_value);
                                activityBase.setCurrentStatus(list_clicked_value);
                                activityBase.setCheckChangeAgents(list_clicked_value);
                                activityBase.setCheckChangeDOs(list_clicked_value);
                                if (activityBase.getCheckChangeAgents()) {
                                    Intent refresh = new Intent(context, ActivityDelivery.class);
                                    refresh.putExtra(KEY_DELIVERY, "2");
                                    context.startActivity(refresh);
                                    ((Activity)context).finish();
                                    dialog.dismiss();
                                } else if (activityBase.getCheckChangeDOs()) {
                                    Intent refresh = new Intent(context, ActivityDelivery.class);
                                    refresh.putExtra(KEY_DELIVERY, "3");
                                    context.startActivity(refresh);
                                    ((Activity)context).finish();
                                    dialog.dismiss();
                                } else {
                                    HashMap<String, String> update = SessionUserData.getSFInstance(context).getStatusDetails();
                                    String nextStatus = update.get(SessionUserData.KEY_NEXT_STATUS);
                                    String agentID = update.get(SessionUserData.KEY_AGENT_ID);
                                    String doID = update.get(SessionUserData.KEY_DO_ID);
                                    sessionUserData.setKeyStatus(db.getCurrent_status(nextStatus, agentID, doID, group));
                                    dialog.dismiss();
                                    context.startActivity(new Intent(context, ActivityMultipleScan.class));
                                    ((Activity)context).finish();
                                }
                            }
                            else if (where_from.contains("2")) {
                                sessionUserData.setKeyAgentId(list_clicked_value);
                                if (activityBase.getCheckChangeDOforAgent(list_clicked_value)) {
                                    Intent refresh = new Intent(context, ActivityDelivery.class);
                                    refresh.putExtra(KEY_DELIVERY, "3");
                                    context.startActivity(refresh);
                                    ((Activity)context).finish();
                                    dialog.dismiss();
                                } else {
                                    HashMap<String, String> update = SessionUserData.getSFInstance(context).getStatusDetails();
                                    String nextStatus = update.get(SessionUserData.KEY_NEXT_STATUS);
                                    String agentID = update.get(SessionUserData.KEY_AGENT_ID);
                                    String doID = update.get(SessionUserData.KEY_DO_ID);
                                    sessionUserData.setKeyStatus(db.getCurrent_status(nextStatus, agentID, doID, group));
                                    dialog.dismiss();
                                    context.startActivity(new Intent(context, ActivityMultipleScan.class));
                                    ((Activity)context).finish();
                                }
                            }
                            else if (where_from.contains("3")) {
                                sessionUserData.setKeyDoId(list_clicked_value);
                                HashMap<String, String> update = SessionUserData.getSFInstance(context).getStatusDetails();
                                String nextStatus = update.get(SessionUserData.KEY_NEXT_STATUS);
                                String agentID = update.get(SessionUserData.KEY_AGENT_ID);
                                String doID = update.get(SessionUserData.KEY_DO_ID);
                                sessionUserData.setKeyStatus(db.getCurrent_status(nextStatus, agentID, doID, group));
                                dialog.dismiss();
                                context.startActivity(new Intent(context, ActivityMultipleScan.class));
                                ((Activity)context).finish();
                            }
                            else {
                                dialog.dismiss();
                                Toast.makeText(context, "Something is wrong!!!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            dialog.dismiss();
                            Toast.makeText(context, "Please Click Again...", Toast.LENGTH_LONG).show();
                        }
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
