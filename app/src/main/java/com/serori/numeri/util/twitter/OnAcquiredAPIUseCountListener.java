package com.serori.numeri.util.twitter;

import java.util.Map;

/**
 */
public interface OnAcquiredAPIUseCountListener {
    void onAcquiredAPIUseCount(Map<TwitterAPIConfirmer.TwitterAPI, Integer> apiRemainingInfo);
}
