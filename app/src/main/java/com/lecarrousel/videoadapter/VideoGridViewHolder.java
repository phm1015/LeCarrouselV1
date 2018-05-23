package com.lecarrousel.videoadapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowGridProductVideoBinding;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.utils.Utils;

import im.ene.toro.exoplayer2.ExoVideoView;
import im.ene.toro.exoplayer2.ExoVideoViewHolder;

/**
 * Created by eneim on 10/11/16.
 */

public class VideoGridViewHolder extends ExoVideoViewHolder {
    public static final int LAYOUT_RES = R.layout.row_grid_product_video;

    private TimelineItem.VideoItem videoItem;
    public RowGridProductVideoBinding mBinding;
    private ProductListModel.Data mData;

    public VideoGridViewHolder(View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
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

        if (mData.isFavourite.equalsIgnoreCase("1")) {
            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_filled);
        } else {
            mBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_empty);
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
                VideoGridViewHolder.super.onPlaybackStarted();
            }
        }).start();
    }

    @Override
    public void onPlaybackPaused() {
        mBinding.layVideo.thumbnail.animate().alpha(1.f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                VideoGridViewHolder.super.onPlaybackPaused();
            }
        }).start();
    }

    @Override
    public void onPlaybackCompleted() {
//        playerView.start();
        mBinding.layVideo.thumbnail.animate().alpha(1.f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                VideoGridViewHolder.super.onPlaybackCompleted();
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
