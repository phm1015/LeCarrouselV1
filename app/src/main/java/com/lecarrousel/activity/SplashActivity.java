package com.lecarrousel.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lecarrousel.R;
import com.lecarrousel.databinding.ActSplashBinding;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class SplashActivity extends Activity {
    private Activity ACTIVITY = SplashActivity.this;
    private ActSplashBinding mBinding;
    private SharedPrefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_splash);
        mPrefs = new SharedPrefs(ACTIVITY);

        ArrayList<String> mArrayPermissions = new ArrayList<>();
        mArrayPermissions.add(Manifest.permission.CAMERA);
        mArrayPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mArrayPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        mArrayPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        mArrayPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        mArrayPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        checkPermission(mArrayPermissions);
    }

    private void hideSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
                    if (Utils.checkNetwork(ACTIVITY) == 1) {
                        requestLogin();
                    } else {
                        Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.err_network_not_available));
                    }
                } else {
                    mPrefs.putBoolean(SharedPrefs.IS_LOGIN, false);
                    mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, "");
                    mPrefs.putString(SharedPrefs.STORE_ID, "");
                    mPrefs.putBoolean(SharedPrefs.IS_FCM_TOKEN_ADDED, false);
                    Intent _intent = new Intent(ACTIVITY, LoginActivity.class);
                    startActivity(_intent);
                    finish();
                }
            }
        }, 1000);
    }

    private void checkPermission(final ArrayList<String> mArrayPermissions) {
        Dexter.withActivity(this).withPermissions(mArrayPermissions).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    requestMasterDetail();
                } else {
                    ArrayList<String> mArrayPermissions = new ArrayList<>();
                    List<PermissionDeniedResponse> mDeniedPermission = report.getDeniedPermissionResponses();
                    for (PermissionDeniedResponse deniedResponse : mDeniedPermission) {
                        mArrayPermissions.add(deniedResponse.getPermissionName());
                    }
                    requestMasterDetail();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void requestLogin() {
        Call<LoginModel> loginCall = new RestClient().getApiService().login(mPrefs.getString(SharedPrefs.LANGUAGE), mPrefs.getString(SharedPrefs.USER_EMAIL), mPrefs.getString(SharedPrefs.USER_PASSWORD));
        loginCall.enqueue(new retrofit2.Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {

                        mPrefs.putBoolean(SharedPrefs.IS_LOGIN, true);
                        mPrefs.putString(SharedPrefs.USER_PHOTO, response.body().data.get(0).logo);
                        mPrefs.putString(SharedPrefs.LOGIN_EMAIL, response.body().data.get(0).email);
                        mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, new Gson().toJson(response.body().data.get(0)));
                        mPrefs.putString(SharedPrefs.STORE_ID,"");

                        Intent mIntent = new Intent(SplashActivity.this, HomeActivity.class);
                        if (getIntent().hasExtra("nt")) {
                            String ni = getIntent().getStringExtra("ni");
                            String nt = getIntent().getStringExtra("nt");
                            String orderId;
                            if(nt.equalsIgnoreCase("1")){
                                orderId = getIntent().getStringExtra("oId");
                            }else{
                                orderId = "";
                            }
                            String userId = getIntent().getStringExtra("userId");
                            mIntent.putExtra("ni", ni);
                            mIntent.putExtra("nt", nt);
                            mIntent.putExtra("oId", orderId);
                            mIntent.putExtra("userId", userId);
                        }
                        startActivity(mIntent);
                        finish();
                    } else {
                        mPrefs.putBoolean(SharedPrefs.IS_LOGIN, false);
                        mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, "");
                        mPrefs.putString(SharedPrefs.STORE_ID, "");
                        mPrefs.putBoolean(SharedPrefs.IS_FCM_TOKEN_ADDED, false);
                        Intent _intent = new Intent(ACTIVITY, LoginActivity.class);
                        startActivity(_intent);
                        finish();
                    }
                } else {
                    mPrefs.putBoolean(SharedPrefs.IS_LOGIN, false);
                    mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, "");
                    mPrefs.putString(SharedPrefs.STORE_ID, "");
                    mPrefs.putBoolean(SharedPrefs.IS_FCM_TOKEN_ADDED, false);
                    Intent _intent = new Intent(ACTIVITY, LoginActivity.class);
                    startActivity(_intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Log.e("error", "" + t.getMessage());
                mPrefs.putBoolean(SharedPrefs.IS_LOGIN, false);
                mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, "");
                mPrefs.putString(SharedPrefs.STORE_ID, "");
                mPrefs.putBoolean(SharedPrefs.IS_FCM_TOKEN_ADDED, false);
                Intent _intent = new Intent(ACTIVITY, LoginActivity.class);
                startActivity(_intent);
                finish();
            }
        });
    }

    private void requestMasterDetail() {
        if (Utils.checkNetwork(ACTIVITY) != 0) {
            Call<MasterDataModel> masterCall = new RestClient().getApiService().masterData(mPrefs.getString(SharedPrefs.LANGUAGE));
            masterCall.enqueue(new retrofit2.Callback<MasterDataModel>() {
                @Override
                public void onResponse(Call<MasterDataModel> call, retrofit2.Response<MasterDataModel> response) {
                    if (response != null) {
                        if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                            mPrefs.putMasterDetail(new Gson().toJson(response.body()));
                            mPrefs.putCountry(response.body().data.get(0).COUNTRIES);
                            mPrefs.putTimeList(response.body().data.get(0).delivery_estimate_time);
                            mPrefs.putGreetingList(response.body().data.get(0).greeting_message);
                        }
                        hideSplashScreen();
                    }
                }

                @Override
                public void onFailure(Call<MasterDataModel> call, Throwable t) {
                    Log.e("error", "" + t.getMessage());
                    hideSplashScreen();
                }
            });
        } else {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.err_network_not_available));
        }
    }
}
