package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AddToCartModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @SerializedName("cart_count")
        public int cart_count;
        @SerializedName("store_id")
        public int store_id;
    }
}
