package com.serori.numeri.stream.event;

import twitter4j.StatusDeletionNotice;

/**
 * OnDeletionNoticeListener
 */
public interface OnDeletionNoticeListener {
    void onDeletionNotice(StatusDeletionNotice statusDeletionNotice);
}
