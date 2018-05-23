package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ParserException;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowHighlightPagerBinding;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;
import com.lecarrousel.videoadapter.RecycleItemClickListener;

import java.util.List;

import im.ene.toro.exoplayer2.Media;
import im.ene.toro.exoplayer2.PlayerCallback;

public class HighLightPagerAdapter extends PagerAdapter {
    private RecycleItemClickListener mItemClickListener;
    private List<ProductListModel.Data> mOfferList;
    private Context context;
    private final SharedPrefs mPrefs;
    private boolean isLogin;

    public HighLightPagerAdapter(Context context, List<ProductListModel.Data> mOfferList, RecycleItemClickListener mItemClickListener) {
        this.context = context;
        this.mOfferList = mOfferList;
        this.mItemClickListener = mItemClickListener;
        this.mPrefs = new SharedPrefs(context);
        isLogin = mPrefs.getBoolean(SharedPrefs.IS_LOGIN);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mOfferList.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup parent, final int position) {
        final RowHighlightPagerBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_highlight_pager, parent, false);

        ProductListModel.Data mProductData = mOfferList.get(position);
        mBinding.tvProductName.setText(mProductData.pName);
        mBinding.tvProductDesc.setText(mProductData.pDesc);
        mBinding.tvProductPrice.setText(String.valueOf(mProductData.pPrice));

        if (mProductData.highlightText != null && mProductData.highlightText.length() > 0) {
            mBinding.tvOfferText.setText(mProductData.highlightText);
            mBinding.tvOfferText.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvOfferText.setVisibility(View.GONE);
        }

        if (mProductData.isFavourite.equalsIgnoreCase("1")) {
            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_filled);
        } else {
            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_empty);
        }

        if (!isLogin) {
            mBinding.ivProductIsFavorite.setVisibility(View.GONE);
        } else {
            mBinding.ivProductIsFavorite.setVisibility(View.VISIBLE);
        }

        if (mProductData.videoUrl != null && mProductData.videoUrl.length() > 0) {
            mBinding.ivProductImg.setVisibility(View.GONE);
            mBinding.layVideoView.setVisibility(View.VISIBLE);
            try {
                mBinding.exoVideoView.setMedia(new Media(Uri.parse(mProductData.videoUrl)), true);
                mBinding.exoVideoView.setUseController(false);
                mBinding.exoVideoView.setResizeMode(0);
                mBinding.exoVideoView.setPlayerCallback(new PlayerCallback() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playWhenReady) {
                            mBinding.thumbnail.setVisibility(View.GONE);
                            mBinding.exoVideoView.setVolume(0f);
                        }
                        if (playbackState == ExoPlayer.STATE_ENDED) {
                            mBinding.exoVideoView.start();
                        }
                    }

                    @Override
                    public boolean onPlayerError(Exception error) {
                        return false;
                    }
                });
            } catch (ParserException e) {
                e.printStackTrace();
            }
        } else {
            mBinding.ivProductImg.setVisibility(View.VISIBLE);
            mBinding.layVideoView.setVisibility(View.GONE);
            if (mOfferList.get(position).imgData.get(0).img != null) {
                Glide.with(context).load(mOfferList.get(position).imgData.get(0).img).centerCrop().crossFade().into(mBinding.ivProductImg);
            }
        }

        mBinding.layMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position, v);
            }
        });

        mBinding.ivProductIsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onFavouriteClick(position, v);
            }
        });


        Utils.changeFont(context, mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(context, mBinding.tvProductName, Utils.FontStyle.BOLD);
        Utils.changeFont(context, mBinding.tvProductPrice, Utils.FontStyle.BOLD);
        Utils.changeFont(context, mBinding.tvProductCurrency, Utils.FontStyle.BOLD);
        parent.addView(mBinding.getRoot(), 0);
        return mBinding.getRoot();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
