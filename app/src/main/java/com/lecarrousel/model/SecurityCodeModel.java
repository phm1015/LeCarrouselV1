package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

public class SecurityCodeModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("security_code")
        public String security_code;
    }
}
