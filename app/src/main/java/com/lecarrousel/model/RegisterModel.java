package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bitwin on 4/18/2017.
 */

public class RegisterModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("id")
        public int id;
        @SerializedName("verificationCode")
        public int verificationCode;
    }
}
