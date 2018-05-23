package com.lecarrousel.videoadapter;

import android.view.View;

public interface RecycleItemClickListener {
    void onItemClick(int position, View view);

    void onFavouriteClick(int position, View view);
}