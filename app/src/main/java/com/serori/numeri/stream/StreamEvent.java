package com.serori.numeri.stream;

import android.util.Log;

import com.serori.numeri.stream.event.OnDeletionNoticeListener;
import com.serori.numeri.stream.event.OnFavoriteListener;
import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.stream.event.OnUnFavoriteListener;
import com.serori.numeri.util.toast.ToastSender;

import java.util.ArrayList;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

/**
 * Streamについてのクラス.
 */

public class StreamEvent implements UserStreamListener, IStreamEvent, StreamSwitcher {

    private TwitterStream userStream;
    private List<OnStatusListener> onStatusListeners = new ArrayList<>();
    private List<OnFavoriteListener> onFavoriteListeners = new ArrayList<>();
    private List<OnUnFavoriteListener> onUnFavoriteListeners = new ArrayList<>();
    private List<OnDeletionNoticeListener> onDeletionNoticeListeners = new ArrayList<>();

    public StreamEvent(TwitterStream stream) {
        stream.addListener(this);
        userStream = stream;
    }

    @Override
    public void startStream() {
        userStream.user();
    }

    public void closeStream() {
        userStream.cleanUp();
    }


    @Override
    public IStreamEvent addOnStatusListener(OnStatusListener listener) {
        onStatusListeners.add(listener);
        Log.v(getClass().toString(), "" + onStatusListeners.size());
        return this;
    }

    @Override
    public void removeOnStatusListener(OnStatusListener listener) {
        onStatusListeners.remove(listener);
    }

    @Override
    public IStreamEvent addOnFavoriteListener(OnFavoriteListener listener) {
        onFavoriteListeners.add(listener);
        return this;
    }

    @Override
    public void removeOnFavoriteListener(OnFavoriteListener listener) {
        onFavoriteListeners.remove(listener);
    }

    @Override
    public IStreamEvent addOnUnFavoriteListener(OnUnFavoriteListener listener) {
        onUnFavoriteListeners.add(listener);
        return this;
    }

    @Override
    public void removeOnUnFavoriteListener(OnUnFavoriteListener listener) {
        onUnFavoriteListeners.remove(listener);
    }

    @Override
    public void addOnDeletionNoticeListener(OnDeletionNoticeListener listener) {
        onDeletionNoticeListeners.add(listener);
    }

    @Override
    public void removeOnDeletionNoticeListener(OnDeletionNoticeListener listener) {
        onDeletionNoticeListeners.remove(listener);
    }

    //以下Streamイベント
    @Override
    public void onDeletionNotice(long directMessageId, long userId) {

    }

    @Override
    public void onFriendList(long[] friendIds) {

    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
        for (OnFavoriteListener onFavoriteListener : onFavoriteListeners) {
            onFavoriteListener.onFavorite(source, target, favoritedStatus);
        }
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
        for (OnUnFavoriteListener onUnFavoriteListener : onUnFavoriteListeners) {
            onUnFavoriteListener.onUnfavorite(source, target, unfavoritedStatus);
        }
    }

    @Override
    public void onFollow(User source, User followedUser) {

    }

    @Override
    public void onUnfollow(User source, User unfollowedUser) {

    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {

    }

    @Override
    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

    }

    @Override
    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

    }

    @Override
    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

    }

    @Override
    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

    }

    @Override
    public void onUserListCreation(User listOwner, UserList list) {

    }

    @Override
    public void onUserListUpdate(User listOwner, UserList list) {

    }

    @Override
    public void onUserListDeletion(User listOwner, UserList list) {

    }

    @Override
    public void onUserProfileUpdate(User updatedUser) {

    }

    @Override
    public void onBlock(User source, User blockedUser) {

    }

    @Override
    public void onUnblock(User source, User unblockedUser) {

    }

    @Override
    public void onStatus(Status status) {
        for (OnStatusListener onStatusListener : onStatusListeners) {
            onStatusListener.onStatus(status);
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        for (OnDeletionNoticeListener onDeletionNoticeListener : onDeletionNoticeListeners) {
            onDeletionNoticeListener.onDeletionNotice(statusDeletionNotice);
            ToastSender.sendToast("destroyTweet " + statusDeletionNotice.getStatusId());
        }
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    public void onStallWarning(StallWarning warning) {

    }

    @Override
    public void onException(Exception ex) {

    }
}