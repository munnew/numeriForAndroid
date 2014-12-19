package com.serori.numeri.OAuth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.stream.StreamEvent;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.FileHandler;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

/**
 * Created by serioriKETC on 2014/12/19.
 */
public class NumeriUserStorager implements NumeriUser{
    private static final String DB_NAME = "user.db";

    public void saveNumeriUser(NumeriUserTable user){
        JdbcPooledConnectionSource connectionSource = null;
        try{
            connectionSource = new JdbcPooledConnectionSource("jdbc:sqlite:" + DB_NAME);
            Dao<NumeriUserTable,?> dao = DaoManager.createDao(connectionSource,NumeriUserTable.class);
            TableUtils.createTableIfNotExists(connectionSource, NumeriUserTable.class);
            dao.createOrUpdate(user);
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(connectionSource != null){
                try{
                    connectionSource.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Twitter getTwitter() {
        return null;
    }

    @Override
    public StreamEvent getStreamEvent() {
        return null;
    }

    @Override
    public AccessToken getAccessToken() {
        return null;
    }

    @DatabaseTable
    private class NumeriUserTable{
        @DatabaseField(canBeNull = false,id = true)
        private String AccessToken;
        @DatabaseField(canBeNull = false)
        private String AccessTokenSecret;

        public void setAccessToken(String accessToken) {
            AccessToken = accessToken;
        }

        public void setAccessTokenSecret(String accessTokenSecret) {
            AccessTokenSecret = accessTokenSecret;
        }

        public String getAccessToken() {
            return AccessToken;
        }

        public String getAccessTokenSecret() {
            return AccessTokenSecret;
        }
    }
}
