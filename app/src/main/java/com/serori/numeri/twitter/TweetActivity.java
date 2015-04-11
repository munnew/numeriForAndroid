package com.serori.numeri.twitter;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
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
import com.serori.numeri.main.Application;
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
    private static boolean isReply = false;
    private static long destinationStatusId;
    private static String tweetText = "";
    private static List<String> destinationUserNames = new ArrayList<>();
    private static NumeriUser currentNumeriUser = null;
    private List<File> appendedImages = new ArrayList<>();
    private LinearLayout appendedImageViews;
    private Map<String, OnStatusListener> onStatusListeners = new LinkedHashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        if (savedInstanceState == null) {
            Button changeUserButton = (Button) findViewById(R.id.changeUser);
            tweetEditText = (EditText) findViewById(R.id.tweeteditText);
            remainingTextView = (TextView) findViewById(R.id.remaining);
            tweetButton = (Button) findViewById(R.id.sendTweet);
            Button addImageButton = (Button) findViewById(R.id.addImageButtoon);
            currentUserTextView = (TextView) findViewById(R.id.currentUser);
            backgroundTimeLine = (TextView) findViewById(R.id.backgroundTimeLine);
            appendedImageViews = (LinearLayout) findViewById(R.id.appendedImages);

            NumeriUser defaultUser = Application.getInstance().getNumeriUsers().getNumeriUsers().get(0);
            currentNumeriUser = currentNumeriUser == null ? defaultUser : currentNumeriUser;
            String currentTweetUserName = currentNumeriUser == null ? Application.getInstance().getNumeriUsers().getNumeriUsers().get(0).getScreenName() : currentNumeriUser.getScreenName();

            currentUserTextView.setText(currentTweetUserName);
            inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

            //OnStatus
            for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
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

        if (isReply) setUserNames();
        if (!tweetText.isEmpty()) {
            tweetEditText.setText(tweetText);
            tweetEditText.setSelection(tweetEditText.length());
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                isReply = false;
                currentNumeriUser = null;
                destinationUserNames.clear();
                appendedImages.clear();
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

        if (isReply) {
            tweetBuilder.setReplyDestinationId(destinationStatusId);
            isReply = false;
        }

        if (!appendedImages.isEmpty()) {
            tweetBuilder.addImages(appendedImages);
        }

        tweetBuilder.tweet();
        tweetEditText.setText("");
        currentNumeriUser = null;
        appendedImages.clear();
        destinationUserNames.clear();
        inputMethodManager.hideSoftInputFromWindow(tweetEditText.getWindowToken(), 0);
        finish();
    }

    private static final int GALLERY = 1;
    private static final int CAMERA = 2;

    private void appendedImage() {
        if (appendedImages.size() < 4) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLERY);
        } else {
            ToastSender.sendToast("4つ以上は添付できません");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = null;
            switch (requestCode) {
                case GALLERY:
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                        cursor.moveToPosition(0);
                        Log.v(toString(), cursor.getString(1));
                        path = cursor.getString(1);
                        File file = new File(path);
                        cursor.close();
                        inputStream.close();
                        appendedImages.add(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case CAMERA:

                    break;
                default:
                    break;
            }
            if (path != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                ImageView appendedImage = new ImageView(this);
                Matrix matrix = new Matrix();
                float y = Application.getInstance().getWindowSize().y / 9;
                float magnification;
                Log.v(toString(), "Height:" + bitmap.getHeight() + "width" + bitmap.getWidth());
                if (bitmap.getHeight() > bitmap.getWidth()) {
                    magnification = y / bitmap.getHeight();
                } else {
                    magnification = y / bitmap.getWidth();
                }
                float width = bitmap.getWidth() * magnification;
                float height = bitmap.getHeight() * magnification;
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, false);
                Log.v(toString(), "magnification:" + magnification);
                matrix.postTranslate(0, 0);
                appendedImage.setImageMatrix(matrix);
                appendedImage.setScaleType(ImageView.ScaleType.MATRIX);
                appendedImage.setImageBitmap(bitmap);
                appendedImageViews.addView(appendedImage);
                appendedImage.setOnClickListener(this::removeAppendedImage);
            }
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

    public static void setDestination(long statusId, List<String> destinationNames) {
        isReply = true;
        destinationStatusId = statusId;
        destinationUserNames.addAll(destinationNames);
    }

    public static void setTweetNumeriUser(NumeriUser numeriUser) {
        currentNumeriUser = numeriUser;
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

    public static void setTweetText(String text) {
        tweetText = text;
    }

    @Override
    public void finish() {
        for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
            numeriUser.getStreamEvent().removeOnStatusListener(onStatusListeners.get(numeriUser.getScreenName()));
        }
        tweetText = "";
        super.finish();
    }
}
