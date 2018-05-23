package com.lecarrousel.fragment;

import android.content.Context;
import android.content.DialogInterface;
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
import com.lecarrousel.adapter.MyOrderDetailAdapter;
import com.lecarrousel.adapter.MyOrderListAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgMyOrderDetailsBinding;
import com.lecarrousel.model.CancelOrderModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.model.OrderDetailModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Bitwin on 3/10/2017.
 */

public class MyOrderDetailFragment extends Fragment implements MyOrderListAdapter.RecycleListClickListener {
    private FrgMyOrderDetailsBinding mBinding;
    private Context mContext;
    private ProgressHUD mProgressHUD;
    private SharedPrefs mPrefs;
    private String userId;
    private MyOrderDetailAdapter mAdapter;
    public ArrayList<OrderDetailModel.ProductData> mArrayOrderList = new ArrayList<>();
    public ArrayList<MasterDataModel.Delivery_estimate_time> mArrayTimeList;
    private String orderId;
    private int order_status;
    private MyOrderFragment myOrderFrg;
    private int orderPosition;
    private boolean isFromPush;
    private Bundle mBundle;

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
        if (mBundle.containsKey("isFrom")) {
            ((HomeActivity) mContext).mBinding.layHeader.tvDelete.setVisibility(View.VISIBLE);
            ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
        }
        if (isFromPush){
            ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
        }
        ((HomeActivity) mContext).setDrawerSwipe(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
        ((HomeActivity) mContext).setVisibilityCart(false);
        ((HomeActivity) mContext).setDrawerSwipe(false);
        ((HomeActivity) mContext).mBinding.layHeader.tvDelete.setVisibility(View.GONE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_my_order_details, container, false);
        mBinding.layMain.setVisibility(View.INVISIBLE);
        mPrefs = new SharedPrefs(mContext);
        setFonts();

        LoginModel.Data mLoginModel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
        userId = String.valueOf(mLoginModel.id);

        mArrayTimeList = mPrefs.getEstimateTimeList();
        mBundle = getArguments();
        if (mBundle != null) {
            if (mBundle.containsKey("isFromPush")) {
                orderId = mBundle.getString("orderId");
                isFromPush = mBundle.getBoolean("isFromPush");
            } else {
                isFromPush = false;
                orderId = mBundle.getString("orderId");
                orderPosition = mBundle.getInt("orderPos");
            }
        }

        mBinding.tvCancelOrReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmText = "";
                if (order_status == 0) {
                    confirmText = getString(R.string.confirm_cancel_text);
                } else if (order_status == 1) {
                    confirmText = getString(R.string.confirm_return_text);
                }

                Utils.showAlertDialogWith2ClickListener(mContext, confirmText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.checkNetwork(mContext) == 1) {
                            requestCancelOrReturnOrder();
                        } else {
                            Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
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
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvAddress, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvOrderDate, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvCardMsg, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvExpecDeliveryDate, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvExpecDeliveryTime, Utils.FontStyle.REGULAR);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Utils.checkNetwork(mContext) == 1) {
            requestOrderDetail();
        } else {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
        }

        mAdapter = new MyOrderDetailAdapter(mContext, mArrayOrderList);
        mBinding.rvOrderDetails.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.rvOrderDetails.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvOrderDetails.setAdapter(mAdapter);

    }


    @Override
    public void onItemClick(int position, View view) {

    }

    public void setParentFragment(MyOrderFragment myOrderFrg) {
        this.myOrderFrg = myOrderFrg;
    }

    private void requestOrderDetail() {
        //Call Webservice bbb
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        final Call<OrderDetailModel> orderCall = new RestClient().getApiService().orderDetail(mPrefs.getString(SharedPrefs.LANGUAGE), userId, orderId);
        orderCall.enqueue(new retrofit2.Callback<OrderDetailModel>() {
            @Override
            public void onResponse(Call<OrderDetailModel> call, retrofit2.Response<OrderDetailModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);

                if (response != null) {
                    mBinding.layMain.setVisibility(View.VISIBLE);
                    mArrayOrderList.clear();
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        //mBinding.tvNoRecord.setVisibility(View.GONE);
                        mArrayOrderList.addAll(response.body().data.get(0).productData);
                        mAdapter.notifyDataSetChanged();
                        mBinding.rvOrderDetails.setVisibility(View.VISIBLE);

                        OrderDetailModel.Data orderData = response.body().data.get(0);
                        order_status = orderData.status;
                        String status = "";
                        switch (orderData.status) {
                            case 0:
                                status = getResources().getString(R.string.processing);
                                mBinding.tvCancelOrReturn.setVisibility(View.VISIBLE);
                                mBinding.tvCancelOrReturn.setText(getString(R.string.cancel_order));
                                break;
                            case 1:
                                status = getResources().getString(R.string.delivered);
//                                mBinding.tvCancelOrReturn.setVisibility(View.VISIBLE);
//                                mBinding.tvCancelOrReturn.setText(getString(R.string.return_order));
                                break;
                            case 2:
                                status = getResources().getString(R.string.canceled);
                                break;
                            case 3:
                                status = getResources().getString(R.string.returned);
                                break;
                            case 4:
                                status = getResources().getString(R.string.out_for_delivery);
                                break;
                            case 5:
                                status = getResources().getString(R.string.fail_to_deliver);
                                break;
                        }
                        mBinding.tvHeader.setText(getResources().getString(R.string.order_detail_header_text, getResources().getString(R.string.order_detail), String.valueOf(orderData.orderId) + "-", status));

                        mBinding.tvDiscountPrice.setText(getString(R.string.discount_text, String.valueOf(orderData.discountPrice)));
                        if (orderData.discountPrice == 0) {
                            mBinding.tvDiscountPrice.setText(String.valueOf(orderData.discountPrice));
                        } else {
                            mBinding.tvDiscountPrice.setText(getString(R.string.discount_text, String.valueOf(orderData.discountPrice)));
                        }
                        mBinding.tvTotalPrice.setText(String.valueOf(orderData.totalPrice));
                        mBinding.tvNameAddress.setText(orderData.contactName);
//                        mBinding.tvAddress.setText(orderData.streetAddress + "\n" + orderData.buildingNo + ","+ orderData.streetNo+ ","
//                                + orderData.zone + "\n" + orderData.country_name);
                        String address = mContext.getResources().getString(R.string.addr_format,orderData.buildingNo,orderData.streetNo,orderData.zone);

                        mBinding.tvAddress.setText(orderData.streetAddress + "\n" + address + "\n" + orderData.country_name);
                        if (!orderData.cartMessage.isEmpty()) {
                            mBinding.tvCardMsg.setText(orderData.cartMessage);
                        }

                        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
                        mBinding.tvOrderDate.setText(Utils.changeDateFormate2(orderData.createdAt, df));
                        if (!orderData.expected_delivery_date.isEmpty()) {
                            mBinding.tvExpecDeliveryDate.setText(Utils.changeDateFormate(orderData.expected_delivery_date, df));

                        }
                        for(int index = 0;index<mArrayTimeList.size();index++){
                            if(mArrayTimeList.get(index).tId == orderData.delivery_estimate_time){
                                mBinding.tvExpecDeliveryTime.setText(mArrayTimeList.get(index).title);
                                break;
                            }
                        }

                    } else {
                        Log.e("Success0", response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDetailModel> call, Throwable t) {
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

    private void requestCancelOrReturnOrder() {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        final Call<CancelOrderModel> cancelOrderCall = new RestClient().getApiService().cancelOrder(mPrefs.getString(SharedPrefs.LANGUAGE), userId, orderId);
        cancelOrderCall.enqueue(new retrofit2.Callback<CancelOrderModel>() {
            @Override
            public void onResponse(Call<CancelOrderModel> call, retrofit2.Response<CancelOrderModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        if (!isFromPush) {
                            if (order_status == 0) {
                                myOrderFrg.refreshList(orderPosition, 2);
                            } else if (order_status == 1) {
                                myOrderFrg.refreshList(orderPosition, 3);
                            }
                        }
                        getActivity().onBackPressed();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelOrderModel> call, Throwable t) {
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
