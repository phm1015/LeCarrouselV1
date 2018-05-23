package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bitwin on 4/6/2017.
 */

public class OrderDetailModel {


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

    public static class ProductData {
        @SerializedName("detailId")
        public int detailId;
        @SerializedName("orderId")
        public int orderId;
        @SerializedName("pId")
        public int pId;
        @SerializedName("sellerId")
        public int sellerId;
        @SerializedName("name")
        public String name;
        @SerializedName("productPrice")
        public int productPrice;
        @SerializedName("quantity")
        public int quantity;
        @SerializedName("totalProductPrice")
        public int totalProductPrice;
        @SerializedName("cartMessage")
        public String cartMessage;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("pName")
        public String pName;
        @SerializedName("pDesc")
        public String pDesc;
        @SerializedName("imgData")
        public List<ImgData> imgData;
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
        @SerializedName("mobile")
        public String mobile;
        @SerializedName("paymentMethod")
        public String paymentMethod;
        @SerializedName("discountPrice")
        public int discountPrice;
        @SerializedName("totalPrice")
        public int totalPrice;
        @SerializedName("expected_delivery_date")
        public String expected_delivery_date;
        @SerializedName("delivery_estimate_time")
        public int delivery_estimate_time;
        @SerializedName("cartMessage")
        public String cartMessage;
        @SerializedName("status")
        public int status;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("address")
        public String address;
        @SerializedName("productData")
        public List<ProductData> productData;
    }
}
