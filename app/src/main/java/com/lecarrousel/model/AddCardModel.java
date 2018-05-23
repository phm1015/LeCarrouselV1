package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bitwin on 3/16/2017.
 */

public class AddCardModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("userId")
        public String userId;
        @SerializedName("cardNumber")
        public String cardNumber;
        @SerializedName("cardHolderName")
        public String cardHolderName;
        @SerializedName("expiryDate")
        public String expiryDate;
        @SerializedName("UpdatedAt")
        public String UpdatedAt;
        @SerializedName("CreatedAt")
        public String CreatedAt;
        @SerializedName("cId")
        public int cId;
    }
}
