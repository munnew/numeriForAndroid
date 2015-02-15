package com.serori.numeri.fragment;

import android.os.AsyncTask;
import android.util.Log;

import com.serori.numeri.listview.item.TimeLineItem;
import com.serori.numeri.util.toast.ToastSender;
import com.serori.numeri.util.twitter.TwitterExceptionDisplay;


import java.util.ArrayList;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 */
public class ListFragment extends NumeriFragment {

    private long listId;
    private List<Long> listUserIds = new ArrayList<>();

    public void setListId(long id) {
        listId = id;
    }

    @Override
    public String getFragmentName() {
        return super.name;
    }

    @Override
    protected void initializeLoad() {
        AsyncTask.execute(() -> {
            Paging pages = new Paging(1);
            pages.count(31);
            try {
                ResponseList<Status> statuses = getNumeriUser().getTwitter().getUserListStatuses(listId, pages);
                long nextCursor = -1;
                while (nextCursor != 0) {
                    PagableResponseList<User> users = getNumeriUser().getTwitter().getUserListMembers(listId, nextCursor);
                    for (User user : users) {
                        listUserIds.add(user.getId());
                    }
                    nextCursor = users.getNextCursor();
                }
                Log.v(toString(), "listUsers" + listUserIds.size());

                getMainActivity().runOnUiThread(() -> {
                    for (Status status : statuses) {
                        getAdapter().add(new TimeLineItem(status, getNumeriUser()));
                    }
                });
                startPseudoStream();

            } catch (TwitterException e) {
                e.printStackTrace();
                if (e.exceededRateLimitation()) {
                    ToastSender.sendToast("exceededRateLimitation");
                } else {
                    ToastSender.sendToast("ネットワークを確認して下さい");
                }
            }
        });
    }

    /**
     * ユーザーリストでの擬似的なストリームを開始する。
     */
    private void startPseudoStream() {
        getNumeriUser().getStreamEvent().addOwnerOnStatusListener(status -> {
            for (Long listUserId : listUserIds) {
                if (status.getUser().getId() == listUserId && status.getUserMentionEntities().length == 0) {
                    getTimelineListView().insertItem(new TimeLineItem(status, getNumeriUser()));
                    break;
                }
            }
        });
    }

    @Override
    protected void onAttachedBottom(TimeLineItem item) {
        AsyncTask.execute(() -> {
            getTimelineListView().onAttachedBottomCallbackEnabled(false);
            Paging paging = new Paging();
            paging.setMaxId(item.getStatusId());
            paging.count(31);
            try {
                ResponseList<Status> statuses = getNumeriUser().getTwitter().getUserListStatuses(listId, paging);
                statuses.remove(0);
                getMainActivity().runOnUiThread(() -> {
                    for (Status status : statuses) {
                        getAdapter().add(new TimeLineItem(status, getNumeriUser()));
                    }
                });
            } catch (TwitterException e) {
                TwitterExceptionDisplay.show(e);
                e.printStackTrace();
            } finally {
                getTimelineListView().onAttachedBottomCallbackEnabled(true);
            }
        });
    }
}
