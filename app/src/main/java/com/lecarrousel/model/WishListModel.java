package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WishListModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class ImgData {
        @SerializedName("isDefault")
        public String isDefault;
        @SerializedName("img")
        public String img;
    }

    public static class Data {
        @SerializedName("pId")
        public int pId;
        @SerializedName("pType")
        public int pType;
        @SerializedName("pCatId")
        public int pCatId;
        @SerializedName("catName")
        public String catName;
        @SerializedName("catDesc")
        public String catDesc;
        @SerializedName("catImage")
        public String catImage;
        @SerializedName("pPrice")
        public int pPrice;
        @SerializedName("status")
        public int status;
        @SerializedName("isFavourite")
        public int isFavourite;
        @SerializedName("createdBy")
        public int createdBy;
        @SerializedName("pName")
        public String pName;
        @SerializedName("pDesc")
        public String pDesc;
        @SerializedName("imgData")
        public List<ImgData> imgData;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("pShareLink")
        public String pShareLink;
        @SerializedName("maxOrderQty")
        public String maxOrderQty;
        @SerializedName("hide_name")
        public String hide_name;
        @SerializedName("hide_description")
        public String hide_description;
    }
}
