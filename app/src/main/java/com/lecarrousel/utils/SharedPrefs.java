package com.lecarrousel.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lecarrousel.model.MasterDataModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefs {
    // Prefrence Key
    public static final String LOGIN_EMAIL = "user_email";
    public static final String LOGIN_RESPONSE = "user_login_response";
    public static final String IS_LOGIN = "is_user_login";
    public static final String IS_FCM_TOKEN_ADDED = "is_fcm_token";
    public static final String LANGUAGE = "language";
    public static final String USER_PHOTO = "user_photo";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PASSWORD = "user_password";
    public static final String CURRENT_LONGITUDE = "lat";
    public static final String CURRENT_LATITUDE = "lon";
    public static final String STORE_ID = "store_id";


    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;

    public SharedPrefs(Context context) {
        this.appSharedPrefs = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        this.prefsEditor = this.appSharedPrefs.edit();
    }


    public void putString(String key, String value) {
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public String getString(String key) {
        return appSharedPrefs.getString(key, "");
    }

    public void putBoolean(String key, boolean value) {
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public Boolean getBoolean(String key) {
        return appSharedPrefs.getBoolean(key, false);
    }

    public void putInteger(String key, int value) {
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    public Integer getInteger(String key) {
        return appSharedPrefs.getInt(key, 0);
    }

    public void putCountry(ArrayList<MasterDataModel.COUNTRIES> arrCountryList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrCountryList);
        putString("countryList", json);
    }

    public void putTimeList(ArrayList<MasterDataModel.Delivery_estimate_time> arrTimeList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrTimeList);
        putString("timeList", json);
    }

    public void putGreetingList(ArrayList<MasterDataModel.Greeting_message> arrGreetList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrGreetList);
        putString("greetMsgList", json);
    }

    public void putMasterDetail(String response) {

        putString("masterList", response);
    }

    public String getMasterDetail() {
        return getString("masterList");
    }

    public ArrayList<MasterDataModel.COUNTRIES> getCountryList() {
        Gson gson = new Gson();
        ArrayList<MasterDataModel.COUNTRIES> COUNTRY = new ArrayList<>();
        String json = getString("countryList");
        Type type = new TypeToken<List<MasterDataModel.COUNTRIES>>() {
        }.getType();
        COUNTRY = gson.fromJson(json, type);
        Log.e("country Size", "" + COUNTRY.size());
        return COUNTRY;
    }

    public ArrayList<MasterDataModel.Delivery_estimate_time> getEstimateTimeList() {
        Gson gson = new Gson();
        ArrayList<MasterDataModel.Delivery_estimate_time> TimeList = new ArrayList<>();
        String json = getString("timeList");
        Type type = new TypeToken<List<MasterDataModel.Delivery_estimate_time>>() {
        }.getType();
        TimeList = gson.fromJson(json, type);
        Log.e("time list Size", "" + TimeList.size());
        return TimeList;
    }

    public ArrayList<MasterDataModel.Greeting_message> getGreetMsgList() {
        Gson gson = new Gson();
        ArrayList<MasterDataModel.Greeting_message> GreetList = new ArrayList<>();
        String json = getString("greetMsgList");
        Type type = new TypeToken<List<MasterDataModel.Greeting_message>>() {
        }.getType();
        GreetList = gson.fromJson(json, type);
        Log.e("greet Size", "" + GreetList.size());
        return GreetList;
    }
}
