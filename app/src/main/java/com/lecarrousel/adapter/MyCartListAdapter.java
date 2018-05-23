package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowOrderDesignBinding;
import com.lecarrousel.model.CartListModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;


public class MyCartListAdapter extends RecyclerView.Adapter<MyCartListAdapter.ItemViewHolder> {

    private final Context mContext;
    private final RowSpinnerAdapter spAdapter;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<CartListModel.Cart_list> mArrayCartList;
    private SharedPrefs mPrefs;
    private ArrayList<String> qtyList = new ArrayList<>();


    public MyCartListAdapter(Context mContext, ArrayList<CartListModel.Cart_list> mArrayCartList, RecycleListClickListener mItemClickListener, RecycleListClickListener mFavouriteClickListener) {
        this.mContext = mContext;
        this.mArrayCartList = mArrayCartList;
        this.mItemClickListener = mItemClickListener;
        mPrefs = new SharedPrefs(mContext);
        spAdapter = new RowSpinnerAdapter(mContext, qtyList);
    }

    @Override
    public MyCartListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RowOrderDesignBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_order_design, viewGroup, false);
        return new ItemViewHolder(mBinding.getRoot(), mBinding);

    }


    @Override
    public void onBindViewHolder(MyCartListAdapter.ItemViewHolder holder, int position) {

        CartListModel.Cart_list mArrayList = mArrayCartList.get(position);
        holder.mRowOrderBinding.tvName.setText(mArrayList.pName);
        holder.mRowOrderBinding.tvDescription.setText(mArrayList.pDesc);
        holder.mRowOrderBinding.tvQuantity.setText(String.valueOf(mArrayList.quantity));
        holder.mRowOrderBinding.tvPrice.setText(String.valueOf(mArrayList.quantity_price));
        Glide.with(mContext).load(mArrayList.imgData.get(0).img).into(holder.mRowOrderBinding.ivProductImg);
        displayQty(position);
        holder.mRowOrderBinding.spQty.setAdapter(spAdapter);

    }

    public int getItemCount() {
        return mArrayCartList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,AdapterView.OnItemSelectedListener {

        public RowOrderDesignBinding mRowOrderBinding;

        public ItemViewHolder(View itemView, RowOrderDesignBinding mRowOrderBinding) {
            super(itemView);
            this.mRowOrderBinding = mRowOrderBinding;

            Utils.changeFont(mContext, mRowOrderBinding.layMain, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowOrderBinding.tvName, Utils.FontStyle.BOLD);
            //Utils.changeFont(mContext, mRowOrderBinding.tvCardMsgLabel, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowOrderBinding.tvCurrency, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowOrderBinding.tvQuantity, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowOrderBinding.tvPrice, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowOrderBinding.tvPieceLabel, Utils.FontStyle.BOLD);

            mRowOrderBinding.ivremoveItem.setOnClickListener(this);
            mRowOrderBinding.layQnty.setOnClickListener(this);
            mRowOrderBinding.spQty.setOnItemSelectedListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivremoveItem:
                    mItemClickListener.OnRemoveItemFromCart(v, getAdapterPosition());
                    break;
                case R.id.layQnty:
                    mRowOrderBinding.spQty.performClick();

                    break;
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
//                mRowOrderBinding.tvQuantity.setText("1");
//                mRowOrderBinding.tvPrice.setText(String.valueOf(mArrayCartList.get(getAdapterPosition()).pPrice));
            } else {
                mItemClickListener.onChangeProductQuantity(position, getAdapterPosition());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
}

public interface RecycleListClickListener {
    void OnRemoveItemFromCart(View v, int position);

    void onChangeProductQuantity(int spinnerPos, int position);

}

    private void displayQty(int pos) {
        if (qtyList.size() > 0) {
            qtyList.clear();
        }
        for (int i = 0; i < (mArrayCartList.get(pos).maxOrderQty+1); i++) {
            if (i == 0) {
                qtyList.add(0, mContext.getResources().getString(R.string.quantity));
            } else {
                qtyList.add("" + (i));
            }
        }
        spAdapter.notifyDataSetChanged();
    }

    public int getTotalAmount() {
        int total = 0;
        for (int i = 0; i < mArrayCartList.size(); i++) {
            total += mArrayCartList.get(i).quantity_price;
        }
        return total;
    }
}
