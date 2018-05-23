package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bitwin on 4/5/2017.
 */

public class PlaceOrderModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("orderId")
        public int orderId;
        @SerializedName("store_id")
        public int store_id;
    }
}
