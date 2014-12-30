package com.serori.numeri.fragment.manager;

/**
 * Created by serioriKETC on 2014/12/27.
 */
public class FragmentManagerItem {
    private String fragmentName;
    private String fragmentKey;

    public FragmentManagerItem(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public void setFragmentKey(String fragmentKey) {
        this.fragmentKey = fragmentKey;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public String getFragmentKey() {
        return fragmentKey;
    }
}
