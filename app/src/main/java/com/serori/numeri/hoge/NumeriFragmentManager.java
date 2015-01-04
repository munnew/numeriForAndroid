package com.serori.numeri.hoge;

import com.serori.numeri.fragment.NumeriFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serioriKETC on 2014/12/31.
 */
public class NumeriFragmentManager {
    private NumeriFragmentManager() {

    }

    List<NumeriFragment> numeriFragments = new ArrayList<>();

    public void putFragments(List<NumeriFragment> numeriFragments) {
        this.numeriFragments.clear();
        this.numeriFragments.addAll(numeriFragments);
    }

    public List<NumeriFragment> getNumeriFragments() {
        List<NumeriFragment> fragments = new ArrayList<>();
        fragments.addAll(numeriFragments);
        return fragments;
    }

    public void clear() {
        numeriFragments.clear();
    }

    protected static NumeriFragmentManager getInstance() {
        return NumeriFragmentManagerHolder.instance;
    }

    private static class NumeriFragmentManagerHolder {
        private static final NumeriFragmentManager instance = new NumeriFragmentManager();
    }
}
