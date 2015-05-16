package com.serori.numeri.twitter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.main.Global;
import com.serori.numeri.stream.event.OnStatusListener;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.util.twitter.TweetBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * TweetActivity
 */
public class TweetActivity extends NumeriActivity implements TextWatcher {
    private EditText tweetEditText;
    private TextView remainingTextView;
    private Button tweetButton;
    private TextView backgroundTimeLine;
    private TextView currentUserTextView;
    private InputMethodManager inputMethodManager;
    private static long destinationStatusId = -1;
    private static String tweetText = "";
    private static List<String> destinationUserNames = new ArrayList<>();
    private static NumeriUser currentNumeriUser = null;
    private List<File> appendedImages = new ArrayList<>();
    private LinearLayout appendedImageViews;
    private Map<String, OnStatusListener> onStatusListeners = new LinkedHashMap<>();

    public static void replyTweet(Context activityContext, NumeriUser numeriUser, SimpleTweetStatus destinationTweetStatus) {
        if (!(activityContext instanceof NumeriActivity)) return;
        currentNumeriUser = numeriUser;
        destinationUserNames.addAll(destinationTweetStatus.getDestinationUserNames());
        if (!destinationTweetStatus.isRT()) {
            destinationStatusId = destinationTweetStatus.getStatusId();
        } else {
            destinationStatusId = destinationTweetStatus.getRetweetedStatusId();
        }
        ((NumeriActivity) activityContext).startActivity(TweetActivity.class, false);
    }

    public static void quoteRetweet(Context activityContext, NumeriUser numeriUser, SimpleTweetStatus quotedTweetStatus) {
        if (!(activityContext instanceof NumeriActivity)) return;
        currentNumeriUser = numeriUser;
        tweetText = "QT " + quotedTweetStatus.getScreenName() + " >" + quotedTweetStatus.getMainText() + " : ";
        ((NumeriActivity) activityContext).startActivity(TweetActivity.class, false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        if (savedInstanceState == null) {
            Button changeUserButton = (Button) findViewById(R.id.changeUser);
            tweetEditText = (EditText) findViewById(R.id.tweetEditText);
            remainingTextView = (TextView) findViewById(R.id.remaining);
            remainingTextView.setText("140");
            tweetButton = (Button) findViewById(R.id.sendTweet);
            Button addImageButton = (Button) findViewById(R.id.addImageButton);
            currentUserTextView = (TextView) findViewById(R.id.currentUser);
            backgroundTimeLine = (TextView) findViewById(R.id.backgroundTimeLine);
            appendedImageViews = (LinearLayout) findViewById(R.id.appendedImages);

            NumeriUser defaultUser = Global.getInstance().getNumeriUsers().getNumeriUsers().get(0);
            currentNumeriUser = currentNumeriUser == null ? defaultUser : currentNumeriUser;
            String currentTweetUserName = currentNumeriUser == null ? Global.getInstance().getNumeriUsers().getNumeriUsers().get(0).getScreenName() : currentNumeriUser.getScreenName();

            currentUserTextView.setText(currentTweetUserName);
            inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

            //OnStatus
            for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
                onStatusListeners.put(numeriUser.getScreenName(), status -> {
                    String owner = status.getUser().getScreenName();
                    String text = status.getText();
                    runOnUiThread(() -> backgroundTimeLine.setText(owner + " : " + text));
                });
                numeriUser.getStreamEvent().addOnStatusListener(onStatusListeners.get(numeriUser.getScreenName()));
            }

            tweetEditText.setOnClickListener(v -> inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED));
            tweetEditText.addTextChangedListener(this);
            tweetButton.setOnClickListener(v -> sendTweet(currentNumeriUser));
            changeUserButton.setOnClickListener(v -> createChangeUserDialog());
            addImageButton.setOnClickListener(v -> {
                appendedImage();
                inputMethodManager.hideSoftInputFromWindow(tweetEditText.getWindowToken(), 0);
            });
        }

        if (destinationStatusId != -1) setUserNames();
        if (!tweetText.isEmpty()) {
            tweetEditText.setText(tweetText);
            tweetEditText.setSelection(tweetEditText.length());
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
            case KeyEvent.KEYCODE_MOVE_HOME:
                inputMethodManager.hideSoftInputFromWindow(tweetEditText.getWindowToken(), 0);
                moveTaskToBack(true);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = 140 - s.length();
        remainingTextView.setText("" + length);
        if (length == 140 || length < 0) {
            tweetButton.setEnabled(false);
        } else {
            tweetButton.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void sendTweet(NumeriUser numeriUser) {

        TweetBuilder tweetBuilder = numeriUser.getTweetBuilder();
        tweetBuilder.setText(tweetEditText.getText().toString());

        if (destinationStatusId != -1) {
            tweetBuilder.setReplyDestinationId(destinationStatusId);
            destinationStatusId = -1;
        }

        if (!appendedImages.isEmpty()) {
            tweetBuilder.addImages(appendedImages);
        }

        tweetBuilder.tweet();
        tweetEditText.setText("");
        inputMethodManager.hideSoftInputFromWindow(tweetEditText.getWindowToken(), 0);
        finish();
    }

    private static final int GALLERY = 1;
    private static final int KITCAT_GALLERY = 3;
    private static final int CAMERA = 2;

    private void appendedImage() {
        if (appendedImages.size() < 4) {
            if (Build.VERSION.SDK_INT < 19) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, KITCAT_GALLERY);
            }
        } else {
            ToastSender.sendToast("4つ以上は添付できません");
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) return;
        Uri uri = null;
        if (requestCode == GALLERY) {
            uri = data.getData();
        } else if (requestCode == KITCAT_GALLERY) {
            uri = data.getData();
            final int takeFrags = data.getFlags() &
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, takeFrags);

        }
    }

    /**
     * 投稿予定の画像をリストから除外する
     *
     * @param image 消したい画像が表示されているView
     */
    private void removeAppendedImage(View image) {
        new AlertDialog.Builder(this).setMessage("選択した画像を投稿予定から除外しますか？")
                .setPositiveButton("はい", (dialog, id) -> {
                    for (int i = 0; i < appendedImageViews.getChildCount(); i++) {
                        if (appendedImageViews.getChildAt(i) == image) {
                            appendedImageViews.removeViewAt(i);
                            appendedImages.remove(i);
                        }
                    }
                })
                .setNegativeButton("いいえ", (dialog, id) -> {
                })
                .create().show();
    }


    private void setUserNames() {
        for (String destinationUserName : destinationUserNames) {
            tweetEditText.setText(tweetEditText.getText() + "@" + destinationUserName + " ");
        }
        tweetEditText.setSelection(tweetEditText.getText().length());
        destinationUserNames.clear();
    }


    private void createChangeUserDialog() {
        List<CharSequence> numeriUsersName = new ArrayList<>();
        List<NumeriUser> numeriUsers = new ArrayList<>();
        for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
            numeriUsersName.add(numeriUser.getScreenName());
            numeriUsers.add(numeriUser);
        }
        new AlertDialog.Builder(this).setItems(numeriUsersName.toArray(new CharSequence[numeriUsersName.size()]), (dialog, which) -> {
            currentNumeriUser = numeriUsers.get(which);
            currentUserTextView.setText(numeriUsersName.get(which));
        }).create().show();
    }


    @Override
    public void finish() {
        for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
            numeriUser.getStreamEvent().removeOnStatusListener(onStatusListeners.get(numeriUser.getScreenName()));
        }
        tweetText = "";
        currentNumeriUser = null;
        destinationStatusId = -1;
        destinationUserNames.clear();
        appendedImages.clear();
        super.finish();
    }
}
