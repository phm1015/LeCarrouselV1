package com.lecarrousel.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "Tag";
    private SharedPrefs mPrefs;
    private String userId = "";

    @Override
    public void onTokenRefresh() {
        mPrefs = new SharedPrefs(getApplication().getApplicationContext());
        //Get hold of the registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log the token
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginmodel.id);
            requestAddToken(userId, refreshedToken);
        } else {
            userId = "";
            requestAddToken(userId, refreshedToken);
        }
    }

    private void requestAddToken(String userId, String refreshedToken) {
        //Call Webservice
        Call<GeneralModel> loginCall = new RestClient().getApiService().addDeviceToken(mPrefs.getString(SharedPrefs.LANGUAGE),userId, refreshedToken, "android");
        loginCall.enqueue(new retrofit2.Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
//                        Log.e("Success", response.body().message);

                    } else {
//                        Log.e("Success", response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                Log.e("error", "" + t.getMessage());
                if (t instanceof HttpException) {
                    Log.e("HTTP", ((HttpException) t).getMessage());
                }
                if (t instanceof IOException) {
                    Log.e("IOException", ((IOException) t).getMessage());
                }
            }
        });
    }
}