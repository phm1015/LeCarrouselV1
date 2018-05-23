package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohan Mistry on 8/3/2017.
 */

public class CategoryListModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class Types {
        @SerializedName("typeId")
        public int typeId;
        @SerializedName("typeName")
        public String typeName;
    }

    public static class Data {
        @SerializedName("catId")
        public int catId;
        @SerializedName("catName")
        public String catName;
        @SerializedName("catDesc")
        public String catDesc;
        @SerializedName("catImage")
        public String catImage;
        @SerializedName("status")
        public String status;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("hide_name")
        public String hide_name;
        @SerializedName("hide_description")
        public String hide_description;
        @SerializedName("Types")
        public ArrayList<Types> Types;
    }
}
