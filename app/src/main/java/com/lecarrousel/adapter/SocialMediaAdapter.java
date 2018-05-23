package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.transition.TransitionManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowCardDesignBinding;
import com.lecarrousel.databinding.RowSocialDesignBinding;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.model.UserAccountModel;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.ViewHolder> {
    private final Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<MasterDataModel.Social_account> mArraySocialList;

    public SocialMediaAdapter(Context mContext, ArrayList<MasterDataModel.Social_account> mArraySocialList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArraySocialList = mArraySocialList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowSocialDesignBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_social_design, parent, false);
        return new ViewHolder(mBinding.getRoot(), mBinding);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RowSocialDesignBinding mRowSocialDesignBinding;

        public ViewHolder(View itemView, final RowSocialDesignBinding mRowSocialDesignBinding) {
            super(itemView);
            this.mRowSocialDesignBinding = mRowSocialDesignBinding;


        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MasterDataModel.Social_account socialItem = mArraySocialList.get(position);
        Glide.with(mContext).load(socialItem.iconUrl)
                .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.gray5).dontAnimate()
                .into(holder.mRowSocialDesignBinding.ivSocial);
        holder.mRowSocialDesignBinding.ivSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position, v);
            }
        });

    }

    public interface RecycleListClickListener {
        void onItemClick(int position, View view);
    }


    @Override
    public int getItemCount() {
        return mArraySocialList.size();
    }


}
