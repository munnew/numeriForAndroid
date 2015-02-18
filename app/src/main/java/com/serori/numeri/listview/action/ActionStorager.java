package com.serori.numeri.listview.action;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.main.Application;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;

/**
 * ツイートに対するアクションについての設定を保存したり読み込むためのクラス
 */
public class ActionStorager {


    public static ActionStorager getInstance() {
        return ActionStoragerHolder.instance;
    }

    /**
     * 現在のアクションに設定を保存する
     */
    public void saveActions() {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ActionTable.class);
            Dao<ActionTable, String> dao = helper.getDao(ActionTable.class);
            for (RespectTapPositionActions respectTapPositionAction : RespectTapPositionActions.values()) {
                dao.createOrUpdate(new ActionTable(respectTapPositionAction.getId(), respectTapPositionAction.getTwitterAction().getId()));
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
     * アクションに付いての設定を読み込み初期化する
     */
    public void initializeActions() {
        ConnectionSource connectionSource = null;
        TwitterActions.Actions[] defaultActions = new TwitterActions.Actions[6];
        int i = 0;
        for (TwitterActions.Actions action : TwitterActions.Actions.values()) {
            defaultActions[i] = action;
            i++;
            if (i == 6) break;
        }
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ActionTable.class);
            Dao<ActionTable, String> dao = helper.getDao(ActionTable.class);
            int j = 0;
            for (RespectTapPositionActions respectTapPositionAction : RespectTapPositionActions.values()) {
                ActionTable table = dao.queryForId(respectTapPositionAction.getId());
                if (table != null) {
                    respectTapPositionAction.setTwitterAction(table.getTwitterActionId());
                } else {
                    respectTapPositionAction.setTwitterAction(defaultActions[j]);
                }
                j++;
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


    private static final class ActionStoragerHolder {
        private static final ActionStorager instance = new ActionStorager();
    }

    /**
     * アクションについての設定の情報を保存するテーブルクラス
     */
    @DatabaseTable(tableName = "actionTable")
    public static class ActionTable {
        public ActionTable() {

        }

        public ActionTable(String actionId, int twitterActionId) {
            this.actionId = actionId;
            this.twitterActionId = twitterActionId;
        }

        @DatabaseField(canBeNull = false, id = true)
        private String actionId;
        @DatabaseField(canBeNull = false)
        private int twitterActionId;

        public String getActionId() {
            return actionId;
        }

        public int getTwitterActionId() {
            return twitterActionId;
        }
    }


    /**
     * タップした位置に対するアクションを保存する列挙型
     */
    public static enum RespectTapPositionActions {
        RIGHT("RIGHT"),
        CENTER("CENTER"),
        LEFT("LEFT"),
        LONG_RIGHT("LONG_RIGHT"),
        LONG_CENTER("LONG_CENTER"),
        LONG_LEFT("LONG_LEFT");


        private String id;
        private TwitterActions.Actions action;

        private RespectTapPositionActions(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public TwitterActions.Actions getTwitterAction() {
            return action;
        }

        public void setTwitterAction(TwitterActions.Actions action) {
            this.action = action;
        }

        public void setTwitterAction(int actionId) {
            for (TwitterActions.Actions action : TwitterActions.Actions.values()) {
                if (actionId == action.getId()) {
                    this.action = action;
                    break;
                }
            }
        }
    }


}
