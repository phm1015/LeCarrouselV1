package com.lecarrousel.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgChangePasswordBinding;
import com.lecarrousel.model.ChangePasswordModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.SecurityCodeModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Bitwin on 3/10/2017.
 */

public class ChangePasswordFragment extends Fragment {

    private FrgChangePasswordBinding mBinding;
    private Context mContext;
    private SharedPrefs mPrefs;
    private String userId = "";
    private ProgressHUD mProgressHUD;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_change_password, container, false);
        mPrefs = new SharedPrefs(mContext);
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
        ((HomeActivity) mContext).setDrawerSwipe(false);

        setFonts();

        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginmodel.id);
            requestSecurityCode(userId);
        }

        mBinding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkNetwork(mContext) == 1) {
                    if (checkValidations()) {
                        requestChangePass(userId, mBinding.etNewPass.getText().toString().trim(),
                                mBinding.etOldPass.getText().toString().trim(), mBinding.etSecurityCode.getText().toString().trim());
                    }
                } else {
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                }
            }
        });

        mBinding.tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestSecurityCode(userId);

            }
        });

        mBinding.etSecurityCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mBinding.etOldPass.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mBinding.etOldPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mBinding.etNewPass.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mBinding.etNewPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mBinding.etConfmNewPass.requestFocus();
                    return true;
                }
                return false;
            }
        });

        return mBinding.getRoot();
    }

    private void setFonts() {
        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvChangePwdLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvOldPass, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvNewPass, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvConfmNewPass, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvSave, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvSecurityLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvResend, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
        ((HomeActivity) mContext).setDrawerSwipe(true);

        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    private boolean checkValidations() {

        if (mBinding.etSecurityCode.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_security_code));
            mBinding.etSecurityCode.requestFocus();
            return false;
        }
        if (mBinding.etOldPass.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_old_pass_msg));
            mBinding.etOldPass.requestFocus();
            return false;
        }

        if (mBinding.etNewPass.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_new_pass_msg));
            mBinding.etNewPass.requestFocus();
            return false;
        }

        if (mBinding.etConfmNewPass.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_confirm_password_msg));
            mBinding.etConfmNewPass.requestFocus();
            return false;
        }

        if((mBinding.etNewPass.getText().toString().equals(mBinding.etOldPass.getText().toString()))){
            Utils.showAlertDialog(mContext, getResources().getString(R.string.new_password_old_password_same_msg));
            mBinding.etNewPass.requestFocus();
            return false;
        }
        if (!(mBinding.etNewPass.getText().toString().equals(mBinding.etConfmNewPass.getText().toString()))) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.password_new_confirm_password_same_msg));
            mBinding.etConfmNewPass.requestFocus();
            return false;
        }

        return true;
    }

    private void requestChangePass(String userId, String new_pass, String old_pass, String security_code) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<ChangePasswordModel> addCardCall = new RestClient().getApiService().changePassword(mPrefs.getString(SharedPrefs.LANGUAGE), userId, new_pass, old_pass, security_code);
        addCardCall.enqueue(new retrofit2.Callback<ChangePasswordModel>() {
            @Override
            public void onResponse(Call<ChangePasswordModel> call, retrofit2.Response<ChangePasswordModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        mPrefs.putString(SharedPrefs.USER_PASSWORD, mBinding.etNewPass.getText().toString().trim());
                        getActivity().onBackPressed();
//                        Utils.showAlertDialogWithClickListener(mContext, response.body().message, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                getActivity().onBackPressed();
//                            }
//                        });

                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordModel> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                Log.e("error", "" + t.getMessage());
                //Utils.showAlertDialog(mContext, t.getMessage());

                if (t instanceof HttpException) {
                    Log.e("HTTP", ((HttpException) t).getMessage());
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                }
                if (t instanceof IOException) {
                    Log.e("IOException", ((IOException) t).getMessage());
                }
            }
        });
    }


    private void requestSecurityCode(String userId) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<SecurityCodeModel> securityCodeCall = new RestClient().getApiService().securityCode(mPrefs.getString(SharedPrefs.LANGUAGE), userId);
        securityCodeCall.enqueue(new retrofit2.Callback<SecurityCodeModel>() {
            @Override
            public void onResponse(Call<SecurityCodeModel> call, retrofit2.Response<SecurityCodeModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Utils.showAlertDialog(mContext, response.body().message);
                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<SecurityCodeModel> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                Log.e("error", "" + t.getMessage());
                //Utils.showAlertDialog(mContext, t.getMessage());
                if (t instanceof HttpException) {
                    Log.e("HTTP", ((HttpException) t).getMessage());
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                }
                if (t instanceof IOException) {
                    Log.e("IOException", ((IOException) t).getMessage());
                }
            }
        });
    }
}
