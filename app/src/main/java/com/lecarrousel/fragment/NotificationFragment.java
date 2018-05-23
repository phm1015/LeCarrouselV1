package com.lecarrousel.fragment;

import android.content.Context;
import android.content.DialogInterface;
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
import com.lecarrousel.adapter.NotificationListAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgNotificationBinding;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.NotificationModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;


public class NotificationFragment extends BaseFragment implements NotificationListAdapter.RecycleListClickListener {
    private ProgressHUD mProgressHUD;
    private Context mContext;
    private FrgNotificationBinding mBinding;
    private SharedPrefs mPrefs;
    private String userId = "";
    private NotificationListAdapter mAdapter;
    private ArrayList<NotificationModel.Data> mArrayWishList = new ArrayList<>();
    private String storeId;

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
        ((HomeActivity) mContext).mBinding.layHeader.tvDelete.setVisibility(View.GONE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
        ((HomeActivity) mContext).setDrawerSwipe(true);
        ((HomeActivity) mContext).setVisibilityCart(false);


        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_notification, container, false);
        mPrefs = new SharedPrefs(mContext);

        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginmodel.id);
            storeId = String.valueOf(loginmodel.store_id);
            if (Utils.checkNetwork(mContext) == 1) {
                requestNotificationList(userId);
            } else {
                Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
            }
        }
        ((HomeActivity) mContext).mBinding.layHeader.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showAlertDialogWith2ClickListener(mContext, getResources().getString(R.string.delete_msg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeNotification(userId, "");
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });
        setFonts();

        return mBinding.getRoot();
    }

    private void setFonts() {

        Utils.changeFont(mContext, mBinding.tvHeader, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding.rvNotification.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mBinding.rvNotification.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new NotificationListAdapter(mContext, mArrayWishList, NotificationFragment.this);
        mBinding.rvNotification.setAdapter(mAdapter);
    }

    private void requestNotificationList(String userId) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<NotificationModel> requestCall = new RestClient().getApiService().notificationList(mPrefs.getString(SharedPrefs.LANGUAGE), userId, new Utils().getTimeZone());
        requestCall.enqueue(new retrofit2.Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, retrofit2.Response<NotificationModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        mArrayWishList.clear();
                        mBinding.rvNotification.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setVisibility(View.GONE);
                        mArrayWishList.addAll(response.body().data);
                        Collections.reverse(mArrayWishList);
                        mAdapter.notifyDataSetChanged();
                        ((HomeActivity) mContext).mBinding.layHeader.tvDelete.setVisibility(View.VISIBLE);
                    } else {
                        ((HomeActivity) mContext).mBinding.layHeader.tvDelete.setVisibility(View.GONE);
                        mBinding.rvNotification.setVisibility(View.GONE);
                        mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setText(response.body().message);
                        Log.e("Success0", response.body().message);
                        //Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationModel> call, Throwable t) {
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

    @Override
    public void onItemDeleteClick(final int position, View view) {
        removeNotification("", mArrayWishList.get(position).notificationId);
    }

    @Override
    public void onItemDetailClick(final int position, View view, String notifType) {

        Utils.readNotification(mContext, mArrayWishList.get(position).notificationId, new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                if (response != null && response.body().status.equalsIgnoreCase("1")) {
                    Log.e("Tag", response.body().message);
                    mArrayWishList.get(position).isRead = "0";
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {

            }
        });

        if(notifType.equalsIgnoreCase("1")){
            MyOrderDetailFragment frgOrderDetail = new MyOrderDetailFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString("orderId", mArrayWishList.get(position).orderId);
            mBundle.putBoolean("isFrom", true);
            frgOrderDetail.setArguments(mBundle);
            replaceFragment(frgOrderDetail, true);
        }
    }


    private void removeNotification(String mUserid, String mNotificationId) {
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<GeneralModel> requestCall = new RestClient().getApiService().removeNotification(mPrefs.getString(SharedPrefs.LANGUAGE), mUserid, mNotificationId);
        requestCall.enqueue(new retrofit2.Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        requestNotificationListWithOutLoader(userId);
                    } else {
                        mProgressHUD.dismissProgressDialog(mProgressHUD);
                        mBinding.rvNotification.setVisibility(View.GONE);
                        mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setText(response.body().message);
                        Log.e("Success0", response.body().message);
                    }
                }else{
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
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

    private void requestNotificationListWithOutLoader(String userId) {
        //Call Webservice
        Call<NotificationModel> requestCall = new RestClient().getApiService().notificationList(mPrefs.getString(SharedPrefs.LANGUAGE), userId, new Utils().getTimeZone());
        requestCall.enqueue(new retrofit2.Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, retrofit2.Response<NotificationModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        mArrayWishList.clear();
                        mBinding.rvNotification.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setVisibility(View.GONE);
                        mArrayWishList.addAll(response.body().data);
                        mAdapter.notifyDataSetChanged();
                        ((HomeActivity) mContext).mBinding.layHeader.tvDelete.setVisibility(View.VISIBLE);
                    } else {
                        ((HomeActivity) mContext).mBinding.layHeader.tvDelete.setVisibility(View.GONE);
                        mBinding.rvNotification.setVisibility(View.GONE);
                        mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setText(response.body().message);
                        Log.e("Success0", response.body().message);
                        //Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationModel> call, Throwable t) {
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