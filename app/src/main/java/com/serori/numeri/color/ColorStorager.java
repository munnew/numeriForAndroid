package com.serori.numeri.color;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.main.Application;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;


/**
 * 色をローカルに保存したり読み込んだりするためのクラス
 */
public class ColorStorager {

    public static ColorStorager getInstance() {
        return ColorStoragerHolder.instance;
    }

    /**
     * 現在設定されている色情報をすべて保存する
     */
    public void saveColorData() {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ColorData.class);
            Dao<ColorData, String> dao = helper.getDao(ColorData.class);
            for (Color color : Colors.values()) {
                ColorData data = new ColorData(color);
                dao.createOrUpdate(data);
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

    /**
     * 色設定をローカルから読み込む。<br>
     * 任意のタイミングで呼び出し。
     */
    public void loadColor() {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ColorData.class);
            Dao<ColorData, String> dao = helper.getDao(ColorData.class);
            for (Colors color : Colors.values()) {
                ColorData colorData = dao.queryForId(color.getColorId());
                if (colorData == null) {
                    Log.v("ColorStrager", "return:defaultColor");
                    if (color == Colors.CHARACTER) {
                        color.setColor("#000000");
                    } else {
                        color.setColor("#FFFFFF");
                    }
                } else {
                    Log.v("ColorStrager", "return:storageColor");
                    color.setColor(colorData.getColor());
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

    /**
     * 色の情報を保存するテーブル
     */
    @DatabaseTable(tableName = "ColorData")
    public static class ColorData {
        public ColorData() {

        }

        public ColorData(Color color) {
            this.colorId = color.getColorId();
            this.color = color.getColor();
        }

        @DatabaseField(canBeNull = false, id = true)
        private String colorId;
        @DatabaseField(canBeNull = false)
        private String color;

        public String getColor() {
            return color;
        }
    }

    private static class ColorStoragerHolder {
        private static final ColorStorager instance = new ColorStorager();
    }

    /**
     * 色を保存する列挙型
     */
    public static enum Colors implements Color {

        NORMAL_ITEM("NORMAL\nITEM"),
        RT_ITEM("RT\nITEM"),
        MENTION_ITEM("MENTION\nITEM"),
        MYTWEET_MARK("MYTWEET\nMARK"),
        CHARACTER("CHARACTER");

        private String color;
        private String colorId;

        private Colors(String id) {
            colorId = id;
        }

        @Override
        public void setColor(String s) {
            this.color = s;
        }

        @Override
        public String getColor() {
            return color;
        }

        @Override
        public String getColorId() {
            return colorId;
        }

    }
}
