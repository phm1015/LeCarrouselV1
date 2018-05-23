package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bitwin on 3/16/2017.
 */

public class AddAddressModel {

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("userId")
        public String userId;
        @SerializedName("contactName")
        public String contactName;
        @SerializedName("phoneNo")
        public String phoneNo;
        @SerializedName("buildingNo")
        public String buildingNo;
        @SerializedName("streetNo")
        public String streetNo;
        @SerializedName("zone")
        public String zone;
        @SerializedName("streetAddress")
        public String streetAddress;
        @SerializedName("city")
        public String city;
        @SerializedName("country_id")
        public String country_id;
        @SerializedName("country_name")
        public String country_name;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("addressId")
        public int addressId;
    }
}
