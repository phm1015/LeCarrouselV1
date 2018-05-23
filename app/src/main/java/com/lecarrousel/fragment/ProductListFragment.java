package com.lecarrousel.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.lecarrousel.adapter.HighLightPagerAdapter;
import com.lecarrousel.adapter.ProductGridAdapter;
import com.lecarrousel.adapter.RowSpinnerCategoryAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgProductListBinding;
import com.lecarrousel.model.CategoryListModel;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;
import com.lecarrousel.videoadapter.RecycleItemClickListener;
import com.lecarrousel.videoadapter.TimelineAdapter;

import java.io.IOException;
import java.util.ArrayList;

import im.ene.toro.Toro;
import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

public class ProductListFragment extends BaseFragment implements RecycleItemClickListener, ProductGridAdapter.RecycleListClickListener {
    private Context mContext;
    private ProgressHUD mProgressHUD;
    private SharedPrefs mPrefs;
    private FrgProductListBinding mBinding;
    private TimelineAdapter mListAdapter;
    private ProductGridAdapter mGridAdapter;
    private HighLightPagerAdapter mHighLightAdapter;
    private String userId, storeId;
    private boolean isGrid = false;
    private LoginModel.Data mLoginModel;
    private int isFavourite = 0, isFavProductPos, isFavHighLightPos;
    private CategoryListModel.Data mArrayCategoryList;
    private RowSpinnerCategoryAdapter spAdapter;
    private boolean isArabic, isHighLightClick;
    public ArrayList<ProductListModel.Data> mArrayProductList = new ArrayList<>();
    public ArrayList<ProductListModel.Data> mArrayHighLightList = new ArrayList<>();

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_product_list, container, false);
        setFonts();
        mPrefs = new SharedPrefs(mContext);
        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            ((HomeActivity) mContext).setVisibilityCart(true);
            mLoginModel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(mLoginModel.id);
            storeId = String.valueOf(mLoginModel.store_id);
        } else {
            storeId = mPrefs.getString(SharedPrefs.STORE_ID);
            ((HomeActivity) mContext).setVisibilityCart(false);
        }
        isArabic = mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar");
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            mArrayCategoryList = new Gson().fromJson(mBundle.getString("data"), CategoryListModel.Data.class);
        }

        CategoryListModel.Types mTypes = new CategoryListModel.Types();
        mTypes.typeId = 0;
        mTypes.typeName = mContext.getResources().getString(R.string.all_product);
        mArrayCategoryList.Types.add(0, mTypes);

        mBinding.tvCategoryTitle.setText(mArrayCategoryList.catName);
        mBinding.tvCategoryDesc.setText(mArrayCategoryList.catDesc);

        if (mArrayCategoryList.hide_name.equalsIgnoreCase("1")) {
            mBinding.tvCategoryTitle.setVisibility(View.GONE);
        } else {
            mBinding.tvCategoryTitle.setVisibility(View.VISIBLE);
        }
        if (mArrayCategoryList.hide_description.equalsIgnoreCase("1")) {
            mBinding.tvCategoryDesc.setVisibility(View.GONE);
        } else {
            mBinding.tvCategoryDesc.setVisibility(View.VISIBLE);
        }

        if (mBinding.tvCategoryDesc.getText().toString().trim().length() == 0) {
            mBinding.tvCategoryDesc.setVisibility(View.GONE);
        } else {
            mBinding.tvCategoryDesc.setVisibility(View.VISIBLE);
        }

        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
        ((HomeActivity) mContext).setDrawerSwipe(false);
        ((HomeActivity) mContext).getCartCount();

        Glide.with(mContext).load(mArrayCategoryList.catImage).centerCrop().crossFade().into(mBinding.ivBanner);
        mBinding.ivGridType.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
        mBinding.ivListType.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));

        mBinding.ivGridType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGrid) {
                    mBinding.ivGridType.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                    mBinding.ivListType.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                    if (mArrayProductList.size() != 0) {
                        mBinding.rvProductList.setVisibility(View.GONE);
                        mBinding.rvProductGrid.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.rvProductGrid.setVisibility(View.GONE);
                        mBinding.rvProductList.setVisibility(View.GONE);

                        if (mArrayHighLightList != null && mArrayHighLightList.size() == 0)
                            mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                    }
                    isGrid = true;
                }
            }
        });

        mBinding.ivListType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGrid) {
                    mBinding.ivListType.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                    mBinding.ivGridType.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                    if (mArrayProductList.size() != 0) {
                        mBinding.rvProductList.setVisibility(View.VISIBLE);
                        mBinding.rvProductGrid.setVisibility(View.GONE);
                    } else {
                        mBinding.rvProductGrid.setVisibility(View.GONE);
                        mBinding.rvProductList.setVisibility(View.GONE);

                        if (mArrayHighLightList != null && mArrayHighLightList.size() == 0)
                            mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                    }
                    isGrid = false;
                }
            }
        });


        mBinding.layCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.spCategory.performClick();
            }
        });

        mBinding.ivArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.vpHighLight.getCurrentItem() != 0) {
                    mBinding.vpHighLight.setCurrentItem(mBinding.vpHighLight.getCurrentItem() - 1);
                }
            }
        });

        mBinding.ivArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.vpHighLight.getCurrentItem() != mArrayHighLightList.size() - 1) {
                    mBinding.vpHighLight.setCurrentItem(mBinding.vpHighLight.getCurrentItem() + 1);
                }
            }
        });

        mBinding.vpHighLight.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    mBinding.ivArrowLeft.setVisibility(View.INVISIBLE);
                    mBinding.ivArrowRight.setVisibility(View.VISIBLE);
                } else if (i == mArrayHighLightList.size() - 1) {
                    mBinding.ivArrowRight.setVisibility(View.INVISIBLE);
                    mBinding.ivArrowLeft.setVisibility(View.VISIBLE);
                } else {
                    mBinding.ivArrowLeft.setVisibility(View.VISIBLE);
                    mBinding.ivArrowRight.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        spAdapter = new RowSpinnerCategoryAdapter(getActivity(), mArrayCategoryList.Types);
        mBinding.spCategory.setAdapter(spAdapter);
        mBinding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBinding.tvCategory.setText(mArrayCategoryList.Types.get(position).typeName);
                if (Utils.checkNetwork(mContext) == 1) {
                    requestHighLightList(userId, String.valueOf(mArrayCategoryList.catId), String.valueOf(mArrayCategoryList.Types.get(position).typeId));
                    requestProductList(userId, "1", String.valueOf(mArrayCategoryList.catId), String.valueOf(mArrayCategoryList.Types.get(position).typeId));
                } else {
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBinding.layHeader.setOnTouchListener(new View.OnTouchListener() {
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
                            if (!isArabic) {
                                getActivity().onBackPressed();
                            }
                        } else if (initialX > finalX) {
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

    private void setFonts() {
        Utils.changeFont(mContext, mBinding.tvCategoryTitle, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvCategoryDesc, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvCategory, Utils.FontStyle.BOLD);
    }

    private void requestProductList(String userId, String pageNo, String catId, String type) {
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<ProductListModel> loginCall = new RestClient().getApiService().productList(mPrefs.getString(SharedPrefs.LANGUAGE), userId, pageNo, catId, type, storeId);
        loginCall.enqueue(new retrofit2.Callback<ProductListModel>() {
            @Override
            public void onResponse(Call<ProductListModel> call, retrofit2.Response<ProductListModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    mArrayProductList.clear();
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        if (response.body().data.size() > 0) {
                            mArrayProductList.addAll(response.body().data);
                            setAdapter();
                            if (isGrid) {
                                mBinding.rvProductGrid.setVisibility(View.VISIBLE);
                                mBinding.rvProductList.setVisibility(View.GONE);
                            } else {
                                mBinding.rvProductGrid.setVisibility(View.GONE);
                                mBinding.rvProductList.setVisibility(View.VISIBLE);
                            }
                            mBinding.layContent.setVisibility(View.VISIBLE);
                            mBinding.tvNoRecord.setVisibility(View.GONE);
                        } else {
                            mBinding.layContent.setVisibility(View.GONE);
                            mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                            mBinding.tvNoRecord.setText(response.body().message);
                        }
                    } else {
                        mBinding.layContent.setVisibility(View.GONE);
                        mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                        mBinding.tvNoRecord.setText(response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductListModel> call, Throwable t) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                mBinding.layContent.setVisibility(View.GONE);
                mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                mBinding.tvNoRecord.setText(mContext.getResources().getString(R.string.data_not_found));
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

    private void requestHighLightList(String userId, String catId, String type) {
        Call<ProductListModel> highLightCall = new RestClient().getApiService().highLightList(mPrefs.getString(SharedPrefs.LANGUAGE), userId, catId, type, storeId);
        highLightCall.enqueue(new retrofit2.Callback<ProductListModel>() {
            @Override
            public void onResponse(Call<ProductListModel> call, retrofit2.Response<ProductListModel> response) {
                if (response != null) {
                    mArrayHighLightList.clear();
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        if (response.body().data.size() > 0) {
                            mBinding.layVpContent.setVisibility(View.VISIBLE);
                            mArrayHighLightList.addAll(response.body().data);
                            mHighLightAdapter = new HighLightPagerAdapter(mContext, mArrayHighLightList, mPagerItemClickListener);
                            mBinding.vpHighLight.setAdapter(mHighLightAdapter);
                            if (mArrayHighLightList.size() > 1) {
                                mBinding.ivArrowLeft.setVisibility(View.INVISIBLE);
                                mBinding.ivArrowRight.setVisibility(View.VISIBLE);
                            } else {
                                mBinding.ivArrowLeft.setVisibility(View.INVISIBLE);
                                mBinding.ivArrowRight.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            mBinding.layVpContent.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("Tag", "NO HIGHLIGHT");
                        mBinding.layVpContent.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductListModel> call, Throwable t) {
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

    @Override
    public void onItemClick(int position, View view) {
        isHighLightClick = false;
        ProductDetailFragment frgProductDetail = new ProductDetailFragment();
        frgProductDetail.setParentFragment(ProductListFragment.this);
        Bundle mBundle = new Bundle();
        mBundle.putString("productDetail", new Gson().toJson(mArrayProductList.get(position)));
        mBundle.putString("banner", mArrayCategoryList.catImage);
        mBundle.putString("catName", mArrayCategoryList.catName);
        mBundle.putString("catDesc", mArrayCategoryList.catDesc);
        mBundle.putString("catId", String.valueOf(mArrayCategoryList.catId));
        mBundle.putBoolean("isDisplayCatName", mArrayCategoryList.hide_name.equalsIgnoreCase("1") ? true : false);
        mBundle.putBoolean("isDisplayCatDesc", mArrayCategoryList.hide_description.equalsIgnoreCase("1") ? true : false);
        mBundle.putInt("itempos", position);
        frgProductDetail.setArguments(mBundle);
        replaceFragment(frgProductDetail, true);
    }

    @Override
    public void onFavouriteClick(int position, View view) {
        if (mArrayProductList.get(position).isFavourite.equalsIgnoreCase("0")) {
            isFavourite = 1;
        } else {
            isFavourite = 0;
        }
        requestFavourite(position, isFavourite, false);
    }

    @Override
    public void onPlayClick(int position, View view) {
        String videoUrl = mArrayProductList.get(position).videoUrl;
        VideoPlayFragment frg = new VideoPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videourl", videoUrl);
        frg.setArguments(bundle);
        frg.show(getFragmentManager(), null);
    }

    private void requestFavourite(final int position, final int isFavourite, final boolean flag) {
        /*if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            userId = String.valueOf(mLoginModel.id);
        } else {
            userId = "";
        }*/
        mProgressHUD = ProgressHUD.show(mContext, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<GeneralModel> favouriteCall;
        if (flag) {
            Log.d("!!!!", "HIGHLIGHT");
            favouriteCall = new RestClient().getApiService().requestFavourite(mPrefs.getString(SharedPrefs.LANGUAGE), userId, String.valueOf(mArrayHighLightList.get(position).pId), String.valueOf(isFavourite), String.valueOf(mArrayCategoryList.catId));
            int prodId = mArrayHighLightList.get(position).pId;
            for (int index = 0; index < mArrayProductList.size(); index++) {
                if (prodId == mArrayProductList.get(index).pId) {
                    isFavProductPos = index;
                    break;
                }
            }
        } else {
            favouriteCall = new RestClient().getApiService().requestFavourite(mPrefs.getString(SharedPrefs.LANGUAGE), userId, String.valueOf(mArrayProductList.get(position).pId), String.valueOf(isFavourite), String.valueOf(mArrayCategoryList.catId));
            int prodId = mArrayProductList.get(position).pId;
            for (int index = 0; index < mArrayHighLightList.size(); index++) {
                if (prodId == mArrayHighLightList.get(index).pId) {
                    isFavHighLightPos = index;
                    break;
                }
            }
        }
        favouriteCall.enqueue(new retrofit2.Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        if (flag) {
                            mArrayHighLightList.get(position).isFavourite = String.valueOf(isFavourite);
                            mHighLightAdapter.notifyDataSetChanged();
                            mArrayProductList.get(isFavProductPos).isFavourite = String.valueOf(isFavourite);
                            mGridAdapter.notifyDataSetChanged();
                            mListAdapter.notifyDataSetChanged();
                        } else {
                            mArrayProductList.get(position).isFavourite = String.valueOf(isFavourite);
                            mGridAdapter.notifyDataSetChanged();
                            mListAdapter.notifyDataSetChanged();
                            mArrayHighLightList.get(isFavHighLightPos).isFavourite = String.valueOf(isFavourite);
                            mHighLightAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
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

    public void refreshList(int position, boolean flag) {

        String isFav;
        if (flag)
            isFav = "1";
        else
            isFav = "0";

        if (isHighLightClick) {
            mArrayHighLightList.get(position).isFavourite = isFav;
            int prodId = mArrayHighLightList.get(position).pId;
            for (int index = 0; index < mArrayProductList.size(); index++) {
                if (prodId == mArrayProductList.get(index).pId) {
                    mArrayProductList.get(index).isFavourite = isFav;
                    mListAdapter.notifyDataSetChanged();
                    mGridAdapter.notifyDataSetChanged();
                    break;
                }
            }
            mHighLightAdapter.notifyDataSetChanged();
        } else {
            mArrayProductList.get(position).isFavourite = isFav;
            int prodId = mArrayProductList.get(position).pId;
            for (int index = 0; index < mArrayHighLightList.size(); index++) {
                if (prodId == mArrayHighLightList.get(index).pId) {
                    mArrayHighLightList.get(index).isFavourite = isFav;
                    mHighLightAdapter.notifyDataSetChanged();
                    break;
                }
            }
            mListAdapter.notifyDataSetChanged();
            mGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mArrayProductList.size() != 0) {
            Toro.unregister(mBinding.rvProductList);
        }
    }

    private void setAdapter() {
        mBinding.rvProductGrid.setLayoutManager(new GridLayoutManager(mContext, 2));
        mBinding.rvProductGrid.setItemAnimator(new DefaultItemAnimator());
        mGridAdapter = new ProductGridAdapter(mContext, mArrayProductList, ProductListFragment.this);
        mBinding.rvProductGrid.setAdapter(mGridAdapter);
        mBinding.rvProductGrid.setNestedScrollingEnabled(false);

        mBinding.rvProductList.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mBinding.rvProductList.setItemAnimator(new DefaultItemAnimator());
        mListAdapter = new TimelineAdapter(mContext, mArrayProductList, TimelineAdapter.TYPE_LIST, ProductListFragment.this);
        mBinding.rvProductList.setHasFixedSize(false);
        mBinding.rvProductList.setAdapter(mListAdapter);
        mBinding.rvProductList.setNestedScrollingEnabled(false);
        Toro.register(mBinding.rvProductList);
    }

    private RecycleItemClickListener mPagerItemClickListener = new RecycleItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            isHighLightClick = true;
            ProductDetailFragment frgProductDetail = new ProductDetailFragment();
            frgProductDetail.setParentFragment(ProductListFragment.this);
            Bundle mBundle = new Bundle();
            mBundle.putString("productDetail", new Gson().toJson(mArrayHighLightList.get(position)));
            mBundle.putString("banner", mArrayCategoryList.catImage);
            mBundle.putString("catName", mArrayCategoryList.catName);
            mBundle.putString("catDesc", mArrayCategoryList.catDesc);
            mBundle.putString("catId", String.valueOf(mArrayCategoryList.catId));
            mBundle.putBoolean("isDisplayCatName", mArrayCategoryList.hide_name.equalsIgnoreCase("1") ? true : false);
            mBundle.putBoolean("isDisplayCatDesc", mArrayCategoryList.hide_description.equalsIgnoreCase("1") ? true : false);
            mBundle.putInt("itempos", position);
            frgProductDetail.setArguments(mBundle);
            replaceFragment(frgProductDetail, true);
        }

        @Override
        public void onFavouriteClick(int position, View view) {
            if (mArrayHighLightList.get(position).isFavourite.equalsIgnoreCase("0")) {
                isFavourite = 1;
            } else {
                isFavourite = 0;
            }
            requestFavourite(position, isFavourite, true);
        }
    };
}
