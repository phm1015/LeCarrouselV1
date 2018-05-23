package com.lecarrousel.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ParserException;
import com.lecarrousel.R;

import im.ene.toro.exoplayer2.ExoPlayerView;
import im.ene.toro.exoplayer2.Media;

/**
 * Created by Bitwin on 4/28/2017.
 */

public class VideoPlayFragment extends android.support.v4.app.DialogFragment{

    ExoPlayerView videoView;
    String videoUrl = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_videoplay, container, false);
        videoView = (ExoPlayerView) view.findViewById(R.id.exoVideoView);

        if(getArguments()!= null){
            videoUrl = getArguments().getString("videourl");
        }
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            videoView.setMedia(new Media(Uri.parse(videoUrl)),true);
        } catch (ParserException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new Dialog(getContext(), R.style.VideoPlay);
    }
}
