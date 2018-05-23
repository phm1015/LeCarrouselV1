package com.lecarrousel.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lecarrousel.R;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.ActForgotPasswordBinding;
import com.lecarrousel.model.ForgetPasswordModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActForgotPasswordBinding mBinding;
    private Activity ACTIVITY = ForgotPasswordActivity.this;
    private ProgressHUD mProgressHUD;
    private SharedPrefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_forgot_password);
        mPrefs = new SharedPrefs(ACTIVITY);
        mBinding.layHeader.layCart.setVisibility(View.INVISIBLE);
        mBinding.layHeader.ivLeftAction.setImageDrawable(ContextCompat.getDrawable(ACTIVITY, R.drawable.ic_back));

        mBinding.layHeader.ivLeftAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBinding.tvRecoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidations()) {

                    if (Utils.checkNetwork(ACTIVITY) == 1) {
                        requestForgotPassword();
                    } else {
                        Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.err_network_not_available));
                    }
                }
            }
        });
        mBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(ACTIVITY, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(ACTIVITY, mBinding.tvEmailAddLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvMobileNo, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvRecoverPass, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvRecoverPassLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvCancel, Utils.FontStyle.BOLD);

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ACTIVITY, LoginActivity.class);
        startActivity(i);
        if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {

            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        }
        finish();
    }

    private boolean checkValidations() {

        if ((mBinding.etEmail.getText().toString().trim().equalsIgnoreCase("")) && (mBinding.etMobileNo.getText().toString().trim().equalsIgnoreCase(""))) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_email_or_mobile_msg));
            return false;
        } else if (!(mBinding.etEmail.getText().toString().trim().equalsIgnoreCase(""))) {
            if (!mBinding.etEmail.getText().toString().trim().matches(Utils.EMAIL_PATTERN)) {
                Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_valid_email_msg));
                mBinding.etEmail.requestFocus();
                return false;
            }
        } else if (mBinding.etMobileNo.getText().toString().trim().equalsIgnoreCase("")) {
            /*if (mBinding.etMobileNo.getText().toString().length() < 10) {
                Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_valid_mobile_no_msg));
                mBinding.etMobileNo.requestFocus();
                return false;
            }*/
            return false;
        }
        return true;
    }

    private void requestForgotPassword() {
        mProgressHUD = ProgressHUD.show(ACTIVITY, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<ForgetPasswordModel> forgetPasswordCall = new RestClient().getApiService().forgotPassword(mPrefs.getString(SharedPrefs.LANGUAGE), mBinding.etEmail.getText().toString().trim(),
                mBinding.etMobileNo.getText().toString().trim());
        forgetPasswordCall.enqueue(new retrofit2.Callback<ForgetPasswordModel>() {
            @Override
            public void onResponse(Call<ForgetPasswordModel> call, retrofit2.Response<ForgetPasswordModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                    Log.e("Success", response.body().message);
                    final String email = response.body().data.get(0).email;
                    Utils.showAlertDialogWithClickListener(ACTIVITY, response.body().message, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent _intent = new Intent(ACTIVITY, ResetPasswordActivity.class);
                            _intent.putExtra(getString(R.string.email),email);
                            startActivity(_intent);
                            finish();
                        }
                    });

                } else {
                    Utils.showAlertDialog(ACTIVITY, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordModel> call, Throwable t) {
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
