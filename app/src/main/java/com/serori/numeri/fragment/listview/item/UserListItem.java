package com.serori.numeri.fragment.listview.item;


import com.serori.numeri.main.Global;
import com.serori.numeri.user.NumeriUser;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import twitter4j.Friendship;
import twitter4j.Relationship;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 */
public class UserListItem {
    private String biggerIconImageUrl = "";
    private String iconImageUrl = "";
    private String userScreenName = "";
    private String userName = "";
    private String bio = "";
    private long userId = -1;
    private NumeriUser numeriUser = null;
    private static ArrayList<OnUpdateRelationshipListener> onUpdateRelationshipListeners = new ArrayList<>();
    private boolean isShowedRelation = false;
    private boolean isMe = false;
    private static volatile Map<String, Relation> relationMap = new LinkedHashMap<>();
    private static boolean observeRelationStarted = false;

    public static void startObserveRelation() {
        if (!observeRelationStarted) {
            observeRelationStarted = true;
            for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
                numeriUser.getStreamEvent()
                        .addOnFollowListener((source, followedUser) -> updateFriendShip(numeriUser, source, followedUser))
                        .addOnUnFollowListener((source1, unfollowedUser) -> updateFriendShip(numeriUser, source1, unfollowedUser));
            }
        }
    }

    private static void updateFriendShip(NumeriUser numeriUser, User source, User target) {
        try {
            if (source.getScreenName().equals(numeriUser.getScreenName())) {
                Relation relation = relationMap.get(createRelationId(numeriUser, target.getId()));
                if (relation != null) {
                    Relationship relationship = numeriUser.getTwitter().showFriendship(source.getId(), target.getId());
                    relation.setIsFollow(relationship.isTargetFollowedBySource());
                    relation.setRelationship(convertRelationString(relationship));
                    for (OnUpdateRelationshipListener onUpdateRelationshipListener : onUpdateRelationshipListeners) {
                        onUpdateRelationshipListener.onUpdateRelationship(target.getId(), relation.isFollow(), relation.getRelationship());
                    }
                }
            } else if (target.getScreenName().equals(numeriUser.getScreenName())) {
                Relation relation = relationMap.get(createRelationId(numeriUser, source.getId()));
                if (relation != null) {
                    Relationship relationship = numeriUser.getTwitter().showFriendship(target.getId(), source.getId());
                    relation.setIsFollow(relationship.isTargetFollowedBySource());
                    relation.setRelationship(convertRelationString(relationship));
                    for (OnUpdateRelationshipListener onUpdateRelationshipListener : onUpdateRelationshipListeners) {
                        onUpdateRelationshipListener.onUpdateRelationship(source.getId(), relation.isFollow(), relation.getRelationship());
                    }

                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    private static String createRelationId(NumeriUser numeriUser, long targetId) {
        return numeriUser.getScreenName() + targetId;
    }

    public UserListItem(User user, NumeriUser numeriUser) {
        this.numeriUser = numeriUser;
        biggerIconImageUrl = user.getBiggerProfileImageURL();
        iconImageUrl = user.getProfileImageURL();
        userScreenName = user.getScreenName();
        userName = user.getName();
        userId = user.getId();
        bio = user.getDescription();

        if (userId == numeriUser.getAccessToken().getUserId()) {
            if (!onUpdateRelationshipListeners.isEmpty()) {
                String relation = "自分";
                for (OnUpdateRelationshipListener onUpdateRelationshipListener : onUpdateRelationshipListeners) {
                    onUpdateRelationshipListener.onUpdateRelationship(userId, false, relation);
                }
                isMe = true;
                isShowedRelation = true;
            }
        }
        if (relationMap.get(createRelationId(numeriUser, userId)) != null) isShowedRelation = true;
    }

    public void setRelationship(Friendship friendship) {
        if (friendship != null) {
            boolean isFollowing = friendship.isFollowing();
            boolean isFollowedBy = friendship.isFollowedBy();
            String relation;
            if (isFollowing && isFollowedBy) {
                relation = "相互";
            } else if (isFollowedBy) {
                relation = "片思われ";
            } else if (isFollowing) {
                relation = "片思い";
            } else {
                relation = "無関心";
            }
            relationMap.put(createRelationId(numeriUser, userId), new Relation(relation, isFollowing));

            for (OnUpdateRelationshipListener onUpdateRelationshipListener : onUpdateRelationshipListeners) {
                onUpdateRelationshipListener.onUpdateRelationship(userId, isFollowing, relation);
            }
            isShowedRelation = true;
        }
    }

    private static String convertRelationString(Relationship relationship) {
        String relation = "";

        if (relationship != null) {
            if (relationship.isTargetFollowedBySource() && relationship.isTargetFollowingSource()) {
                relation = "相互";
            } else if (relationship.isTargetFollowedBySource()) {
                relation = "片思い";
            } else if (relationship.isTargetFollowingSource()) {
                relation = "片思われ";
            } else {
                relation = "無関心";
            }
        }
        return relation;
    }

    public String getBiggerIconImageUrl() {
        return biggerIconImageUrl;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserId() {
        return userId;
    }

    public String getBio() {
        return bio;
    }

    public boolean isFollow() {
        return relationMap.get(createRelationId(numeriUser, userId)).isFollow();
    }

    public String getRelationship() {
        return relationMap.get(createRelationId(numeriUser, userId)).getRelationship();
    }

    public boolean isShowedRelation() {
        return isShowedRelation;
    }

    public NumeriUser getNumeriUser() {
        return numeriUser;
    }

    public boolean isMe() {
        return isMe;
    }

    public static void removeOnUpdateRelationshipListener(OnUpdateRelationshipListener listener) {
        onUpdateRelationshipListeners.remove(listener);
    }

    public static void addOnUpdateRelationshipListener(OnUpdateRelationshipListener listener) {
        onUpdateRelationshipListeners.add(listener);
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public static class Relation {
        private String relationship = "";
        private boolean isFollow = false;

        public Relation(String relationship, boolean isFollow) {
            this.relationship = relationship;
            this.isFollow = isFollow;
        }

        public String getRelationship() {
            return relationship;
        }

        public void setRelationship(String relationship) {
            this.relationship = relationship;
        }

        public boolean isFollow() {
            return isFollow;
        }

        public void setIsFollow(boolean isFollow) {
            this.isFollow = isFollow;
        }
    }
}

