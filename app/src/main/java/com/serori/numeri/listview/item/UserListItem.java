package com.serori.numeri.listview.item;


import com.serori.numeri.user.NumeriUser;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import twitter4j.Friendship;
import twitter4j.Relationship;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 */
public class UserListItem {
    @Getter
    private String biggerIconImageUrl = "";
    @Getter
    private String iconImageUrl = "";
    @Getter
    private String userScreenName = "";
    @Getter
    private String userName = "";
    @Getter
    private String bio = "";
    @Getter
    private long userId = -1;
    @Getter
    private final NumeriUser numeriUser;
    private final static ArrayList<OnUpdateRelationshipListener> onUpdateRelationshipListeners = new ArrayList<>();
    @Getter
    private boolean isShowedRelation = false;
    @Getter
    private boolean isMe = false;
    private static volatile Map<String, Relation> relationMap = new LinkedHashMap<>();
    @Getter
    private boolean isProtected;

    public static void startObserveRelation(NumeriUser numeriUser) {
        numeriUser.getStreamEvent()
                .addOnFollowListener((source, followedUser) -> updateFriendShip(numeriUser, source, followedUser))
                .addOnUnFollowListener((source1, unfollowedUser) -> updateFriendShip(numeriUser, source1, unfollowedUser));
    }

    private static void updateFriendShip(NumeriUser numeriUser, User source, User target) {
        try {
            if (source.getScreenName().equals(numeriUser.getScreenName())) {
                Relation relation = relationMap.get(createRelationId(numeriUser, target.getId()));
                if (relation != null) {
                    Relationship relationship = numeriUser.getTwitter().showFriendship(source.getId(), target.getId());
                    relation.setFollow(relationship.isTargetFollowedBySource());
                    relation.setRelationship(convertRelationString(relationship));
                    for (OnUpdateRelationshipListener onUpdateRelationshipListener : onUpdateRelationshipListeners) {
                        onUpdateRelationshipListener.onUpdateRelationship(target.getId(), relation.isFollow(), relation.getRelationship());
                    }
                }
            } else if (target.getScreenName().equals(numeriUser.getScreenName())) {
                Relation relation = relationMap.get(createRelationId(numeriUser, source.getId()));
                if (relation != null) {
                    Relationship relationship = numeriUser.getTwitter().showFriendship(target.getId(), source.getId());
                    relation.setFollow(relationship.isTargetFollowedBySource());
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
        isProtected = user.isProtected();
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


    public boolean isFollow() {
        return relationMap.get(createRelationId(numeriUser, userId)).isFollow();
    }

    public String getRelationship() {
        return relationMap.get(createRelationId(numeriUser, userId)).getRelationship();
    }


    public static void removeOnUpdateRelationshipListener(OnUpdateRelationshipListener listener) {
        onUpdateRelationshipListeners.remove(listener);
    }

    public static void addOnUpdateRelationshipListener(OnUpdateRelationshipListener listener) {
        onUpdateRelationshipListeners.add(listener);
    }


    public static class Relation {
        @Getter
        @Setter
        private String relationship = "";
        @Getter
        @Setter
        private boolean isFollow = false;

        public Relation(String relationship, boolean isFollow) {
            this.relationship = relationship;
            this.isFollow = isFollow;
        }

    }
}

