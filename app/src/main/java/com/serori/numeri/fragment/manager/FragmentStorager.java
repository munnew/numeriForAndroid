package com.serori.numeri.fragment.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.application.Application;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by serioriKETC on 2014/12/27.
 */
public class FragmentStorager {

    public void saveFragmentData(FragmentsTable table) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, FragmentsTable.class);
            Dao<FragmentsTable, String> dao = helper.getDao(FragmentsTable.class);
            dao.createOrUpdate(table);
            Application.getInstance().destroyMainActivity();
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

    public void deleteFragmentData(String fragmentKey) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, FragmentsTable.class);
            Dao<FragmentsTable, String> dao = helper.getDao(FragmentsTable.class);
            dao.deleteById(fragmentKey);
            Application.getInstance().destroyMainActivity();
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

    public List<FragmentsTable> getFragmentsData() {
        ConnectionSource connectionSource = null;
        List<FragmentsTable> fragmentsData = new ArrayList<>();
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
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

    public static final String MENTIONS = "Mentions";
    public static final String TL = "TimeLine";


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

        public FragmentsTable() {

        }

        public FragmentsTable(String fragmentType, String fragmentName, String userToken) {
            this.fragmentType = fragmentType;
            this.fragmentName = fragmentName;
            this.userToken = userToken;
            this.fragmentKey = fragmentType + fragmentName + userToken;
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
    }

    public static FragmentStorager getInstance() {
        return FragmentStoragerHolder.instance;
    }

    private static class FragmentStoragerHolder {
        private static final FragmentStorager instance = new FragmentStorager();
    }
}
