package com.serori.numeri.color;

import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.application.Application;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;


/**
 * Created by serioriKETC on 2014/12/30.
 */
public class ColorStorager {

    public static ColorStorager getInstance() {
        return ColorStoragerHolder.instance;
    }


    public boolean colorsIsEmpty() {
        ConnectionSource connectionSource = null;
        boolean isEmpty = true;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ColorData.class);
            Dao<ColorData, String> dao = helper.getDao(ColorData.class);
            isEmpty = dao.queryForAll().isEmpty();

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
        return isEmpty;
    }


    public void saveColorData(ColorData data) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ColorData.class);
            Dao<ColorData, String> dao = helper.getDao(ColorData.class);
            dao.createOrUpdate(data);
            Application.getInstance().onToast("色設定を保存しました。", Toast.LENGTH_SHORT);
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

    public String loadColorForId(String colorId) {
        ConnectionSource connectionSource = null;
        String color;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ColorData.class);
            Dao<ColorData, String> dao = helper.getDao(ColorData.class);
            ColorData colorData = dao.queryForId(colorId);
            if (colorData == null) {
                Log.v("ColorStrager", "return:defaultColor");
                color = "#FFFFFF";
            } else {
                Log.v("ColorStrager", "return:storageColor");
                color = colorData.getColor();
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
        return color;
    }

    public static final String RT_ITEM = "RT_ITEM";
    public static final String MENTION_ITEM = "MENTION_ITEM";
    public static final String NOMAL_ITEM = "NOMAL_ITEM";

    @DatabaseTable(tableName = "ColorData")
    public static class ColorData {
        public ColorData() {

        }

        public ColorData(String colorId, String color) {
            this.colorId = colorId;
            this.color = color;
        }

        @DatabaseField(canBeNull = false, id = true)
        private String colorId;
        @DatabaseField(canBeNull = false)
        private String color;

        public String getColorId() {
            return colorId;
        }

        public String getColor() {
            return color;
        }
    }

    private static class ColorStoragerHolder {
        private static final ColorStorager instance = new ColorStorager();
    }
}
