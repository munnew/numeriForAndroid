package com.serori.numeri.util.twitter;


import android.os.AsyncTask;
import android.widget.Toast;

import com.serori.numeri.application.Application;
import com.serori.numeri.user.NumeriUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

public class TweetBuilder {
    private NumeriUser numeriUser;
    private List<File> images = new ArrayList<>();
    private long statusId = -1;
    private String text;

    public TweetBuilder(NumeriUser numeriUser) {
        this.numeriUser = numeriUser;
    }


    public TweetBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public TweetBuilder addImages(List<File> images) {
        this.images.addAll(images);
        return this;
    }

    public TweetBuilder setReplyDestinationId(long statusId) {
        this.statusId = statusId;
        return this;
    }

    public void tweet() {
        if (numeriUser == null) {
            throw new NullPointerException();
        }

        StatusUpdate statusUpdate = new StatusUpdate(text);
        Twitter twitter = numeriUser.getTwitter();

        if (statusId != -1)
            statusUpdate.setInReplyToStatusId(statusId);
        AsyncTask.execute(() -> {
            if (!images.isEmpty()) {
                Application.getInstance().onToast("画像をアップロードします", Toast.LENGTH_SHORT);
                long[] medias = new long[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    try {
                        UploadedMedia uploadedMedia = numeriUser.getTwitter().uploadMedia(images.get(i));
                        medias[i] = uploadedMedia.getMediaId();
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
                statusUpdate.setMediaIds(medias);
            }

            try {
                twitter.updateStatus(statusUpdate);
                Application.getInstance().onToast("ツイートに成功しました", Toast.LENGTH_SHORT);
            } catch (TwitterException e) {
                Application.getInstance().onToast("ツイートに失敗しました", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        });
    }


}
