package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lecarrousel.R;
import com.lecarrousel.databinding.RowCardDesignBinding;
import com.lecarrousel.model.UserAccountModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class AddCardAdapter extends RecyclerView.Adapter<AddCardAdapter.ViewHolder> {
    private final Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<UserAccountModel.Card> mArrayCardList;
    private SharedPrefs mPrefs;

    public AddCardAdapter(Context mContext, ArrayList<UserAccountModel.Card> mArrayCardList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArrayCardList = mArrayCardList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowCardDesignBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_card_design, parent, false);
        mPrefs = new SharedPrefs(mContext);
        return new ViewHolder(mBinding.getRoot(), mBinding);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RowCardDesignBinding mRowCardDesignBinding;

        public ViewHolder(View itemView, final RowCardDesignBinding mRowCardDesignBinding) {
            super(itemView);
            this.mRowCardDesignBinding = mRowCardDesignBinding;

            Utils.changeFont(mContext, mRowCardDesignBinding.layMain, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowCardDesignBinding.tvCreditCardLbl, Utils.FontStyle.BOLD);

        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        UserAccountModel.Card mCardList = mArrayCardList.get(position);

        if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
            holder.mRowCardDesignBinding.tvCardNumber.setText(mCardList.cardNumber.substring(mCardList.cardNumber.length() - 4) + " " + "**** **** ****");
        } else {
            holder.mRowCardDesignBinding.tvCardNumber.setText("**** **** **** " + mCardList.cardNumber.substring(mCardList.cardNumber.length() - 4));
        }


        holder.mRowCardDesignBinding.addCardCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onCardClick(position, v);
            }
        });
    }

    public interface RecycleListClickListener {
        void onCardClick(int position, View view);
    }


    @Override
    public int getItemCount() {
        return mArrayCardList.size();
    }


}
