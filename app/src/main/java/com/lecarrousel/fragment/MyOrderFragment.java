package com.lecarrousel.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.adapter.MyOrderListAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgMyOrdersBinding;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.OrderListModel;
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

public class MyOrderFragment extends BaseFragment implements MyOrderListAdapter.RecycleListClickListener {
    private FrgMyOrdersBinding mBinding;
    private Context mContext;
    private ProgressHUD mProgressHUD;
    private SharedPrefs mPrefs;
    private MyOrderListAdapter mAdapter;
    public ArrayList<OrderListModel.Data> mArrayOrderList = new ArrayList<>();
    private String userId;

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
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
        ((HomeActivity) mContext).setDrawerSwipe(true);
        ((HomeActivity) mContext).mBinding.layHeader.layCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewOrderFragment frgReviewOrder = new ReviewOrderFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString("fromWhere", "MyOrder");
                frgReviewOrder.setArguments(mBundle);
                replaceFragment(frgReviewOrder, true);
            }
        });

        ((HomeActivity) mContext).setVisibilityCart(true);
        ((HomeActivity) mContext).getCartCount();

        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_my_orders, container, false);
        mPrefs = new SharedPrefs(mContext);

        setFonts();

        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginModel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginModel.id);
            if (Utils.checkNetwork(mContext) == 1) {
                requestProductList(userId);
                ((HomeActivity) mContext).getCartCount();
            } else {
                Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
            }
        }
        return mBinding.getRoot();
    }

    private void setFonts() {
        Utils.changeFont(mContext, mBinding.tvHeader, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.rvOrderList.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mBinding.rvOrderList.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MyOrderListAdapter(mContext, mArrayOrderList, MyOrderFragment.this, MyOrderFragment.this);
        mBinding.rvOrderList.setAdapter(mAdapter);
    }


    @Override
    public void onItemClick(int position, View view) {
        MyOrderDetailFragment frgOrderDetail = new MyOrderDetailFragment();
        frgOrderDetail.setParentFragment(this);
        Bundle mBundle = new Bundle();
        mBundle.putString("orderId", String.valueOf(mArrayOrderList.get(position).orderId));
        mBundle.putInt("orderPos", position);
        frgOrderDetail.setArguments(mBundle);
        replaceFragment(frgOrderDetail, true);
    }

    public void refreshList(int position, int status) {
        switch (status) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                mArrayOrderList.get(position).status = status;
                mAdapter.notifyDataSetChanged();
                break;
            case 3:
                mArrayOrderList.get(position).status = status;
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void requestProductList(String userId) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<OrderListModel> loginCall = new RestClient().getApiService().orderList(mPrefs.getString(SharedPrefs.LANGUAGE), userId);
        loginCall.enqueue(new retrofit2.Callback<OrderListModel>() {
            @Override
            public void onResponse(Call<OrderListModel> call, retrofit2.Response<OrderListModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    mArrayOrderList.clear();
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        mBinding.tvNoRecord.setVisibility(View.GONE);
                        mArrayOrderList.addAll(response.body().data);
                        mAdapter.notifyDataSetChanged();
                        mBinding.rvOrderList.setVisibility(View.VISIBLE);
                    } else {
                        mAdapter.notifyDataSetChanged();
                        mBinding.rvOrderList.setVisibility(View.GONE);
                        mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setText(response.body().message);
                        Log.e("Success0", response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderListModel> call, Throwable t) {
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
