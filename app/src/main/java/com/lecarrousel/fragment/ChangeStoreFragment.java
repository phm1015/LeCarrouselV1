package com.lecarrousel.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.adapter.ChangeStoreAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgChangeStoreBinding;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Bitwin on 3/10/2017.
 */

public class ChangeStoreFragment extends Fragment implements ChangeStoreAdapter.RecycleListClickListener {

    private FrgChangeStoreBinding mBinding;
    private Context mContext;
    private SharedPrefs mPrefs;
    private String userId = "", storeId;
    private ProgressHUD mProgressHUD;
    private ChangeStoreAdapter mAdapter;
    private ArrayList<MasterDataModel.COUNTRIES> mStoreList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_change_store, container, false);
        mPrefs = new SharedPrefs(mContext);
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
        ((HomeActivity) mContext).setDrawerSwipe(false);
        ((HomeActivity) mContext).setVisibilityCart(false);

        mStoreList = mPrefs.getCountryList();
        if (mStoreList.size() > 0) {
            mAdapter = new ChangeStoreAdapter(mContext, mStoreList, this);
        }
        mBinding.rvStoreList.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.rvStoreList.setAdapter(mAdapter);
        setFonts();
        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginmodel.id);
            storeId = String.valueOf(loginmodel.store_id);
        } else {
            storeId = mPrefs.getString(SharedPrefs.STORE_ID);
        }

        mBinding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.showAlertDialogWith2ClickListener(mContext, getString(R.string.chnage_store_id), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (userId.equalsIgnoreCase("")) {
                            mPrefs.putString(SharedPrefs.STORE_ID, storeId);
                            String imgpath = Utils.getCountryModelFromId(getActivity(), Integer.parseInt(storeId)).countryFlag;
                            ((HomeActivity) mContext).changeCountryFlag(imgpath);
                            ((HomeActivity) mContext).changeFragment(new CategoryFragment());
                            getActivity().onBackPressed();
                        } else {
                            if (Utils.checkNetwork(mContext) == 1) {
                                requestChangeStore();
                            } else {
                                Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                            }
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            }
        });
        return mBinding.getRoot();
    }


    private void setFonts() {
        Utils.changeFont(mContext, mBinding.tvHeader, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvConfirm, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStoreClick(int position, View view) {
        storeId = String.valueOf(mStoreList.get(position).country_id);
    }

    private void requestChangeStore() {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<LoginModel> changeStoreCall = new RestClient().getApiService().changeUserStore(mPrefs.getString(SharedPrefs.LANGUAGE), userId, storeId);
        changeStoreCall.enqueue(new retrofit2.Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        //Log.e("Success1", response.body().message);
                        mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, new Gson().toJson(response.body().data.get(0)));
                        //mPrefs.putString(SharedPrefs.STORE_ID,String.valueOf(response.body().data.get(0).store_id));

                        String imgpath = Utils.getCountryModelFromId(getActivity(), response.body().data.get(0).store_id).countryFlag;
                        ((HomeActivity) mContext).changeCountryFlag(imgpath);
                        ((HomeActivity) mContext).changeFragment(new CategoryFragment());
                        getActivity().onBackPressed();
                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                Log.e("error", "" + t.getMessage());
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
