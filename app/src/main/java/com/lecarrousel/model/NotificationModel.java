package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationModel {


    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @SerializedName("notificationId")
        public String notificationId;
        @SerializedName("userId")
        public String userId;
        @SerializedName("pId")
        public String pId;
        @SerializedName("notificationType")
        public String notificationType;
        @SerializedName("notificationText")
        public String notificationText;
        @SerializedName("orderId")
        public String orderId;
        @SerializedName("messageId")
        public int messageId;
        @SerializedName("isRead")
        public String isRead;
        @SerializedName("isDeleted")
        public int isDeleted;
        @SerializedName("isSendToUser")
        public int isSendToUser;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("orderStatus")
        public String orderStatus;
        @SerializedName("eta")
        public String eta;
    }
}
