package com.lecarrousel.videoadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.BaseAdapter;
import im.ene.toro.ToroAdapter;


public class TimelineAdapter extends BaseAdapter<ToroAdapter.ViewHolder> implements OrderedPlayList {
    public static final int TYPE_GRID = 0;
    public static final int TYPE_LIST = 1;
    public static final int TYPE_PHOTO = 2;
    public static final int TYPE_VIDEO = 3;

    private ArrayList<TimelineItem> items;
    private int ITEM_COUNT = 0;
    private final int listType;
    private RecycleItemClickListener mItemClickListener;
    private SharedPrefs mPrefs;
    private boolean isLogin;

    public TimelineAdapter(Context mContext, List<ProductListModel.Data> data, int listType, RecycleItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
        this.listType = listType;
        setupList(data);
        mPrefs = new SharedPrefs(mContext);
        isLogin = mPrefs.getBoolean(SharedPrefs.IS_LOGIN);
    }

    private void setupList(List<ProductListModel.Data> data) {
        items = new ArrayList<>();
        for (int index = 0; index < data.size(); index++) {
            items.add(new TimelineItem(data.get(index)));
        }
        ITEM_COUNT = items.size();

    }

    @NonNull
    @Override
    protected TimelineItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ViewHolder viewHolder = TimelineViewHolder.createViewHolder(parent, viewType, listType);
        viewHolder.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                mItemClickListener.onItemClick(position, v);
            }
        });

        if (viewHolder instanceof VideoViewHolder) {
            VideoViewHolder mHolder = (VideoViewHolder) viewHolder;
            if (isLogin) {
                mHolder.mBinding.ivProductIsFavorite.setVisibility(View.VISIBLE);
                mHolder.mBinding.ivProductIsFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = viewHolder.getAdapterPosition();
                        mItemClickListener.onFavouriteClick(position, v);
                    }
                });
            } else {
                mHolder.mBinding.ivProductIsFavorite.setVisibility(View.INVISIBLE);
            }

        } else if (viewHolder instanceof PhotoViewHolder) {
            PhotoViewHolder mHolder = (PhotoViewHolder) viewHolder;
            if (isLogin) {
                mHolder.mBinding.ivProductIsFavorite.setVisibility(View.VISIBLE);
                mHolder.mBinding.ivProductIsFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = viewHolder.getAdapterPosition();
                        mItemClickListener.onFavouriteClick(position, v);
                    }
                });
            } else {
                mHolder.mBinding.ivProductIsFavorite.setVisibility(View.INVISIBLE);
            }
        } else if (viewHolder instanceof VideoGridViewHolder) {
            VideoGridViewHolder mHolder = (VideoGridViewHolder) viewHolder;
            mHolder.mBinding.ivProductIsFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    mItemClickListener.onFavouriteClick(position, v);
                }
            });
        } else if (viewHolder instanceof PhotoGridViewHolder) {
            PhotoGridViewHolder mHolder = (PhotoGridViewHolder) viewHolder;
            mHolder.mBinding.ivProductIsFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    mItemClickListener.onFavouriteClick(position, v);
                }
            });
        }

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    @Override
    public int firstVideoPosition() {
        int firstVideo = -1;
        for (int i = 0; i < ITEM_COUNT; i++) {
            if (TimelineItem.VideoItem.class.getSimpleName().equals(getItem(i).getEmbedItem().getClassName())) {
                firstVideo = i;
                break;
            }
        }
        return firstVideo;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        String itemClassName = getItem(position).getEmbedItem().getClassName();
        return TimelineItem.VideoItem.class.getSimpleName().equals(itemClassName) ? TYPE_VIDEO : TYPE_PHOTO;
    }
}
