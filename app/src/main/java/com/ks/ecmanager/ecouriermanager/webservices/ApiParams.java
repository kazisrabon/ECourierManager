/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.webservices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ApiParams {

    public static final long SPLASH_TIME_OUT = 3 * 1000;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat longDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static final DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    public static final DateFormat longTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
//  http://ecourier.com.bd
//    public static final String TAG_BASE_URL = "http://ecourier.com.bd/manager";
    public static final String TAG_BASE_URL = "http://test.ecourier.com.bd/manager";

    public static final boolean TAG_SUCCESS = true;
    public static final boolean TAG_FAILURE = false;

    public static final String USER_TYPE_ADMIN = "1";
    public static final String USER_TYPE_USER = "2";

    // common params
    public static final String PARAM_AUTHENTICATION_KEY = "authentication_key";//authentication_key
    public static final String PARAM_ADMIN_ID = "admin_id";
    public static final String PARAM_GROUP = "group";//group

    // user login
    public static final String TAG_LOGIN_KEY = "andLogin.php";
    public static final String PARAM_USER_NAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_USER_TYPE = "user_type";

    // product search
    public static final String TAG_CONSIGNMENT_SEARCH_KEY = "andConsignmentSearch.php";
    public static final String TAG_CONSIGNMENT_NO = "consignment_no";
    public static final String TAG_RECEIVER_NO = "receiver_no";
    public static final String TAG_PRODUCT_ID = "product_id";

    // consignment list
    public static final String TAG_CONSIGNMENT_LIST_KEY = "andConsignmentList.php";
    public static final String PARAM_TYPE = "type";

    // parcel update
    public static final String TAG_CONSIGNMENT_UPDATE_KEY = "andConsignmentListUpdate.php";
    public static final String PARAM_PARCEL_PRICE = "product_price";
    public static final String PARAM_RECIPIENT_NAME = "recipient_name";
    public static final String PARAM_RECIPIENT_MOBILE = "recipient_mobile";
    public static final String PARAM_RECIPIENT_ADDRESS = "recipient_address";

    // product review
    public static final String TAG_USER_REVIEW_KEY = "userReview.php";
    public static final String TAG_CONSIGNMENT_ID = "consignment_id";//consignment_id
    public static final String PARAM_SERVICE_REVIEW = "service_review";//service_review
    public static final String PARAM_PRODUCT_REVIEW = "product_reviw";//product_reviw
    public static final String PARAM_FILE_NAME = "filename";

    // reports
    public static final String TAG_REPORTS_KEY = "andReports.php";
    public static final String PARAM_FROM_DATE = "from_date";
    public static final String PARAM_TO_DATE = "to_date";
    public static final String PARAM_RECIPIENT_TYPE = "type";

    // profile
    public static final String TAG_PROFILE_KEY = "userProfile.php";
    public static final String TAG_PROFILE_UPDATE_KEY = "userProfileUpdate.php";

    // announcements
    public static final String TAG_ANNOUNCEMENT_KEY= "andAnnouncements.php";
    public static final String PARAM_ANNOUNCEMENT_ID= "id";

//    attendece
    public static final String TAG_AGENT_ATTENDENCE_UPDATE_KEY = "andAttendance.php";
    public static final String PARAM_DISTANCE= "distance";

//    update profile
    public static final String TAG_AGENT_PROFLE_UPDATE_KEY = "userProfileUpdate.php";
    public static final String PARAM_BLOOD_GROUP= "blood_group";
    public static final String PARAM_NID= "nid";
    public static final String PARAM_DOB= "dob";

//    do list
    public static final String TAG_DO_LIST_KEY = "getDoList.php";

//    do agents list
    public static final String TAG_DO_AGENT_LIST_KEY = "getAgentList.php";

//    parcel status update
    public static final String TAG_PARCEL_STATUS_UPDATE_KEY = "andParcelStatusUpdate.php";
    public static final String PARAM_CANCEL_CALL_TIME= "cancel_call_time";
    public static final String PARAM_RETURN_AGENT= "rtn_agent";
    public static final String PARAM_D_DO= "d_do";
    public static final String PARAM_AGENT_ID = "agent_id";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_COMMENT = "comment";
    public static final String PARAM_COLLECTED_AMOUNT = "collected_amount";
    public static final String PARAM_COLLECTED_ITEMS = "items";
    public static final String PARAM_CONSIGNMENT_NO = "consignment_no";

//    app config
    public static final String TAG_APP_CONFIG = "appConfig.php";

//    bulk
    public static final String TAG_BULK = "andMultipleParcelStatusUpdate.php";
}
