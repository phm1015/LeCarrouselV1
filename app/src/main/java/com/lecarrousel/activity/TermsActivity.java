package com.lecarrousel.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.lecarrousel.databinding.ActTermsBinding;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class TermsActivity extends Activity {
    private Activity ACTIVITY = TermsActivity.this;
    private ActTermsBinding mBinding;
    private SharedPrefs mPrefs;
    final String mimeType = "text/html";
    final String encoding = "UTF-8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_terms);
        mPrefs = new SharedPrefs(ACTIVITY);

        Utils.changeFont(ACTIVITY, mBinding.tvHeader, Utils.FontStyle.BOLD);
        mBinding.layHeader.ivLeftAction.setImageResource(R.drawable.ic_back);
        mBinding.layHeader.ivCart.setVisibility(View.GONE);

        mBinding.layHeader.ivLeftAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        MasterDataModel masterDetailModel = new Gson().fromJson(mPrefs.getMasterDetail(), MasterDataModel.class);

        if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
            mBinding.tvHeader.setText(masterDetailModel.data.get(0).term_condition.get(0).title_ar);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                mBinding.tvTerms.setText(Html.fromHtml(masterDetailModel.data.get(0).term_condition.get(0).desc_ar, Html.FROM_HTML_MODE_COMPACT));
//            } else {
//                mBinding.tvTerms.setText(Html.fromHtml(masterDetailModel.data.get(0).term_condition.get(0).desc_ar));
//            }
            mBinding.webView.loadDataWithBaseURL("", masterDetailModel.data.get(0).term_condition.get(0).desc_ar, mimeType, encoding, "");
        } else {
            mBinding.tvHeader.setText(masterDetailModel.data.get(0).term_condition.get(0).title_en);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                mBinding.tvTerms.setText(Html.fromHtml(masterDetailModel.data.get(0).term_condition.get(0).desc_en, Html.FROM_HTML_MODE_COMPACT));
//            } else {
//                mBinding.tvTerms.setText(Html.fromHtml(masterDetailModel.data.get(0).term_condition.get(0).desc_en));
//            }
            mBinding.webView.loadDataWithBaseURL("", masterDetailModel.data.get(0).term_condition.get(0).desc_en, mimeType, encoding, "");
        }
    }

}
