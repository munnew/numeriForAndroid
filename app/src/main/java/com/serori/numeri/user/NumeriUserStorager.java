package com.serori.numeri.user;


import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.main.Global;
import com.serori.numeri.main.manager.FragmentStorager;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NumeriUserStorager {

    private NumeriUserStorager() {
    }

    public static NumeriUserStorager getInstance() {
        return NumeriUserStoragerHolder.instance;
    }

    public void saveNumeriUser(NumeriUserTable userTable) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, NumeriUserTable.class);
            Dao<NumeriUserTable, String> dao = helper.getDao(NumeriUserTable.class);
            dao.createOrUpdate(userTable);
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

    public List<NumeriUserTable> loadNumeriUserTables() {
        ConnectionSource connectionSource = null;
        List<NumeriUserTable> tables = new ArrayList<>();
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, NumeriUserTable.class);
            Dao<NumeriUserTable, String> dao = helper.getDao(NumeriUserTable.class);
            tables.addAll(dao.queryForAll());
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
        return tables;
    }

    public void deleteUser(String token) {
        Log.v("delete", "start");
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, NumeriUserTable.class);
            TableUtils.createTableIfNotExists(connectionSource, FragmentStorager.FragmentsTable.class);
            Dao<NumeriUserTable, String> numeriUserTablesDao = helper.getDao(NumeriUserTable.class);
            Dao<FragmentStorager.FragmentsTable, String> fragmentsTableDao = helper.getDao(FragmentStorager.FragmentsTable.class);
            numeriUserTablesDao.deleteById(token);
            List<FragmentStorager.FragmentsTable> fragmentsTables = new ArrayList<>();
            fragmentsTables.addAll(fragmentsTableDao.queryForAll());
            for (FragmentStorager.FragmentsTable fragmentsTable : fragmentsTables) {
                if (fragmentsTable.getUserToken().equals(token)) {
                    Log.v("delete", fragmentsTable.getFragmentName());
                    fragmentsTableDao.delete(fragmentsTable);
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
    }

    private static class NumeriUserStoragerHolder {
        private static final NumeriUserStorager instance = new NumeriUserStorager();
    }

    @DatabaseTable(tableName = "numeriUser")
    public static class NumeriUserTable {

        @DatabaseField(canBeNull = false, id = true)
        private String AccessToken;
        @DatabaseField(canBeNull = false)
        private String AccessTokenSecret;
        @DatabaseField(canBeNull = false)
        private String screenName;

        public NumeriUserTable() {

        }

        public NumeriUserTable(String token, String tokenSecret, String screenName) {
            this.AccessToken = token;
            this.AccessTokenSecret = tokenSecret;
            this.screenName = screenName;
        }

        public String getAccessToken() {
            return AccessToken;
        }

        public String getAccessTokenSecret() {
            return AccessTokenSecret;
        }

        public String getScreenName() {
            return screenName;
        }

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }
    }
}
