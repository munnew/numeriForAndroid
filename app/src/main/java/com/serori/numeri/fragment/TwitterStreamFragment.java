package com.serori.numeri.fragment;

import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 */
public abstract class TwitterStreamFragment extends NumeriFragment implements OnStatusListener {


    abstract protected void onTweetStats(Status status);

    /**
     * ResponseListを取得する
     * 取得した際にリストの先頭のステータスを削除する必要がある場合topDeleteにtrue
     *
     * @param paging Paging
     * @param topDelete 0番目のstatusを削除
     * @return  ResponseList<Status>
     */
    protected final ResponseList<Status> getTweets(Paging paging, boolean topDelete) {
        ResponseList<Status> statuses = null;
        try {
            statuses = getResponseList(paging);
            if (!statuses.isEmpty() && topDelete) {
                statuses.remove(0);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
            TwitterExceptionDisplay.show(e);
        }
        return statuses;
    }

    /**
     * getTweetsで取得するレスポンスリストの読み込みの処理
     * 例) getNumeriUser().getTwitter().getHomeTimeline(paging);
     *
     * @param paging Paging
     * @return ResponseList<Status>
     * @throws TwitterException
     */
    abstract ResponseList<Status> getResponseList(Paging paging) throws TwitterException;


    @Override
    public void onStatus(Status status) {
        onTweetStats(status);
    }

    @Override
    public void onDestroy() {
        NumeriUser numeriUser = getNumeriUser();
        if (numeriUser != null)
            getNumeriUser().getStreamEvent().removeOnStatusListener(this);
        super.onDestroy();
    }
}
