package com.serori.numeri.main.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.fragment.ListFragment;
import com.serori.numeri.fragment.MentionsFragment;
import com.serori.numeri.fragment.NumeriFragment;
import com.serori.numeri.fragment.TimeLineFragment;
import com.serori.numeri.main.Global;
import com.serori.numeri.user.NumeriUser;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FragmentStorager {
    /**
     * Fragmentを生成するための情報を保存する
     *
     * @param table Fragmentを生成するための情報
     */
    public void saveFragmentData(FragmentsTable table) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, FragmentsTable.class);
            Dao<FragmentsTable, String> dao = helper.getDao(FragmentsTable.class);
            dao.createOrUpdate(table);
            Global.getInstance().destroyMainActivity();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 指定したキーのFragmentの情報を削除する
     *
     * @param fragmentKey 削除したいFragmentのキー
     */
    public void deleteFragmentData(String fragmentKey) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, FragmentsTable.class);
            Dao<FragmentsTable, String> dao = helper.getDao(FragmentsTable.class);
            dao.deleteById(fragmentKey);
            Global.getInstance().destroyMainActivity();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Fragmentを生成するための情報のリストを取得する
     *
     * @return Fragmentを生成するための情報のリスト
     */
    public List<FragmentsTable> getFragmentsData() {
        ConnectionSource connectionSource = null;
        List<FragmentsTable> fragmentsData = new ArrayList<>();
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, FragmentsTable.class);
            Dao<FragmentsTable, String> dao = helper.getDao(FragmentsTable.class);
            fragmentsData.addAll(dao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return fragmentsData;
    }

    /**
     * 渡されたユーザーリスト内に存在するユーザーが持ち主である複数の保存されたFragmentを返します<br>
     * 空のリストを渡された場合はIllegalArgumentExceptionが発生
     *
     * @param numeriUsers ユーザーたち
     * @return Fragmentのリスト
     */
    public List<NumeriFragment> getFragments(List<NumeriUser> numeriUsers) {
        if (numeriUsers.isEmpty()) {
            throw new IllegalArgumentException("userが存在しません");
        }
        ConnectionSource connectionSource = null;
        List<NumeriFragment> fragments = new ArrayList<>();
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, FragmentsTable.class);
            Dao<FragmentsTable, String> dao = helper.getDao(FragmentsTable.class);

            for (FragmentsTable table : dao.queryForAll()) {
                if (table.getFragmentType().equals(FragmentType.TL.getId())) {
                    for (NumeriUser numeriUser : numeriUsers) {
                        if (numeriUser.getAccessToken().getToken().equals(table.getUserToken())) {
                            fragments.add(initNumeriFragment(numeriUser, new TimeLineFragment()));
                        }
                    }
                } else if (table.getFragmentType().equals(FragmentType.MENTIONS.getId())) {
                    for (NumeriUser numeriUser : numeriUsers) {
                        if (numeriUser.getAccessToken().getToken().equals(table.getUserToken())) {
                            fragments.add(initNumeriFragment(numeriUser, new MentionsFragment()));
                        }
                    }
                } else if (table.getFragmentType().equals(FragmentType.LIST.getId())) {
                    for (NumeriUser numeriUser : numeriUsers) {
                        if (numeriUser.getAccessToken().getToken().equals(table.getUserToken())) {
                            ListFragment listFragment = new ListFragment();
                            listFragment.setFragmentName(table.getFragmentName());
                            listFragment.setNumeriUser(numeriUser);
                            listFragment.setListId(table.getListId());
                            fragments.add(listFragment);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return fragments;
    }

    private NumeriFragment initNumeriFragment(NumeriUser numeriUser, NumeriFragment numeriFragment) {
        String screenName;
        screenName = numeriUser.getScreenName();
        numeriFragment.setNumeriUser(numeriUser);
        numeriFragment.setFragmentName(screenName);
        return numeriFragment;
    }

    /**
     * Fragmentを生成するための情報を保存するテーブルクラス
     */
    @DatabaseTable(tableName = "fragments")
    public static class FragmentsTable {

        @DatabaseField(canBeNull = false)
        private String fragmentType;
        @DatabaseField(canBeNull = false)
        private String userToken;

        @DatabaseField(canBeNull = false)
        private String fragmentName;

        @DatabaseField(canBeNull = false, id = true)
        private String fragmentKey;

        @DatabaseField(canBeNull = true)
        private long listId;

        public FragmentsTable() {

        }

        public FragmentsTable(FragmentType type, String fragmentName, String userToken) {
            this.fragmentType = type.getId();
            this.fragmentName = fragmentName;
            this.userToken = userToken;
            this.fragmentKey = fragmentType + fragmentName + userToken;
        }

        public FragmentsTable(FragmentType type, String fragmentName, String userToken, long listId) {
            this.fragmentType = type.getId();
            this.fragmentName = fragmentName;
            this.userToken = userToken;
            this.fragmentKey = fragmentType + fragmentName + userToken;
            this.listId = listId;
        }

        public String getFragmentType() {
            return fragmentType;
        }

        public String getUserToken() {
            return userToken;
        }

        public String getFragmentName() {
            return fragmentName;
        }

        public String getFragmentKey() {
            return fragmentKey;
        }

        public long getListId() {
            return listId;
        }
    }

    public static FragmentStorager getInstance() {
        return FragmentStoragerHolder.instance;
    }

    private static class FragmentStoragerHolder {
        private static final FragmentStorager instance = new FragmentStorager();
    }

    /**
     * Fragmentの種類を表す列挙型
     */
    public enum FragmentType {
        TL("TimeLine"),
        MENTIONS("Mentions"),
        LIST("リスト"),
        FillInMessage("カキコ");


        private String id;

        FragmentType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
