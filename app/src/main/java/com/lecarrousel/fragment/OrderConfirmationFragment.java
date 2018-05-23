package com.lecarrousel.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;
import com.lecarrousel.databinding.FrgOrderConfirmationBinding;
import com.lecarrousel.utils.Utils;

public class OrderConfirmationFragment extends BaseFragment {


    private FrgOrderConfirmationBinding mBinding;
    private Context mContext;
    private boolean isBackToOrder = false;

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
        ((HomeActivity) mContext).setDrawerSwipe(false);
        ((HomeActivity) mContext).setMenuIcon(R.drawable.ic_back);
        ((HomeActivity) mContext).setVisibilityCart(false);
        ((HomeActivity) mContext).mBinding.layHeader.ivLeftAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).backToDashBoard();
                ((HomeActivity) mContext).setIvLeftAction();
            }
        });
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frg_order_confirmation, container, false);
        setFonts();

        String thisLink = getString(R.string.my_order_link);
        String yourString = getString(R.string.you_can_check);
        SpannableString content = new SpannableString(yourString.trim());

        content.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                isBackToOrder = true;
                ((HomeActivity) mContext).backToMyOrder();
                ((HomeActivity) mContext).setIvLeftAction();
            }
        }, yourString.indexOf(thisLink), yourString.indexOf(thisLink) + thisLink.length(),0);

        mBinding.tvYouCan.setText(content);
        mBinding.tvYouCan.setMovementMethod(LinkMovementMethod.getInstance());

        thisLink = getString(R.string.only_terms) ;
        yourString = getString(R.string.only_terms)+ " "+getString(R.string.apply);
        content = new SpannableString(yourString.trim());

        content.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                TermsFragment termsFrg = new TermsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("isFrom","thankyou");
                termsFrg.setArguments(bundle);
                replaceFragment(termsFrg,true);
            }
        }, yourString.indexOf(thisLink), yourString.indexOf(thisLink) + thisLink.length(),0);

        mBinding.tvTerms.setText(content);
        mBinding.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());

        Bundle mBundle = getArguments();
        if (mBundle != null) {
            String orderId = mBundle.getString("orderId");
            mBinding.tvOrderId.setText(orderId);

            ((HomeActivity) mContext).getCartCount();
            Log.e("Tag1", orderId);
        }

        mBinding.tvBackTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).backToDashBoard();
                ((HomeActivity) mContext).setIvLeftAction();
            }
        });

        return mBinding.getRoot();
    }


    private void setFonts() {
        Utils.changeFont(mContext, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(mContext, mBinding.tvHeader, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvThankYou, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvOrderId, Utils.FontStyle.BOLD);
        Utils.changeFont(mContext, mBinding.tvBackTo, Utils.FontStyle.BOLD);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(!isBackToOrder){
            ((HomeActivity) mContext).backToDashBoard();
            ((HomeActivity) mContext).setIvLeftAction();
        }
    }
}
