package com.serori.numeri.userprofile;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.activity.SubsidiaryActivity;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.fragment.FavoriteTweetsFragment;
import com.serori.numeri.fragment.FollowUserListFragment;
import com.serori.numeri.fragment.FollowerUserListFragment;
import com.serori.numeri.fragment.UserInfoPagerAdapter;
import com.serori.numeri.fragment.UserPublicTimeLineFragment;
import com.serori.numeri.imageview.NumeriImageView;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.IDs;
import twitter4j.Relationship;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 *
 */
public class UserInformationActivity extends SubsidiaryActivity implements ViewPager.OnPageChangeListener {
    private static long userId = -1;
    private static String screenName = "";
    private static NumeriUser numeriUser;
    private User user = null;
    private boolean isBlocking = false;
    private boolean isMuted = false;
    private Handler handler = new Handler();

    private Button followButton;
    private TextView relationIndicator;
    private ViewPager viewPager;
    private UserInfoPagerAdapter userInfoPagerAdapter;
    private List<Button> buttonList = new ArrayList<>();
    private List<Button> buttonList2 = new ArrayList<>();
    private Button showTweetButton;
    private Button showFavoriteButton;
    private Button showFollowButton;
    private Button showFollowerButton;
    private Button tweetNum;
    private Button favoriteNum;
    private Button followNum;
    private Button followerNum;

    public static void show(Context context, long id, NumeriUser numeriUser) {
        if ((context instanceof NumeriActivity)) {
            Log.v("UserInformationActivity", "show");
            userId = id;
            screenName = "";
            UserInformationActivity.numeriUser = numeriUser;
            ((NumeriActivity) context).startActivity(UserInformationActivity.class, false);
        }
    }

    public static void show(Context context, String screenName, NumeriUser numeriUser) {
        if ((context instanceof NumeriActivity)) {
            Log.v("UserInformationActivity", "show");
            userId = -1;
            UserInformationActivity.screenName = screenName;
            UserInformationActivity.numeriUser = numeriUser;
            ((NumeriActivity) context).startActivity(UserInformationActivity.class, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        if (userId == -1 && screenName.equals("")) {
            throw new IllegalStateException("userIdとscreenNameのどちらも設定されていません");
        }
        if (savedInstanceState == null) {
            init();
            initTabAction();
        }

    }


    private void setCurrentItem(int item) {
        if (userInfoPagerAdapter.getCount() > item) {
            viewPager.setCurrentItem(item);
        }
    }

    private void init() {
        boolean isDarkTheme = ConfigurationStorager.EitherConfigurations.DARK_THEME.isEnabled();
        String textColor = isDarkTheme ? "#FFFFFF" : "#000000";
        NumeriImageView iconImage = (NumeriImageView) findViewById(R.id.userInfoIcon);
        TextView screenName = (TextView) findViewById(R.id.userInfoScreenName);
        TextView userName = (TextView) findViewById(R.id.userInfoName);
        followButton = (Button) findViewById(R.id.userInfoFollowButton);
        relationIndicator = (TextView) findViewById(R.id.userInfoRelationIndicator);
        ImageView isProtectedImage = (ImageView) findViewById(R.id.userInfoProtectedKey);
        TextView bio = (TextView) findViewById(R.id.BIO);
        TextView location = (TextView) findViewById(R.id.userInfoLocation);
        tweetNum = (Button) findViewById(R.id.userInfoTweetNum);
        favoriteNum = (Button) findViewById(R.id.userInfoFavoriteNum);
        followNum = (Button) findViewById(R.id.userInfoFollowNum);
        followerNum = (Button) findViewById(R.id.userInfoFollowerNum);
        buttonList2.add(tweetNum);
        buttonList2.add(favoriteNum);
        buttonList2.add(followNum);
        buttonList2.add(followerNum);
        showTweetButton = (Button) findViewById(R.id.userInfoShowTweetButton);
        showFavoriteButton = (Button) findViewById(R.id.userInfoShowFavoriteButton);
        showFollowButton = (Button) findViewById(R.id.userInfoShowFollowButton);
        showFollowerButton = (Button) findViewById(R.id.userInfoShowFollowerButton);
        buttonList.add(showTweetButton);
        buttonList.add(showFavoriteButton);
        buttonList.add(showFollowButton);
        buttonList.add(showFollowerButton);
        for (Button button : buttonList) {
            button.setTextColor(Color.parseColor(textColor));
        }
        new Thread(() -> {
            try {
                if (userId != -1) {
                    user = numeriUser.getTwitter().showUser(userId);
                } else {
                    user = numeriUser.getTwitter().showUser(UserInformationActivity.screenName);
                    userId = user.getId();
                }
                handler.post(() -> {
                    iconImage.setImage(true, NumeriImageView.ProgressType.LOAD_ICON, user.getBiggerProfileImageURL());
                    screenName.setText(user.getScreenName());
                    userName.setText(user.getName());
                    if (user.isProtected()) isProtectedImage.setVisibility(View.VISIBLE);
                    bio.setText(user.getDescription());
                    location.setText(user.getLocation());
                    tweetNum.setText("" + user.getStatusesCount());
                    favoriteNum.setText("" + user.getFavouritesCount());
                    followNum.setText("" + user.getFriendsCount());
                    followerNum.setText("" + user.getFollowersCount());
                    if (!(user.getId() == numeriUser.getAccessToken().getUserId())) {
                        setRelationShip(user);
                    } else {
                        relationIndicator.setText("自分");
                    }
                    initFragments();
                    initFollowRequestComponent();
                });

            } catch (TwitterException e) {
                e.printStackTrace();
                TwitterExceptionDisplay.show(e);
            }
        }).start();
    }

    private void initFragments() {
        viewPager = (ViewPager) findViewById(R.id.userInfoViewPager);
        userInfoPagerAdapter = new UserInfoPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setOnPageChangeListener(this);
        UserPublicTimeLineFragment timeLineFragment = new UserPublicTimeLineFragment();
        timeLineFragment.setNumeriUser(numeriUser);
        timeLineFragment.setUserId(userId);
        FavoriteTweetsFragment favoriteTweetsFragment = new FavoriteTweetsFragment();
        favoriteTweetsFragment.setNumeriUser(numeriUser);
        favoriteTweetsFragment.setUserId(userId);
        FollowUserListFragment followUserListFragment = new FollowUserListFragment();
        followUserListFragment.setNumeriUser(numeriUser);
        followUserListFragment.setUserId(userId);
        FollowerUserListFragment followerUserListFragment = new FollowerUserListFragment();
        followerUserListFragment.setNumeriUser(numeriUser);
        followerUserListFragment.setUserId(userId);

        userInfoPagerAdapter.add(timeLineFragment);
        userInfoPagerAdapter.add(favoriteTweetsFragment);
        userInfoPagerAdapter.add(followUserListFragment);
        userInfoPagerAdapter.add(followerUserListFragment);
        viewPager.setAdapter(userInfoPagerAdapter);
    }

    private void initTabAction() {
        showTweetButton.setOnClickListener(v -> setCurrentItem(0));
        tweetNum.setOnClickListener(v -> setCurrentItem(0));
        showFavoriteButton.setOnClickListener(v -> setCurrentItem(1));
        favoriteNum.setOnClickListener(v -> setCurrentItem(1));
        showFollowButton.setOnClickListener(v -> setCurrentItem(2));
        followNum.setOnClickListener(v -> setCurrentItem(2));
        showFollowerButton.setOnClickListener(v -> setCurrentItem(3));
        followerNum.setOnClickListener(v -> setCurrentItem(3));
    }


    private void updateRelationshipIndicator(User user, Relationship relationship) {
        if (relationship != null) {
            if (relationship.isTargetFollowedBySource()) {
                followButton.setText("フォロー解除");
                followButton.setBackgroundColor(getResources().getColor(R.color.un_follow_color));
            } else {
                followButton.setText(user.isProtected() ? "フォローリクエスト" : "フォローする");
                followButton.setBackgroundColor(getResources().getColor(R.color.follow_color));
            }
            if (relationship.isTargetFollowedBySource() && relationship.isTargetFollowingSource()) {
                relationIndicator.setText("相互");
            } else if (relationship.isTargetFollowedBySource()) {
                relationIndicator.setText("片思い");
            } else if (relationship.isTargetFollowingSource()) {
                relationIndicator.setText("片思われ");
            } else {
                if (relationship.isSourceBlockingTarget()) {
                    relationIndicator.setText("ブロック中");
                    followButton.setText("");
                    followButton.setBackgroundColor(getResources().getColor(R.color.not_selected_color));
                    followButton.setOnClickListener(null);
                    return;
                } else {
                    relationIndicator.setText("無関心");
                }
            }
            followButton.setOnClickListener(v -> updateFriendship(user, relationship));
            if (relationship.isSourceMutingTarget()) {
                relationIndicator.setText(relationIndicator.getText() + "\n" + "ミュート中");
            }
        }
    }

    private void updateFriendship(User user, Relationship relationship) {
        new Thread(() -> {
            try {
                if (!relationship.isSourceBlockingTarget()) {
                    if (relationship.isTargetFollowedBySource()) {
                        numeriUser.getTwitter().destroyFriendship(user.getId());
                    } else {
                        numeriUser.getTwitter().createFriendship(user.getId());
                    }
                    followButton.setOnClickListener(null);
                }
            } catch (TwitterException e) {
                TwitterExceptionDisplay.show(e);
            }
            setRelationShip(user);
        }).start();
    }

    private void setRelationShip(User user) {
        new Thread(() -> {
            try {
                Relationship relationship = numeriUser.getTwitter().showFriendship(numeriUser.getAccessToken().getUserId(), user.getId());
                handler.post(() -> {
                    updateRelationshipIndicator(user, relationship);
                    isBlocking = relationship.isSourceBlockingTarget();
                    isMuted = relationship.isSourceMutingTarget();
                });
            } catch (TwitterException e) {
                e.printStackTrace();
                TwitterExceptionDisplay.show(e);
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (numeriUser.getAccessToken().getUserId() != userId)
            getMenuInflater().inflate(R.menu.menu_user_information, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_block:
                block();
                return true;
            case R.id.action_mute:
                mute();
                return true;
            case R.id.action_report_spam:
                spamBlock();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void finish() {
        userId = -1;
        screenName = "";
        super.finish();
    }

    private void initFollowRequestComponent() {
        if (userId == numeriUser.getAccessToken().getUserId()) {
            LinearLayout followRequest = (LinearLayout) findViewById(R.id.followRequest);
            TextView followRequestNumText = (TextView) findViewById(R.id.followRequestNum);
            Handler handler = new Handler();
            new Thread(() -> {
                long nextCursor = -1;
                List<IDs> iDsList = new ArrayList<>();
                List<Long> followRequestUserIds = new ArrayList<>();
                while (nextCursor != 0) {
                    try {
                        IDs iDs = numeriUser.getTwitter().getIncomingFriendships(nextCursor);
                        iDsList.add(iDs);
                        nextCursor = iDs.getNextCursor();
                    } catch (TwitterException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                for (IDs iDs : iDsList) {
                    for (long id : iDs.getIDs()) {
                        followRequestUserIds.add(id);
                    }
                }
                int followRequestNum = followRequestUserIds.size();
                handler.post(() -> {
                    followRequestNumText.setText("" + followRequestNum);
                    followRequest.setVisibility(followRequestNum > 0 ? View.VISIBLE : View.GONE);
                });
            }).start();
        }
    }

    private void block() {
        if (user != null) {
            String message = !isBlocking ? "このユーザーをブッロクしますか？" : "このユーザーをブロック解除しますか？";
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(message).setPositiveButton("はい", (dialog, which) -> {
                        new Thread(() -> {
                            try {
                                if (!isBlocking) {
                                    numeriUser.getTwitter().createBlock(user.getId());
                                } else {
                                    numeriUser.getTwitter().destroyBlock(user.getId());
                                }
                                setRelationShip(user);
                            } catch (TwitterException e) {
                                TwitterExceptionDisplay.show(e);
                            }
                        }).start();

                    }).setNegativeButton("キャンセル", null).create();
            setCurrentShowDialog(alertDialog);
        }
    }

    private void mute() {
        if (user != null) {
            String message = !isMuted ? "このユーザーをミュートしますか？" : "このユーザーをミュート解除しますか？";
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(message).setPositiveButton("はい", (dialog, which) -> {
                        new Thread(() -> {
                            try {
                                if (!isMuted) {
                                    numeriUser.getTwitter().createMute(user.getId());
                                } else {
                                    numeriUser.getTwitter().destroyMute(user.getId());
                                }
                                setRelationShip(user);
                            } catch (TwitterException e) {
                                TwitterExceptionDisplay.show(e);
                            }
                        }).start();
                    }).setNegativeButton("キャンセル", null).create();
            setCurrentShowDialog(alertDialog);
        }
    }

    private void spamBlock() {
        if (user != null) {
            String message = "このユーザーをスパム報告しますか？";
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(message).setPositiveButton("はい", (dialog, which) -> {
                        new Thread(() -> {
                            try {
                                numeriUser.getTwitter().reportSpam(user.getId());
                                setRelationShip(user);
                            } catch (TwitterException e) {
                                TwitterExceptionDisplay.show(e);
                            }
                        }).start();
                    }).setNegativeButton("キャンセル", null).create();
            setCurrentShowDialog(alertDialog);
        }
    }

    //以下ViewPagerイベント
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < buttonList.size(); i++) {
            if (position == i) {
                Log.v(toString(), "onPageSelected" + position);
                buttonList.get(i).setBackgroundColor(getResources().getColor(R.color.selected_color));
                buttonList2.get(i).setBackgroundColor(getResources().getColor(R.color.selected_color));
            } else {
                buttonList.get(i).setBackgroundColor(getResources().getColor(R.color.not_selected_color));
                buttonList2.get(i).setBackgroundColor(getResources().getColor(R.color.not_selected_color));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
