package com.serori.numeri.fragment.manager;

/**
 * 保存されたFragmentが削除されたかどうかを監視するクラス
 */
public class FragmentDataDeleteObserver {
    private OnFragmentDataDeleteListener onFragmentDataDeleteListener;

    public static FragmentDataDeleteObserver getInstance() {
        return FragmentDataDeleteObserverHolder.instance;
    }

    public void setOnFragmentDataDeleteListener(OnFragmentDataDeleteListener onFragmentDataDeleteListener) {
        this.onFragmentDataDeleteListener = onFragmentDataDeleteListener;
    }

    public void onFragmentDeataDelete(int position) {
        if (onFragmentDataDeleteListener != null) {
            onFragmentDataDeleteListener.OnFragmentDataDelete(position);
        }
    }

    private static class FragmentDataDeleteObserverHolder {
        private static FragmentDataDeleteObserver instance = new FragmentDataDeleteObserver();
    }
}
