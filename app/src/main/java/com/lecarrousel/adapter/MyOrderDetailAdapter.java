package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowOrderDetailDesignBinding;
import com.lecarrousel.model.OrderDetailModel;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;


public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.ItemViewHolder> {

    private final Context mContext;
    private ArrayList<OrderDetailModel.ProductData> mArrayCartList;


    public MyOrderDetailAdapter(Context mContext, ArrayList<OrderDetailModel.ProductData> mArrayCartList) {
        this.mContext = mContext;
        this.mArrayCartList = mArrayCartList;
    }

    @Override
    public MyOrderDetailAdapter.ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RowOrderDetailDesignBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_order_detail_design, viewGroup, false);
        return new ItemViewHolder(mBinding.getRoot(), mBinding);

    }


    @Override
    public void onBindViewHolder(MyOrderDetailAdapter.ItemViewHolder holder, int position) {

        OrderDetailModel.ProductData mArrayList = mArrayCartList.get(position);
        holder.mRowOrderBinding.tvName.setText(mArrayList.pName);
        holder.mRowOrderBinding.tvDescription.setText(mArrayList.pDesc);
        holder.mRowOrderBinding.tvQuantity.setText(String.valueOf(mArrayList.quantity));
        holder.mRowOrderBinding.tvPrice.setText(String.valueOf(mArrayList.totalProductPrice));
        /*if(mArrayList.cartMessage.equalsIgnoreCase("")){
            holder.mRowOrderBinding.tvCardMsgLabel.setVisibility(View.GONE);
            holder.mRowOrderBinding.tvCardMsg.setVisibility(View.GONE);
        }else{
            holder.mRowOrderBinding.tvCardMsg.setText(String.valueOf(mArrayList.cartMessage));
        }*/
        Glide.with(mContext).load(mArrayList.imgData.get(0).img).into(holder.mRowOrderBinding.ivProductImg);

    }
    
    public int getItemCount() {
        return mArrayCartList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public RowOrderDetailDesignBinding mRowOrderBinding;

        public ItemViewHolder(View itemView, RowOrderDetailDesignBinding mRowOrderBinding) {
            super(itemView);
            this.mRowOrderBinding = mRowOrderBinding;

            Utils.changeFont(mContext, mRowOrderBinding.layMain, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowOrderBinding.tvDescription, Utils.FontStyle.REGULAR);
            //Utils.changeFont(mContext, mRowOrderBinding.tvCardMsg, Utils.FontStyle.REGULAR);

        }

    }

}
