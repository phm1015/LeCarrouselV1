package com.lecarrousel.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.gson.Gson;
import com.lecarrousel.App;
import com.lecarrousel.R;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.ActLoginBinding;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

public class LoginActivity extends Activity {
    private ActLoginBinding mBinding;
    private Activity ACTIVITY = LoginActivity.this;
    private ProgressHUD mProgressHUD;
    private SharedPrefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_login);
        mPrefs = new SharedPrefs(ACTIVITY);

        mBinding.tvArebic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrefs.putString(SharedPrefs.LANGUAGE, "ar");
                App.setLocale(new Locale("ar"));
                ACTIVITY.finish();
                startActivity(new Intent(ACTIVITY, SplashActivity.class));
            }
        });
        mBinding.tvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrefs.putString(SharedPrefs.LANGUAGE, "en");
                App.setLocale(new Locale("en"));
                ACTIVITY.finish();
                startActivity(new Intent(ACTIVITY, SplashActivity.class));
            }
        });

        mBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidations()) {
                    if (Utils.checkNetwork(ACTIVITY) == 1) {
                        requestLogin();
                    } else {
                        Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.err_network_not_available));
                    }
                }
            }
        });

        if (mPrefs.getString(SharedPrefs.LOGIN_EMAIL) != null) {
            mBinding.etEmail.setText(mPrefs.getString(SharedPrefs.LOGIN_EMAIL));
            mBinding.etEmail.setSelection(mPrefs.getString(SharedPrefs.LOGIN_EMAIL).length());
        }

        mBinding.layCreateAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ACTIVITY, RegisterActivity.class);

                startActivity(i);
                if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                } else {
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                finish();
            }
        });

        mBinding.tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ACTIVITY, ForgotPasswordActivity.class));
                if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                } else {
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                finish();
            }
        });

        mBinding.tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPrefs.getString(SharedPrefs.STORE_ID).equalsIgnoreCase("")){
                    mPrefs.putString(SharedPrefs.STORE_ID,"178");
                }
                Intent _intent = new Intent(ACTIVITY, HomeActivity.class);
                startActivity(_intent);
                if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                } else {
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                finish();
            }
        });
        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(ACTIVITY, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(ACTIVITY, mBinding.tvEnglish, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvArebic, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvEmailAddLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvPasswordLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvCreateNewPassLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvSkip, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvLogin, Utils.FontStyle.BOLD);
    }

    private boolean checkValidations() {
        if (mBinding.etEmail.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_email_msg));
            mBinding.etEmail.requestFocus();
            return false;
        }
        if (!(mBinding.etEmail.getText().toString().trim().matches(Utils.EMAIL_PATTERN))) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_valid_email_msg));
            mBinding.etEmail.requestFocus();
            return false;
        }
        if (mBinding.etPassword.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_password_msg));
            mBinding.etPassword.requestFocus();
            return false;
        }
        if (mBinding.etPassword.getText().toString().trim().length() < 6) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.password_minimum_character_msg));
            mBinding.etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void requestLogin() {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(LoginActivity.this, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<LoginModel> loginCall = new RestClient().getApiService().login(mPrefs.getString(SharedPrefs.LANGUAGE),
                mBinding.etEmail.getText().toString().trim(),
                mBinding.etPassword.getText().toString().trim());
        loginCall.enqueue(new retrofit2.Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                //Utils.HideProgress(DIALOG);
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success", response.body().message);
                        mPrefs.putBoolean(SharedPrefs.IS_LOGIN, true);
                        mPrefs.putString(SharedPrefs.USER_PHOTO, response.body().data.get(0).logo);
                        mPrefs.putString(SharedPrefs.LOGIN_EMAIL, response.body().data.get(0).email);
                        mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, new Gson().toJson(response.body().data.get(0)));
                        mPrefs.putString(SharedPrefs.USER_EMAIL, mBinding.etEmail.getText().toString().trim());
                        mPrefs.putString(SharedPrefs.USER_PASSWORD, mBinding.etPassword.getText().toString().trim());
                        mPrefs.putString(SharedPrefs.STORE_ID,"");
                        LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
                        Log.e("Tag", loginmodel.email);
                        Intent _intent = new Intent(ACTIVITY, HomeActivity.class);
                        startActivity(_intent);
                        if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                        } else {
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                        }
                        finish();
                    } else {
                        Utils.showAlertDialog(ACTIVITY, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                Log.e("error", "" + t.getMessage());
                //Utils.showAlertDialog(ACTIVITY, t.getMessage());

                if (t instanceof HttpException) {
                    Log.e("HTTP", ((HttpException) t).getMessage());
                    Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.err_network_not_available));
                }
                if (t instanceof IOException) {
                    Log.e("IOException", ((IOException) t).getMessage());
                }
            }
        });
    }
}



