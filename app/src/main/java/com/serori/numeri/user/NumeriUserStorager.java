package com.serori.numeri.user;


import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.main.Application;
import com.serori.numeri.fragment.manager.FragmentStorager;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.auth.AccessToken;

/**
 * Created by seroriKETC on 2014/12/19.
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
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
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

    public List<AccessToken> loadNumeriUserTokens() {
        ConnectionSource connectionSource = null;
        List<AccessToken> tokens = new ArrayList<>();
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, NumeriUserTable.class);
            Dao<NumeriUserTable, String> dao = helper.getDao(NumeriUserTable.class);
            List<NumeriUserTable> tables = new ArrayList<>();
            tables.addAll(dao.queryForAll());

            for (NumeriUserTable table : tables) {
                tokens.add(new AccessToken(table.getAccessToken(), table.getAccessTokenSecret()));
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
        return tokens;
    }

    public void deleteUser(String token) {
        Log.v("delete", "start");
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
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

        public String getAccessToken() {
            return AccessToken;
        }

        public void setAccessToken(String accessToken) {
            AccessToken = accessToken;
        }

        public String getAccessTokenSecret() {
            return AccessTokenSecret;
        }

        public void setAccessTokenSecret(String accessTokenSecret) {
            AccessTokenSecret = accessTokenSecret;
        }
    }
}
