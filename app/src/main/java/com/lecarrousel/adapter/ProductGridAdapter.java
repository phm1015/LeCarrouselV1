package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RecyclerFooterBinding;
import com.lecarrousel.databinding.RowGridProductBinding;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Bitwin on 3/9/2017.
 */

public class ProductGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;
    private final Context mContext;
    private final SharedPrefs mPrefs;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<ProductListModel.Data> mArrayProductList;

    public ProductGridAdapter(Context mContext, ArrayList<ProductListModel.Data> mArrayProductList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArrayProductList = mArrayProductList;
        this.mItemClickListener = mItemClickListener;
        this.mPrefs = new SharedPrefs(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_FOOTER) {
            RecyclerFooterBinding footerBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.recycler_footer, viewGroup, false);
            return new FooterViewHolder(footerBinding.getRoot());
        } else {
            RowGridProductBinding mRowGridProductBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_grid_product, viewGroup, false);
            Utils.changeFont(mContext, mRowGridProductBinding.tvProdNameGrid, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowGridProductBinding.tvProductGridDesc, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowGridProductBinding.tvProductPrice, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowGridProductBinding.tvProductCurrency, Utils.FontStyle.BOLD);
            return new ProductGridAdapter.MyViewHolder(mRowGridProductBinding.getRoot(), mRowGridProductBinding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < (mArrayProductList.size())) {
            return TYPE_ITEM;
        }
        return TYPE_FOOTER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            ProductListModel.Data mArrayProdList = mArrayProductList.get(position);
            myViewHolder.mRowGridProductBinding.tvProdNameGrid.setText(mArrayProdList.pName);
            myViewHolder.mRowGridProductBinding.tvProductGridDesc.setText(mArrayProdList.pDesc);
            myViewHolder.mRowGridProductBinding.tvProductPrice.setText(String.valueOf(mArrayProdList.pPrice));

            if (mArrayProdList.highlightText != null && mArrayProdList.highlightText.length() > 0) {
                myViewHolder.mRowGridProductBinding.tvOfferText.setText(mArrayProdList.highlightText);
                myViewHolder.mRowGridProductBinding.tvOfferText.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.mRowGridProductBinding.tvOfferText.setVisibility(View.GONE);
            }

            if (mArrayProdList.isFavourite.equalsIgnoreCase("1")) {
                myViewHolder.mRowGridProductBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_filled);
            } else {
                myViewHolder.mRowGridProductBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_empty);
            }

            if (!mPrefs.getBoolean(SharedPrefs.IS_LOGIN)) {
                myViewHolder.mRowGridProductBinding.ivProductIsFavorite.setVisibility(View.GONE);
            } else {
                myViewHolder.mRowGridProductBinding.ivProductIsFavorite.setVisibility(View.VISIBLE);
            }

            if (mArrayProdList.videoUrl != null && mArrayProdList.videoUrl.length() > 0) {
                Glide.with(mContext).load(mArrayProdList.thumb_video_url)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade()
                        .into(myViewHolder.mRowGridProductBinding.ivGridProdImg);
                myViewHolder.mRowGridProductBinding.ivPlay.setVisibility(View.VISIBLE);
            } else {
                Glide.with(mContext).load(mArrayProdList.imgData.get(0).img)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade()
                        .into(myViewHolder.mRowGridProductBinding.ivGridProdImg);
                myViewHolder.mRowGridProductBinding.ivPlay.setVisibility(View.GONE);
            }

            if (mArrayProdList.isHighlighted == 1) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myViewHolder.mRowGridProductBinding.ivGridProdImg.getLayoutParams();
                layoutParams.setMargins(4, 0, 4, 0);
                myViewHolder.mRowGridProductBinding.ivGridProdImg.setLayoutParams(layoutParams);
                RelativeLayout.LayoutParams layoutParamsPlaceHolder = (RelativeLayout.LayoutParams) myViewHolder.mRowGridProductBinding.ivPlaceHolder.getLayoutParams();
                layoutParamsPlaceHolder.setMargins(4, 0, 4, 0);
                myViewHolder.mRowGridProductBinding.ivPlaceHolder.setLayoutParams(layoutParamsPlaceHolder);
                myViewHolder.mRowGridProductBinding.layMain.setBackgroundResource(R.drawable.highlight_bg);
            } else {
                myViewHolder.mRowGridProductBinding.layMain.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mArrayProductList.size() + 2);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RowGridProductBinding mRowGridProductBinding;

        public MyViewHolder(View itemView, RowGridProductBinding mRowGridProductBinding) {
            super(itemView);
            this.mRowGridProductBinding = mRowGridProductBinding;
            mRowGridProductBinding.layMain.setOnClickListener(this);
            mRowGridProductBinding.ivProductIsFavorite.setOnClickListener(this);
            mRowGridProductBinding.ivPlay.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ivProductIsFavorite) {
                mItemClickListener.onFavouriteClick(getAdapterPosition(), v);
            } else if (v.getId() == R.id.layMain) {
                mItemClickListener.onItemClick(getAdapterPosition(), v);
            } else if (v.getId() == R.id.ivPlay) {
                mItemClickListener.onPlayClick(getAdapterPosition(), v);
            }
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface RecycleListClickListener {
        void onItemClick(int position, View view);

        void onFavouriteClick(int position, View view);

        void onPlayClick(int position, View view);
    }
}

