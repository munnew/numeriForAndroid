package com.serori.numeri.oauth;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.temp.activity.NumeriActivity;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.user.NumeriUserStorager;
import com.serori.numeri.userprofile.UserInformationActivity;

import java.util.List;

/**
 * OAuthActivityに表示するリストビューのアダプタ
 */
public class UserListItemAdapter extends ArrayAdapter<NumeriUser> {
    private LayoutInflater layoutInflater;

    public UserListItemAdapter(Context context, int resource, List<NumeriUser> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NumeriUser item = getItem(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.item_user, null);
        }
        TextView screenName = (TextView) convertView.findViewById(R.id.screenName);
        screenName.setText(item.getScreenName());
        Button userDeleteButton = (Button) convertView.findViewById(R.id.userDelete);

        userDeleteButton.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setPositiveButton("はい", (dialog, which) -> {
                        NumeriUserStorager.getInstance().deleteUser(item.getAccessToken().getToken());
                        UserDeleteObserver.getInstance().onUserDelete(position);
                    }).setNegativeButton("キャンセル", null).create();
            ((NumeriActivity) getContext()).setCurrentShowDialog(alertDialog);
        });
        convertView.setOnTouchListener((v, event) -> onTouchEvent(v, event, item));

        return convertView;
    }

    private boolean onTouchEvent(View view, MotionEvent event, NumeriUser item) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.findViewById(R.id.overlay).setBackgroundColor(getContext().getResources().getColor(R.color.touched));
                break;
            case MotionEvent.ACTION_UP:
                view.findViewById(R.id.overlay).setBackgroundColor(getContext().getResources().getColor(R.color.transparency));
                UserInformationActivity.show(getContext(), item.getScreenName(), item);
            default:
                view.findViewById(R.id.overlay).setBackgroundColor(getContext().getResources().getColor(R.color.transparency));
        }
        return true;
    }
}
