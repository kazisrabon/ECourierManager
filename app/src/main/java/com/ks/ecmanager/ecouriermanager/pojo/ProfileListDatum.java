/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileListDatum {

    private String space = " ";

    @SerializedName("avg_review")
    @Expose
    private String avgReview;

    @SerializedName("blood_group")
    @Expose
    private String blood_group;

    @SerializedName("nid")
    @Expose
    private String nid;

    @SerializedName("m_name")
    @Expose
    private String name;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    @SerializedName("join_date")
    @Expose
    private String joinDate;

    @SerializedName("do_location")
    @Expose
    private String deliveryZone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getDeliveryZone() {
        return deliveryZone;
    }

    public void setDeliveryZone(String deliveryZone) {
        this.deliveryZone = deliveryZone;
    }


    public String getAvgReview() {
        return avgReview;
    }

    public void setAvgReview(String avgReview) {
        this.avgReview = avgReview;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    @Override
    public String toString() {
        return name
                +space+joinDate
                +space+deliveryZone
                +space+avgReview
                +space+profilePic;
    }
}
