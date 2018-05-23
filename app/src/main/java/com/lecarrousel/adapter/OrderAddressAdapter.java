package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lecarrousel.R;
import com.lecarrousel.databinding.RowAddressSelectDesignBinding;
import com.lecarrousel.model.CartListModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class OrderAddressAdapter extends RecyclerView.Adapter<OrderAddressAdapter.ViewHolder> {
    private Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<CartListModel.Default_address> mArrayAddressList;
    private SharedPrefs mPrefs;
    private int lastCheckPos = -1;

    public OrderAddressAdapter(Context mContext, ArrayList<CartListModel.Default_address> mArrayAddressList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArrayAddressList = mArrayAddressList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mPrefs = new SharedPrefs(mContext);
        RowAddressSelectDesignBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_address_select_design, parent, false);
        return new ViewHolder(mBinding.getRoot(), mBinding);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public RowAddressSelectDesignBinding mRowAddrDesignBinding;

        public ViewHolder(View itemView,RowAddressSelectDesignBinding mRowAddrDesignBinding) {
            super(itemView);
            this.mRowAddrDesignBinding = mRowAddrDesignBinding;

            mRowAddrDesignBinding.layMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lastCheckPos != getAdapterPosition()){
                        lastCheckPos = getAdapterPosition();
                        notifyItemRangeChanged(0,mArrayAddressList.size());
                        mItemClickListener.onAddressClick(getAdapterPosition(),v);
                    }

                }
            });

            Utils.changeFont(mContext, mRowAddrDesignBinding.layMain, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowAddrDesignBinding.tvNameAddress, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowAddrDesignBinding.tvMyAddressLabel, Utils.FontStyle.BOLD);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CartListModel.Default_address mAddressList = mArrayAddressList.get(position);
        holder.mRowAddrDesignBinding.tvNameAddress.setText(mAddressList.contactName);
//        holder.mRowAddrDesignBinding.tvAddress.setText(mAddressList.streetAddress + "\n" + mAddressList.buildingNo + "," +
//                mAddressList.streetNo + ","+ mAddressList.zone + "\n"+mAddressList.country_name);
        holder.mRowAddrDesignBinding.tvAddress.setText(mAddressList.streetAddress+"\n"+
                mContext.getResources().getString(R.string.addr_format,mAddressList.buildingNo,mAddressList.streetNo,mAddressList.zone)
                +"\n" +mAddressList.country_name);
        holder.mRowAddrDesignBinding.rbAddressSelect.setChecked(position == lastCheckPos);


    }

    public interface RecycleListClickListener {
        void onAddressClick(int position, View view);
    }


    @Override
    public int getItemCount() {
        return mArrayAddressList.size();
    }

    public void unCheckSavedAddress(){
        lastCheckPos = -1;
        notifyItemRangeChanged(0,mArrayAddressList.size());
    }

}