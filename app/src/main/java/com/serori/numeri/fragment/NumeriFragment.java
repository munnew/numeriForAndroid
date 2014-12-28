package com.serori.numeri.fragment;

import com.serori.numeri.user.NumeriUser;

/**
 * Fragmentに実装するべきインターフェース
 */
public interface NumeriFragment {
    String getFragmentName();

    void setFragmentName(String name);

    void setNumeriUser(NumeriUser numeriUser);

}
