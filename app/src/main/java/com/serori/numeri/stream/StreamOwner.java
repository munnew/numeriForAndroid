package com.serori.numeri.stream;

/**
 * Created by seroriKETC on 2014/12/20.
 */
public interface StreamOwner {
    void addOwnerOnStatusListener(OnStatusListener listener);

    void addOwnerOnfavoriteListener(OnFavoriteListener listener);



}
