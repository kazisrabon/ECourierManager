/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConsignmentListDatum implements Serializable {

    @SerializedName("recipient_name")
    @Expose
    private String recipient_name;

    @SerializedName("recipient_mobile")
    @Expose
    private String recipient_mobile;

    @SerializedName("recipient_address")
    @Expose
    private String recipient_address;

    @SerializedName("consignment_no")
    @Expose
    private String consignment_no;

    @SerializedName("product_id")
    @Expose
    private String product_id;

    @SerializedName("items")
    @Expose
    private String items;

    @SerializedName("item_type")
    @Expose
    private String item_type;

    @SerializedName("sender_group")
    @Expose
    private String sender_group;

    @SerializedName("eso")
    @Expose
    private String eso;

    @SerializedName("eso_mobile")
    @Expose
    private String eso_mobile;

    @SerializedName("alter_mobile")
    @Expose
    private String alter_mobile;

    @SerializedName("order_time")
    @Expose
    private String order_time;

    @SerializedName("product_price")
    @Expose
    private String product_price;

    @SerializedName("shipping_price")
    @Expose
    private String shipping_price;

    @SerializedName("payment_method")
    @Expose
    private String payment_method;

    @SerializedName("parcel_status")
    @Expose
    private String parcel_status;

    @SerializedName("status_code")
    @Expose
    private String status_code;

    @SerializedName("actual_delivery_time")
    @Expose
    private String actual_delivery_time;

    @SerializedName("collected_amount")
    @Expose
    private String collected_amount;

    @SerializedName("items_delivered")
    @Expose
    private String items_delivered;

    @SerializedName("source_do")
    @Expose
    private String source_do;

    @SerializedName("destination_do")
    @Expose
    private String destination_do;

    @SerializedName("pick_agent")
    @Expose
    private String pick_agent;

    @SerializedName("delivery_agent")
    @Expose
    private String delivery_agent;

    @SerializedName("return_agent")
    @Expose
    private String return_agent;

    public String getRecipient_name() {
        return recipient_name;
    }

    public void setRecipient_name(String recipient_name) {
        this.recipient_name = recipient_name;
    }

    public String getRecipient_mobile() {
        return recipient_mobile;
    }

    public void setRecipient_mobile(String recipient_mobile) {
        this.recipient_mobile = recipient_mobile;
    }

    public String getRecipient_address() {
        return recipient_address;
    }

    public void setRecipient_address(String recipient_address) {
        this.recipient_address = recipient_address;
    }

    public String getConsignment_no() {
        return consignment_no;
    }

    public void setConsignment_no(String consignment_no) {
        this.consignment_no = consignment_no;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public String getSender_group() {
        return sender_group;
    }

    public void setSender_group(String sender_group) {
        this.sender_group = sender_group;
    }

    public String getEso() {
        return eso;
    }

    public void setEso(String eso) {
        this.eso = eso;
    }

    public String getEso_mobile() {
        return eso_mobile;
    }

    public void setEso_mobile(String eso_mobile) {
        this.eso_mobile = eso_mobile;
    }

    public String getAlter_mobile() {
        return alter_mobile;
    }

    public void setAlter_mobile(String alter_mobile) {
        this.alter_mobile = alter_mobile;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getShipping_price() {
        return shipping_price;
    }

    public void setShipping_price(String shipping_price) {
        this.shipping_price = shipping_price;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getParcel_status() {
        return parcel_status;
    }

    public void setParcel_status(String parcel_status) {
        this.parcel_status = parcel_status;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getActual_delivery_time() {
        return actual_delivery_time;
    }

    public void setActual_delivery_time(String actual_delivery_time) {
        this.actual_delivery_time = actual_delivery_time;
    }

    public String getCollected_amount() {
        return collected_amount;
    }

    public void setCollected_amount(String collected_amount) {
        this.collected_amount = collected_amount;
    }

    public String getItems_delivered() {
        return items_delivered;
    }

    public void setItems_delivered(String items_delivered) {
        this.items_delivered = items_delivered;
    }

    public String getSource_do() {
        return source_do;
    }

    public void setSource_do(String source_do) {
        this.source_do = source_do;
    }

    public String getDestination_do() {
        return destination_do;
    }

    public void setDestination_do(String destination_do) {
        this.destination_do = destination_do;
    }

    public String getReturn_agent() {
        return return_agent;
    }

    public void setReturn_agent(String return_agent) {
        this.return_agent = return_agent;
    }

    public String getPick_agent() {
        return pick_agent;
    }

    public void setPick_agent(String pick_agent) {
        this.pick_agent = pick_agent;
    }

    public String getDelivery_agent() {
        return delivery_agent;
    }

    public void setDelivery_agent(String delivery_agent) {
        this.delivery_agent = delivery_agent;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status_code = "+status_code+", alter_mobile = "+alter_mobile+", return_agent = "+return_agent+", product_id = "+product_id+", delivery_agent = "+delivery_agent+", product_price = "+product_price+", pick_agent = "+pick_agent+", recipient_mobile = "+recipient_mobile+", sender_group = "+sender_group+", eso_mobile = "+eso_mobile+", items_delivered = "+items_delivered+", recipient_name = "+recipient_name+", destination_do = "+destination_do+", collected_amount = "+collected_amount+", eso = "+eso+", items = "+items+", order_time = "+order_time+", actual_delivery_time = "+actual_delivery_time+", consignment_no = "+consignment_no+", payment_method = "+payment_method+", recipient_address = "+recipient_address+", source_do = "+source_do+", parcel_status = "+parcel_status+", shipping_price = "+shipping_price+", item_type = "+item_type+"]";
    }
}