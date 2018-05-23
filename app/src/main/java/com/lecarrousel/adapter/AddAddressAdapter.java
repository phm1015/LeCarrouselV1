package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lecarrousel.R;
import com.lecarrousel.databinding.RowAddressDesignBinding;
import com.lecarrousel.model.UserAccountModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class AddAddressAdapter extends RecyclerView.Adapter<AddAddressAdapter.ViewHolder> {
    private final Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<UserAccountModel.Address> mArrayAddressList;
    private SharedPrefs mPrefs;

    public AddAddressAdapter(Context mContext, ArrayList<UserAccountModel.Address> mArrayAddressList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArrayAddressList = mArrayAddressList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mPrefs = new SharedPrefs(mContext);
        RowAddressDesignBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_address_design, parent, false);
        return new ViewHolder(mBinding.getRoot(), mBinding);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public RowAddressDesignBinding mRowAddrDesignBinding;

        public ViewHolder(View itemView, final RowAddressDesignBinding mRowAddrDesignBinding) {
            super(itemView);
            this.mRowAddrDesignBinding = mRowAddrDesignBinding;

            Utils.changeFont(mContext, mRowAddrDesignBinding.layMain, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowAddrDesignBinding.tvNameAddress, Utils.FontStyle.BOLD);
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        UserAccountModel.Address mAddressList = mArrayAddressList.get(position);
        holder.mRowAddrDesignBinding.tvNameAddress.setText(mAddressList.contactName);
//        holder.mRowAddrDesignBinding.tvAddress.setText(mAddressList.streetAddress + "\n" + mAddressList.buildingNo + "," +
//                mAddressList.streetNo + ","+ mAddressList.zone +"\n"+mAddressList.country_name);
        holder.mRowAddrDesignBinding.tvAddress.setText(mAddressList.streetAddress+"\n"+
                mContext.getResources().getString(R.string.addr_format,mAddressList.buildingNo,mAddressList.streetNo,mAddressList.zone)
                +"\n" +mAddressList.country_name);
                holder.mRowAddrDesignBinding.addAddressRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onAddressClick(position, v);
            }
        });


    }

    public interface RecycleListClickListener {
        void onAddressClick(int position, View view);
    }


    @Override
    public int getItemCount() {
        return mArrayAddressList.size();
    }

}