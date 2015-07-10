package com.serori.numeri.media;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.serori.numeri.R;
import com.serori.numeri.temp.activity.NumeriActivity;
import com.serori.numeri.temp.imageview.NumeriImageView;


/**
 * MediaFragment
 */
public class MediaFragment extends Fragment {

    private String mediaUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media, container, false);
        setRetainInstance(true);
        NumeriImageView mediaImageView = (NumeriImageView) rootView.findViewById(R.id.mediaImageView);
        mediaImageView.setOnLoadCompletedListener(image -> mediaImageView.setSaveImageFunctionEnabled(true, (NumeriActivity) getActivity()));
        mediaImageView.setImage(mediaUri);
        return rootView;
    }

    public void setMediaUri(String uri) {
        mediaUri = uri;
    }
}
