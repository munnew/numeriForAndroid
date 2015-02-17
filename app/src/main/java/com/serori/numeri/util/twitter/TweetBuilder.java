package com.serori.numeri.util.twitter;


import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.SimpleAsyncTask;
import com.serori.numeri.util.toast.ToastSender;

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

    @SuppressWarnings("unchecked")
    public void tweet() {
        if (numeriUser == null) {
            throw new NullPointerException();
        }

        StatusUpdate statusUpdate = new StatusUpdate(text);
        Twitter twitter = numeriUser.getTwitter();

        if (statusId != -1)
            statusUpdate.setInReplyToStatusId(statusId);

        new SimpleAsyncTask<List<File>, Void>() {
            @Override
            protected Void doInBackground(List<File> images) {
                if (!images.isEmpty()) {
                    ToastSender.sendToast("画像をアップロードします");
                    long[] medias = new long[images.size()];
                    for (int i = 0; i < images.size(); i++) {
                        try {
                            UploadedMedia uploadedMedia = numeriUser.getTwitter().uploadMedia(images.get(i));
                            medias[i] = uploadedMedia.getMediaId();
                        } catch (TwitterException e) {
                            TwitterExceptionDisplay.show(e);
                            e.printStackTrace();
                        }
                    }
                    statusUpdate.setMediaIds(medias);
                }
                try {
                    twitter.updateStatus(statusUpdate);
                    ToastSender.sendToast("ツイートに成功しました");
                } catch (TwitterException e) {
                    ToastSender.sendToast("ツイートに失敗しました");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //none
            }
        }.execute(images);
    }
}
