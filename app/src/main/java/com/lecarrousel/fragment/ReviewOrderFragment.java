package com.lecarrousel.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.activity.CardPaymentActivity;
import com.lecarrousel.activity.GreetMessageActivity;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.adapter.CountryListAdapter;
import com.lecarrousel.adapter.MyCartListAdapter;
import com.lecarrousel.adapter.OrderAddressAdapter;
import com.lecarrousel.adapter.OrderCardAdapter;
import com.lecarrousel.adapter.TimeListAdapter;
import com.lecarrousel.customview.CustomEditText;
import com.lecarrousel.customview.ProgressHUD;
import com.lecarrousel.databinding.FrgReviewOrderBinding;
import com.lecarrousel.model.AddAddressModel;
import com.lecarrousel.model.AddToCartModel;
import com.lecarrousel.model.CartListModel;
import com.lecarrousel.model.CouponModel;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.model.PlaceOrderModel;
import com.lecarrousel.retrofit.RestClient;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Bitwin on 3/10/2017.
 */

public class ReviewOrderFragment extends BaseFragment implements MyCartListAdapter.RecycleListClickListener,
        OrderAddressAdapter.RecycleListClickListener, OrderCardAdapter.RecycleListClickListener {

    public static final int GREET_REQUEST = 13;
    private static final int REQUEST_GET_TRANSACTION_ID = 14;
    private FrgReviewOrderBinding mBinding;
    private Context mContext;
    private ProgressHUD mProgressHUD;
    private SharedPrefs mPrefs;
    private String userId;
    private MyCartListAdapter mCartAdapter;
    //    private OrderCardAdapter mDebitCardAdapter;
    private OrderAddressAdapter mAddressAdapter;
    public ArrayList<CartListModel.Cart_list> mArrayCartList = new ArrayList<>();
    public ArrayList<CartListModel.Debit_card_list> mArrayDebitCardList = new ArrayList<>();
    public ArrayList<CartListModel.Default_address> mArrayAddressList = new ArrayList<>();

    private int selectedAddressPos = -1;
    //    private int selectedCardPos = -1;
    private String promoCode = "", cardMessages, expectedDeliveryDate, estimateTime = "", contactName, streetAddress, buildingNo, streetNo, zone, city, countryName, mobileNo, paymentMethod;
    private ArrayList<MasterDataModel.COUNTRIES> mCountryList;
    private ArrayList<MasterDataModel.Delivery_estimate_time> mArrayTimeList;
    private String countryCode;
    private boolean isTvReviewEnabled = false, isTvAddressEnabled = false, isArabic;
    private String address_id = "", mUserStoreId, stdGreetMsg;
    private boolean isOtherAdd = false;
    private boolean isConfirmCode = false, isCouponApplied, isDeliveryDateSelected;
    private AlertDialog mAlertDialog;
    private EditText etOtp;
    private int mUserCountryId;
    private long minDateInMillis = 0, maxDateInMillis = 0;
    private int totalAmount = 0;
    private String transactionId = "0";

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
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideSoftKeyboard(mContext);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GREET_REQUEST) {
            if (resultCode == RESULT_OK) {
                stdGreetMsg = data.getStringExtra("GreetMsg");
                mBinding.layReviewOrderDetail.tvStdCardMsg.setText(stdGreetMsg);
            }
        }else if(requestCode == REQUEST_GET_TRANSACTION_ID){
            if(resultCode == RESULT_OK){
                transactionId = data.getStringExtra("transactionId");
                Log.e("transId" ,"- "+transactionId);
                requestPlaceOrder(promoCode, "", contactName, buildingNo, streetNo, zone, streetAddress, city, mobileNo, String.valueOf(mUserCountryId), countryName, paymentMethod, expectedDeliveryDate, estimateTime, cardMessages);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity) mContext).setVisibilityCart(false);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_review_order, container, false);
        mPrefs = new SharedPrefs(mContext);
        setFonts();
        Bundle mBundle = getArguments();

        if (mBundle != null) {
            ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
            ((HomeActivity) mContext).setDrawerSwipe(false);
        } else {
            ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_menu);
            ((HomeActivity) mContext).setDrawerSwipe(true);
        }

        isArabic = mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar");
        if (isArabic) {
            mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
        } else {
            mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
        }

        mBinding.layReviewOrderDetail.etCardMessage.setEnabled(false);
        disableEts();

        expectedDeliveryDate = getString(R.string.date_format_blank);
        mCountryList = mPrefs.getCountryList();
        if (mCountryList.size() > 0) {
            CountryListAdapter countryAdapter = new CountryListAdapter(getActivity(), mCountryList);
            mBinding.layOrderAddress.spCountry.setAdapter(countryAdapter);
        }

        mArrayTimeList = mPrefs.getEstimateTimeList();
        MasterDataModel.Delivery_estimate_time exampleModel = new MasterDataModel.Delivery_estimate_time();
        exampleModel.title = getResources().getString(R.string.time);
        mArrayTimeList.add(0, exampleModel);
        TimeListAdapter timeAdapter = new TimeListAdapter(getActivity(), mArrayTimeList);
        mBinding.layReviewOrderDetail.spPrefTime.setAdapter(timeAdapter);

        mBinding.layReviewOrderDetail.etCouponCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Utils.checkNetwork(mContext) == 1) {
                        requestCouponCode();
                    } else {
                        Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                    }
                }
                return true;
            }
        });

        mBinding.layReviewOrderDetail.layRbStdMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.layReviewOrderDetail.rbStdMsg.setChecked(true);
                mBinding.layReviewOrderDetail.rbCustomMsg.setChecked(false);
                disableEnableStdMsgs(0);
            }
        });
        mBinding.layReviewOrderDetail.layRbCustomMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.layReviewOrderDetail.rbStdMsg.setChecked(false);
                mBinding.layReviewOrderDetail.rbCustomMsg.setChecked(true);
                disableEnableStdMsgs(1);
            }
        });
        mBinding.layOrderAddress.etContactName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mBinding.layOrderAddress.etStreetAddress.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mBinding.layReviewOrderDetail.etCouponCode.setOnEditTextImeBackListener(new CustomEditText.EditTextImeBackListener() {
            @Override
            public void onImeBack() {
                requestCouponCode();
            }
        });

        mBinding.tvReviewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTvReviewEnabled) {
                    if (mBinding.vflipper.getDisplayedChild() != 0) {

                        if (isArabic) {
                            mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
                        } else {
                            mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
                        }
                        mBinding.vflipper.setDisplayedChild(0);
                        mBinding.tvReviewOrder.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                        mBinding.tvDelivery.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                        mBinding.tvPayment.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));

                        if (isArabic) {
                            mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
                        } else {
                            mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
                        }

//                        disable address tab if again tap on revieworder tab when in address page
                        isTvAddressEnabled = false;
                    }

                }
            }
        });

        mBinding.tvDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTvAddressEnabled) {
                    if (mBinding.vflipper.getDisplayedChild() != 1) {

                        if (mBinding.vflipper.getDisplayedChild() == 2) {
                            if (isArabic) {
                                mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
                            } else {
                                mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
                            }
                        }
                        mBinding.vflipper.setDisplayedChild(1);
                        mBinding.tvReviewOrder.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                        mBinding.tvDelivery.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                        mBinding.tvPayment.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));

                        if (isArabic) {
                            mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
                        } else {
                            mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
                        }
                    }

                }
            }
        });

        mBinding.layReviewOrderDetail.layExpecDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });

        mBinding.layReviewOrderDetail.spPrefTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*if (isDeliveryDateSelected) {
                    estimateTime = String.valueOf(mArrayTimeList.get(position).tId);
                }*/
                if (position == 0) {
                    estimateTime = "";
                    mBinding.layReviewOrderDetail.tvPrefTime.setText(mArrayTimeList.get(position).title);
                    //mBinding.layReviewOrderDetail.tvPrefTime.setTextColor(ContextCompat.getColor(mContext,R.color.gray7));
                } else {
                    estimateTime = String.valueOf(mArrayTimeList.get(position).tId);
                    mBinding.layReviewOrderDetail.tvPrefTime.setText(mArrayTimeList.get(position).title);
                    //mBinding.layReviewOrderDetail.tvPrefTime.setTextColor(ContextCompat.getColor(mContext,R.color.brown));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.layReviewOrderDetail.etCardMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        mBinding.layReviewOrderDetail.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.hideSoftKeyboard(mContext);
                if (!promoCode.equalsIgnoreCase(mBinding.layReviewOrderDetail.etCouponCode.getText().toString().trim())) {
                    isConfirmCode = true;
                    requestCouponCode();
                } else {
                    if (expectedDeliveryDate.equalsIgnoreCase(getString(R.string.date_format_blank))) {
                        Utils.showAlertDialog(mContext, getString(R.string.enter_delivery_date));
                    } else {
                        isTvReviewEnabled = true;
                        if (mBinding.layReviewOrderDetail.spPrefTime.getSelectedItemPosition() == 0) {
                            estimateTime = "";
                        } else {
                            int tid = ((MasterDataModel.Delivery_estimate_time) mBinding.layReviewOrderDetail.spPrefTime.getSelectedItem()).tId;
                            estimateTime = String.valueOf(tid);
                        }
                        if (mBinding.layReviewOrderDetail.rbStdMsg.isChecked()) {
                            cardMessages = stdGreetMsg;
                        } else if (mBinding.layReviewOrderDetail.rbCustomMsg.isChecked()) {
                            cardMessages = mBinding.layReviewOrderDetail.etCardMessage.getText().toString();
                        }
                        mBinding.vflipper.showNext();
                        mBinding.tvReviewOrder.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                        mBinding.tvDelivery.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                    }

                }
            }
        });


        mBinding.layOrderAddress.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isArabic) {
                    mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
                } else {
                    mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
                }
                mBinding.vflipper.setDisplayedChild(0);
                mBinding.tvReviewOrder.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                mBinding.tvDelivery.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                mBinding.tvPayment.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));

                if (isArabic) {
                    mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
                } else {
                    mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
                }
            }
        });

        mBinding.layOrderPayment.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(mBinding.vflipper.getDisplayedChild() == 2){
                if (isArabic) {
                    mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
                } else {
                    mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
                }
                //}
                mBinding.vflipper.setDisplayedChild(1);
                mBinding.tvReviewOrder.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                mBinding.tvDelivery.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                mBinding.tvPayment.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));

                if (isArabic) {
                    mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_left));
                } else {
                    mBinding.vflipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.enter_from_right));
                }
            }
        });

        mBinding.layOrderAddress.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mContext);
                if (isAnyAddressSelected()) {
                    if (selectedAddressPos != -1) {
                        CartListModel.Default_address address = mArrayAddressList.get(selectedAddressPos);
                        contactName = address.contactName;
                        streetAddress = address.streetAddress;
                        buildingNo = address.buildingNo;
                        streetNo = address.streetNo;
                        zone = address.zone;
//                        city = address.city;
                        city = "";
                        mobileNo = address.phoneNo;
                        mBinding.vflipper.showNext();
                        mBinding.tvDelivery.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                        mBinding.tvPayment.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                    } else if (addressCheckValidation()) {
                        contactName = mBinding.layOrderAddress.etContactName.getText().toString().trim();
                        streetAddress = mBinding.layOrderAddress.etStreetAddress.getText().toString().trim();
                        buildingNo = mBinding.layOrderAddress.etBuildingNo.getText().toString().trim();
                        streetNo = mBinding.layOrderAddress.etStreetNo.getText().toString().trim();
                        zone = mBinding.layOrderAddress.etZone.getText().toString().trim();
                        city = "";
                        mobileNo = mBinding.layOrderAddress.layMobileEdit.etMobileNo.getText().toString().trim();
                        mBinding.vflipper.showNext();
                        mBinding.tvDelivery.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                        mBinding.tvPayment.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                    }
                    isTvAddressEnabled = true;
                } else {
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.select_atleast_one_address_msg));
                }

            }
        });

        mBinding.layOrderAddress.layNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.layOrderAddress.rbOtherAddress.isChecked()) {
                    mBinding.layOrderAddress.rbOtherAddress.setChecked(true);
                    mAddressAdapter.unCheckSavedAddress();
                    selectedAddressPos = -1;
                    enableEts();
                }

            }
        });

//        mBinding.layOrderPayment.layAddCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mBinding.layOrderAddress.rbOtherAddress.isChecked()) {
//
//                    isOtherAdd = true;
//                } else {
//                    isOtherAdd = false;
//                }
//                AddCardFragment addCardFrg = new AddCardFragment();
//                Bundle mBundle = new Bundle();
//                mBundle.putBoolean("isOtherAdd", isOtherAdd);
//                mBundle.putString("coupon_code", promoCode);
//                mBundle.putString("contact_name", contactName);
//                mBundle.putString("building_no", buildingNo);
//                mBundle.putString("street_no", streetNo);
//                mBundle.putString("zone", zone);
//                mBundle.putString("street", streetAddress);
//                mBundle.putString("city", city);
//                mBundle.putString("mobile", mobileNo);
//                mBundle.putString("country_id", String.valueOf(mUserStoreId));
//                mBundle.putString("country_name", countryName);
//                mBundle.putString("payment_method", paymentMethod);
//                mBundle.putString("delivery_date", expectedDeliveryDate);
//                mBundle.putString("estimate_time", estimateTime);
//                mBundle.putString("card_msg", cardMessages);
//                addCardFrg.setArguments(mBundle);
//                replaceFragment(addCardFrg, true);
//            }
//        });

        mBinding.layOrderPayment.layCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!mBinding.layOrderPayment.rbCod.isChecked()) {
//                    mBinding.layOrderPayment.rbCod.setChecked(true);
//                    mDebitCardAdapter.unCheckSavedCard();
//                    selectedPaymentMethod = 0;
//                }
                mBinding.layOrderPayment.rbCod.setChecked(true);
                mBinding.layOrderPayment.rbCreditCard.setChecked(false);
                mBinding.layOrderPayment.rbDebitCard.setChecked(false);
                paymentMethod = "cod";
            }
        });

        mBinding.layOrderPayment.layAddCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!mBinding.layOrderPayment.rbCreditCard.isChecked()) {
//                    mBinding.layOrderPayment.rbCreditCard.setChecked(true);
//                    mDebitCardAdapter.unCheckSavedCard();
//                    selectedPaymentMethod = 1;
//                }

                mBinding.layOrderPayment.rbCod.setChecked(false);
                mBinding.layOrderPayment.rbCreditCard.setChecked(true);
                mBinding.layOrderPayment.rbDebitCard.setChecked(false);
                paymentMethod = "credit card";
            }
        });

        mBinding.layOrderPayment.layAddDebitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!mBinding.layOrderPayment.rbDebitCard.isChecked()) {
//                    mBinding.layOrderPayment.rbDebitCard.setChecked(true);
//                    mDebitCardAdapter.unCheckSavedCard();
//                    selectedPaymentMethod = 2;
//                }

                mBinding.layOrderPayment.rbCod.setChecked(false);
                mBinding.layOrderPayment.rbCreditCard.setChecked(false);
                mBinding.layOrderPayment.rbDebitCard.setChecked(true);
                paymentMethod = "debit card";
            }
        });


        mBinding.layOrderPayment.rbCod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.layOrderPayment.tvConfirm.setText(getResources().getString(R.string.get_otp));
                } else {
                    mBinding.layOrderPayment.tvConfirm.setText(getResources().getString(R.string.confirm));
                }
            }
        });

        mBinding.layOrderPayment.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBinding.layOrderPayment.rbCod.isChecked()) {
                    paymentMethod = "cod";
                    mBinding.layOrderPayment.tvConfirm.setText(getResources().getString(R.string.get_otp));
                    if (Utils.checkNetwork(mContext) == 1) {
                        requestCOD(countryCode + mobileNo);
                    } else {
                        Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                    }
                } else if (mBinding.layOrderPayment.rbCreditCard.isChecked()) {

                    Intent intent = new Intent(mContext, CardPaymentActivity.class);
                    intent.putExtra("orderAmount", String.valueOf(totalAmount));
                    startActivityForResult(intent, REQUEST_GET_TRANSACTION_ID);

                } else if (mBinding.layOrderPayment.rbDebitCard.isChecked()) {

                    Intent intent = new Intent(mContext, CardPaymentActivity.class);
                    intent.putExtra("orderAmount", String.valueOf(totalAmount));
                    startActivityForResult(intent, REQUEST_GET_TRANSACTION_ID);
                } else {
                    Utils.showAlertDialog(mContext, getResources().getString(R.string.select_payment_method_msg));
                }
            }
        });

        return mBinding.getRoot();
    }


    private void requestCOD(String mobileNo) {
        //Call Webservice bbb
        Log.d("MMMMMM", " " + mobileNo);
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<GeneralModel> getCOD = new RestClient().getApiService().getConfirmOrder(mPrefs.getString(SharedPrefs.LANGUAGE), userId, mobileNo);
        getCOD.enqueue(new retrofit2.Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful()) {
                        if (response.body().status.equalsIgnoreCase("1")) {
                            showDialog();
                        } else {
                            Utils.showAlertDialog(mContext, response.body().message);
                        }
                    } else {
                        Log.e("Tag", "did not get cod");
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

    private void showDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.custom_alert, null);
        builder.setView(v);
        etOtp = (EditText) v.findViewById(R.id.etSecurityCode);
        builder.setTitle(getString(R.string.verification));
        builder.setPositiveButton(getString(R.string.confirm), null);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(false);
        mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (checkOtpValidations()) {
                            if (Utils.checkNetwork(mContext) == 1) {
                                requestPlaceOrder(promoCode, etOtp.getText().toString().trim(), contactName, buildingNo, streetNo, zone, streetAddress, city, mobileNo, String.valueOf(mUserCountryId), countryName, paymentMethod, expectedDeliveryDate, estimateTime, cardMessages);
                                mAlertDialog.dismiss();
                            } else {
                                Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
                            }
                        }
                    }

                    private boolean checkOtpValidations() {

                        if (etOtp.getText().toString().trim().equalsIgnoreCase("")) {
                            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_otp_code));
                            etOtp.requestFocus();
                            return false;
                        }
                        return true;
                    }
                });
            }
        });
        mAlertDialog.show();
    }

    private void requestCouponCode() {
        //Call Webservice bbb
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<CouponModel> getDiscount = new RestClient().getApiService().getDiscount(mPrefs.getString(SharedPrefs.LANGUAGE), userId,
                mBinding.layReviewOrderDetail.etCouponCode.getText().toString().trim(),
                new Utils().getTimeZone());
        getDiscount.enqueue(new retrofit2.Callback<CouponModel>() {
            @Override
            public void onResponse(Call<CouponModel> call, retrofit2.Response<CouponModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);

                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        isCouponApplied = true;
                        promoCode = response.body().data.get(0).couponCode;
                        displayDiscount(response.body().data.get(0).discount);

                        mBinding.layReviewOrderDetail.layDiscountPrice.setVisibility(View.VISIBLE);
                        mBinding.layReviewOrderDetail.tvDiscountPrice.setText(response.body().data.get(0).totaldiscount);
                        mBinding.layReviewOrderDetail.tvTotalPrice.setText(response.body().data.get(0).totalorderprice);

                        if (isConfirmCode) {
                            isConfirmCode = false;
                            isTvReviewEnabled = true;
                            cardMessages = mBinding.layReviewOrderDetail.etCardMessage.getText().toString();
                            mBinding.vflipper.showNext();
                            mBinding.tvReviewOrder.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                            mBinding.tvDelivery.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow2));
                        }

                    } else {
                        isCouponApplied = false;
                        totalAmount = mCartAdapter.getTotalAmount();
                        mBinding.layReviewOrderDetail.tvTotalPrice.setText(String.valueOf(totalAmount));
                        Log.d("!!!!!!", "COUPON NOT APPLIED");
                        if (isConfirmCode) {
                            isConfirmCode = false;
                        }
                        mBinding.layReviewOrderDetail.layDiscountPrice.setVisibility(View.GONE);
                        Utils.showAlertDialog(mContext, response.body().message);
                    }

                }
            }

            @Override
            public void onFailure(Call<CouponModel> call, Throwable t) {
                if (isConfirmCode) {
                    isConfirmCode = false;
                }
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

    private void displayDiscount(int discount) {
        int displayAmt = (discount * mCartAdapter.getTotalAmount()) / 100;
        Log.e("Tag", "Discount: " + displayAmt);
        mBinding.layReviewOrderDetail.layDiscountPrice.setVisibility(View.VISIBLE);
        mBinding.layReviewOrderDetail.tvDiscountPrice.setText(String.valueOf(displayAmt));
//        int total_price = mCartAdapter.getTotalAmount() - displayAmt;
        totalAmount = mCartAdapter.getTotalAmount() - displayAmt;
        if (totalAmount < 0) {
            mBinding.layReviewOrderDetail.tvTotalPrice.setText("0");
        } else {
            mBinding.layReviewOrderDetail.tvTotalPrice.setText(String.valueOf(totalAmount));
        }

    }

    private void setFonts() {
        Utils.changeGravityAsPerLanguage(mPrefs, mBinding.layMain);
        Utils.changeFont(mContext, mBinding.tvReviewOrder, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvDelivery, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvPayment, Utils.FontStyle.BOLD);

        Utils.changeFont(mContext, mBinding.layReviewOrderDetail.tvCouponCodeLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layReviewOrderDetail.tvTotalLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layReviewOrderDetail.tvDiscountPrice, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layReviewOrderDetail.tvCurrencyDis, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layReviewOrderDetail.tvMinus, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layReviewOrderDetail.tvTotalPrice, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layReviewOrderDetail.tvCurrencyTotal, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layReviewOrderDetail.tvConfirm, Utils.FontStyle.BOLD);

        Utils.changeFont(mContext, mBinding.layOrderAddress.tvContactNameLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.tvStreetAddress, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.tvBuilding, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.tvStreetNo, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.tvZone, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.lblCountry, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.layMobileEdit.tvMobileNo, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.tvConfirm, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.tvOtherAddressLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderAddress.tvBack, Utils.FontStyle.BOLD);

        Utils.changeFont(mContext, mBinding.layOrderPayment.tvCodLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderPayment.tvConfirm, Utils.FontStyle.BOLD);
//        Utils.changeFont(mContext, mBinding.layOrderPayment.tvAddCardLabel, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderPayment.tvAddCreditCard, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderPayment.tvAddDebitCard, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.layOrderPayment.tvBack, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.layReviewOrderDetail.rvReviewOrder.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mBinding.layReviewOrderDetail.rvReviewOrder.setItemAnimator(new DefaultItemAnimator());
        mCartAdapter = new MyCartListAdapter(mContext, mArrayCartList, ReviewOrderFragment.this, ReviewOrderFragment.this);
        mBinding.layReviewOrderDetail.rvReviewOrder.setAdapter(mCartAdapter);
        mBinding.layReviewOrderDetail.rvReviewOrder.setNestedScrollingEnabled(false);

        mBinding.layOrderAddress.rvAddressList.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mBinding.layOrderAddress.rvAddressList.setItemAnimator(new DefaultItemAnimator());
        mAddressAdapter = new OrderAddressAdapter(mContext, mArrayAddressList, ReviewOrderFragment.this);
        mBinding.layOrderAddress.rvAddressList.setAdapter(mAddressAdapter);

//        mBinding.layOrderPayment.rvCardList.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
//        mBinding.layOrderPayment.rvCardList.setItemAnimator(new DefaultItemAnimator());
//        mDebitCardAdapter = new OrderCardAdapter(mContext, mArrayDebitCardList, ReviewOrderFragment.this);
//        mBinding.layOrderPayment.rvCardList.setAdapter(mDebitCardAdapter);


        if (mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
            LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
            userId = String.valueOf(loginmodel.id);
            mUserCountryId = loginmodel.country_id;
            mUserStoreId = String.valueOf(loginmodel.store_id);
            int storeId = Integer.parseInt(mUserStoreId);
            for (int i = 0; i < mCountryList.size(); i++) {
                if (mCountryList.get(i).country_id == storeId) {
                    countryCode = mCountryList.get(i).country_code;
                    countryName = mCountryList.get(i).country_name;
                    mBinding.layOrderAddress.tvCountry.setText(countryName);
                    mBinding.layOrderAddress.layMobileEdit.tvCountryCode.setVisibility(View.VISIBLE);
                    mBinding.layOrderAddress.layMobileEdit.tvCountryCode.setText(countryCode);
                    break;
                }
            }

            if (Utils.checkNetwork(mContext) == 1) {
                requestCartList(userId);
                ((HomeActivity) mContext).getCartCount();
            } else {
                Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
            }

            MasterDataModel.COUNTRIES mUserCountry = Utils.getCountryModelFromId(getActivity(), mUserCountryId);

            /*if (mUserCountry.card_payment == 1 && mUserCountry.cod_payment == 1) {
                mBinding.layOrderPayment.layCod.setVisibility(View.VISIBLE);
                mBinding.layOrderPayment.rvCardList.setVisibility(View.VISIBLE);
                mBinding.layOrderPayment.layAddCard.setVisibility(View.VISIBLE);
            } else if (mUserCountry.card_payment == 1) {
                mBinding.layOrderPayment.layCod.setVisibility(View.GONE);
            } else if (mUserCountry.cod_payment == 1) {
                mBinding.layOrderPayment.rvCardList.setVisibility(View.GONE);
                mBinding.layOrderPayment.layAddCard.setVisibility(View.GONE);
            }*/

            if (mUserCountry.cod_payment == 1) {
                mBinding.layOrderPayment.layCod.setVisibility(View.VISIBLE);
            } else {
                mBinding.layOrderPayment.layCod.setVisibility(View.GONE);
            }

            if (mUserCountry.debit_card_payment == 1) {
                mBinding.layOrderPayment.layAddDebitCard.setVisibility(View.VISIBLE);
            } else {
                mBinding.layOrderPayment.layAddDebitCard.setVisibility(View.GONE);
            }

            if (mUserCountry.credit_card_payment == 1) {
                mBinding.layOrderPayment.layAddCreditCard.setVisibility(View.VISIBLE);
            } else {
                mBinding.layOrderPayment.layAddCreditCard.setVisibility(View.GONE);
            }
        }

    }

    public void requestCartList(String userId) {
        //Call Webservice bbb
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<CartListModel> getCartCall = new RestClient().getApiService().cartList(mPrefs.getString(SharedPrefs.LANGUAGE), userId, mUserStoreId);
        getCartCall.enqueue(new retrofit2.Callback<CartListModel>() {
            @Override
            public void onResponse(Call<CartListModel> call, retrofit2.Response<CartListModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);

                if (response != null) {
                    //mArrayOrderList.clear();
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        mBinding.vflipper.setVisibility(View.VISIBLE);
                        mArrayCartList.addAll(response.body().data.get(0).cart_list);
                        mArrayDebitCardList.addAll(response.body().data.get(0).debit_card_list);

                        int storeId = Integer.parseInt(mUserStoreId);
                        List<CartListModel.Default_address> addressList = response.body().data.get(0).default_address;
                        for (int i = 0; i < addressList.size(); i++) {
                            if (addressList.get(i).country_id == storeId) {
                                mArrayAddressList.add(addressList.get(i));
                            }
                        }

                        if (mArrayAddressList.size() == 0) {
                            mBinding.layOrderAddress.rbOtherAddress.setChecked(true);
                            enableEts();
                        }
                        mCartAdapter.notifyDataSetChanged();
                        mAddressAdapter.notifyDataSetChanged();
//                        mDebitCardAdapter.notifyDataSetChanged();
                        totalAmount = mCartAdapter.getTotalAmount();
                        mBinding.layReviewOrderDetail.tvTotalPrice.setText(String.valueOf(totalAmount));
                    } else {
                        Log.e("Tag", "No data");
                        mBinding.vflipper.setVisibility(View.GONE);
                        mBinding.tvNoData.setVisibility(View.VISIBLE);
                        mBinding.tvNoData.setText(response.body().message);
                        //Log.e("Success0", response.body().message);
                    }

                }
            }

            @Override
            public void onFailure(Call<CartListModel> call, Throwable t) {
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

    private void requestRemoveCartItem(String user_id, String cart_id, final int position) {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<GeneralModel> removeItemCall = new RestClient().getApiService().removeCartItem(mPrefs.getString(SharedPrefs.LANGUAGE), user_id,
                cart_id);
        removeItemCall.enqueue(new retrofit2.Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                //Utils.HideProgress(DIALOG);
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful() && response.body().status.equalsIgnoreCase("1")) {
                        Log.e("Success", response.body().message);
                        mArrayCartList.remove(position);
                        //mCartAdapter.removeMessage(position);
                        mCartAdapter.notifyDataSetChanged();
                        if (mArrayCartList.size() == 0) {
                            mBinding.vflipper.setVisibility(View.GONE);
                            mBinding.tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            totalAmount = mCartAdapter.getTotalAmount();
                            mBinding.layReviewOrderDetail.tvTotalPrice.setText(String.valueOf(totalAmount));
                            if (isCouponApplied) {
                                requestCouponCode();
                            }
                        }
                        ((HomeActivity) mContext).getCartCount();
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

    private void requestUpdateCart(final int spinnerPos, final int itemPos) {
        //Call Webservice
        CartListModel.Cart_list model = mArrayCartList.get(itemPos);

        mProgressHUD = ProgressHUD.show(mContext, getResources().getString(R.string.please_wait), true, true, false, null);
        Call<AddToCartModel> favouriteCall = new RestClient().getApiService().addToCart(mPrefs.getString(SharedPrefs.LANGUAGE),
                userId, String.valueOf(model.pId), String.valueOf(spinnerPos), "2", mUserStoreId);
        favouriteCall.enqueue(new retrofit2.Callback<AddToCartModel>() {
            @Override
            public void onResponse(Call<AddToCartModel> call, retrofit2.Response<AddToCartModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);
                if (response != null) {
                    if (response.isSuccessful()) {
                        if (response.body().status.equalsIgnoreCase("1")) {
                            Log.e("Success", response.body().message);
//                        Utils.showAlertDialog(mContext, response.body().message);
                            ((HomeActivity) mContext).setCartCount(String.valueOf(response.body().data.get(0).cart_count));

                            mArrayCartList.get(itemPos).quantity = spinnerPos;
                            mArrayCartList.get(itemPos).quantity_price = (mArrayCartList.get(itemPos).price) * spinnerPos;
                            mCartAdapter.notifyDataSetChanged();
                            totalAmount = mCartAdapter.getTotalAmount();
                            mBinding.layReviewOrderDetail.tvTotalPrice.setText(String.valueOf(totalAmount));
                            if (isCouponApplied) {
                                requestCouponCode();
                            }

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

                    }
                }
            }

            @Override
            public void onFailure(Call<AddToCartModel> call, Throwable t) {
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

    private void requestPlaceOrder(String couponCode, String confirmOrderCode, String contactName, String building, String street_no, String zone, String streetAddress, String city, String mobile, String country_id, String country_name, String payment_method, String delivery_date, String estimate_time, String card_msg) {
        //Call Webservice bbb
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<PlaceOrderModel> getCartCall = new RestClient().getApiService().placeOrder(mPrefs.getString(SharedPrefs.LANGUAGE), userId, couponCode, confirmOrderCode, contactName, building, street_no, zone, streetAddress, city, mobile, mUserStoreId, country_name, payment_method, delivery_date, estimate_time, card_msg, mUserStoreId, transactionId);
        getCartCall.enqueue(new retrofit2.Callback<PlaceOrderModel>() {
            @Override
            public void onResponse(Call<PlaceOrderModel> call, retrofit2.Response<PlaceOrderModel> response) {
                mProgressHUD.dismissProgressDialog(mProgressHUD);

                if (response != null) {
                    if (response.isSuccessful()) {
                        if (response.body().status.equalsIgnoreCase("1")) {
                            int orderId = response.body().data.orderId;
                            OrderConfirmationFragment confirmOrderFrg = new OrderConfirmationFragment();
                            Bundle mBundle = new Bundle();
                            mBundle.putString("orderId", String.valueOf(orderId));
                            confirmOrderFrg.setArguments(mBundle);
                            replaceFragment(confirmOrderFrg, true);
                        } else if (response.body().status.equalsIgnoreCase("3")) {
                            final int storeId = response.body().data.store_id;
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
                            if (mBinding.layOrderPayment.rbCod.isChecked()) {
                                Utils.showAlertDialogWithClickListener(mContext, response.body().message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAlertDialog.show();
                                    }
                                });
                                etOtp.setText("");
                                ((Vibrator) mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(800);
                                Log.e("Tag", "order not placed");
                            } else {
                                Utils.showAlertDialog(mContext, response.body().message);
                            }
                        }
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

    }

    @Override
    public void onAddressClick(int position, View view) {
        if (mBinding.layOrderAddress.rbOtherAddress.isChecked()) {
            mBinding.layOrderAddress.rbOtherAddress.setChecked(false);
            disableEts();
        }

        selectedAddressPos = position;
    }

    @Override
    public void onCardClick(int position, View view) {
//        if (mBinding.layOrderPayment.rbCod.isChecked()) {
//            mBinding.layOrderPayment.rbCod.setChecked(false);
//        }
//        selectedCardPos = position;
    }

    @Override
    public void OnRemoveItemFromCart(View v, final int position) {

        Utils.showAlertDialogWith2ClickListener(mContext, getString(R.string.remove_record), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.checkNetwork(mContext) == 1) {
                    requestRemoveCartItem(userId, String.valueOf(mArrayCartList.get(position).cId), position);
                    //Log.e("Tag", "ctaid " + String.valueOf(mArrayCartList.get(position).cId));
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

    @Override
    public void onChangeProductQuantity(int spinnerPos, int position) {

        if (Utils.checkNetwork(mContext) == 1) {
            requestUpdateCart(spinnerPos, position);
        } else {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.err_network_not_available));
        }

    }

    private boolean isAnyAddressSelected() {
        if (mBinding.layOrderAddress.rbOtherAddress.isChecked() || selectedAddressPos != -1) {
            return true;
        } else {
            return false;
        }
    }

//    private boolean isAnyCardSelected() {
//        if (mBinding.layOrderPayment.rbCod.isChecked() || selectedCardPos != -1) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    private boolean addressCheckValidation() {
        if (mBinding.layOrderAddress.etContactName.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_contact_name));
            mBinding.layOrderAddress.etContactName.requestFocus();
            return false;
        }

        if (mBinding.layOrderAddress.etStreetAddress.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_address_msg));
            mBinding.layOrderAddress.etStreetAddress.requestFocus();
            return false;
        }

        if (mBinding.layOrderAddress.etBuildingNo.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_building_msg));
            mBinding.layOrderAddress.etBuildingNo.requestFocus();
            return false;
        }
        if (mBinding.layOrderAddress.etStreetNo.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_street_no_msg));
            mBinding.layOrderAddress.etStreetNo.requestFocus();
            return false;
        }
        if (mBinding.layOrderAddress.etZone.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_zone_msg));
            mBinding.layOrderAddress.etZone.requestFocus();
            return false;
        }

        if (mBinding.layOrderAddress.layMobileEdit.etMobileNo.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_mobile_no_msg));
            mBinding.layOrderAddress.layMobileEdit.etMobileNo.requestFocus();
            return false;
        }
        /*if (mBinding.layOrderAddress.layMobileEdit.etMobileNo.getText().toString().trim().length() < 10) {
            Utils.showAlertDialog(mContext, getResources().getString(R.string.enter_valid_mobile_no_msg));
            mBinding.layOrderAddress.layMobileEdit.etMobileNo.requestFocus();
            return false;
        }*/

        return true;
    }

    public void enableEts() {
        mBinding.layOrderAddress.etContactName.setEnabled(true);
        mBinding.layOrderAddress.etStreetAddress.setEnabled(true);
        mBinding.layOrderAddress.etBuildingNo.setEnabled(true);
        mBinding.layOrderAddress.etStreetNo.setEnabled(true);
        mBinding.layOrderAddress.etZone.setEnabled(true);
        //mBinding.layOrderAddress.etCity.setEnabled(true);
        mBinding.layOrderAddress.layMobileEdit.etMobileNo.setEnabled(true);
        mBinding.layOrderAddress.layCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void disableEts() {
        mBinding.layOrderAddress.etContactName.setEnabled(false);
        mBinding.layOrderAddress.etStreetAddress.setEnabled(false);
        mBinding.layOrderAddress.etBuildingNo.setEnabled(false);
        mBinding.layOrderAddress.etStreetNo.setEnabled(false);
        mBinding.layOrderAddress.etZone.setEnabled(false);
        //mBinding.layOrderAddress.etCity.setEnabled(false);
        mBinding.layOrderAddress.layMobileEdit.etMobileNo.setEnabled(false);
    }

    private void openGreetMsgActivity() {
        Intent in = new Intent(mContext, GreetMessageActivity.class);
        startActivityForResult(in, GREET_REQUEST);
        getActivity().overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
    }

    private void disableEnableStdMsgs(int status) {
        if (status == 0) {
            mBinding.layReviewOrderDetail.layStdMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mBinding.layReviewOrderDetail.spGreetMsg.performClick();
                    openGreetMsgActivity();
                }
            });
            mBinding.layReviewOrderDetail.etCardMessage.setEnabled(false);
            mBinding.layReviewOrderDetail.tvStdCardMsg.setTextColor(ContextCompat.getColor(mContext, R.color.brown));
            mBinding.layReviewOrderDetail.etCardMessage.setTextColor(ContextCompat.getColor(mContext, R.color.gray7));
        } else {
            mBinding.layReviewOrderDetail.layStdMsg.setOnClickListener(null);
            mBinding.layReviewOrderDetail.etCardMessage.setEnabled(true);
            mBinding.layReviewOrderDetail.tvStdCardMsg.setTextColor(ContextCompat.getColor(mContext, R.color.gray7));
            mBinding.layReviewOrderDetail.etCardMessage.setTextColor(ContextCompat.getColor(mContext, R.color.brown));
        }
    }

    private void requestAddAddress() {
        //Call Webservice
        mProgressHUD = ProgressHUD.show(mContext, mContext.getResources().getString(R.string.please_wait), true, true, false, null);
        Call<AddAddressModel> addAddressCall = new RestClient().getApiService().addAddress(mPrefs.getString(SharedPrefs.LANGUAGE),
                userId, address_id,
                mBinding.layOrderAddress.etContactName.getText().toString(),
                mBinding.layOrderAddress.etBuildingNo.getText().toString(),
                mBinding.layOrderAddress.etStreetNo.getText().toString(),
                mBinding.layOrderAddress.etZone.getText().toString(),
                mBinding.layOrderAddress.etStreetAddress.getText().toString(),
                "",
                mBinding.layOrderAddress.layMobileEdit.etMobileNo.getText().toString(), String.valueOf(mUserCountryId), countryName);
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

    private void openDatePickerDialog() {
        final Calendar mCurrentDate = Calendar.getInstance();
        int year = mCurrentDate.get(Calendar.YEAR);
        int month = mCurrentDate.get(Calendar.MONTH);
        int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                mCurrentDate.set(Calendar.YEAR, year);
                mCurrentDate.set(Calendar.MONTH, month);
                mCurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (mCurrentDate.getTimeInMillis() < minDateInMillis || mCurrentDate.getTimeInMillis() > maxDateInMillis) {
                    //Log.d("!!!!"," "+mCurrentDate.getTimeInMillis()+" "+minDateInMillis+ " "+maxDateInMillis );
                    /*if (isDeliveryDateSelected) {
                        estimateTime = "";
                        isDeliveryDateSelected = false;
                    }*/
                    expectedDeliveryDate = getString(R.string.date_format_blank);
                    mBinding.layReviewOrderDetail.tvExpecDeliveryDate.setTextColor(ContextCompat.getColor(mContext, R.color.gray7));
                    mBinding.layReviewOrderDetail.tvExpecDeliveryDate.setText(getString(R.string.date_format_text));
                    Utils.showAlertDialog(mContext, getString(R.string.enter_valid_delivery_date));
                } else {
                    /*if (!isDeliveryDateSelected) {
                        estimateTime = String.valueOf(mArrayTimeList.get(0).tId);
                        isDeliveryDateSelected = true;
                    }*/
                    StringBuilder builder = new StringBuilder();
                    builder.append(dayOfMonth + "-");
                    builder.append((month + 1) + "-");
                    builder.append(year);
                    expectedDeliveryDate = builder.toString();
                    String displayDate = Utils.changeDateFormate(builder.toString(), new SimpleDateFormat(getString(R.string.date_format)));
                    mBinding.layReviewOrderDetail.tvExpecDeliveryDate.setTextColor(ContextCompat.getColor(mContext, R.color.brown));
                    mBinding.layReviewOrderDetail.tvExpecDeliveryDate.setText(displayDate);
                    mBinding.layReviewOrderDetail.layPrefTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBinding.layReviewOrderDetail.spPrefTime.performClick();
                        }
                    });
                    mBinding.layReviewOrderDetail.tvPrefTime.setTextColor(ContextCompat.getColor(mContext, R.color.brown));
                }

            }


        }, year, month, day);

        DatePicker dp = dpd.getDatePicker();
        minDateInMillis = mCurrentDate.getTimeInMillis();
        dp.setMinDate(minDateInMillis);
        mCurrentDate.add(Calendar.DAY_OF_MONTH, 30);
        maxDateInMillis = mCurrentDate.getTimeInMillis();
        dp.setMaxDate(maxDateInMillis);
        dpd.show();
    }

}