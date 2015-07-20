package com.serori.numeri.listview.action;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.main.Global;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;

import lombok.Getter;
import lombok.Setter;

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
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
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
            if (action == null) {
                defaultActions[i] = TwitterActions.Actions.ACTION_NONE;
                i++;
            } else if (action != TwitterActions.Actions.FAVORITE && action != TwitterActions.Actions.RT) {
                defaultActions[i] = action;
                i++;
            }
            if (i == 6) break;
        }
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ActionTable.class);
            Dao<ActionTable, String> dao = helper.getDao(ActionTable.class);
            int j = 0;
            for (RespectTapPositionActions respectTapPositionAction : RespectTapPositionActions.values()) {
                ActionTable table = dao.queryForId(respectTapPositionAction.getId());
                if (table != null) {
                    boolean success = respectTapPositionAction.setTwitterActionForId(table.getTwitterActionId());
                    if (!success) {
                        respectTapPositionAction.setTwitterAction(defaultActions[j]);
                    }
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
        @DatabaseField(canBeNull = false, id = true)
        private String actionId;
        @DatabaseField(canBeNull = false)
        @Getter
        private String twitterActionId;

        public ActionTable() {

        }

        public ActionTable(String actionId, String twitterActionId) {
            this.actionId = actionId;
            this.twitterActionId = twitterActionId;
        }
    }


    /**
     * タップした位置に対するアクションを保存する列挙型
     */
    public enum RespectTapPositionActions {
        RIGHT("RIGHT"),
        CENTER("CENTER"),
        LEFT("LEFT"),
        LONG_RIGHT("LONG_RIGHT"),
        LONG_CENTER("LONG_CENTER"),
        LONG_LEFT("LONG_LEFT");

        @Getter
        private String id;
        @Getter
        @Setter
        private TwitterActions.Actions twitterAction;

        RespectTapPositionActions(String id) {
            this.id = id;
        }

        /**
         * twitterActionをセット
         *
         * @param twitterActionId TwitterActionID
         * @return true:成功 false 失敗
         */
        public boolean setTwitterActionForId(String twitterActionId) {
            boolean success = false;
            for (TwitterActions.Actions action : TwitterActions.Actions.values()) {
                if (twitterActionId.equals(action.getId())) {
                    this.twitterAction = action;
                    success = true;
                    break;
                }
            }
            return success;
        }
    }
}
