package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bitwin on 3/30/2017.
 */

public class CartListModel {


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

    public static class Cart_list {
        @SerializedName("cId")
        public int cId;
        @SerializedName("userId")
        public int userId;
        @SerializedName("pId")
        public int pId;
        @SerializedName("quantity")
        public int quantity;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("pType")
        public int pType;
        @SerializedName("pCatId")
        public String pCatId;
        @SerializedName("maxOrderQty")
        public int maxOrderQty;
        @SerializedName("catName")
        public String catName;
        @SerializedName("catDesc")
        public String catDesc;
        @SerializedName("catImage")
        public String catImage;
        @SerializedName("price")
        public int price;
        @SerializedName("quantity_price")
        public int quantity_price;
        @SerializedName("status")
        public int status;
        @SerializedName("isFavourite")
        public String isFavourite;
        @SerializedName("createdBy")
        public int createdBy;
        @SerializedName("pName")
        public String pName;
        @SerializedName("pDesc")
        public String pDesc;
        @SerializedName("imgData")
        public List<ImgData> imgData;
        @SerializedName("updatedAt")
        public String updatedAt;
    }

    public static class Default_address {
        @SerializedName("addressId")
        public int addressId;
        @SerializedName("userId")
        public int userId;
        @SerializedName("contactName")
        public String contactName;
        @SerializedName("phoneNo")
        public String phoneNo;
        @SerializedName("buildingNo")
        public String buildingNo;
        @SerializedName("streetNo")
        public String streetNo;
        @SerializedName("zone")
        public String zone;
        @SerializedName("streetAddress")
        public String streetAddress;
        @SerializedName("city")
        public String city;
        @SerializedName("country_id")
        public int country_id;
        @SerializedName("country_name")
        public String country_name;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
    }

    public static class Debit_card_list {
        @SerializedName("cId")
        public int cId;
        @SerializedName("userId")
        public int userId;
        @SerializedName("cardNumber")
        public String cardNumber;
        @SerializedName("cardHolderName")
        public String cardHolderName;
        @SerializedName("expiryDate")
        public String expiryDate;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
    }

    public static class Data {
        @SerializedName("cart_list")
        public List<Cart_list> cart_list;
        @SerializedName("default_address")
        public List<Default_address> default_address;
        @SerializedName("debit_card_list")
        public List<Debit_card_list> debit_card_list;
    }
}
