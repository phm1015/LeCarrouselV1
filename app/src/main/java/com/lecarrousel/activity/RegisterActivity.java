package com.lecarrousel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.adapter.CountryListAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.ActRegisterBinding;
import com.lecarrousel.fragment.TermsFragment;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.model.RegisterModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

public class RegisterActivity extends AppCompatActivity {
    private ActRegisterBinding mBinding;
    private Activity ACTIVITY = RegisterActivity.this;
    private ProgressHUD mProgressHUD;
    private SharedPrefs mPrefs;
    private String strCountryCode;
    private ArrayList<MasterDataModel.COUNTRIES> mCountryList = new ArrayList<>();
    private CountryListAdapter countryAdapter;
    private MasterDataModel.COUNTRIES modelCountry;
    private String userId;
    private AlertDialog mAlertDialog;
    private EditText etDialog;
    private boolean isTermsChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = new SharedPrefs(ACTIVITY);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_register);
        mBinding.layHeader.layCart.setVisibility(View.INVISIBLE);
        mBinding.layHeader.ivLeftAction.setImageDrawable(ContextCompat.getDrawable(ACTIVITY, R.drawable.ic_back));
        mCountryList = mPrefs.getCountryList();
        mBinding.layCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.spCountry.performClick();
            }
        });


        mBinding.layMobileEdit.etMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.layMobileEdit.etMobileNo.getText().length() == 0) {
                    mBinding.layMobileEdit.etMobileNo.setHint(getResources().getString(R.string.hint_enter_mobile));
                } else {
                    mBinding.layMobileEdit.etMobileNo.setHint("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (mCountryList.size() > 0) {
            countryAdapter = new CountryListAdapter(ACTIVITY, mCountryList);
            mBinding.spCountry.setAdapter(countryAdapter);
        }
        /*Country Spinner*/
        mBinding.spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBinding.tvCountry.setText(mCountryList.get(position).country_name);
                mBinding.layMobileEdit.tvCountryCode.setVisibility(View.VISIBLE);
                mBinding.layMobileEdit.tvCountryCode.setText(mCountryList.get(position).country_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            strCountryCode = tm.getNetworkCountryIso();
            boolean isMatched = false;
            for (int index = 0; index < mCountryList.size(); index++) {
                if (mCountryList.get(index).short_code.equalsIgnoreCase(strCountryCode)) {
                    Log.e("Tag", "Set Local Country" + mCountryList.get(index).country_code);
                    modelCountry = mCountryList.get(index);
                    mBinding.spCountry.setSelection(index);
                    isMatched = true;
                }
            }

            if (!isMatched) {
                for (int i = 0; i < mCountryList.size(); i++) {
                    if (mCountryList.get(i).country_name.equalsIgnoreCase("QATAR")) {
                        Log.e("Tag", "Set Default Country" + mCountryList.get(i).country_code);
                        modelCountry = mCountryList.get(i);
                        mBinding.spCountry.setSelection(i);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Tag", "Exception:" + e);
            for (int i = 0; i < mCountryList.size(); i++) {
                if (mCountryList.get(i).country_name.equalsIgnoreCase("QATAR")) {
                    Log.e("Tag", "Set Default Country" + mCountryList.get(i).country_code);
                    modelCountry = mCountryList.get(i);
                    mBinding.spCountry.setSelection(i);
                }
            }
        }

        mBinding.layHeader.ivLeftAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBinding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidations()) {

                    if (Utils.checkNetwork(ACTIVITY) == 1) {
                        requestRegister();
                    } else {
                        Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.err_network_not_available));
                    }
                }
            }
        });

        mBinding.laySignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }

        });

        mBinding.tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ACTIVITY, ForgotPasswordActivity.class));
            }
        });

        mBinding.layCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        mBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    isTermsChecked = true;
                    mBinding.tvRegister.setTextColor(ContextCompat.getColor(ACTIVITY,R.color.brown));
                }else{
                    isTermsChecked = false;
                    mBinding.tvRegister.setTextColor(ContextCompat.getColor(ACTIVITY,R.color.brown_light));
                }
            }
        });


        String thisLink = getString(R.string.by_signing_up) + "\n" + getString(R.string.terms_condition_policy);
        String yourString = getString(R.string.terms_condition_policy);
        SpannableStringBuilder content = new SpannableStringBuilder();
        content.append(thisLink.trim());

        content.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(ACTIVITY,TermsActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(ACTIVITY,R.color.yellow));
            }
        }, thisLink.indexOf(yourString), thisLink.indexOf(yourString) + yourString.length(),0);

        mBinding.lblTerms.setText(content);
        mBinding.lblTerms.setMovementMethod(LinkMovementMethod.getInstance());

        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(ACTIVITY, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(ACTIVITY, mBinding.etPassword, Utils.FontStyle.REGULAR);
        Utils.changeFont(ACTIVITY, mBinding.tvEmailAddLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvPasswordLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvPasswordConfmLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvName, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.layMobileEdit.tvMobileNo, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvHaveAnAccount, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvRegister, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvRegisterAcc, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.tvCancel, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.lblCountry, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.lblTerms, Utils.FontStyle.BOLD);
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
        if (mBinding.etConfPassword.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_confirm_password_msg));
            mBinding.etConfPassword.requestFocus();
            return false;
        }
        /*if (mBinding.etConfPassword.getText().toString().trim().length() < 8) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.password_minimum_character_msg));
            mBinding.etConfPassword.requestFocus();
            return false;
        }*/
        if (!(mBinding.etPassword.getText().toString().equals(mBinding.etConfPassword.getText().toString()))) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.password_confirm_password_same_msg));
            mBinding.etConfPassword.requestFocus();
            return false;
        }
        if (mBinding.etName.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_name_msg));
            mBinding.etName.requestFocus();
            return false;
        }
//        if (!mBinding.etName.getText().toString().trim().matches("[a-zA-Z ]*")) {
//            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_valid_name_msg));
//            mBinding.etName.requestFocus();
//            return false;
//        }
        if (mBinding.layMobileEdit.etMobileNo.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_mobile_no_msg));
            mBinding.layMobileEdit.etMobileNo.requestFocus();
            return false;
        }
        /*if (mBinding.layMobileEdit.etMobileNo.getText().toString().trim().length() < 10) {
            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_valid_mobile_no_msg));
            mBinding.layMobileEdit.etMobileNo.requestFocus();
            return false;
        }*/
        if(!isTermsChecked){
            return false;
        }

        return true;
    }

    private void requestRegister() {
        mProgressHUD = ProgressHUD.show(RegisterActivity.this, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<RegisterModel> registercall = new RestClient().getApiService().register(mPrefs.getString(SharedPrefs.LANGUAGE),
                mBinding.etEmail.getText().toString().trim(),
                mBinding.etPassword.getText().toString().trim(), mBinding.etName.getText().toString().trim(),
                mBinding.layMobileEdit.etMobileNo.getText().toString().trim(),
                String.valueOf(mCountryList.get(mBinding.spCountry.getSelectedItemPosition()).country_id));
        registercall.enqueue(new retrofit2.Callback<RegisterModel>() {
            @Override
            public void onResponse(Call<RegisterModel> call, retrofit2.Response<RegisterModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                //Log.d("!!!!!",response.body().toString());
                if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {

                    userId = String.valueOf(response.body().data.id);
                    Log.d("CODE", " " + response.body().data.verificationCode);
                    showDialog();

                } else {
                    Utils.showAlertDialog(ACTIVITY, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<RegisterModel> call, Throwable t) {
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

    private void verifyRegister(String securityCode) {
        mProgressHUD = ProgressHUD.show(RegisterActivity.this, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<LoginModel> verifyCall = new RestClient().getApiService().verifyCode(mPrefs.getString(SharedPrefs.LANGUAGE), userId, securityCode);

        verifyCall.enqueue(new retrofit2.Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);

                if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                    mAlertDialog.dismiss();
                    Log.e("Success", response.body().message);
                    mPrefs.putBoolean(SharedPrefs.IS_LOGIN, true);
                    mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, new Gson().toJson(response.body().data.get(0)));
                    mPrefs.putString(SharedPrefs.USER_PHOTO, response.body().data.get(0).logo);
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
                    etDialog.setText("");
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(800);
                    Utils.showAlertDialog(ACTIVITY, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                mAlertDialog.dismiss();
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

    private void showDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(ACTIVITY, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.custom_alert, null);
        builder.setView(v);
        etDialog = (EditText) v.findViewById(R.id.etSecurityCode);
        builder.setTitle(getString(R.string.verification));
        builder.setPositiveButton(getString(R.string.register), null);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(false);
        mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (checkOtpValidations()) {
                            if (Utils.checkNetwork(ACTIVITY) == 1) {
                                verifyRegister(etDialog.getText().toString());

                            } else {
                                Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.err_network_not_available));
                            }
                        }


                    }

                    private boolean checkOtpValidations() {

                        if (etDialog.getText().toString().trim().equalsIgnoreCase("")) {
                            Utils.showAlertDialog(ACTIVITY, getResources().getString(R.string.enter_security_code));
                            etDialog.requestFocus();
                            return false;
                        }


                        return true;
                    }

                });
            }
        });

        mAlertDialog.show();
    }

}

