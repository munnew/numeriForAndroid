package com.serori.numeri.stream;

/**
 * Created by seroriKETC on 2014/12/20.
 */
public interface IStreamEvent {
    IStreamEvent addOwnerOnStatusListener(OnStatusListener listener);

    IStreamEvent addOwnerOnfavoriteListener(OnFavoriteListener listener);



}
