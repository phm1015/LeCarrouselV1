package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bitwin on 4/5/2017.
 */

public class OrderListModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class ImgData {
        @SerializedName("isDefault")
        public String isDefault;
        @SerializedName("img")
        public String img;
    }

    public static class Data {
        @SerializedName("orderId")
        public int orderId;
        @SerializedName("orderIncreementId")
        public String orderIncreementId;
        @SerializedName("userId")
        public int userId;
        @SerializedName("couponCode")
        public String couponCode;
        @SerializedName("contactName")
        public String contactName;
        @SerializedName("streetAddress")
        public String streetAddress;
        @SerializedName("city")
        public String city;
        @SerializedName("country_id")
        public int country_id;
        @SerializedName("country_name")
        public String country_name;
        @SerializedName("mobile")
        public String mobile;
        @SerializedName("paymentMethod")
        public String paymentMethod;
        @SerializedName("discountPrice")
        public int discountPrice;
        @SerializedName("totalPrice")
        public int totalPrice;
        @SerializedName("status")
        public int status;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("address")
        public String address;
        @SerializedName("pName")
        public String pName;
        @SerializedName("imgData")
        public List<ImgData> imgData;
    }
}
