package com.lecarrousel.videoadapter;

import android.support.annotation.NonNull;

import com.lecarrousel.model.ProductListModel;

/**
 * Created by eneim on 10/11/16.
 */

public class TimelineItem {
    @NonNull
    private final EmbedItem embedItem;

    public TimelineItem(ProductListModel.Data data) {
        if (data.videoUrl != null && data.videoUrl.length() > 0) {
            embedItem = new VideoItem(data);
        } else {
            embedItem = new PhotoItem(data);
        }
    }


    @NonNull
    public EmbedItem getEmbedItem() {
        return embedItem;
    }


    public static class PhotoItem implements EmbedItem {
        private ProductListModel.Data data;

        public PhotoItem(ProductListModel.Data data) {
            this.data = data;
        }

        public ProductListModel.Data getData() {
            return data;
        }


        @Override
        public String getClassName() {
            return getClass().getSimpleName();
        }
    }

    public static class VideoItem implements EmbedItem {
        private ProductListModel.Data data;

        public VideoItem(ProductListModel.Data data) {
            this.data = data;
        }

        public ProductListModel.Data getData() {
            return data;
        }

        @Override
        public String getClassName() {
            return getClass().getSimpleName();
        }
    }

    interface EmbedItem {
        @NonNull
        String getClassName();
    }
}
