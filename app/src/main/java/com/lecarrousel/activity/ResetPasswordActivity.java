package com.lecarrousel.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lecarrousel.R;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.ActResetPasswordBinding;
import com.lecarrousel.model.UpdateCard;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActResetPasswordBinding mBinding;
    private Activity ACTIVITY = ResetPasswordActivity.this;
    private ProgressHUD mProgressHUD;
    private SharedPrefs mPrefs;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_reset_password);
        mPrefs = new SharedPrefs(ACTIVITY);
        mBinding.layHeader.layCart.setVisibility(View.INVISIBLE);
        mBinding.layHeader.ivLeftAction.setImageDrawable(ContextCompat.getDrawable(ACTIVITY, R.drawable.ic_back));

        email = getIntent().getStringExtra(getString(R.string.email));

        mBinding.layHeader.ivLeftAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBinding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidations()) {

                    if (Utils.checkNetwork(ACTIVITY) == 1) {
                        requestResetPassword();
                    } else {
                        Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.err_network_not_available));
                    }
                }
            }
        });

        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(ACTIVITY, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(ACTIVITY, mBinding.tvSecurityLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvNewPass, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvConfmNewPass, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvSave, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvResetPwdLabel, Utils.FontStyle.BOLD);

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

        if (mBinding.etSecurityCode.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_security_code));
            mBinding.etSecurityCode.requestFocus();
            return false;
        }

        if (mBinding.etNewPass.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_new_pass_msg));
            mBinding.etNewPass.requestFocus();
            return false;
        }

        if (mBinding.etConfmNewPass.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_confirm_password_msg));
            mBinding.etConfmNewPass.requestFocus();
            return false;
        }

        if (!(mBinding.etNewPass.getText().toString().equals(mBinding.etConfmNewPass.getText().toString()))) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.password_confirm_password_same_msg));
            mBinding.etConfmNewPass.requestFocus();
            return false;
        }

        return true;
    }

    private void requestResetPassword() {
        mProgressHUD = ProgressHUD.show(ACTIVITY, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<UpdateCard> resetPasswordCall = new RestClient().getApiService().resetPassword(mPrefs.getString(SharedPrefs.LANGUAGE), email,
                mBinding.etSecurityCode.getText().toString().trim(),mBinding.etNewPass.getText().toString().trim());
        resetPasswordCall.enqueue(new retrofit2.Callback<UpdateCard>() {
            @Override
            public void onResponse(Call<UpdateCard> call, retrofit2.Response<UpdateCard> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                    Log.e("Success", response.body().message);
                    Intent _intent = new Intent(ACTIVITY, LoginActivity.class);
                    startActivity(_intent);
                    finish();
                } else {
                    Utils.showAlertDialog(ACTIVITY, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<UpdateCard> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                Log.e("error", "" + t.getMessage());
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
