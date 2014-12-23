package com.serori.numeri.stream;

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

public class StreamEvent implements UserStreamListener, IStreamEvent, StreamOwner {

    public StreamEvent() {
    }

    private TwitterStream userStream;
    private List<OnStatusListener> onStatusListeners = new ArrayList<>();
    private List<OnFavoriteListener> onFavoriteListeners = new ArrayList<>();

    public void setUserStream(TwitterStream stream) {
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
    public void addOwnerOnStatusListener(OnStatusListener listener) {
        onStatusListeners.add(listener);
    }

    @Override
    public void addOwnerOnfavoriteListener(OnFavoriteListener listener) {
        onFavoriteListeners.add(listener);
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
