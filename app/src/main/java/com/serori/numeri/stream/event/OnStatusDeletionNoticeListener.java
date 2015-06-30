package com.serori.numeri.stream.event;

import twitter4j.StatusDeletionNotice;

/**
 * OnDeletionNoticeListener
 */
public interface OnStatusDeletionNoticeListener {
    void onStatusDeletionNotice(StatusDeletionNotice statusDeletionNotice);
}
