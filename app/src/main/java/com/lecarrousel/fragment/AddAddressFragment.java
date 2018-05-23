package com.lecarrousel.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.adapter.CountryListAdapter;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgAddAddressBinding;
import com.lecarrousel.model.AddAddressModel;
import com.lecarrousel.model.DeleteCardModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.model.UpdateCard;
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

public class AddAddressFragment extends Fragment {

    private FrgAddAddressBinding mBinding;
    private Context mContext;
    private SharedPrefs mPrefs;
    private String userId = "";
    private ProgressHUD mProgressHUD;
    private String mContactName,mBuildingNo,mStreetNo,mZone, mStreet, mCIty, mAddressId = "", mPhoneNo;
    private boolean isEdited = false;

    private ArrayList<MasterDataModel.COUNTRIES> mCountryList = new ArrayList<>();
    private CountryListAdapter countryAdapter;
    private int countryId = 0;
    private String countryName = "";


    private String strCountryCode;
    private MasterDataModel.COUNTRIES modelCountry;
    private MyProfileFragment myProfileFragment;

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_add_address, container, false);
        mPrefs = new SharedPrefs(mContext);
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
        ((HomeActivity) mContext).setDrawerSwipe(false);
        mCountryList = mPrefs.getCountryList();
        if (mCountryList.size() > 0) {
            countryAdapter = new CountryListAdapter(getActivity(), mCountryList);
            mBinding.spCountry.setAdapter(countryAdapter);
        }
        setFonts();
        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginmodel.id);

        }

        mBinding.etContactName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mBinding.etStreetAddress.requestFocus();
                    return true;
                }
                return false;
            }
        });
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
        Log.e("Tag1", mAddressId);
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            mBinding.tvAddNewAddress.setText(getResources().getString(R.string.address_details));
            mContactName = mBundle.getString("contactName");
            mBuildingNo = mBundle.getString("buildingNo");
            mStreetNo = mBundle.getString("streetNo");
            mZone = mBundle.getString("zone");
            mStreet = mBundle.getString("street");
            mCIty = mBundle.getString("city");
            mPhoneNo = mBundle.getString("mobile");
            mAddressId = mBundle.getString("addressId");
            countryId = mBundle.getInt("country_id");
            mBinding.tvSave.setText(getResources().getString(R.string.update));
            mBinding.tvAddNewAddress.setText(getResources().getString(R.string.address_details));
            mBinding.tvDelete.setVisibility(View.VISIBLE);
            Log.e("Tag1", mAddressId);

            MasterDataModel.COUNTRIES mCountryModel = new Utils().getCountryModelFromId(getActivity(), countryId);
            if (mCountryModel != null) {
                countryName = mCountryModel.country_name;
                mBinding.tvCountry.setText(countryName);
                mBinding.spCountry.setSelection(mCountryModel.spinner_position);
                mBinding.layMobileEdit.tvCountryCode.setText(mCountryModel.country_code);
            }

        } else {
            mBinding.tvAddNewAddress.setText(getResources().getString(R.string.add_address));
            try {
                TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

                strCountryCode = tm.getNetworkCountryIso();
                boolean isMatched = false;
                for (int i = 0; i < mCountryList.size(); i++) {
                    if (mCountryList.get(i).short_code.equalsIgnoreCase(strCountryCode)) {
                        modelCountry = mCountryList.get(i);
                        mBinding.spCountry.setSelection(i);
                        isMatched = true;
                    }
                }
                if (!isMatched) {
                    for (int i = 0; i < mCountryList.size(); i++) {
                        if (mCountryList.get(i).country_name.equalsIgnoreCase("QATAR")) {
                            Log.e("Tag", "Country code:2" + mCountryList.get(i).country_code);
                            modelCountry = mCountryList.get(i);
                            mBinding.spCountry.setSelection(i);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Tag", "Exception:" + e);
                for (int i = 0; i < mCountryList.size(); i++) {
                    if (mCountryList.get(i).country_name.equalsIgnoreCase("QATAR")) {
                        Log.e("Tag", "Country code:4" + mCountryList.get(i).country_code);
                        modelCountry = mCountryList.get(i);
                        mBinding.spCountry.setSelection(i);
                    }
                }
            }
        }


        mBinding.etContactName.setText(mContactName);
        mBinding.etBuildingNo.setText(mBuildingNo);
        mBinding.etStreetNo.setText(mStreetNo);
        mBinding.etZone.setText(mZone);
        mBinding.etStreetAddress.setText(mStreet);
        //mBinding.etCity.setText(mCIty);
        mBinding.layMobileEdit.etMobileNo.setText(mPhoneNo);

        mBinding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkNetwork(mContext) == 1) {
                    if (checkValidations()) {
                        if (mAddressId.equalsIgnoreCase("")) {
                            requestAddAddress(userId, mAddressId, mBinding.etContactName.getText().toString().trim(),
                                    mBinding.etBuildingNo.getText().toString().trim(),
                                    mBinding.etStreetNo.getText().toString().trim(),
                                    mBinding.etZone.getText().toString().trim(),
                                    mBinding.etStreetAddress.getText().toString().trim(),
                                    "",
                                    mBinding.layMobileEdit.etMobileNo.getText().toString().trim());
                        } else {
                            requestUpdateAddress(userId, mAddressId, mBinding.etContactName.getText().toString().trim(),
                                    mBinding.etBuildingNo.getText().toString().trim(),
                                    mBinding.etStreetNo.getText().toString().trim(),
                                    mBinding.etZone.getText().toString().trim(),
                                    mBinding.etStreetAddress.getText().toString().trim(),
                                    "",
                                    mBinding.layMobileEdit.etMobileNo.getText().toString().trim());
                        }

                    }
                } else {
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                }
            }
        });

        mBinding.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.showAlertDialogWith2ClickListener(mContext, getResources().getString(R.string.remove_record), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.checkNetwork(mContext) == 1) {
                            requestDeleteAddress(userId, mAddressId);
                        } else {
                            Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


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
                mBinding.layMobileEdit.tvCountryCode.setVisibility(View.VISIBLE);
                mBinding.layMobileEdit.tvCountryCode.setText(mCountryList.get(position).country_code);
                countryId = mCountryList.get(position).country_id;
                countryName = mCountryList.get(position).country_name;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return mBinding.getRoot();
    }


    private void setFonts() {
        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvContactNameLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvStreetAddress, Utils.FontStyle.BOLD);
        //Utils.changeFont(mContext, mBinding.tvCity, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layMobileEdit.tvMobileNo, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvSave, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvDelete, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvAddNewAddress, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.lblCountry, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvBuilding, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvStreetNo, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvZone, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private boolean checkValidations() {

        if (mBinding.etContactName.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_contact_name));
            mBinding.etContactName.requestFocus();
            return false;
        }

        if (mBinding.etStreetAddress.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_address_msg));
            mBinding.etStreetAddress.requestFocus();
            return false;
        }

        if (mBinding.etBuildingNo.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_building_msg));
            mBinding.etBuildingNo.requestFocus();
            return false;
        }
        if (mBinding.etStreetNo.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_street_no_msg));
            mBinding.etStreetNo.requestFocus();
            return false;
        }
        if (mBinding.etZone.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_zone_msg));
            mBinding.etZone.requestFocus();
            return false;
        }
        /*if (mBinding.etCity.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_city_msg));
            mBinding.etCity.requestFocus();
            return false;
        }*/

        if (mBinding.layMobileEdit.etMobileNo.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_mobile_no_msg));
            mBinding.layMobileEdit.etMobileNo.requestFocus();
            return false;
        }
        /*if (mBinding.layMobileEdit.etMobileNo.getText().toString().trim().length() < 10) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_valid_mobile_no_msg));
            mBinding.layMobileEdit.etMobileNo.requestFocus();
            return false;
        }*/

        return true;
    }

    private void requestAddAddress(final String userId, String address_id, String contact_name,String building,String street_no,String zone, String street_address, String city, String mobile) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<AddAddressModel> addAddressCall = new RestClient().getApiService().addAddress(mPrefs.getString(SharedPrefs.LANGUAGE), userId, address_id, contact_name, building, street_no, zone, street_address, city, mobile, String.valueOf(countryId), countryName);
        addAddressCall.enqueue(new retrofit2.Callback<AddAddressModel>() {
            @Override
            public void onResponse(Call<AddAddressModel> call, retrofit2.Response<AddAddressModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        isEdited = true;
                        getActivity().onBackPressed();

//                        Utils.showAlertDialogWithClickListener(mContext, response.body().message, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                getActivity().onBackPressed();
//                            }
//                        });
                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddAddressModel> call, Throwable t) {
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


    private void requestUpdateAddress(String userId, String address_id, String contact_name,String building,String street_no,String zone, String street_address, String city, String mobile) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<UpdateCard> updateAddressCall = new RestClient().getApiService().updateAddress(mPrefs.getString(SharedPrefs.LANGUAGE), userId, address_id, contact_name, building, street_no, zone, street_address, city, mobile, String.valueOf(countryId), countryName);
        updateAddressCall.enqueue(new retrofit2.Callback<UpdateCard>() {
            @Override
            public void onResponse(Call<UpdateCard> call, retrofit2.Response<UpdateCard> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        isEdited = true;
                        getActivity().onBackPressed();

//                        Utils.showAlertDialogWithClickListener(mContext, response.body().message, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                getActivity().onBackPressed();
//                            }
//                        });
                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateCard> call, Throwable t) {
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

    private void requestDeleteAddress(String userId, String addr_id) {
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<DeleteCardModel> deletecall = new RestClient().getApiService().deleteAddress(mPrefs.getString(SharedPrefs.LANGUAGE), userId, addr_id);
        deletecall.enqueue(new retrofit2.Callback<DeleteCardModel>() {
            @Override
            public void onResponse(Call<DeleteCardModel> call, retrofit2.Response<DeleteCardModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                        isEdited = true;
                        getActivity().onBackPressed();

//                        Utils.showAlertDialogWithClickListener(mContext, response.body().message, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                getActivity().onBackPressed();
//                            }
//                        });
                    }
                } else {
                    Log.e("Success0", response.body().message);
                    Utils.showAlertDialog(mContext, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<DeleteCardModel> call, Throwable t) {
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


    public void setParentFragment(MyProfileFragment myProfileFragment) {
        this.myProfileFragment = myProfileFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
        ((HomeActivity) mContext).setDrawerSwipe(true);
        if (isEdited) {
            myProfileFragment.requestUserDetail(userId);
        }
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }
}
