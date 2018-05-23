package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartCountListModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @SerializedName("cart_count")
        public int cart_count;
    }
}
