package com.serori.numeri.listview.item;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.color.ColorStorager;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.temp.imageview.NumeriImageView;
import com.serori.numeri.media.MediaActivity;
import com.serori.numeri.twitter.SimpleTweetStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * タイムラインを表示するためのアダプタ
 */
public class TimeLineItemAdapter extends ArrayAdapter<SimpleTweetStatus> {
    private LayoutInflater layoutInflater;
    private List<SimpleTweetStatus> simpleTweetStatusList;

    public TimeLineItemAdapter(Context context, int resource, List<SimpleTweetStatus> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        simpleTweetStatusList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleTweetStatus simpleTweetStatus = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_timeline, null);
        }
        float textSize = ConfigurationStorager.NumericalConfigurations.CHARACTER_SIZE.getNumericValue() + 8;
        NumeriImageView iconImageView = (NumeriImageView) convertView.findViewById(R.id.userIcon);

        TextView screenNameTextView = (TextView) convertView.findViewById(R.id.timeLine_screenName);
        screenNameTextView.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        screenNameTextView.setTextSize(textSize * (float) 0.86);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.timeLine_name);
        nameTextView.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        nameTextView.setTextSize(textSize * (float) 0.86);

        TextView mainTextView = (TextView) convertView.findViewById(R.id.timeLine_maintext);
        mainTextView.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        mainTextView.setTextSize(textSize);

        TextView viaTextView = (TextView) convertView.findViewById(R.id.timeLine_via);
        viaTextView.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        viaTextView.setTextSize(8);

        TextView createdTime = (TextView) convertView.findViewById(R.id.createdDate);
        createdTime.setTextColor(Color.parseColor(ColorStorager.Colors.CHARACTER.getColor()));
        createdTime.setTextSize(8);

        ImageView favoriteStar = (ImageView) convertView.findViewById(R.id.favoriteStar);
        View isMyTweetState = convertView.findViewById(R.id.isMyTweetStateView);

        ImageView isProtectedUser = (ImageView) convertView.findViewById(R.id.key);
        if (simpleTweetStatus.isProtectedUser()) {
            isProtectedUser.setVisibility(View.VISIBLE);
        } else {
            isProtectedUser.setVisibility(View.GONE);
        }

        boolean useHighResolution = ConfigurationStorager.EitherConfigurations.USE_HIGH_RESOLUTION_ICON.isEnabled();
        String iconUrl = useHighResolution ? simpleTweetStatus.getBiggerProfileImageURL() : simpleTweetStatus.getProfileImageURL();
        iconImageView.setImage(true, NumeriImageView.ProgressType.LOAD_ICON, iconUrl);

        screenNameTextView.setText(simpleTweetStatus.getScreenName());
        nameTextView.setText(simpleTweetStatus.getName());
        mainTextView.setText(simpleTweetStatus.getText());
        viaTextView.setText(simpleTweetStatus.getVia());
        createdTime.setText(simpleTweetStatus.getCreatedTime());

        LinearLayout appendedImageThumbnails = (LinearLayout) convertView.findViewById(R.id.appendedImageThumbnails);
        if (ConfigurationStorager.EitherConfigurations.DISPLAY_IMAGE_THUMB.isEnabled()) {
            List<String> thumbnailUris = simpleTweetStatus.getThumbnailUris();
            List<NumeriImageView> thumbnails = new ArrayList<>();
            for (int i = 0; i < appendedImageThumbnails.getChildCount(); i++) {
                NumeriImageView thumbnail = (NumeriImageView) appendedImageThumbnails.getChildAt(i);
                thumbnails.add(thumbnail);
            }
            if (thumbnailUris.isEmpty()) {
                for (NumeriImageView thumbnail : thumbnails) {
                    thumbnail.setBackgroundColor(thumbnail.getResources().getColor(R.color.transparency));
                    thumbnail.setImage(null);
                    thumbnail.setOnClickListener(null);
                    thumbnail.setClickable(false);
                }
                appendedImageThumbnails.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < thumbnails.size(); i++) {
                    NumeriImageView thumbnail = thumbnails.get(i);
                    if (i < thumbnailUris.size()) {
                        thumbnail.setVisibility(View.VISIBLE);
                        thumbnail.setImageDrawable(null);
                        thumbnail.setBackgroundColor(convertView.getResources().getColor(R.color.imageView_BackGround));
                        thumbnail.setImage(true, NumeriImageView.ProgressType.NONE, thumbnailUris.get(i));
                        int j = i;
                        thumbnail.setOnClickListener(view -> MediaActivity.show(getContext(), simpleTweetStatus.getMediaUris(), j));
                    } else {
                        thumbnail.setBackgroundColor(thumbnail.getResources().getColor(R.color.transparency));
                        thumbnail.setImage(null);
                        thumbnail.setOnClickListener(null);
                        thumbnail.setVisibility(View.GONE);
                    }
                }
                appendedImageThumbnails.setVisibility(View.VISIBLE);
            }
        } else {
            appendedImageThumbnails.setVisibility(View.GONE);
        }


        if (simpleTweetStatus.isFavorite()) {
            favoriteStar.setVisibility(View.VISIBLE);
        } else {
            favoriteStar.setVisibility(View.GONE);
        }

        if (simpleTweetStatus.isMyTweet()) {
            isMyTweetState.setBackgroundColor(Color.parseColor(ColorStorager.Colors.MYTWEET_MARK.getColor()));
        } else {
            isMyTweetState.setBackgroundColor(Color.parseColor("#00000000"));
        }

        if (simpleTweetStatus.isRT()) {
            convertView.setBackgroundColor(Color.parseColor(ColorStorager.Colors.RT_ITEM.getColor()));
            return convertView;
        }

        if (simpleTweetStatus.isMention()) {
            convertView.setBackgroundColor(Color.parseColor(ColorStorager.Colors.MENTION_ITEM.getColor()));
            return convertView;
        }

        convertView.setBackgroundColor(Color.parseColor(ColorStorager.Colors.NORMAL_ITEM.getColor()));

        return convertView;
    }

    public void remove(long statusId) {
        SimpleTweetStatus simpleStatus = null;
        for (SimpleTweetStatus simpleTweetStatus : simpleTweetStatusList) {
            if (simpleTweetStatus.getStatusId() == statusId) {
                simpleStatus = simpleTweetStatus;
                break;
            }
        }

        if (simpleStatus != null) {
            simpleTweetStatusList.remove(simpleStatus);
        }
    }

}
