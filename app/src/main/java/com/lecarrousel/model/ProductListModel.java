package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bitwin on 3/9/2017.
 */

public class ProductListModel {

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
        public String pCatId;
        @SerializedName("pPrice")
        public int pPrice;
        @SerializedName("status")
        public int status;
        @SerializedName("isHighlighted")
        public int isHighlighted;
        @SerializedName("isFavourite")
        public String isFavourite;
        @SerializedName("createdBy")
        public int createdBy;
        @SerializedName("pStock")
        public int pStock;
        @SerializedName("pUnlimitedStock")
        public int pUnlimitedStock;
        @SerializedName("maxOrderQty")
        public int maxOrderQty;
        @SerializedName("pName")
        public String pName;
        @SerializedName("pDesc")
        public String pDesc;
        @SerializedName("highlightText")
        public String highlightText;
        @SerializedName("imgData")
        public List<ImgData> imgData;
        @SerializedName("videoUrl")
        public String videoUrl;
        @SerializedName("thumb_video_url")
        public String thumb_video_url;
        @SerializedName("pShareLink")
        public String pShareLink;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
    }
}
