package com.serori.numeri.stream;

import android.util.Log;

import com.serori.numeri.stream.event.OnStatusDeletionNoticeListener;
import com.serori.numeri.stream.event.OnFavoriteListener;
import com.serori.numeri.stream.event.OnFollowListener;
import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.stream.event.OnUnFavoriteListener;
import com.serori.numeri.stream.event.OnUnFollowListener;
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

public final class StreamEvent implements UserStreamListener, IStreamEvent, StreamSwitcher {

    private final TwitterStream userStream;
    private final List<OnStatusListener> onStatusListeners = new ArrayList<>();
    private final List<OnFavoriteListener> onFavoriteListeners = new ArrayList<>();
    private final List<OnUnFavoriteListener> onUnFavoriteListeners = new ArrayList<>();
    private final List<OnStatusDeletionNoticeListener> onStatusDeletionNoticeListeners = new ArrayList<>();
    private final List<OnFollowListener> onFollowListeners = new ArrayList<>();
    private final List<OnUnFollowListener> onUnFollowListeners = new ArrayList<>();

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
    public IStreamEvent addOnStatusDeletionNoticeListener(OnStatusDeletionNoticeListener listener) {
        onStatusDeletionNoticeListeners.add(listener);
        return this;
    }

    @Override
    public IStreamEvent addOnFollowListener(OnFollowListener listener) {
        onFollowListeners.add(listener);
        return this;
    }

    @Override
    public IStreamEvent addOnUnFollowListener(OnUnFollowListener listener) {
        onUnFollowListeners.add(listener);
        return this;
    }

    @Override
    public IStreamEvent addOnFavoriteListener(OnFavoriteListener listener) {
        onFavoriteListeners.add(listener);
        return this;
    }


    @Override
    public IStreamEvent addOnUnFavoriteListener(OnUnFavoriteListener listener) {
        onUnFavoriteListeners.add(listener);
        return this;
    }

    @Override
    public void removeOnStatusListener(OnStatusListener listener) {
        onStatusListeners.remove(listener);
    }

    @Override
    public void removeOnFavoriteListener(OnFavoriteListener listener) {
        onFavoriteListeners.remove(listener);
    }

    @Override
    public void removeOnUnFavoriteListener(OnUnFavoriteListener listener) {
        onUnFavoriteListeners.remove(listener);
    }

    @Override
    public void removeOnDeletionNoticeListener(OnStatusDeletionNoticeListener listener) {
        onStatusDeletionNoticeListeners.remove(listener);
    }

    @Override
    public void removeOnFollowListener(OnFollowListener listener) {
        onFollowListeners.remove(listener);
    }

    @Override
    public void removeOnUnFollowListener(OnUnFollowListener listener) {
        onUnFollowListeners.remove(listener);
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
        List<OnFavoriteListener> onFavoriteListeners1 = new ArrayList<>();
        onFavoriteListeners1.addAll(onFavoriteListeners);
        for (OnFavoriteListener onFavoriteListener : onFavoriteListeners1) {
            onFavoriteListener.onFavorite(source, target, favoritedStatus);
        }
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
        List<OnUnFavoriteListener> onUnFavoriteListeners1 = new ArrayList<>();
        onUnFavoriteListeners1.addAll(onUnFavoriteListeners);
        for (OnUnFavoriteListener onUnFavoriteListener : onUnFavoriteListeners1) {
            onUnFavoriteListener.onUnfavorite(source, target, unfavoritedStatus);
        }
    }

    @Override
    public void onFollow(User source, User followedUser) {
        List<OnFollowListener> onFollowListeners1 = new ArrayList<>();
        onFollowListeners1.addAll(onFollowListeners);
        for (OnFollowListener onFollowListener : onFollowListeners1) {
            onFollowListener.onFollow(source, followedUser);
        }
    }

    @Override
    public void onUnfollow(User source, User unfollowedUser) {
        List<OnUnFollowListener> onUnFollowListeners1 = new ArrayList<>();
        onUnFollowListeners1.addAll(onUnFollowListeners);
        for (OnUnFollowListener onUnFollowListener : onUnFollowListeners1) {
            onUnFollowListener.onUnFollow(source, unfollowedUser);
        }
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
        List<OnStatusListener> onStatusListeners1 = new ArrayList<>();
        onStatusListeners1.addAll(onStatusListeners);
        for (OnStatusListener onStatusListener : onStatusListeners1) {
            onStatusListener.onStatus(status);
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        List<OnStatusDeletionNoticeListener> onStatusDeletionNoticeListeners1 = new ArrayList<>();
        onStatusDeletionNoticeListeners1.addAll(onStatusDeletionNoticeListeners);
        for (OnStatusDeletionNoticeListener onStatusDeletionNoticeListener : onStatusDeletionNoticeListeners1) {
            onStatusDeletionNoticeListener.onStatusDeletionNotice(statusDeletionNotice);
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