package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bitwin on 3/7/2017.
 */

public class ForgetPasswordModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public class Data {
        @SerializedName("email")
        public String email;
        @SerializedName("forgotPasswordCode")
        public String forgotPasswordCode;
    }
}
