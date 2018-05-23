package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bitwin on 3/16/2017.
 */

public class UserAccountModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class User_information {
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
    }

    public static class Address {
        @SerializedName("addressId")
        public int addressId;
        @SerializedName("userId")
        public int userId;
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
        public int country_id;
        @SerializedName("country_name")
        public String country_name;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
    }

    public static class Card {
        @SerializedName("cId")
        public int cId;
        @SerializedName("userId")
        public int userId;
        @SerializedName("cardNumber")
        public String cardNumber;
        @SerializedName("cardHolderName")
        public String cardHolderName;
        @SerializedName("expiryDate")
        public String expiryDate;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
    }

    public static class Data {
        @SerializedName("user_information")
        public List<User_information> user_information;
        @SerializedName("address")
        public List<Address> address;
        @SerializedName("card")
        public List<Card> card;
    }
}
