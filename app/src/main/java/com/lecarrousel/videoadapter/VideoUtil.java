package com.lecarrousel.videoadapter;

import android.support.annotation.NonNull;

public class VideoUtil {
    public static String genVideoId(@NonNull String videoUri, int playbackOrder, Object... manifest) {
        StringBuilder builder = new StringBuilder();
        builder.append(videoUri).append(":").append(playbackOrder);
        if (manifest != null && manifest.length > 0) {
            for (Object o : manifest) {
                builder.append(":").append(o.toString());
            }
        }
        return builder.toString();
    }
}
