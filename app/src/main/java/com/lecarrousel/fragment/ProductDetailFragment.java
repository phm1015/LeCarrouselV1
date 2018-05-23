package com.lecarrousel.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.activity.LoginActivity;
import com.lecarrousel.adapter.ProductDetailPagerAdapter;
import com.lecarrousel.adapter.RowSpinnerAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgProductDetailBinding;
import com.lecarrousel.model.AddToCartModel;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

public class ProductDetailFragment extends BaseFragment {
    private Context mContext;
    private SharedPrefs mPrefs;
    private FrgProductDetailBinding mBinding;
    private String userId, mBannerLink, mCatId, mCatName, mCatDesc, storeId;
    private ProductListModel.Data mProductDetail;
    private RowSpinnerAdapter spAdapter;
    private ArrayList<String> qtyList = new ArrayList<>();
    private LoginModel.Data mLoginModel;
    private ProgressHUD mProgressHUD;
    private int isFavourite;
    private ProductListFragment productListFragment;
    private WishListFragment wishlistfragment;
    private int itemPos;
    private String SELECTED_PATH;
    private String from = "";
    private boolean payNowClick = false, hideName = false, hideDescription = false;
    private boolean isArabic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void setParentFragment(ProductListFragment productListFragment) {
        this.productListFragment = productListFragment;
    }

    public void setParentWishListFragment(WishListFragment wishListFragment) {
        this.wishlistfragment = wishListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_product_detail, container, false);

        mPrefs = new SharedPrefs(mContext);
        isArabic = mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar");

        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            ((HomeActivity) mContext).setVisibilityCart(true);
            mLoginModel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(mLoginModel.id);
            storeId = String.valueOf(mLoginModel.store_id);
            mBinding.ivProductIsFavorite.setVisibility(View.VISIBLE);
        } else {
            userId = "";
            storeId = mPrefs.getString(SharedPrefs.STORE_ID);
            ((HomeActivity) mContext).setVisibilityCart(false);
        }

        Bundle mBundle = getArguments();
        if (mBundle != null) {
            mBannerLink = mBundle.getString("banner");
            mCatName = mBundle.getString("catName");
            mCatDesc = mBundle.getString("catDesc");
            from = mBundle.getString("from", "");
            mCatId = mBundle.getString("catId");
            itemPos = mBundle.getInt("itempos");
            hideName = mBundle.getBoolean("isDisplayCatName");
            hideDescription = mBundle.getBoolean("isDisplayCatDesc");
            mProductDetail = new Gson().fromJson(mBundle.getString("productDetail"), ProductListModel.Data.class);
            Log.e("Tag1", mCatId + " " + from);
        }

        setDisplayValue();
        mBinding.vpProducts.setAdapter(new ProductDetailPagerAdapter(mContext, mProductDetail.imgData));
        mBinding.layShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mProductDetail.pShareLink;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

//                new DownloadImage().execute(mProductDetail.imgData.get(0).img);
            }
        });

        mBinding.tvAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userId.equalsIgnoreCase("")) {
                    Utils.showLogInDialog(mContext, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().startActivity(new Intent(mContext, LoginActivity.class));
                            getActivity().finish();
                        }
                    });
                } else {
                    if (Utils.checkNetwork(mContext) == 1) {
                        payNowClick = false;
                        requestAddToCart();
                    } else {
                        Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                    }
                }

            }
        });

        mBinding.tvPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId.equalsIgnoreCase("")) {
                    Utils.showLogInDialog(mContext, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().startActivity(new Intent(mContext, LoginActivity.class));
                            getActivity().finish();
                        }
                    });
                } else {
                    if (Utils.checkNetwork(mContext) == 1) {
                        payNowClick = true;
                        requestAddToCart();
                    } else {
                        Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                    }
                }
            }
        });

        mBinding.layBanner.setOnTouchListener(new View.OnTouchListener() {
            float initialX, finalX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        initialX = event.getX();
                    case (MotionEvent.ACTION_UP):
                        finalX = event.getX();

                        if (initialX < finalX) {
                            //Log.d("!!!!!", "moving right");
                            if (!isArabic) {
                                getActivity().onBackPressed();
                            }

                        } else if (initialX > finalX) {
                            //Log.d("!!!!!!", "moving left");
                            if (isArabic) {
                                getActivity().onBackPressed();
                            }
                        }
                    default:
                }

                return true;
            }
        });
        return mBinding.getRoot();

    }

    private void requestAddToCart() {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<AddToCartModel> favouriteCall = new RestClient().getApiService().addToCart(mPrefs.getString(SharedPrefs.LANGUAGE),
                userId,
                String.valueOf(mProductDetail.pId),
                mBinding.tvQty.getText().toString(),
                "1", storeId);
        favouriteCall.enqueue(new retrofit2.Callback<AddToCartModel>() {
            @Override
            public void onResponse(Call<AddToCartModel> call, final retrofit2.Response<AddToCartModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful()) {
                        if (response.body().status.equalsIgnoreCase("1")) {
                            Log.e("Success", response.body().message);
//                        when pay now is clicked
                            if (payNowClick) {
                                ReviewOrderFragment reviewFrg = new ReviewOrderFragment();
                                Bundle mBundle = new Bundle();
                                mBundle.putString("fromWhere", "ProductDetail");
                                reviewFrg.setArguments(mBundle);
                                replaceFragment(reviewFrg, true);
                            }
                            ((HomeActivity) mContext).setCartCount(String.valueOf(response.body().data.get(0).cart_count));
                        } else if (response.body().status.equalsIgnoreCase("3")) {

                            final int storeId = response.body().data.get(0).store_id;

                            Utils.showAlertDialogWithClickListener(mContext, getResources().getString(R.string.storeid_not_match), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
                                    loginmodel.store_id = storeId;
                                    mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, new Gson().toJson(loginmodel));
                                    String imgPath = Utils.getCountryModelFromId(getActivity(), storeId).countryFlag;
                                    ((HomeActivity) mContext).changeCountryFlag(imgPath);
                                    ((HomeActivity) mContext).changeFragment(new CategoryFragment());
                                    getActivity().onBackPressed();
                                }
                            });
                        } else {
                            Utils.showAlertDialog(mContext, response.body().message);
                        }

                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddToCartModel> call, Throwable t) {
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

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressHUD = ProgressHUD.show(mContext, getResources().getString(R.string.please_wait), true, true, false, null);


        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            SELECTED_PATH = new Utils().reduceImageSize(1000, 60, result, "ShareImage");
            // Close progressdialog
            mProgressHUD.dismissProgressDialog(mProgressHUD);

            try {
                Log.e("TAG", "PATH =" + SELECTED_PATH);
                Uri imageUri = Uri.parse("file://" + SELECTED_PATH);
                Log.e("TAG", "URI" + imageUri);
                Intent shareIntent = new Intent();
                shareIntent.setType("image/*");
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, mProductDetail.pDesc);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mProductDetail.pDesc);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                mContext.startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share)));
            } catch (Exception e) {
                Log.e("Tag", e.getMessage());
            }
        }
    }

    private void setDisplayValue() {
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
        ((HomeActivity) mContext).setDrawerSwipe(false);
        ((HomeActivity) mContext).getCartCount();
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvProductName, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvShare, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvQuantityLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvTotalLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvQty, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layPrice, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layCartButtons, Utils.FontStyle.BOLD);

        mBinding.tvCategoryTitle.setText(mCatName);
        mBinding.tvCategoryDesc.setText(mCatDesc);
        Glide.with(mContext).load(mBannerLink).centerCrop().crossFade().into(mBinding.ivBanner);
        if (hideName) {
            mBinding.tvCategoryTitle.setVisibility(View.GONE);
        } else {
            mBinding.tvCategoryTitle.setVisibility(View.VISIBLE);
        }
        if (hideDescription) {
            mBinding.tvCategoryDesc.setVisibility(View.GONE);
        } else {
            mBinding.tvCategoryDesc.setVisibility(View.VISIBLE);
        }
        if (mBinding.tvCategoryDesc.getText().toString().trim().length() == 0) {
            mBinding.tvCategoryDesc.setVisibility(View.GONE);
        } else {
            mBinding.tvCategoryDesc.setVisibility(View.VISIBLE);
        }




        mBinding.tvProductName.setText(mProductDetail.pName);
        if (mProductDetail.isFavourite.equalsIgnoreCase("1")) {
            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_filled);
        } else {
            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_empty);
        }
        mBinding.tvProductDesc.setText(mProductDetail.pDesc);
        mBinding.tvPrice.setText(String.valueOf(mProductDetail.pPrice));
        if (mProductDetail.imgData.size() == 1) {
            mBinding.ivArrowLeft.setVisibility(View.INVISIBLE);
            mBinding.ivArrowRight.setVisibility(View.INVISIBLE);
        } else {
            mBinding.ivArrowLeft.setVisibility(View.VISIBLE);
            mBinding.ivArrowRight.setVisibility(View.VISIBLE);
        }
        mBinding.ivArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProductDetail.imgData.size() > mBinding.vpProducts.getCurrentItem()) {
                    mBinding.vpProducts.setCurrentItem(mBinding.vpProducts.getCurrentItem() + 1);
                }

            }
        });
        mBinding.ivArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.vpProducts.getCurrentItem() > 0) {
                    mBinding.vpProducts.setCurrentItem(mBinding.vpProducts.getCurrentItem() - 1);
                }

            }
        });

        mBinding.ivProductIsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (from.equalsIgnoreCase("")) {
                    if (mProductDetail.isFavourite.equalsIgnoreCase("0")) {
                        isFavourite = 1;
                    } else {
                        isFavourite = 0;
                    }
                    requestFavourite(isFavourite);
                } else {
                    Utils.showAlertDialogWith2ClickListener(mContext, getResources().getString(R.string.remove_record), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestFavourite(0);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }

            }
        });

        mBinding.layQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.spQty.performClick();
            }
        });
        spAdapter = new RowSpinnerAdapter(getActivity(), qtyList);
        mBinding.spQty.setAdapter(spAdapter);
        displayQty();
        mBinding.spQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mBinding.tvQty.setText("1");
                    mBinding.tvPrice.setText(String.valueOf(mProductDetail.pPrice));
                } else {
                    mBinding.tvPrice.setText(String.valueOf(mProductDetail.pPrice * (position)));
                    mBinding.tvQty.setText(qtyList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void requestFavourite(final int isFavourite) {
        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            userId = String.valueOf(mLoginModel.id);
        } else {
            userId = "";
        }
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<GeneralModel> favouriteCall = new RestClient().getApiService().requestFavourite(mPrefs.getString(SharedPrefs.LANGUAGE), userId,
                String.valueOf(mProductDetail.pId), String.valueOf(isFavourite), mCatId);
        favouriteCall.enqueue(new retrofit2.Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                //Utils.HideProgress(DIALOG);
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success", response.body().message);
                        if (String.valueOf(isFavourite).equalsIgnoreCase("1")) {
                            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_filled);
                            productListFragment.refreshList(itemPos, true);

                        } else {
                            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_empty);
                            if (from.equalsIgnoreCase("")) {
                                productListFragment.refreshList(itemPos, false);
                            } else {
                                wishlistfragment.refreshList(itemPos);
                                getActivity().onBackPressed();
                            }
                        }
                        mProductDetail.isFavourite = String.valueOf(isFavourite);
                        //Utils.showAlertDialog(mContext, response.body().message);

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

    private void displayQty() {

        if (qtyList.size() > 0) {
            qtyList.clear();
        }
        for (int i = 0; i < (mProductDetail.maxOrderQty + 1); i++) {
            if (i == 0) {
                qtyList.add(0, mContext.getResources().getString(R.string.quantity));
            } else {
                qtyList.add("" + (i));
            }
        }
        spAdapter.notifyDataSetChanged();
    }

}
