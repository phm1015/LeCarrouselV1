package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowChangeStoreBinding;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class ChangeStoreAdapter extends RecyclerView.Adapter<ChangeStoreAdapter.ViewHolder> {
    private Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<MasterDataModel.COUNTRIES> mArrayStoreList;
    private SharedPrefs mPrefs;
    private int storeId;

    public ChangeStoreAdapter(Context mContext, ArrayList<MasterDataModel.COUNTRIES> mArrayStoreList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArrayStoreList = mArrayStoreList;
        this.mItemClickListener = mItemClickListener;
        mPrefs = new SharedPrefs(mContext);
        LoginModel.Data loginmodel = new Gson().fromJson(mPrefs.getString(SharedPrefs.LOGIN_RESPONSE), LoginModel.Data.class);
        //storeId = Integer.parseInt(mPrefs.getString(SharedPrefs.STORE_ID));
        if(mPrefs.getBoolean(SharedPrefs.IS_LOGIN)){
            storeId = loginmodel.store_id;
        }else{
            storeId = Integer.parseInt(mPrefs.getString(SharedPrefs.STORE_ID));
        }

        for (int i = 0; i < mArrayStoreList.size(); i++) {

            if (mArrayStoreList.get(i).country_id == storeId) {
                mArrayStoreList.get(i).is_checked = true;
                break;
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowChangeStoreBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_change_store, parent, false);
        return new ViewHolder(mBinding.getRoot(), mBinding);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public RowChangeStoreBinding mRowChangeStoreBinding;

        public ViewHolder(View itemView, final RowChangeStoreBinding mRowChangeStoreBinding) {
            super(itemView);
            this.mRowChangeStoreBinding = mRowChangeStoreBinding;


            Utils.changeFont(mContext, mRowChangeStoreBinding.layMain, Utils.FontStyle.BOLD);
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MasterDataModel.COUNTRIES mStoreList = mArrayStoreList.get(position);
        holder.mRowChangeStoreBinding.tvCountryName.setText(mStoreList.country_name);
        holder.mRowChangeStoreBinding.rbStoreSelect.setChecked(mStoreList.is_checked);
        holder.mRowChangeStoreBinding.layMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int index = 0; index < mArrayStoreList.size(); index++) {
                    if (index == position) {
                        mArrayStoreList.get(index).is_checked = true;
                    } else {
                        mArrayStoreList.get(index).is_checked = false;
                    }
                }
                notifyDataSetChanged();
                mItemClickListener.onStoreClick(position, v);
            }
        });
        Glide.with(mContext).load(mStoreList.countryFlag).into(holder.mRowChangeStoreBinding.ivCountryFlag);
    }

    public interface RecycleListClickListener {
        void onStoreClick(int position, View view);
    }


    @Override
    public int getItemCount() {
        return mArrayStoreList.size();
    }

}