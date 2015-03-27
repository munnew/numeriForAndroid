package com.serori.numeri.fragment;

import android.util.Log;

import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.util.async.SimpleAsyncTask;
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
 * ユーザーリストを表示するFragment
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
        SimpleAsyncTask.backgroundExecute(() -> {
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

                getActivity().runOnUiThread(() -> {
                    for (Status status : statuses) {
                        getAdapter().add(SimpleTweetStatus.build(status, getNumeriUser()));
                    }
                });
                startPseudoStream();

            } catch (TwitterException e) {
                e.printStackTrace();
                TwitterExceptionDisplay.show(e);
            }
        });
    }

    /**
     * ユーザーリストでの擬似的なストリームを開始する。
     */
    private void startPseudoStream() {
        getNumeriUser().getStreamEvent().addOnStatusListener(status -> {
            for (Long listUserId : listUserIds) {
                if (status.getUser().getId() == listUserId && status.getUserMentionEntities().length == 0) {
                    getTimelineListView().insertItem(SimpleTweetStatus.build(status, getNumeriUser()));
                    break;
                }
            }
        });
    }

    @Override
    protected void onAttachedBottom(SimpleTweetStatus item) {
        getTimelineListView().onAttachedBottomCallbackEnabled(false);
        SimpleAsyncTask.backgroundExecute(() -> {
            Paging paging = new Paging();
            paging.setMaxId(item.getStatusId());
            paging.count(31);
            try {
                ResponseList<Status> statuses = getNumeriUser().getTwitter().getUserListStatuses(listId, paging);
                if(!statuses.isEmpty())statuses.remove(0);
                getActivity().runOnUiThread(() -> {
                    for (Status status : statuses) {
                        getAdapter().add(SimpleTweetStatus.build(status, getNumeriUser()));
                    }
                    getTimelineListView().onAttachedBottomCallbackEnabled(true);
                });
            } catch (TwitterException e) {
                TwitterExceptionDisplay.show(e);
                e.printStackTrace();
                getTimelineListView().onAttachedBottomCallbackEnabled(true);
            }
        });
    }
}
