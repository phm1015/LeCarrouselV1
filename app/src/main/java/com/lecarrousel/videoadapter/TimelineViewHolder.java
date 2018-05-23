/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lecarrousel.videoadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.ene.toro.ToroAdapter;

/**
 * Created by eneim on 10/11/16.
 */

public abstract class TimelineViewHolder extends ToroAdapter.ViewHolder {
    private static LayoutInflater inflater;
    protected Context mContext;

    public TimelineViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
    }

    @Override
    public void onAttachedToWindow() {
    }

    @Override
    public void onDetachedFromWindow() {
    }

    static ToroAdapter.ViewHolder createViewHolder(ViewGroup parent, int type, int listType) {
        if (inflater == null || inflater.getContext() != parent.getContext()) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        final ToroAdapter.ViewHolder viewHolder;
        final View view;
        if (listType == TimelineAdapter.TYPE_GRID) {
            switch (type) {
                case TimelineAdapter.TYPE_VIDEO:
                    view = inflater.inflate(VideoGridViewHolder.LAYOUT_RES, parent, false);
                    viewHolder = new VideoGridViewHolder(view);
                    break;
                default:
                    view = inflater.inflate(PhotoGridViewHolder.LAYOUT_RES, parent, false);
                    viewHolder = new PhotoGridViewHolder(view);
                    break;
            }
        } else {
            switch (type) {
                case TimelineAdapter.TYPE_VIDEO:
                    view = inflater.inflate(VideoViewHolder.LAYOUT_RES, parent, false);
                    viewHolder = new VideoViewHolder(view);
                    break;
                default:
                    view = inflater.inflate(PhotoViewHolder.LAYOUT_RES, parent, false);
                    viewHolder = new PhotoViewHolder(view);
                    break;
            }
        }
        return viewHolder;
    }
}
