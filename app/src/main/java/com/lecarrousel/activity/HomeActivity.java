package com.lecarrousel.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.lecarrousel.App;
import com.lecarrousel.R;
import com.lecarrousel.adapter.DrawerAdapter;
import com.lecarrousel.adapter.SocialMediaAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.ActHomeBinding;
import com.lecarrousel.fragment.CategoryFragment;
import com.lecarrousel.fragment.ChangeStoreFragment;
import com.lecarrousel.fragment.MyOrderDetailFragment;
import com.lecarrousel.fragment.MyOrderFragment;
import com.lecarrousel.fragment.MyProfileFragment;
import com.lecarrousel.fragment.NotificationFragment;
import com.lecarrousel.fragment.ProductDetailFragment;
import com.lecarrousel.fragment.ProductListFragment;
import com.lecarrousel.fragment.ReviewOrderFragment;
import com.lecarrousel.fragment.TermsFragment;
import com.lecarrousel.fragment.WishListFragment;
import com.lecarrousel.model.CartCountListModel;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.model.SideMenuModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.TakePicture;
import com.lecarrousel.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

public class HomeActivity extends FragmentActivity implements SocialMediaAdapter.RecycleListClickListener {
    public ActHomeBinding mBinding;
    private Activity ACTIVITY = HomeActivity.this;
    private ArrayList<SideMenuModel> arrDrawerList = new ArrayList<>();
    private SharedPrefs mPrefs;
    private String userId = "", storeId;
    public int mMenuSelection = 0;
    private LoginModel.Data mLoginModel;
    private ProgressHUD mProgressHUD;
    private ArrayList<MasterDataModel.Social_account> mArraySocialList = new ArrayList<>();
    private ArrayList<MasterDataModel.COUNTRIES> mStoreList = new ArrayList<>();
    private SocialMediaAdapter mSocialAdapter;
    public int clickPosition;
    private boolean isLogin;
    private MyProfileFragment mMyProfileFrg;
    private ReviewOrderFragment frgReviewOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_home);
        mPrefs = new SharedPrefs(ACTIVITY);
        mStoreList = mPrefs.getCountryList();
        isLogin = mPrefs.getBoolean(SharedPrefs.IS_LOGIN);
        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            mLoginModel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(mLoginModel.id);
            storeId = String.valueOf(mLoginModel.store_id);
            MasterDataModel.COUNTRIES countryModel = Utils.getCountryModelFromId(ACTIVITY, Integer.parseInt(storeId));
            changeCountryFlag(countryModel.countryFlag);

            mBinding.sideMenu.tvUserName.setText(mLoginModel.name);
            Glide.with(ACTIVITY).load(mPrefs.getString(SharedPrefs.USER_PHOTO)).asBitmap().centerCrop().dontAnimate().into(new BitmapImageViewTarget(mBinding.sideMenu.ivProfilePic) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(ACTIVITY.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mBinding.sideMenu.ivProfilePic.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            storeId = mPrefs.getString(SharedPrefs.STORE_ID);
            MasterDataModel.COUNTRIES countryModel = Utils.getCountryModelFromId(ACTIVITY, Integer.parseInt(storeId));
            changeCountryFlag(countryModel.countryFlag);

            Glide.with(ACTIVITY).load(R.drawable.ic_user_holder).asBitmap().centerCrop().dontAnimate().into(new BitmapImageViewTarget(mBinding.sideMenu.ivProfilePic) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(ACTIVITY.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mBinding.sideMenu.ivProfilePic.setImageDrawable(circularBitmapDrawable);
                }
            });
        }

        setIvLeftAction();

        setDrawerArray();

        DrawerAdapter adapter = new DrawerAdapter(this, R.layout.row_menu_list, arrDrawerList);
        mBinding.sideMenu.lvMenu.setAdapter(adapter);
        setListViewHeightBasedOnChildren(mBinding.sideMenu.lvMenu);
        mBinding.sideMenu.lvMenu.setOnItemClickListener(new DrawerItemClickListener());
        setDrawerSwipe(true);

        mBinding.sideMenu.tvArebic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("en")) {
                    mPrefs.putString(SharedPrefs.LANGUAGE, "ar");
                    App.setLocale(new Locale("ar"));
                    ACTIVITY.finish();
                    startActivity(new Intent(ACTIVITY, SplashActivity.class));
                } else {
                    mBinding.drawerLayout.closeDrawer(Gravity.START);
                }
            }
        });

        mBinding.sideMenu.tvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                    mPrefs.putString(SharedPrefs.LANGUAGE, "en");
                    App.setLocale(new Locale("en"));
                    ACTIVITY.finish();
                    startActivity(new Intent(ACTIVITY, SplashActivity.class));
                } else {
                    mBinding.drawerLayout.closeDrawer(Gravity.START);
                }
            }
        });

        mBinding.layHeader.layCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new ReviewOrderFragment());
            }
        });

        mBinding.sideMenu.tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMenuSelection = 11;
                changeFragment(new TermsFragment());
                mBinding.drawerLayout.closeDrawer(Gravity.START);
                mMenuSelection = -1;
                clickPosition = -1;
            }
        });

        mBinding.sideMenu.ivCountryFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuSelection = -1;
                clickPosition = -1;
                mBinding.drawerLayout.closeDrawer(Gravity.START);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                    transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                } else {
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                }

                transaction.add(R.id.frgContainer, new ChangeStoreFragment());
                transaction.commit();

            }
        });

        // Change Font
        Utils.changeFont(ACTIVITY, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(ACTIVITY, mBinding.sideMenu.tvEnglish, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.sideMenu.tvArebic, Utils.FontStyle.BOLD);
        Utils.changeFont(ACTIVITY, mBinding.sideMenu.tvTerms, Utils.FontStyle.BOLD);

        requestMasterDetail();

//        if (!mPrefs.getBoolean(SharedPrefs.IS_FCM_TOKEN_ADDED)) {
            requestFcmToken();
//        }

        changeFragment(new CategoryFragment());

        mBinding.drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {

            }

            @Override
            public void onDrawerOpened(View view) {
            }

            @Override
            public void onDrawerClosed(View view) {
                if (mMenuSelection != clickPosition) {
                    selectItem(clickPosition);
                }
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment frg = getSupportFragmentManager().findFragmentById(R.id.frgContainer);
                if (frg instanceof CategoryFragment) {
                    if (isLogin) {
                        setVisibilityCart(true);
                    } else {
                        setVisibilityCart(false);
                    }
                    setMenuIcon(R.drawable.ic_menu);
                    setDrawerSwipe(true);
                } else if (frg instanceof MyOrderFragment) {
                    setVisibilityCart(true);
                    setMenuIcon(R.drawable.ic_menu);
                    setDrawerSwipe(true);
                } else if (frg instanceof ProductDetailFragment) {
                    if (isLogin) {
                        setVisibilityCart(true);
                    } else {
                        setVisibilityCart(false);
                    }
                    setMenuIcon(R.drawable.ic_back);
                    setDrawerSwipe(false);
                } else if (frg instanceof ProductListFragment) {
                    if (isLogin) {
                        setVisibilityCart(true);
                    } else {
                        setVisibilityCart(false);
                    }
                    setMenuIcon(R.drawable.ic_back);
                    setDrawerSwipe(false);
                } else if (frg instanceof WishListFragment) {
                    setVisibilityCart(true);
                    setMenuIcon(R.drawable.ic_menu);
                    setDrawerSwipe(true);
                }


            }
        });

        // notification payload
        checkNotification();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
//            getCartCount();
        } else {
            if (mBinding.drawerLayout.isDrawerOpen(Gravity.START)) {
                mBinding.drawerLayout.closeDrawer(Gravity.START);
            } else {
                Utils.showAlertDialogExit(ACTIVITY, getResources().getString(R.string.exit_txt));
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setDrawerArray() {
        SideMenuModel model = new SideMenuModel();
        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            model.name = getResources().getString(R.string.occassion);
            model.icon = R.drawable.ic_occassion;
            arrDrawerList.add(model);

            model = new SideMenuModel();
            model.name = getResources().getString(R.string.my_orders);
            model.icon = R.drawable.ic_my_orders;
            arrDrawerList.add(model);

            model = new SideMenuModel();
            model.name = getResources().getString(R.string.wishlist);
            model.icon = R.drawable.ic_whishlist;
            arrDrawerList.add(model);


            model = new SideMenuModel();
            model.name = getResources().getString(R.string.my_cart);
            model.icon = R.drawable.ic_mycart;
            arrDrawerList.add(model);


            model = new SideMenuModel();
            model.name = getResources().getString(R.string.my_profile);
            model.icon = R.drawable.ic_profile;
            arrDrawerList.add(model);

            model = new SideMenuModel();
            model.name = getResources().getString(R.string.notifications);
            model.icon = R.drawable.ic_notification;
            arrDrawerList.add(model);

            model = new SideMenuModel();
            model.name = getResources().getString(R.string.logout);
            model.icon = R.drawable.ic_logout;
            arrDrawerList.add(model);
        } else {
            model = new SideMenuModel();
            model.name = getResources().getString(R.string.login);
            model.icon = R.drawable.ic_logout;
            arrDrawerList.add(model);
        }
    }

    @Override
    public void onItemClick(int position, View view) {
        String mSocialName = mArraySocialList.get(position).name;
        String mPageId = mArraySocialList.get(position).pageId;
        String mUrl = mArraySocialList.get(position).accountUrl;
        Log.e("Tag", "URL: " + mArraySocialList.get(position).accountUrl);
        if (mSocialName.equalsIgnoreCase("facebook")) {
            openFBPage(mUrl, mPageId);
        } else if (mSocialName.equalsIgnoreCase("twitter")) {
            openTwitter(mPageId, mUrl);
        } else if (mSocialName.equalsIgnoreCase("instagram")) {
            openInstagram(mPageId);
        } else if (mSocialName.equalsIgnoreCase("linkedin")) {
            openLinkedIn(mPageId, mUrl);
        } else if (mSocialName.equalsIgnoreCase("googleplus")) {
            openGooglePlus(mPageId);
        } else if (mSocialName.equalsIgnoreCase("youtube")) {
            openYoutube(mPageId);
        } else if (mSocialName.equalsIgnoreCase("pintrest")) {
            openPintrest(mUrl);
        } else {
            String url = mArraySocialList.get(position).accountUrl.trim();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    private void openPintrest(String mUrl) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(mUrl));
        startActivity(i);
    }

    private void openYoutube(String mPageId) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mPageId));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + mPageId));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void openGooglePlus(String mPageId) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + mPageId + "/posts")));
    }

    private void openLinkedIn(String mPageId, String mUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://" + mPageId));
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.isEmpty()) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=" + mPageId));
            }
            startActivity(intent);
        } catch (Exception e) {
            Log.e("Tag", "Error: " + e.getMessage());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(mUrl));
            startActivity(i);
        }
    }

    private void openInstagram(String pageId) {
        Uri uri = Uri.parse("http://instagram.com/_u/" + pageId);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/" + pageId)));
        }
    }

    private void openTwitter(String user_id, String twitterUrl) {
        Intent intent = null;
        try {
            // get the Twitter app if possible
            this.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + user_id));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
        }
        this.startActivity(intent);
    }

    private void openFBPage(String FACEBOOK_URL, String FACEBOOK_PAGE_ID) {
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse("fb://page/" + FACEBOOK_PAGE_ID));
                startActivity(facebookIntent);
            } else { //older versions of fb app
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse("fb://page/" + FACEBOOK_PAGE_ID));
                startActivity(i);
//                return "fb://page/" + FACEBOOK_PAGE_ID;
            }


        } catch (PackageManager.NameNotFoundException e) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(FACEBOOK_URL));
            startActivity(i);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
                if (mMenuSelection == position) {
                    mBinding.drawerLayout.closeDrawer(Gravity.START);
                } else {
                    if (position != 6) {
                        mBinding.drawerLayout.closeDrawer(Gravity.START);
                        clickPosition = position;
                    } else {
                        selectItem(position);
                    }
                }
            } else {
                Intent mIntent = new Intent(ACTIVITY, LoginActivity.class);
                startActivity(mIntent);
                finish();
            }
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:  // OCCASSIONS
                mMenuSelection = 0;
                changeFragment(new CategoryFragment());
                break;
            case 1: // MY ORDERS
                mMenuSelection = 1;
                MyOrderFragment myOrderFrg = new MyOrderFragment();
                changeFragment(myOrderFrg);
                break;
            case 2: // MY WISHLIST
                mMenuSelection = 2;
                changeFragment(new WishListFragment());
                break;
            case 3: // MY CART
                mMenuSelection = 3;
                frgReviewOrder = new ReviewOrderFragment();
                changeFragment(frgReviewOrder);
                break;
            case 4: // my profile
                mMenuSelection = 4;
                mMyProfileFrg = new MyProfileFragment();
                changeFragment(mMyProfileFrg);
                break;

            case 5: // Notifications
                mMenuSelection = 5;
                NotificationFragment frgNotificationOrder = new NotificationFragment();
                changeFragment(frgNotificationOrder);
                break;
            case 6: // LOGOUT
                Utils.showLogoutDialog(ACTIVITY, mLogoutClickListener);
                break;
        }
    }

    public void setMenuIcon(int id) {
        mBinding.layHeader.ivLeftAction.setImageResource(id);
    }

    public void setIvLeftAction() {
        mBinding.layHeader.ivLeftAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    if (mBinding.drawerLayout.isDrawerOpen(Gravity.START)) {
                        mBinding.drawerLayout.closeDrawer(Gravity.START);
                    } else {
                        mBinding.drawerLayout.openDrawer(Gravity.START);
                    }
                }
            }
        });
    }

    public void setDrawerSwipe(boolean slide) {
        if (!slide) {
            mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        } else {
            mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.STATE_IDLE);
        }

    }

    public void resetUpdatedProfile() {
        mLoginModel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
        mBinding.sideMenu.tvUserName.setText(mLoginModel.name);
        Glide.with(ACTIVITY).load(mPrefs.getString(SharedPrefs.USER_PHOTO)).asBitmap().centerCrop().dontAnimate().into(new BitmapImageViewTarget(mBinding.sideMenu.ivProfilePic) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(ACTIVITY.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                mBinding.sideMenu.ivProfilePic.setImageDrawable(circularBitmapDrawable);
            }

        });
    }

    public void setVisibilityCart(boolean visibility) {
        if (!visibility) {
            mBinding.layHeader.layCart.setVisibility(View.INVISIBLE);
        } else {
            mBinding.layHeader.layCart.setVisibility(View.VISIBLE);
        }
    }

    public void setCartCount(String strCount) {

        if (strCount.equalsIgnoreCase("0")) {
            mBinding.layHeader.tvCartCount.setVisibility(View.INVISIBLE);
        } else {
            mBinding.layHeader.tvCartCount.setVisibility(View.VISIBLE);
            mBinding.layHeader.tvCartCount.setText(strCount);
        }
    }


    private void requestFcmToken() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Tag", "Fcm Token: " + refreshedToken);

        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            userId = String.valueOf(mLoginModel.id);
        } else {
            userId = "";
        }
        if (Utils.checkNetwork(ACTIVITY) == 1 && refreshedToken != null && refreshedToken.length() > 0) {
            requestAddToken(userId, refreshedToken);
        }
    }

    public void getCartCount() {
        if (Utils.checkNetwork(ACTIVITY) != 0) {
            if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
                userId = String.valueOf(mLoginModel.id);
                requestCartCount(userId);
            }
        }
    }

    private void requestCartCount(String userId) {
        //Call Webservice
        Call<CartCountListModel> loginCall = new RestClient().getApiService().requestGetCartCount(mPrefs.getString(SharedPrefs.LANGUAGE), userId);
        loginCall.enqueue(new retrofit2.Callback<CartCountListModel>() {
            @Override
            public void onResponse(Call<CartCountListModel> call, retrofit2.Response<CartCountListModel> response) {
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        setCartCount(String.valueOf(response.body().data.get(0).cart_count));
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<CartCountListModel> call, Throwable t) {
                Log.e("error", "" + t.getMessage());
                if (t instanceof HttpException) {
                    Log.e("HTTP", ((HttpException) t).getMessage());
                }
                if (t instanceof IOException) {
                    Log.e("IOException", ((IOException) t).getMessage());
                }
            }
        });
    }

    private void requestAddToken(String userId, String refreshedToken) {
        //Call Webservice

        Call<GeneralModel> loginCall = new RestClient().getApiService().addDeviceToken(mPrefs.getString(SharedPrefs.LANGUAGE), userId, refreshedToken, "android");
        loginCall.enqueue(new retrofit2.Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
//                        Log.e("Success1", response.body().message);
                        mPrefs.putBoolean(SharedPrefs.IS_FCM_TOKEN_ADDED, true);
                    } else {
//                        Log.e("Success0", response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                Log.e("error", "" + t.getMessage());
                if (t instanceof HttpException) {
                    Log.e("HTTP", ((HttpException) t).getMessage());
                }
                if (t instanceof IOException) {
                    Log.e("IOException", ((IOException) t).getMessage());
                }
            }
        });
    }

    private void requestMasterDetail() {
        if (mPrefs.getMasterDetail() != null) {
            MasterDataModel masterDetailModel = new Gson().fromJson(mPrefs.getMasterDetail(), MasterDataModel.class);
            if (masterDetailModel.status.equalsIgnoreCase("1")) {
                mArraySocialList.addAll(masterDetailModel.data.get(0).social_account);
                mBinding.sideMenu.rvSocialMedia.setLayoutManager(new GridLayoutManager(ACTIVITY, 5));
                mBinding.sideMenu.rvSocialMedia.setItemAnimator(new DefaultItemAnimator());
                mSocialAdapter = new SocialMediaAdapter(ACTIVITY, mArraySocialList, HomeActivity.this);
                mBinding.sideMenu.rvSocialMedia.setAdapter(mSocialAdapter);
                if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                    SpannableString content = new SpannableString(masterDetailModel.data.get(0).term_condition.get(0).title_ar.trim());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    mBinding.sideMenu.tvTerms.setText(content);
                } else {
                    SpannableString content = new SpannableString(masterDetailModel.data.get(0).term_condition.get(0).title_en.trim());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    mBinding.sideMenu.tvTerms.setText(content);
                }
            }
        }
    }

    public void changeFragment(Fragment mFrg) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        }

        transaction.replace(R.id.frgContainer, mFrg);
        transaction.commit();
    }

    private DialogInterface.OnClickListener mLogoutClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            removeDeviceToken();
        }
    };

    public void backToDashBoard() {
        if (mMenuSelection == 0) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            mMenuSelection = 0;
            clickPosition = 0;
            CategoryFragment categoryFrg = new CategoryFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            } else {
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            }

            transaction.replace(R.id.frgContainer, categoryFrg);
            transaction.commit();
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    public void backToMyOrder() {
        mMenuSelection = 1;
        clickPosition = 1;
        MyOrderFragment myOrderFrg = new MyOrderFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        }

        transaction.replace(R.id.frgContainer, myOrderFrg);
        transaction.commit();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void removeDeviceToken() {
        if (Utils.checkNetwork(ACTIVITY) != 0) {
            //Call Webservice
            mProgressHUD = ProgressHUD.show(ACTIVITY, ACTIVITY.getResources().getString(R.string.please_wait), true, true, false, null);
            Call<GeneralModel> removeDeviceTokenCall = new RestClient().getApiService().removeDeviceToken(mPrefs.getString(SharedPrefs.LANGUAGE), userId, FirebaseInstanceId.getInstance().getToken(), "android");
            removeDeviceTokenCall.enqueue(new retrofit2.Callback<GeneralModel>() {
                @Override
                public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                    mProgressHUD.dismiss();
                    if (response != null) {
                        if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                            Log.e("Success", response.body().message);
                            mPrefs.putBoolean(SharedPrefs.IS_LOGIN, false);
                            mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, "");
                            mPrefs.putString(SharedPrefs.STORE_ID, "");
                            mPrefs.putBoolean(SharedPrefs.IS_FCM_TOKEN_ADDED, false);
                            Intent mIntent = new Intent(ACTIVITY, LoginActivity.class);
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            ACTIVITY.startActivity(mIntent);
                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                            ACTIVITY.finish();
                        } else {
                            Log.e("Success", response.body().message);
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeneralModel> call, Throwable t) {
                    Log.e("error", "" + t.getMessage());
                    mProgressHUD.dismiss();
                }
            });
        }
    }

    public void changeCountryFlag(String ImgPath) {
        Glide.with(ACTIVITY).load(ImgPath).into(mBinding.sideMenu.ivCountryFlag);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReviewOrderFragment.GREET_REQUEST) {
            //Log.e("!!!!","GREEt");
            frgReviewOrder.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == TakePicture.CAMERA_CAPTURE || requestCode == TakePicture.GALLERY || requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            mMyProfileFrg.onActivityResult(requestCode, resultCode, data);
            Log.e("Tag", "OnActivity");
        }

    }

    private void checkNotification() {
        if (getIntent().hasExtra("nt") && getIntent().getStringExtra("nt").equalsIgnoreCase("1")) {
            if (getIntent().hasExtra("oId") && getIntent().hasExtra("userId")) {

                String ni = getIntent().getStringExtra("ni");
                Log.d("IDDDD","!!! "+ni);
                String orderId = getIntent().getStringExtra("oId");
                // String userId = getIntent().getStringExtra("userId");

                Utils.readNotification(HomeActivity.this, ni, null);

                MyOrderDetailFragment frgOrderDetail = new MyOrderDetailFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString("orderId", orderId);
                mBundle.putBoolean("isFromPush", true);
                frgOrderDetail.setArguments(mBundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                    transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else {
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                transaction.addToBackStack(null);
                transaction.replace(R.id.frgContainer, frgOrderDetail);
                transaction.commit();
            }
        } else if (getIntent().hasExtra("nt") && getIntent().getStringExtra("nt").equalsIgnoreCase("2")) {
            String ni = getIntent().getStringExtra("ni");
            Utils.readNotification(HomeActivity.this, ni, null);
        }
    }


}
