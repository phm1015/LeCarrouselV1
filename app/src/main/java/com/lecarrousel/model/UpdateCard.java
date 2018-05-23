package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bitwin on 3/27/2017.
 */

public class UpdateCard {

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public class Data {
    }
}
