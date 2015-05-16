package com.serori.numeri.main.manager;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.main.Global;
import com.serori.numeri.main.MainActivity;
import com.serori.numeri.user.NumeriUser;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.UserList;

/**
 * Fragment管理用のActivity
 */
public class FragmentManagerActivity extends NumeriActivity implements OnFragmentDataDeleteListener {
    private FragmentManagerItemAdapter adapter;
    private List<FragmentManagerItem> managerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Fragment", "create");
        setContentView(R.layout.activity_fragment_manager);
        ListView fragmentsListView = (ListView) findViewById(R.id.fragmentsList);
        adapter = new FragmentManagerItemAdapter(this, 0, managerItems);
        fragmentsListView.setAdapter(adapter);
        Button addFragmentButton = (Button) findViewById(R.id.addFragment);
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
        addFragmentButton.setOnClickListener(v -> showAddFragmentDialog());
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

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddFragmentDialog() {
        List<CharSequence> fragmentAttribute = new ArrayList<>();
        for (FragmentStorager.FragmentType fragmentType : FragmentStorager.FragmentType.values()) {
            fragmentAttribute.add(fragmentType.getId());
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setItems(fragmentAttribute.toArray(new CharSequence[fragmentAttribute.size()]), (dialog, which) -> {

                    if (fragmentAttribute.get(which).equals(FragmentStorager.FragmentType.TL.getId())) {
                        createTimeLineFragmentsListDialog();
                    }

                    if (fragmentAttribute.get(which).equals(FragmentStorager.FragmentType.MENTIONS.getId())) {
                        createMentionsFragmentsListDialog();
                    }

                    if (fragmentAttribute.get(which).equals(FragmentStorager.FragmentType.LIST.getId())) {
                        createListFragmentsListDialog();
                    }
                }).create();
        setCurrentShowDialog(alertDialog);
    }

    private void createTimeLineFragmentsListDialog() {
        List<CharSequence> timeLineNames = new ArrayList<>();
        List<String> userTokens = new ArrayList<>();
        for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
            timeLineNames.add(numeriUser.getScreenName());
            userTokens.add(numeriUser.getAccessToken().getToken());
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this).setItems(timeLineNames.toArray(new CharSequence[timeLineNames.size()]), (dialog, which) -> {
            for (int i = 0; i < timeLineNames.size(); i++) {
                if (which == i) {
                    FragmentStorager.FragmentsTable table = new FragmentStorager.FragmentsTable(FragmentStorager.FragmentType.TL, timeLineNames.get(i).toString(), userTokens.get(i));
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
        }).create();
        setCurrentShowDialog(alertDialog);

    }

    private void createMentionsFragmentsListDialog() {
        List<CharSequence> mentionsNames = new ArrayList<>();
        List<String> userTokens = new ArrayList<>();
        for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
            mentionsNames.add(numeriUser.getScreenName());
            userTokens.add(numeriUser.getAccessToken().getToken());
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this).setItems(mentionsNames.toArray(new CharSequence[mentionsNames.size()]), (dialog, which) -> {
            for (int i = 0; i < mentionsNames.size(); i++) {
                if (which == i) {
                    FragmentStorager.FragmentsTable table = new FragmentStorager.FragmentsTable(FragmentStorager.FragmentType.MENTIONS, mentionsNames.get(i).toString(), userTokens.get(i));
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
        }).create();
        setCurrentShowDialog(alertDialog);

    }

    private void createListFragmentsListDialog() {
        List<CharSequence> userNames = new ArrayList<>();
        List<NumeriUser> users = new ArrayList<>();
        for (NumeriUser numeriUser : Global.getInstance().getNumeriUsers().getNumeriUsers()) {
            userNames.add(numeriUser.getScreenName());
            users.add(numeriUser);
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this).setItems(userNames.toArray(new CharSequence[userNames.size()]), (dialog, which) -> {
            AsyncTask.execute(() -> {
                try {

                    ResponseList<UserList> lists = users.get(which).getTwitter().getUserLists(users.get(which).getScreenName());
                    List<CharSequence> listNames = new ArrayList<>();
                    for (UserList list : lists) {
                        listNames.add(list.getName());
                    }
                    runOnUiThread(() -> {
                        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                                .setItems(listNames.toArray(new CharSequence[listNames.size()]), (dialog1, which1) -> {
                                    FragmentStorager.FragmentsTable table = new FragmentStorager.FragmentsTable(FragmentStorager.FragmentType.LIST, lists.get(which1).getName(), users.get(which).getAccessToken().getToken(), lists.get(which1).getId());
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
                                    lists.get(which1).getId();
                                }).create();
                        setCurrentShowDialog(alertDialog1);
                    });

                } catch (TwitterException e) {
                    e.printStackTrace();
                }

            });
        }).create();
        setCurrentShowDialog(alertDialog);
    }

    /**
     * 未実装、もしかしたら実装しない
     */
    private void createDMFragmentsListDialog() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Global.getInstance().isDestroyMainActivity()) {
                startActivity(MainActivity.class, true);
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public void OnFragmentDataDelete(int position) {
        adapter.remove(adapter.getItem(position));
    }
}
