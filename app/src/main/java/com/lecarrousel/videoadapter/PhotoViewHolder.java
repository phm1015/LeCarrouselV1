package com.lecarrousel.videoadapter;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowListProductImageBinding;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.utils.Utils;

/**
 * Created by eneim on 10/11/16.
 */

public class PhotoViewHolder extends TimelineViewHolder {
    public static final int LAYOUT_RES = R.layout.row_list_product_image;

    private TimelineItem.PhotoItem photoItem;
    public RowListProductImageBinding mBinding;
    private ProductListModel.Data mData;


    public PhotoViewHolder(View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
        Utils.changeFont(itemView.getContext(), mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(itemView.getContext(), mBinding.tvProductName, Utils.FontStyle.BOLD);
        Utils.changeFont(itemView.getContext(), mBinding.tvProductPrice, Utils.FontStyle.BOLD);
        Utils.changeFont(itemView.getContext(), mBinding.tvProductCurrency, Utils.FontStyle.BOLD);
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, @Nullable Object object) {
        photoItem = (TimelineItem.PhotoItem) ((TimelineItem) object).getEmbedItem();
        mData = photoItem.getData();

        mBinding.tvProductName.setText(mData.pName);
        mBinding.tvProductDescrp.setText(mData.pDesc);
        mBinding.tvProductPrice.setText(String.valueOf(mData.pPrice));

        if (mData.highlightText != null && mData.highlightText.length() > 0) {
            mBinding.tvOfferText.setText(mData.highlightText);
            mBinding.tvOfferText.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvOfferText.setVisibility(View.GONE);
        }

        if (mData.isFavourite.equalsIgnoreCase("1")) {
            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_filled);
        } else {
            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_empty);
        }

        for (int index = 0; index < mData.imgData.size(); index++) {
            if (mData.imgData.get(index).isDefault.equalsIgnoreCase("1")) {
                Glide.with(itemView.getContext()).load(mData.imgData.get(index).img)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade()
                        .into(mBinding.ivProductImg);
            }
        }

        if (mData.isHighlighted == 1) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.ivProductImg.getLayoutParams();
            layoutParams.setMargins(4, 0, 4, 0);
            mBinding.ivProductImg.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams layoutParamsPlaceHolder = (RelativeLayout.LayoutParams) mBinding.ivPlaceHolder.getLayoutParams();
            layoutParamsPlaceHolder.setMargins(4, 0, 4, 0);
            mBinding.ivPlaceHolder.setLayoutParams(layoutParamsPlaceHolder);
            mBinding.layMain.setBackgroundResource(R.drawable.highlight_bg);
        } else {
            mBinding.layMain.setBackgroundColor(Color.WHITE);
        }
    }
}
