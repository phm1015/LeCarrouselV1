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
import com.lecarrousel.adapter.WishListAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgWishlistBinding;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.WishListModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;


public class WishListFragment extends BaseFragment implements WishListAdapter.RecycleListClickListener {
    private ProgressHUD mProgressHUD;
    private Context mContext;
    private FrgWishlistBinding mBinding;
    private SharedPrefs mPrefs;
    private String userId = "";
    private WishListAdapter mAdapter;
    private ArrayList<WishListModel.Data> mArrayWishList = new ArrayList<>();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
        ((HomeActivity) mContext).setDrawerSwipe(true);
        ((HomeActivity) mContext).setVisibilityCart(true);
        ((HomeActivity) mContext).getCartCount();

        ((HomeActivity) mContext).mBinding.layHeader.layCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewOrderFragment frgReviewOrder = new ReviewOrderFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString("fromWhere", "wishList");
                frgReviewOrder.setArguments(mBundle);
                replaceFragment(frgReviewOrder, true);
            }
        });

        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_wishlist, container, false);
        mPrefs = new SharedPrefs(mContext);

        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginModel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginModel.id);
            storeId = String.valueOf(loginModel.store_id);
            if (Utils.checkNetwork(mContext) == 1) {
                requestWishList(userId);
            } else {
                Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
            }
        }
        setFonts();
        return mBinding.getRoot();
    }

    private void setFonts() {
        Utils.changeFont(mContext, mBinding.tvHeader, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.rvWishList.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mBinding.rvWishList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new WishListAdapter(mContext, mArrayWishList, WishListFragment.this);
        mBinding.rvWishList.setAdapter(mAdapter);
    }

    private void requestWishList(String userId) {
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<WishListModel> loginCall = new RestClient().getApiService().wishList(mPrefs.getString(SharedPrefs.LANGUAGE), userId, storeId);
        loginCall.enqueue(new retrofit2.Callback<WishListModel>() {
            @Override
            public void onResponse(Call<WishListModel> call, retrofit2.Response<WishListModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        mArrayWishList.clear();
                        mBinding.rvWishList.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setVisibility(View.GONE);
                        mArrayWishList.addAll(response.body().data);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mBinding.rvWishList.setVisibility(View.GONE);
                        mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setText(response.body().message);
                        Log.e("Success0", response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<WishListModel> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                Log.e("error", "" + t.getMessage());
            }
        });
    }

    @Override
    public void onItemDeleteClick(final int position, View view) {
        Utils.showAlertDialogWith2ClickListener(mContext, getResources().getString(R.string.delete_msg), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestFavourite(0, position);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemDetailClick(int position, View view) {
        ProductDetailFragment frgProductDetail = new ProductDetailFragment();
        frgProductDetail.setParentWishListFragment(WishListFragment.this);
        Bundle mBundle = new Bundle();
        mBundle.putString("productDetail", new Gson().toJson(mArrayWishList.get(position)));
        mBundle.putString("banner", mArrayWishList.get(position).catImage);
        mBundle.putString("catName", mArrayWishList.get(position).catName);
        mBundle.putString("catDesc", mArrayWishList.get(position).catDesc);
        mBundle.putString("catId", String.valueOf(mArrayWishList.get(position).pCatId));
        mBundle.putBoolean("isDisplayCatName", mArrayWishList.get(position).hide_name.equalsIgnoreCase("1") ? true : false);
        mBundle.putBoolean("isDisplayCatDesc", mArrayWishList.get(position).hide_description.equalsIgnoreCase("1") ? true : false);
        mBundle.putString("from", "wishlist");
        mBundle.putInt("itempos", position);
        frgProductDetail.setArguments(mBundle);
        replaceFragment(frgProductDetail, true);
    }

    private void requestFavourite(final int isFavourite, final int position) {

        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<GeneralModel> favouriteCall = new RestClient().getApiService().requestFavourite(mPrefs.getString(SharedPrefs.LANGUAGE), userId,
                String.valueOf(mArrayWishList.get(position).pId), String.valueOf(isFavourite), String.valueOf(mArrayWishList.get(position).pCatId));
        favouriteCall.enqueue(new retrofit2.Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                //Utils.HideProgress(DIALOG);
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success", response.body().message);
                        mArrayWishList.remove(position);
                        mAdapter.notifyDataSetChanged();
//                        Utils.showAlertDialog(mContext, response.body().message);

                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                Log.e("error", "" + t.getMessage());
                //Utils.showAlertDialog(ACTIVITY, t.getMessage());

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

    public void refreshList(int pos) {
        mArrayWishList.remove(pos);
        mAdapter.notifyDataSetChanged();
    }

}