package com.serori.numeri.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.config.ConfigurationStorager;
import com.serori.numeri.listview.TimeLineListView;
import com.serori.numeri.listview.item.TimeLineItemAdapter;
import com.serori.numeri.twitter.SimpleTweetStatus;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class NumeriFragmentHolder {
    protected String fragmentName = "fragment";
    private NumeriUser numeriUser = null;

    private TimeLineListView timelineListView;
    private TimeLineItemAdapter adapter;
    private List<SimpleTweetStatus> timeLineItems;
    protected Context context;

    public abstract NumeriFragment createFragment(String fragmentName, NumeriUser numeriUser);


    /**
     * �^�C�����C����\������Fragment���p�����ׂ��N���X<br>
     * �e��Activity��NumeriActivity���p�����Ă���K�v������
     */
    public abstract class NumeriFragment extends Fragment implements AttachedBottomListener {


        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (!(getActivity() instanceof NumeriActivity)) {
                throw new IllegalStateException("�e�̃A�N�e�B�r�e�B��NunmeriActivity���p�����Ă܂���");
            }
            View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
            if (numeriUser == null) {
                throw new NullPointerException("numeriUser���Z�b�g����Ă��܂���");
            }
            Log.v(fragmentName, "crate");
            setRetainInstance(true);
            context = rootView.getContext();
            timelineListView = (TimeLineListView) rootView.findViewById(R.id.timeLineListView);
            timelineListView.setAttachedBottomListener(this);
            if (savedInstanceState == null) {
                timeLineItems = new ArrayList<>();
                adapter = new TimeLineItemAdapter(context, 0, timeLineItems);
                timelineListView.setAdapter(adapter);
                initializeLoad();
            } else {
                timelineListView.setAdapter(adapter);
                Log.v("restoredInfo:", fragmentName + getNumeriUser().getAccessToken().getUserId());
            }
            timelineListView.onTouchItemEnabled(getNumeriUser(), getActivity());
            timelineListView.startObserveFavorite(getNumeriUser());
            return rootView;
        }

        protected TimeLineListView getTimelineListView() {
            return timelineListView;
        }

        protected TimeLineItemAdapter getAdapter() {
            return adapter;
        }

        protected List<SimpleTweetStatus> getTimeLineItems() {
            return timeLineItems;
        }

        public String getFragmentName() {
            return fragmentName;
        }

        public void setFragmentName(String name) {
            fragmentName = name;
        }

        public void setNumeriUser(NumeriUser user) {
            numeriUser = user;
        }

        protected NumeriUser getNumeriUser() {
            return numeriUser;
        }

        /**
         * �^�C�����C�����擾���ĕ\�����鏈�����������郁�\�b�h
         */
        protected abstract void initializeLoad();

        /**
         * ���X�g�r���[�̈�ԉ��܂ŃX�N���[�������ۂɔ�������C�x���g�n���h��
         *
         * @param item ��ԉ��̃A�C�e��
         */
        protected abstract void onAttachedBottom(SimpleTweetStatus item);

        @Override
        public void attachedBottom(SimpleTweetStatus item) {
            if (ConfigurationStorager.EitherConfigurations.CONFIRMATION_LESS_GET_TWEET.isEnabled()) {
                onAttachedBottom(item);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage("�c�C�[�g���X�ɓǂݍ��݂܂����H")
                        .setNegativeButton("������", (dailog, id) -> {
                        })
                        .setPositiveButton("�͂�", (dialog, id) -> {
                            onAttachedBottom(item);
                        })
                        .create();
                ((NumeriActivity) getActivity()).setCurrentShowDialog(alertDialog);
            }
        }

        /**
         * �e��NumeriActivity���擾����
         *
         * @return �e��NumeriActivity
         */
        protected NumeriActivity getNumeriActivity() {
            return (NumeriActivity) getActivity();
        }
    }


}
