package com.lecarrousel.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.adapter.AddAddressAdapter;
import com.lecarrousel.adapter.AddCardAdapter;
import com.lecarrousel.adapter.CountryListAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgMyProfileBinding;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.model.UpdateProfileModel;
import com.lecarrousel.model.UserAccountModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.TakePicture;
import com.lecarrousel.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

import static com.lecarrousel.utils.Utils.PERMISSION_FOR_CAMERA;

/**
 * Created by Bitwin on 3/10/2017.
 */

public class MyProfileFragment extends BaseFragment implements AddCardAdapter.RecycleListClickListener, AddAddressAdapter.RecycleListClickListener {
    private FrgMyProfileBinding mBinding;
    private Context mContext;
    private ProgressHUD mProgressHUD;
    private ArrayList<UserAccountModel.Card> mArrayCardList = new ArrayList<>();
    private ArrayList<UserAccountModel.Address> mArrayAddressList = new ArrayList<>();
    private AddCardAdapter mCardAdapter;
    private AddAddressAdapter mAddressAdapter;
    private SharedPrefs mPrefs;
    private String userId;
    private String SELECTED_PATH = "";
    private ArrayList<MasterDataModel.COUNTRIES> mCountryList = new ArrayList<>();
    private CountryListAdapter countryAdapter;
    private int countryId = 0;

    /* Camera Rohan*/
    private TakePicture mTakePicture;
    /* Camera END Rohan*/

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
        ((HomeActivity) mContext).setVisibilityCart(false);
        ((HomeActivity) mContext).setDrawerSwipe(true);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_my_profile, container, false);
        mPrefs = new SharedPrefs(mContext);
        mTakePicture = new TakePicture((Activity) mContext);
        setFonts();


        mBinding.layMobileEdit.tvCountryCode.setVisibility(View.INVISIBLE);
        mBinding.tvCountry.setVisibility(View.INVISIBLE);

        mCountryList = mPrefs.getCountryList();
        if (mCountryList.size() > 0) {
            countryAdapter = new CountryListAdapter(getActivity(), mCountryList);
            mBinding.spCountry.setAdapter(countryAdapter);
        }
        return mBinding.getRoot();
    }

    private void setFonts() {
        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvEmailAddressLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvName, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layMobileEdit.tvMobileNo, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvChangePasslbl, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvDeliveryLbl, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvAddNewAddress, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvSave, Utils.FontStyle.BOLD);
//        Utils.changeFont(mContext, mBinding.tvCardsLbl, Utils.FontStyle.BOLD);
//        Utils.changeFont(mContext, mBinding.tvAddNewCard, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvMyProfile, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.lblCountry, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvCountry, Utils.FontStyle.REGULAR);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginModel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginModel.id);
        }

        if (Utils.checkNetwork(mContext) == 1) {
            requestUserDetail(userId);
        } else {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
        }

        mBinding.layMobileEdit.etMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.layMobileEdit.etMobileNo.getText().length() == 0) {
                    mBinding.layMobileEdit.etMobileNo.setHint(getResources().getString(R.string.hint_enter_mobile));
                } else {
                    mBinding.layMobileEdit.etMobileNo.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.rvAddressList.setNestedScrollingEnabled(false);
        mBinding.tvAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAddressFragment frgAddAddressProfile = new AddAddressFragment();
                frgAddAddressProfile.setParentFragment(MyProfileFragment.this);
                replaceFragment(frgAddAddressProfile, true);
            }
        });

//        mBinding.tvAddNewCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AddCardProfileFragment frgAddNewCardProfile = new AddCardProfileFragment();
//                frgAddNewCardProfile.setParentFragment(MyProfileFragment.this);
//                replaceFragment(frgAddNewCardProfile, true);
//            }
//        });

        mBinding.layChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordFragment changePassFrg = new ChangePasswordFragment();
                replaceFragment(changePassFrg, true);

            }
        });

        mBinding.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > 22) {
                    checkPermision();
                } else {
                    selectProfilePic();
                }

            }
        });
        mBinding.layCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.spCountry.performClick();
            }
        });

        /*Country Spinner*/
        mBinding.spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBinding.tvCountry.setText(mCountryList.get(position).country_name);
                mBinding.layMobileEdit.tvCountryCode.setText(mCountryList.get(position).country_code);
                countryId = mCountryList.get(position).country_id;
                Log.e("Tag", "position" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBinding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkNetwork(mContext) == 1) {
                    if (checkValidation()) {
                        requestUpdateProfile(SELECTED_PATH);
                    }
                } else {
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                }
            }
        });
    }

    private void checkPermision() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.CAMERA) ||
                    !ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_FOR_CAMERA);
            } else {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_FOR_CAMERA);
            }
        } else {
            selectProfilePic();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FOR_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    selectProfilePic();
                }
                if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //Show permission explanation dialog...
                        return;
                    } else {
                        Utils.showAlertDialogWithTwoClickListener(mContext, getResources().getString(R.string.permission_camera), getResources().getString(R.string.ok), getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }
                }
                return;
            }
        }
    }

    private void selectProfilePic() {
        try {
            mTakePicture.selectImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestUserDetail(String userId) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<UserAccountModel> userAccountCall = new RestClient().getApiService().userAccount(mPrefs.getString(SharedPrefs.LANGUAGE), userId);
        userAccountCall.enqueue(new retrofit2.Callback<UserAccountModel>() {
            @Override
            public void onResponse(Call<UserAccountModel> call, retrofit2.Response<UserAccountModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        mArrayCardList.clear();
                        mArrayAddressList.clear();
                        if (response.body().data.size() != 0) {
                            UserAccountModel.User_information user = response.body().data.get(0).user_information.get(0);
                            MasterDataModel.COUNTRIES mCountryModel = new Utils().getCountryModelFromId(getActivity(), response.body().data.get(0).user_information.get(0).country_id);

                            if (mCountryModel != null) {
                                mBinding.layMobileEdit.tvCountryCode.setVisibility(View.VISIBLE);
                                mBinding.tvCountry.setVisibility(View.VISIBLE);
                                mBinding.tvCountry.setText(mCountryModel.country_name);
                                mBinding.layMobileEdit.tvCountryCode.setText(mCountryModel.country_code);
                                mBinding.spCountry.setSelection(mCountryModel.spinner_position);
                                countryId = mCountryModel.country_id;
                            }

                            mBinding.tvEmailAddress.setText(user.email);
                            mBinding.layMobileEdit.etMobileNo.setText(user.phone);
                            mBinding.etName.setText(user.name);

                            mArrayCardList.addAll(response.body().data.get(0).card);
                            mCardAdapter = new AddCardAdapter(mContext, mArrayCardList, MyProfileFragment.this);
//                            mBinding.rvCardList.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
//                            mBinding.rvCardList.setItemAnimator(new DefaultItemAnimator());
//                            mBinding.rvCardList.setAdapter(mCardAdapter);

                            mArrayAddressList.addAll(response.body().data.get(0).address);
                            mAddressAdapter = new AddAddressAdapter(mContext, mArrayAddressList, MyProfileFragment.this);
                            mBinding.rvAddressList.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
                            mBinding.rvAddressList.setItemAnimator(new DefaultItemAnimator());
                            mBinding.rvAddressList.setAdapter(mAddressAdapter);

                            mPrefs.putString(SharedPrefs.USER_PHOTO, user.logo);
                            Glide.with(mContext).load(mPrefs.getString(SharedPrefs.USER_PHOTO)).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .into(mBinding.ivProfilePic);
                        }
                    } else {
                        Log.e("Success0", response.body().message);
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserAccountModel> call, Throwable t) {
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


    //

    @Override
    public void onCardClick(int position, View view) {

        AddCardProfileFragment frgAddNewCardProfile = new AddCardProfileFragment();
        frgAddNewCardProfile.setParentFragment(MyProfileFragment.this);
        Bundle mBundle = new Bundle();
        mBundle.putString("cardHolderName", mArrayCardList.get(position).cardHolderName);
        mBundle.putString("cardNumber", mArrayCardList.get(position).cardNumber);
        mBundle.putString("cardExpiry", mArrayCardList.get(position).expiryDate);
        mBundle.putString("cardId", String.valueOf(mArrayCardList.get(position).cId));
        frgAddNewCardProfile.setArguments(mBundle);
        replaceFragment(frgAddNewCardProfile, true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Tag", "Onactivity My Profile ");
        try {
            SELECTED_PATH = mTakePicture.onActivityResult(requestCode, resultCode, data);
            Log.e("Tag", "Path: " + SELECTED_PATH);

            Glide.with(mContext).load(SELECTED_PATH).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(mBinding.ivProfilePic);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestUpdateProfile(String imagePath) {
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("user_photo", file.getName(), requestFile);

        mProgressHUD = ProgressHUD.show(mContext, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<UpdateProfileModel> updateProfile = null;
        if (!SELECTED_PATH.equalsIgnoreCase("")) {
            updateProfile = new RestClient().getApiService().updateProfileWithImage(mPrefs.getString(SharedPrefs.LANGUAGE), userId, mBinding.etName.getText().toString().trim(),
                    mBinding.layMobileEdit.etMobileNo.getText().toString().trim(), String.valueOf(countryId), body);
        } else {
            updateProfile = new RestClient().getApiService().updateProfileWithoutImage(mPrefs.getString(SharedPrefs.LANGUAGE), userId, mBinding.etName.getText().toString().trim(),
                    mBinding.layMobileEdit.etMobileNo.getText().toString().trim(), String.valueOf(countryId));

        }
        updateProfile.enqueue(new retrofit2.Callback<UpdateProfileModel>() {
            @Override
            public void onResponse(Call<UpdateProfileModel> call, retrofit2.Response<UpdateProfileModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                    Log.e("Success!!!>>", response.body().message);
                    mPrefs.putString(SharedPrefs.LOGIN_RESPONSE, new Gson().toJson(response.body().data));
                    mPrefs.putString(SharedPrefs.USER_PHOTO, response.body().data.logo);

                    ((HomeActivity) mContext).resetUpdatedProfile();
//                    Utils.showAlertDialog(mContext, response.body().message);
                } else {
                    Utils.showAlertDialog(mContext, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileModel> call, Throwable t) {
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

    private boolean checkValidation() {
        if (mBinding.etName.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_name_msg));
            return false;
        } else if (mBinding.layMobileEdit.etMobileNo.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_mobile_no_msg));
            return false;
        }
        return true;
    }

    @Override
    public void onAddressClick(int position, View view) {
        AddAddressFragment frgAddAddressProfile = new AddAddressFragment();
        frgAddAddressProfile.setParentFragment(MyProfileFragment.this);
        Bundle mBundle = new Bundle();
        mBundle.putString("contactName", mArrayAddressList.get(position).contactName);
        mBundle.putString("buildingNo", mArrayAddressList.get(position).buildingNo);
        mBundle.putString("streetNo", mArrayAddressList.get(position).streetNo);
        mBundle.putString("zone", mArrayAddressList.get(position).zone);
        mBundle.putString("street", mArrayAddressList.get(position).streetAddress);
        mBundle.putString("city", mArrayAddressList.get(position).city);
        mBundle.putString("mobile", mArrayAddressList.get(position).phoneNo);
        mBundle.putString("addressId", String.valueOf(mArrayAddressList.get(position).addressId));
        mBundle.putInt("country_id", mArrayAddressList.get(position).country_id);
        frgAddAddressProfile.setArguments(mBundle);
        replaceFragment(frgAddAddressProfile, true);

    }

}
