package com.lecarrousel.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.databinding.FrgTermsBinding;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;


public class TermsFragment extends BaseFragment {
    private FrgTermsBinding mBinding;
    private Context mContext;
    private SharedPrefs mPrefs;
    final String mimeType = "text/html";
    final String encoding = "UTF-8";

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_terms, container, false);
        mPrefs = new SharedPrefs(mContext);

        if(getArguments()!= null){
            ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
            ((HomeActivity) mContext).setDrawerSwipe(false);
            ((HomeActivity) mContext).setIvLeftAction();
        }else{
            ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
            ((HomeActivity) mContext).setDrawerSwipe(true);
        }
        ((HomeActivity) mContext).setVisibilityCart(false);
        setFonts();

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
        return mBinding.getRoot();
    }

    private void setFonts() {
        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvHeader, Utils.FontStyle.BOLD);
    }
}
