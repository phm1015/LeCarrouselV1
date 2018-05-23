package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rohan Mistry on 7/3/2017.
 */

public class LoginModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @SerializedName("id")
        public int id;
        @SerializedName("role")
        public String role;
        @SerializedName("name")
        public String name;
        @SerializedName("email")
        public String email;
        @SerializedName("phone")
        public String phone;
        @SerializedName("country_id")
        public int country_id;
        @SerializedName("store_id")
        public int store_id;
        @SerializedName("branch_id")
        public int branch_id;
        @SerializedName("location")
        public String location;
        @SerializedName("latitude")
        public String latitude;
        @SerializedName("longitude")
        public String longitude;
        @SerializedName("logo")
        public String logo;
        @SerializedName("deviceId")
        public String deviceId;
        @SerializedName("deviceType")
        public String deviceType;
        @SerializedName("notification_status")
        public int notification_status;
        @SerializedName("securityCode")
        public String securityCode;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("status")
        public String status;
        @SerializedName("blockTill")
        public String blockTill;
        @SerializedName("confirmOrderCode")
        public String confirmOrderCode;
        @SerializedName("forgotPasswordCode")
        public String forgotPasswordCode;
    }
}
