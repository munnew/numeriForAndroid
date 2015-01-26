package com.serori.numeri.media;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.serori.numeri.R;
import com.serori.numeri.imageview.NumeriImageView;
import com.serori.numeri.toast.ToastSender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 */
public class MediaFragment extends Fragment {

    private String mediaUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media, container, false);
        setRetainInstance(true);
        NumeriImageView mediaImageView = (NumeriImageView) rootView.findViewById(R.id.mediaImageView);

        mediaImageView.setOnLoadCompletedListener(image -> {
            mediaImageView.setOnLongClickListener(view -> {
                String fileName = image.toString().substring(24) + ".jpg";
                new AlertDialog.Builder(rootView.getContext()).setMessage("この画像を保存しますか？")
                        .setNegativeButton("いいえ", (dialog, id) -> {
                        })
                        .setPositiveButton("はい", (dialog, id) -> {
                            FileOutputStream outputStream = null;
                            try {
                                File file = new File(Environment.getExternalStorageDirectory()
                                        .getPath() + "/numeri/");
                                boolean existence;
                                if (!file.exists()) {
                                    existence = file.mkdir();
                                } else {
                                    existence = true;
                                }
                                if (existence) {
                                    outputStream = new FileOutputStream(file.getAbsolutePath() + "/" + fileName);
                                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                    ToastSender.getInstance().sendToast(fileName + "を保存しました");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                        .create().show();
                return true;
            });
        });
        mediaImageView.startLoadImage(null, mediaUri);

        return rootView;
    }

    public void setMediaUri(String uri) {
        mediaUri = uri;
    }
}
