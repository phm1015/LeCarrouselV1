package com.lecarrousel.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bitwin on 3/22/2017.
 */

public class MasterDataModel {
    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public static class Social_account {
        @SerializedName("sid")
        public int sid;
        @SerializedName("name")
        public String name;
        @SerializedName("iconUrl")
        public String iconUrl;
        @SerializedName("accountUrl")
        public String accountUrl;
        @SerializedName("pageId")
        public String pageId;
        @SerializedName("status")
        public int status;
        @SerializedName("priority")
        public int priority;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
    }

    public static class Term_condition {
        @SerializedName("tid")
        public int tid;
        @SerializedName("title_en")
        public String title_en;
        @SerializedName("title_ar")
        public String title_ar;
        @SerializedName("desc_en")
        public String desc_en;
        @SerializedName("desc_ar")
        public String desc_ar;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
    }

    public static class COUNTRIES {
        @SerializedName("country_id")
        public int country_id;
        @SerializedName("country_name")
        public String country_name;
        @SerializedName("short_code")
        public String short_code;
        @SerializedName("country_code")
        public String country_code;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("countryFlag")
        public String countryFlag;
        @SerializedName("time_zone")
        public String time_zone;
        @SerializedName("card_payment")
        public int card_payment;
        @SerializedName("credit_card_payment")
        public int credit_card_payment;
        @SerializedName("debit_card_payment")
        public int debit_card_payment;
        @SerializedName("cod_payment")
        public int cod_payment;
        @SerializedName("status")
        public String status;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("spinner_position")
        public int spinner_position;
        @SerializedName("is_checked")
        public boolean is_checked;
    }

    public static class Delivery_estimate_time {
        @SerializedName("tId")
        public int tId;
        @SerializedName("title")
        public String title;
        @SerializedName("fromTime")
        public String fromTime;
        @SerializedName("toTime")
        public String toTime;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
    }

    public static class Greeting_message {
        @SerializedName("gId")
        public String gId;
        @SerializedName("catID")
        public String catID;
        @SerializedName("msg")
        public String msg;
    }

    public static class Data {
        @SerializedName("social_account")
        public ArrayList<Social_account> social_account;
        @SerializedName("term_condition")
        public ArrayList<Term_condition> term_condition;
        @SerializedName("COUNTRIES")
        public ArrayList<COUNTRIES> COUNTRIES;
        @SerializedName("delivery_estimate_time")
        public ArrayList<Delivery_estimate_time> delivery_estimate_time;
        @SerializedName("greeting_message")
        public ArrayList<Greeting_message> greeting_message;
    }
}
