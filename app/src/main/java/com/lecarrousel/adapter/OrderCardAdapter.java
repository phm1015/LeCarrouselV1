package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lecarrousel.R;
import com.lecarrousel.databinding.RowCardSelectDesignBinding;
import com.lecarrousel.model.CartListModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class OrderCardAdapter extends RecyclerView.Adapter<OrderCardAdapter.ViewHolder> {
    private final Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<CartListModel.Debit_card_list> mArrayCardList;
    private SharedPrefs mPrefs;
    private int lastCheckPos = -1;
    //private RadioButton rbLastChecked = null;


    public OrderCardAdapter(Context mContext, ArrayList<CartListModel.Debit_card_list> mArrayCardList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArrayCardList = mArrayCardList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowCardSelectDesignBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_card_select_design, parent, false);
        mPrefs = new SharedPrefs(mContext);
        return new ViewHolder(mBinding.getRoot(), mBinding);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public RowCardSelectDesignBinding mRowCardDesignBinding;

        public ViewHolder(View itemView, final RowCardSelectDesignBinding mRowCardDesignBinding) {
            super(itemView);
            this.mRowCardDesignBinding = mRowCardDesignBinding;
            mRowCardDesignBinding.layMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastCheckPos != getAdapterPosition()) {
                        lastCheckPos = getAdapterPosition();
                        notifyItemRangeChanged(0, mArrayCardList.size());
                        mItemClickListener.onCardClick(getAdapterPosition(), v);
                    }

                }
            });

            Utils.changeFont(mContext, mRowCardDesignBinding.layMain, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowCardDesignBinding.tvCreditCardLbl, Utils.FontStyle.BOLD);

        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CartListModel.Debit_card_list mCardList = mArrayCardList.get(position);

        if (mPrefs.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
            holder.mRowCardDesignBinding.tvCardNumber.setText(mCardList.cardNumber.substring(mCardList.cardNumber.length() - 4) + " " + "**** **** ****");
        } else {
            holder.mRowCardDesignBinding.tvCardNumber.setText("**** **** **** " + mCardList.cardNumber.substring(mCardList.cardNumber.length() - 4));
        }

        holder.mRowCardDesignBinding.rbCardSelect.setChecked(position == lastCheckPos);

    }

    public interface RecycleListClickListener {
        void onCardClick(int position, View view);
    }


    @Override
    public int getItemCount() {
        return mArrayCardList.size();
    }

    public void unCheckSavedCard() {
        lastCheckPos = -1;
        notifyItemRangeChanged(0, mArrayCardList.size());
    }


}
