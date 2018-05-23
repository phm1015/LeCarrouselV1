package com.lecarrousel.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.adapter.CategoryAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgDashboardBinding;
import com.lecarrousel.model.CategoryListModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Rohan Mistry on 7/3/2017.
 */

public class CategoryFragment extends BaseFragment implements CategoryAdapter.RecycleListClickListener {

    private Context mContext;
    private FrgDashboardBinding mBinding;
    private SharedPrefs mPrefs;
    private String userId = "",storeId = "";
    private ProgressHUD mProgressHUD;
    private ArrayList<CategoryListModel.Data> mArrayCategoryList = new ArrayList<>();
    private CategoryAdapter mAdapter;

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
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
        ((HomeActivity) mContext).setVisibilityCart(true);
        ((HomeActivity) mContext).setDrawerSwipe(true);
        ((HomeActivity) mContext).mBinding.layHeader.layCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewOrderFragment frgReviewOrder = new ReviewOrderFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString("fromWhere", "category");
                frgReviewOrder.setArguments(mBundle);
                replaceFragment(frgReviewOrder, true);
            }
        });
        //((HomeActivity) mContext).flagMenuOrBack = 0;
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_dashboard, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPrefs = new SharedPrefs(mContext);

        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginmodel.id);
            storeId = String.valueOf(loginmodel.store_id);
            if (Utils.checkNetwork(mContext) == 1) {
                requestCategoryList(userId,storeId);
                ((HomeActivity) mContext).getCartCount();

            } else {
                Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
            }
        } else {
            userId = "";
            storeId = mPrefs.getString(SharedPrefs.STORE_ID);;
            ((HomeActivity) mContext).setVisibilityCart(false);
            if (Utils.checkNetwork(mContext) == 1) {
                requestCategoryList(userId,storeId);
            } else {
                Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
            }

        }

        mBinding.rvCategory.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mBinding.rvCategory.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onItemClick(int position, View view) {
        Log.e("Tag", "Click");

        Fragment frgProductList = new ProductListFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("data",new Gson().toJson(mArrayCategoryList.get(position)));
        frgProductList.setArguments(mBundle);
        replaceFragment(frgProductList, true);

    }

    private void requestCategoryList(String userId,String storeId) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<CategoryListModel> loginCall = new RestClient().getApiService().categoryList(mPrefs.getString(SharedPrefs.LANGUAGE), userId,storeId);
        loginCall.enqueue(new retrofit2.Callback<CategoryListModel>() {
            @Override
            public void onResponse(Call<CategoryListModel> call, retrofit2.Response<CategoryListModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        mArrayCategoryList.clear();
                        if (response.body().data.size() != 0) {
                            mBinding.rvCategory.setVisibility(View.VISIBLE);
                            mBinding.tvNoRecord.setVisibility(View.GONE);
                            mArrayCategoryList.addAll(response.body().data);
                            mAdapter = new CategoryAdapter(mContext, mArrayCategoryList, CategoryFragment.this);
                            mBinding.rvCategory.setAdapter(mAdapter);
                        } else {
                            mBinding.rvCategory.setVisibility(View.GONE);
                            mBinding.tvNoRecord.setVisibility(View.VISIBLE);

                        }
                    } else {
                        mBinding.rvCategory.setVisibility(View.GONE);
                        mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                        Log.e("Success0", response.body().message);
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryListModel> call, Throwable t) {
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
