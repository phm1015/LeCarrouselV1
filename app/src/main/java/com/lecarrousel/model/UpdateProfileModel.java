package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bitwin on 3/24/2017.
 */

public class UpdateProfileModel {

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public class Data {
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
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("status")
        public String status;
        @SerializedName("country_id")
        public int country_id;
        @SerializedName("store_id")
        public int store_id;
    }
}
