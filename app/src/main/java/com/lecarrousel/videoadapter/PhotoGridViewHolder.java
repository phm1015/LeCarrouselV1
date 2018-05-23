package com.lecarrousel.videoadapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowGridProductImageBinding;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.utils.Utils;

/**
 * Created by eneim on 10/11/16.
 */

public class PhotoGridViewHolder extends TimelineViewHolder {
    public static final int LAYOUT_RES = R.layout.row_grid_product_image;

    private TimelineItem.PhotoItem photoItem;
    public RowGridProductImageBinding mBinding;
    private ProductListModel.Data mData;


    public PhotoGridViewHolder(View itemView) {
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
    }
}
