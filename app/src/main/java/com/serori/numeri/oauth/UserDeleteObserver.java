package com.serori.numeri.oauth;

/**
 * Created by serioriKETC on 2014/12/29.
 */
public class UserDeleteObserver {
    private UserDeleteObserver() {

    }

    private OnUserDeleteListener onUserDeleteListener = null;

    public static UserDeleteObserver getInstance() {
        return UserDeleteObserverHolder.instance;
    }

    public void setOnUserDeleteListener(OnUserDeleteListener onUserDeleteListener) {
        this.onUserDeleteListener = onUserDeleteListener;
    }

    public void onUserDelete(int position) {
        if (onUserDeleteListener != null) {
            onUserDeleteListener.onUserDelete(position);
        }
    }


    private static class UserDeleteObserverHolder {
        private static final UserDeleteObserver instance = new UserDeleteObserver();
    }
}
