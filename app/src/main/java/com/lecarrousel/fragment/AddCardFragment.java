package com.lecarrousel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgAddCardBinding;
import com.lecarrousel.model.AddAddressModel;
import com.lecarrousel.model.AddCardModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.DateTime;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;


public class AddCardFragment extends BaseFragment {

    private FrgAddCardBinding mBinding;
    private Context mContext;
    private SharedPrefs mPrefs;
    private String userId = "", storeId;
    private ProgressHUD mProgressHUD;
    private String strCardWithDash;
    private int keyDel;
    private String mCardId = "";
    private String couponCode, contactName, buildingNo, streetNo, zone, streetAddress, city, countryId, countryName, mobile, paymentMethod = "card", expecDeliveryDate, estimateTime, cardMsg;
    private boolean isOtherAdd = false;
    private String address_id = "";
    String mCardNumber;

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_add_card, container, false);

        mPrefs = new SharedPrefs(mContext);
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
        ((HomeActivity) mContext).setDrawerSwipe(false);

        setFonts();


        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginmodel.id);
            storeId = String.valueOf(loginmodel.store_id);
            //storeId = mPrefs.getString(SharedPrefs.STORE_ID);
        }

        Bundle placeOrderInfo = getArguments();
        if (placeOrderInfo != null) {
            isOtherAdd = placeOrderInfo.getBoolean("isOtherAdd");
            couponCode = placeOrderInfo.getString("coupon_code");
            contactName = placeOrderInfo.getString("contact_name");
            buildingNo = placeOrderInfo.getString("building_no");
            streetNo = placeOrderInfo.getString("street_no");
            zone = placeOrderInfo.getString("zone");
            streetAddress = placeOrderInfo.getString("street");
            city = placeOrderInfo.getString("city");
            mobile = placeOrderInfo.getString("mobile");
            countryId = placeOrderInfo.getString("country_id");
            countryName = placeOrderInfo.getString("country_name", "");
            //paymentMethod = placeOrderInfo.getString("payment_method", "card");
            expecDeliveryDate = placeOrderInfo.getString("delivery_date");
            estimateTime = placeOrderInfo.getString("estimate_time");
            cardMsg = placeOrderInfo.getString("card_msg");
        }
        mBinding.etCardNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mBinding.etCardName.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mBinding.etCardName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    openExpire();
                    return true;
                }
                return false;
            }
        });
        mBinding.etCardNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean flag = true;
                String eachBlock[] = mBinding.etCardNum.getText().toString().split("-");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {
                    mBinding.etCardNum.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((mBinding.etCardNum.getText().length() + 1) % 5) == 0) {

                            if (mBinding.etCardNum.getText().toString().split("-").length <= 3) {
                                mBinding.etCardNum.setText(mBinding.etCardNum.getText() + "-");
                                mBinding.etCardNum.setSelection(mBinding.etCardNum.getText().length());
                            }
                        }
                        strCardWithDash = mBinding.etCardNum.getText().toString();
                    } else {
                        strCardWithDash = mBinding.etCardNum.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    mBinding.etCardNum.setText(strCardWithDash);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mBinding.laySaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBinding.cbSave.isChecked()) {
                    mBinding.cbSave.setChecked(false);
                } else {
                    mBinding.cbSave.setChecked(true);
                }
            }
        });

        mBinding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkNetwork(mContext) == 1) {
                    mCardNumber = mBinding.etCardNum.getText().toString().trim().replace("-", "");
                    if (checkValidations()) {

//                        if (mBinding.cbSave.isChecked()) {
//                            requestAddCard(userId, mCardId, mCardNumber,
//                                    mBinding.etCardName.getText().toString().trim(),
//                                    mBinding.tvExpiryDate.getText().toString().trim());
//                        } else {
//                        requestPlaceOrder(couponCode, contactName, buildingNo, streetNo, zone, streetAddress, city, mobile, countryId, countryName, paymentMethod, expecDeliveryDate,estimateTime,cardMsg);
//                        }
                    }
                } else {
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                }
            }
        });

//        mBinding.tvDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Utils.checkNetwork(mContext) == 1) {
//                    requestDeleteCard(userId, mCardId);
//                } else {
//                    Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
//                }
//
//            }
//        });

        mBinding.layExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExpire();
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void setFonts() {
        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvCardName, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvCardNum, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvExpiryDateLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvCvvLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvSaveCardLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvConfirm, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvDelete, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvHeader, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private boolean checkValidations() {

        if (mBinding.etCardNum.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_card_num_msg));
            mBinding.etCardNum.requestFocus();
            return false;
        }

        if (mBinding.etCardNum.getText().toString().trim().length() < 19) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_valid_card_no_msg));
            mBinding.etCardNum.requestFocus();
            return false;
        }

        if (mBinding.etCardName.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_card_name_msg));
            mBinding.etCardName.requestFocus();
            return false;
        }

        if (mBinding.tvExpiryDate.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_expiry_date_require_msg));
            return false;
        }

        if (mBinding.etCVV.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_security_code_msg));
            return false;
        }
        if (mBinding.etCVV.getText().toString().trim().length() < 3) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_valid_security_code_msg));
            return false;
        }

        return true;
    }

    /*private void requestPlaceOrder(String couponCode, String contactName, String building, String street_no, String zone, String streetAddress, String city, String mobile, String country_id, String country_name, String payment_method, String delivery_date,String estimate_time, String card_msg) {
        //Call Webservice bbb
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<PlaceOrderModel> getCartCall = new RestClient().getApiService().placeOrder(mPrefs.getString(SharedPrefs.LANGUAGE), userId, couponCode, "", contactName, building, street_no, zone, streetAddress, city, mobile, storeId, country_name, payment_method, delivery_date,estimate_time, card_msg, storeId);
        getCartCall.enqueue(new retrofit2.Callback<PlaceOrderModel>() {

            @Override
            public void onResponse(Call<PlaceOrderModel> call, retrofit2.Response<PlaceOrderModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);

                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {

                        if (mBinding.cbSave.isChecked()) {
                            requestAddCard(userId, mCardId, mCardNumber,
                                    mBinding.etCardName.getText().toString().trim(),
                                    mBinding.tvExpiryDate.getText().toString().trim());
                        }
//                        if (isOtherAdd) {
//                            requestAddAddress();
//                        }
                        int orderId = response.body().data.orderId;
                        OrderConfirmationFragment confirmOrderFrg = new OrderConfirmationFragment();
                        Bundle mBundle = new Bundle();
                        mBundle.putString("orderId", String.valueOf(orderId));
                        confirmOrderFrg.setArguments(mBundle);
                        replaceFragment(confirmOrderFrg, true);
                    } else {
                        Log.e("Tag", "order not placed");
                    }

                }
            }

            @Override
            public void onFailure(Call<PlaceOrderModel> call, Throwable t) {
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

    }*/

    private void requestAddAddress() {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<AddAddressModel> addAddressCall = new RestClient().getApiService().addAddress(mPrefs.getString(SharedPrefs.LANGUAGE),
                userId, address_id, contactName, buildingNo, streetNo, zone,
                streetAddress,
                city,
                mobile, String.valueOf(countryId), countryName);
        addAddressCall.enqueue(new retrofit2.Callback<AddAddressModel>() {
            @Override
            public void onResponse(Call<AddAddressModel> call, retrofit2.Response<AddAddressModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);

//                        Utils.showAlertDialogWithClickListener(mContext, response.body().message, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                getActivity().onBackPressed();
//                            }
//                        });
                    } else {
//                        Utils.showAlertDialog(mContext, response.body().message);
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


    private void requestAddCard(String userId, String card_id, String card_num, String cardholder_name, String expiry_date) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<AddCardModel> addCardCall = new RestClient().getApiService().addCard(mPrefs.getString(SharedPrefs.LANGUAGE), userId, card_id, card_num, cardholder_name, expiry_date);
        addCardCall.enqueue(new retrofit2.Callback<AddCardModel>() {
            @Override
            public void onResponse(Call<AddCardModel> call, retrofit2.Response<AddCardModel> response) {
                if (response != null) {
                    mProgressHUD.dismissProgressDialog(mProgressHUD);
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success1", response.body().message);
                    } else {
                        Utils.showAlertDialog(mContext, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddCardModel> call, Throwable t) {
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

    private void openExpire() {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_expiry);

        final NumberPicker NP_MONTH = (NumberPicker) dialog.findViewById(R.id.npMonth);
        final NumberPicker NP_YEAR = (NumberPicker) dialog.findViewById(R.id.npYear);
        final Button BTN_OK = (Button) dialog.findViewById(R.id.btnOk);
        final Button BTN_CANCEL = (Button) dialog.findViewById(R.id.btnCancel);
        NP_MONTH.setMinValue(0);
        NP_MONTH.setMaxValue(11);
        NP_MONTH.setDisplayedValues(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});

        final int _currentYear, _expiryYear, _currentMonth;
        DateTime _dateTime = new DateTime();
        _currentMonth = _dateTime.getMonthOfYear();
        _currentYear = _dateTime.getYear();
        _expiryYear = _currentYear + 20;
        String strYear[] = new String[21];
        for (int index = 0; index < 21; index++) {
            strYear[index] = String.valueOf(_currentYear + index);
        }
        NP_YEAR.setMinValue(_currentYear);
        NP_YEAR.setMaxValue(_expiryYear);
        NP_YEAR.setDisplayedValues(strYear);
        BTN_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NP_YEAR.getValue() == _currentYear) {
                    if (NP_MONTH.getValue() <= _currentMonth) {
                        Utils.showAlertDialog(mContext, getResources().getString(R.string.error_Please_enter_valid_expirydate_card));
                        return;
                    }
                }
                mBinding.tvExpiryDate.setText((NP_MONTH.getValue() + 1) + "/" + NP_YEAR.getValue());
                mBinding.tvExpiryDate.setTextColor(getResources().getColor(R.color.black));
                dialog.dismiss();
            }
        });
        BTN_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

}
