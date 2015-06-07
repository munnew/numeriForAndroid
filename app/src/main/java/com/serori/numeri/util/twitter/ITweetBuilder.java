package com.serori.numeri.util.twitter;

import java.io.File;
import java.util.List;

/**
 */
public interface ITweetBuilder {
    /**
     * ツイートするテキストをセットする
     *
     * @param text ツイートするテキスト
     * @return 自身のインスタンス
     */
    TweetBuilder setText(String text);

    /**
     * 投稿する画像をセットする
     *
     * @param images サイズが4以下のリスト
     * @return 自身のインスタンス
     */
    TweetBuilder addImages(List<File> images);

    /**
     * リプライ先のStatusIdをセットする
     *
     * @param statusId statusId
     * @return 自身のインスタンス
     */
    TweetBuilder setReplyDestinationId(long statusId);

    /**
     * @return Tweet ツイート可能なTweetオブジェクト
     */
    Tweet create();
}
