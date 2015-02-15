package com.serori.numeri.userprofile;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.imageview.NumeriImageView;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.async.AsyncTaskUtil;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Friendship;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 *
 */
public class UserInformationActivity extends NumeriActivity implements ViewPager.OnPageChangeListener {
    private static long userId;
    private static NumeriUser numeriUser;

    private Relationship relationship;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        ViewPager viewPager = (ViewPager) findViewById(R.id.userInfoViewPager);
        UserInfoPagerAdapter userInfoPagerAdapter = new UserInfoPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(userInfoPagerAdapter);
        if (savedInstanceState == null) {
            init();
        }

    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @SuppressWarnings("unchecked")
    private void init() {
        NumeriImageView iconImage = (NumeriImageView) findViewById(R.id.userInfoIcon);
        TextView screenName = (TextView) findViewById(R.id.userInfoScreenName);
        TextView userName = (TextView) findViewById(R.id.userInfoName);
        Button followButtton = (Button) findViewById(R.id.userInfoFollowButton);
        TextView relationIndicator = (TextView) findViewById(R.id.userInfoRelationIndicator);
        ImageView isProtectedImage = (ImageView) findViewById(R.id.userInfoProtectedKey);
        TextView bio = (TextView) findViewById(R.id.BIO);
        TextView location = (TextView) findViewById(R.id.userInfoLocation);
        TextView tweetNum = (TextView) findViewById(R.id.userInfoTweetNum);
        TextView favoriteNum = (TextView) findViewById(R.id.userInfoFavoriteNum);
        TextView followNum = (TextView) findViewById(R.id.userInfoFollowNum);
        TextView followerNum = (TextView) findViewById(R.id.userInfoFollowerNum);

        List<Object> UserInfoObjects = new ArrayList<>();


        AsyncTaskUtil<Twitter, List<Object>> task = new AsyncTaskUtil<>(numeriUser.getTwitter());
        task.setBackgroundRunnable(twitter -> {
            try {
                UserInfoObjects.add(twitter.showUser(userId));
                UserInfoObjects.add(twitter.showFriendship(numeriUser.getAccessToken().getUserId(), userId));
                return UserInfoObjects;
            } catch (TwitterException e) {
                e.printStackTrace();
                ToastSender.sendToast("ユーザー情報の取得に失敗しましました");
            }
            return null;
        });
        task.setOnPostUiThreadRunnable(objects1 -> {
            if (objects1 != null) {
                User user = (User) objects1.get(0);
                Relationship relationship = (Relationship) objects1.get(1);

                iconImage.startLoadImage(NumeriImageView.ProgressType.LOAD_ICON, user.getBiggerProfileImageURL());

                screenName.setText(user.getScreenName());
                userName.setText(user.getName());
                if (user.isProtected()) isProtectedImage.setVisibility(View.VISIBLE);
                if (relationship.isTargetFollowedBySource()) {
                    followButtton.setText("フォロー解除");
                    followButtton.setBackgroundColor(Color.parseColor(getString(R.string.un_follow_color)));
                } else {
                    followButtton.setText("フォロー");
                    followButtton.setBackgroundColor(Color.parseColor(getString(R.string.follow_color)));
                }
                if (relationship.isTargetFollowedBySource() && relationship.isTargetFollowingSource()) {
                    relationIndicator.setText("相互");
                } else if (relationship.isTargetFollowedBySource() && !relationship.isTargetFollowingSource()) {
                    relationIndicator.setText("片思い");
                } else if (!relationship.isTargetFollowedBySource() && relationship.isTargetFollowingSource()) {
                    relationIndicator.setText("片思われ");
                } else {
                    if (relationship.isSourceBlockingTarget())
                        relationIndicator.setText("ブロック中");
                    relationIndicator.setText("無関心");
                }
                bio.setText(user.getDescription());
                location.setText(user.getLocation());
                tweetNum.setText("" + user.getStatusesCount());
                favoriteNum.setText("" + user.getFavouritesCount());
                followNum.setText("" + user.getFriendsCount());
                followerNum.setText("" + user.getFollowersCount());
            }
        });
        task.execute();
    }


    public static void setUserId(long id) {
        userId = id;
    }

    public static void setNumeriUser(NumeriUser numeriUser) {
        UserInformationActivity.numeriUser = numeriUser;
    }


    //以下ViewPagerイベント
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
