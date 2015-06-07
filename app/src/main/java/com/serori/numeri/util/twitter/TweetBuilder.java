package com.serori.numeri.util.twitter;

import com.serori.numeri.user.NumeriUser;

import java.io.File;
import java.util.List;


public class TweetBuilder implements ITweetBuilder {
    private Tweet tweet = new Tweet();

    public TweetBuilder(NumeriUser numeriUser) {
        if (numeriUser == null) throw new NullPointerException("コンストラクターにnullを渡されました");
        tweet.setNumeriUser(numeriUser);
    }

    @Override
    public TweetBuilder setText(String text) {
        tweet.setText(text);
        return this;
    }

    @Override
    public TweetBuilder addImages(List<File> images) {
        if (images.size() > 4)
            throw new IllegalArgumentException("セットできるリストの長さは4までです");
        tweet.setImages(images);
        return this;
    }

    @Override
    public TweetBuilder setReplyDestinationId(long statusId) {
        tweet.setStatusId(statusId);
        return this;
    }

    @Override
    public Tweet create() {
        return tweet;
    }


}
