package com.serori.numeri.twitter;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.serori.numeri.application.Application;
import com.serori.numeri.R;
import com.serori.numeri.hoge.OnStatusListener;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.twitter.TweetBuilder;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;


/**
 * Created by seroriKETC on 2014/12/21.
 */
public class TweetActivity extends Activity implements TextWatcher, OnStatusListener {
    private EditText tweetEditText;
    private TextView remainingTextView;
    private Button tweetButton;
    private Button changeUserButton;
    private TextView backgroundTimeLine;
    private TextView currentUserTextView;
    private InputMethodManager inputMethodManager;
    private static boolean isReply = false;
    private static long destinationStatusId;
    private static List<String> destinationUserNames = new ArrayList<>();
    private NumeriUser currentNumeriUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        if (savedInstanceState == null) {
            tweetEditText = (EditText) findViewById(R.id.tweeteditText);
            remainingTextView = (TextView) findViewById(R.id.remaining);
            changeUserButton = (Button) findViewById(R.id.changeUser);
            tweetButton = (Button) findViewById(R.id.sendTweet);
            currentUserTextView = (TextView) findViewById(R.id.currentUser);
            backgroundTimeLine = (TextView) findViewById(R.id.backgroundTimeLine);
            currentUserTextView.setText(Application.getInstance().getNumeriUsers().getNumeriUsers().get(0).getScreenName());
            currentNumeriUser = Application.getInstance().getNumeriUsers().getNumeriUsers().get(0);
            inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
            for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
                numeriUser.getStreamEvent().addOwnerOnStatusListener(this);
            }
            Log.v("Tweet", "create");
            tweetEditText.setOnClickListener(v -> inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED));
            tweetEditText.addTextChangedListener(this);
            tweetButton.setOnClickListener(v -> sendTweet(currentNumeriUser));
            changeUserButton.setOnClickListener(v -> createChangeUserDialog());
        }

        if (isReply) {
            setUserNames();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isReply = false;
            finish();
            return true;
        }
        return false;
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
        if (isReply) {
            new TweetBuilder().setNumeriUser(numeriUser).setReplyDestinationId(destinationStatusId).setText(tweetEditText.getText().toString()).tweet();
            isReply = false;
        } else {
            new TweetBuilder().setNumeriUser(numeriUser).setText(tweetEditText.getText().toString()).tweet();
        }
        tweetEditText.setText("");
        inputMethodManager.hideSoftInputFromWindow(tweetEditText.getWindowToken(), 0);
        finish();
    }

    @Override
    public void onStatus(Status status) {
        String owner = status.getUser().getScreenName();
        String text = status.getText();
        runOnUiThread(() -> backgroundTimeLine.setText(owner + " : " + text));
    }

    public static void setDestination(long statusId, List<String> destinationNames) {
        isReply = true;
        destinationStatusId = statusId;
        for (String destinationName : destinationNames) {
            destinationUserNames.add(destinationName);
        }
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
        for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
            numeriUsersName.add(numeriUser.getScreenName());
            numeriUsers.add(numeriUser);
        }
        new AlertDialog.Builder(this).setItems(numeriUsersName.toArray(new CharSequence[numeriUsersName.size()]), (dialog, which) -> {
            currentNumeriUser = numeriUsers.get(which);
            currentUserTextView.setText(numeriUsersName.get(which));
        }).create().show();
    }
}
