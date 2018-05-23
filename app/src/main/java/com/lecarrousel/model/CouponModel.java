package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CouponModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @SerializedName("discountId")
        public int discountId;
        @SerializedName("couponCode")
        public String couponCode;
        @SerializedName("discount")
        public int discount;
        @SerializedName("name")
        public String name;
        @SerializedName("description")
        public String description;
        @SerializedName("startDate")
        public String startDate;
        @SerializedName("endDate")
        public String endDate;
        @SerializedName("status")
        public String status;
        @SerializedName("totalprice")
        public String totalprice;
        @SerializedName("totaldiscount")
        public String totaldiscount;
        @SerializedName("totalorderprice")
        public String totalorderprice;
    }
}
