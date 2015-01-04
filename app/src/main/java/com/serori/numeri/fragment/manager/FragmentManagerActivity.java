package com.serori.numeri.fragment.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.serori.numeri.R;
import com.serori.numeri.hoge.Application;
import com.serori.numeri.main.MainActivity;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;

/**
 * Created by serioriKETC on 2014/12/27.
 */
public class FragmentManagerActivity extends Activity implements OnFragmentDataDeleteListener {
    private ListView fragmentsListView;
    private FragmentManagerItemAdapter adapter;
    private List<FragmentManagerItem> managerItems = new ArrayList<>();
    private CharSequence[] fragmentAttribute = {"TimeLine", "Mentions", "リスト", "DM"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Fragment", "create");
        setContentView(R.layout.activity_fragment_manager);
        fragmentsListView = (ListView) findViewById(R.id.fragmentsList);
        adapter = new FragmentManagerItemAdapter(this, 0, managerItems);
        fragmentsListView.setAdapter(adapter);

        FragmentDataDeleteObserver.getInstance().setOnFragmentDataDeleteListener(this);
        List<FragmentManagerItem> items = new ArrayList<>();
        FragmentStorager fragmentStorager = FragmentStorager.getInstance();
        List<FragmentStorager.FragmentsTable> fragmentsTable = new ArrayList<>();
        fragmentsTable.addAll(fragmentStorager.getFragmentsData());
        for (FragmentStorager.FragmentsTable table : fragmentsTable) {
            FragmentManagerItem item = new FragmentManagerItem(table.getFragmentType() + " : " + table.getFragmentName());
            item.setFragmentKey(table.getFragmentKey());
            items.add(item);
        }
        adapter.addAll(items);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragment_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addfragment:
                new AlertDialog.Builder(this)
                        .setItems(fragmentAttribute, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    createTimeLineFragmentsListDialog();
                                    break;
                                case 1:
                                    createMentionsFragmentsListDialog();
                                    break;
                                case 2:
                                    createListFragmentsListDialog();
                                    break;
                                case 3:
                                    createDMFragmentsListDialog();
                                    break;
                                default:
                                    break;
                            }
                        }).create().show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void createTimeLineFragmentsListDialog() {
        List<CharSequence> timeLineNames = new ArrayList<>();
        List<String> userTokens = new ArrayList<>();
        AsyncTask.execute(() -> {
            for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
                timeLineNames.add(numeriUser.getScreenName());
                userTokens.add(numeriUser.getAccessToken().getToken());
            }

            runOnUiThread(() -> {
                new AlertDialog.Builder(this).setItems(timeLineNames.toArray(new CharSequence[timeLineNames.size()]), (dialog, which) -> {
                    for (int i = 0; i < timeLineNames.size(); i++) {
                        if (which == i) {
                            FragmentStorager.FragmentsTable table = new FragmentStorager.FragmentsTable(FragmentStorager.TL, timeLineNames.get(i).toString(), userTokens.get(i));
                            FragmentStorager.getInstance().saveFragmentData(table);
                            FragmentManagerItem item = new FragmentManagerItem(table.getFragmentType() + " : " + table.getFragmentName());
                            item.setFragmentKey(table.getFragmentKey());
                            boolean addFlag = true;
                            for (int j = 0; j < adapter.getCount(); j++) {
                                if (item.getFragmentKey().equals(adapter.getItem(j).getFragmentKey())) {
                                    addFlag = false;
                                }
                            }
                            if (addFlag) {
                                adapter.add(item);
                            }
                        }
                    }
                }).create().show();
            });
        });

    }

    private void createMentionsFragmentsListDialog() {
        List<CharSequence> mentionsNames = new ArrayList<>();
        List<String> userTokens = new ArrayList<>();
        AsyncTask.execute(() -> {
            for (NumeriUser numeriUser : Application.getInstance().getNumeriUsers().getNumeriUsers()) {
                mentionsNames.add(numeriUser.getScreenName());
                userTokens.add(numeriUser.getAccessToken().getToken());
            }

            runOnUiThread(() -> {
                new AlertDialog.Builder(this).setItems(mentionsNames.toArray(new CharSequence[mentionsNames.size()]), (dialog, which) -> {
                    for (int i = 0; i < mentionsNames.size(); i++) {
                        if (which == i) {
                            FragmentStorager.FragmentsTable table = new FragmentStorager.FragmentsTable(FragmentStorager.MENTIONS, mentionsNames.get(i).toString(), userTokens.get(i));
                            FragmentStorager.getInstance().saveFragmentData(table);
                            FragmentManagerItem item = new FragmentManagerItem(table.getFragmentType() + " : " + table.getFragmentName());
                            item.setFragmentKey(table.getFragmentKey());
                            boolean addFlag = true;
                            for (int j = 0; j < adapter.getCount(); j++) {
                                if (item.getFragmentKey().equals(adapter.getItem(j).getFragmentKey())) {
                                    addFlag = false;
                                }
                            }
                            if (addFlag) {
                                adapter.add(item);
                            }
                        }
                    }
                }).create().show();
            });
        });

    }

    private void createListFragmentsListDialog() {

    }

    private void createDMFragmentsListDialog() {

    }

    private void startMainActivity(boolean isFinish) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startMainActivity(true);
            return true;
        }
        return false;
    }

    @Override
    public void OnFragmentDataDelete(int position) {
        adapter.remove(adapter.getItem(position));
    }
}
