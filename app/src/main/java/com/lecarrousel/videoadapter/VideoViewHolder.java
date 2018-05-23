package com.lecarrousel.videoadapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowListProductVideoBinding;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.utils.Utils;

import im.ene.toro.exoplayer2.ExoVideoView;
import im.ene.toro.exoplayer2.ExoVideoViewHolder;

/**
 * Created by eneim on 10/11/16.
 */

public class VideoViewHolder extends ExoVideoViewHolder {
    public static final int LAYOUT_RES = R.layout.row_list_product_video;

    private TimelineItem.VideoItem videoItem;
    public RowListProductVideoBinding mBinding;
    private ProductListModel.Data mData;
    private Context mContext;

    public VideoViewHolder(View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
        mContext = itemView.getContext();
        Utils.changeFont(itemView.getContext(), mBinding.layMain, Utils.FontStyle.REGULAR);
        Utils.changeFont(itemView.getContext(), mBinding.tvProductName, Utils.FontStyle.BOLD);
        Utils.changeFont(itemView.getContext(), mBinding.tvProductPrice, Utils.FontStyle.BOLD);
        Utils.changeFont(itemView.getContext(), mBinding.tvProductCurrency, Utils.FontStyle.BOLD);
    }

    @Override
    protected ExoVideoView findVideoView(View itemView) {
        return (ExoVideoView) itemView.findViewById(R.id.video);
    }

    @Override
    protected void onBind(RecyclerView.Adapter adapter, @Nullable Object object) {
        if (!(object instanceof TimelineItem) || !(((TimelineItem) object).getEmbedItem() instanceof TimelineItem.VideoItem)) {
            throw new IllegalArgumentException("Only VideoItem is accepted");
        }
        videoItem = (TimelineItem.VideoItem) ((TimelineItem) object).getEmbedItem();
        mData = videoItem.getData();
        playerView.setMedia(Uri.parse(videoItem.getData().videoUrl));

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

//        if (mData.isHighlighted == 1) {
//            mBinding.layMain.setPadding(4, 4, 4, 4);
//            mBinding.layMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
//        } else {
//            mBinding.layMain.setBackgroundColor(Color.WHITE);
//        }

        if (mData.isHighlighted == 1) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBinding.layVideoView.getLayoutParams();
            layoutParams.setMargins(4, 0, 4, 0);
            mBinding.layVideoView.setLayoutParams(layoutParams);
            mBinding.layMain.setBackgroundResource(R.drawable.highlight_bg);
        } else {
            mBinding.layMain.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void setOnItemClickListener(View.OnClickListener listener) {
        super.setOnItemClickListener(listener);
        this.playerView.setOnClickListener(listener);
    }

    @Nullable
    @Override
    public String getMediaId() {
        return VideoUtil.genVideoId(this.videoItem.getData().videoUrl, getAdapterPosition());
    }

    @Override
    public void onVideoPreparing() {
        super.onVideoPreparing();
    }

    @Override
    public void onVideoPrepared() {
        super.onVideoPrepared();
        playerView.setVolume(0);
    }

    @Override
    public void onViewHolderBound() {
        super.onViewHolderBound();
        Glide.with(itemView.getContext()).load(R.drawable.exo_controls_play)
                .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(mBinding.layVideo.thumbnail);
    }

    @Override
    public void onPlaybackStarted() {
        playerView.setVolume(0);
        mBinding.layVideo.thumbnail.animate().alpha(0.f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                VideoViewHolder.super.onPlaybackStarted();
            }
        }).start();
    }

    @Override
    public void onPlaybackPaused() {
        mBinding.layVideo.thumbnail.animate().alpha(1.f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                VideoViewHolder.super.onPlaybackPaused();
            }
        }).start();
    }

    @Override
    public void onPlaybackCompleted() {
//        playerView.start();
        mBinding.layVideo.thumbnail.animate().alpha(1.f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                VideoViewHolder.super.onPlaybackCompleted();
            }
        }).start();
    }

    @Override
    public boolean onPlaybackError(Exception error) {
        mBinding.layVideo.thumbnail.animate().alpha(1.f).setDuration(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Immediately finish the animation.
            }
        }).start();
        return super.onPlaybackError(error);
    }
}
