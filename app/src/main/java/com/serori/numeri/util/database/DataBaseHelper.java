package com.serori.numeri.util.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.serori.numeri.application.Application;

/**
 * Created by seroriKETC on 2014/12/19.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final int DB_VERSION = 1;
    public DataBaseHelper(Context context) {
        super(context, Application.getInstance().getDbName(), null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}