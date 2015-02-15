package com.serori.numeri.config;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.main.Application;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;

/**
 *
 */
public class ConfigurationStorager {

    public static ConfigurationStorager getInstance() {
        return ConfigurationStoragerHolder.instance;
    }

    public void saveNumercalConfigTable(NumericalConfiguration numericalConfiguration) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, NumericalConfigTable.class);
            Dao<NumericalConfigTable, String> dao = helper.getDao(NumericalConfigTable.class);
            NumericalConfigTable table = new NumericalConfigTable(numericalConfiguration);
            dao.createOrUpdate(table);
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

    public void saveEitherConfigTable(EitherConfiguration eitherConfiguration) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, EitherConfigTable.class);
            Dao<EitherConfigTable, String> dao = helper.getDao(EitherConfigTable.class);
            EitherConfigTable table = new EitherConfigTable(eitherConfiguration);
            dao.createOrUpdate(table);

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

    public void loadConfigurations() {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Application.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, EitherConfigTable.class);
            TableUtils.createTableIfNotExists(connectionSource, NumericalConfigTable.class);
            Dao<EitherConfigTable, String> dao = helper.getDao(EitherConfigTable.class);
            Dao<NumericalConfigTable, String> dao1 = helper.getDao(NumericalConfigTable.class);
            for (EitherConfigurations eitherConfiguration : EitherConfigurations.values()) {
                EitherConfigTable table = dao.queryForId(eitherConfiguration.getId());
                if (table != null) {
                    eitherConfiguration.setEnabled(table.getEnabled());
                }
            }

            for (NumericalConfigurations numericalConfiguration : NumericalConfigurations.values()) {
                NumericalConfigTable table = dao1.queryForId(numericalConfiguration.getId());
                if (table != null) {
                    numericalConfiguration.setNumericValue(table.getNumericValue());
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

    @DatabaseTable(tableName = "eitherConfig")
    public static class EitherConfigTable {
        EitherConfigTable() {

        }

        @DatabaseField(canBeNull = false, id = true)
        private String id;
        @DatabaseField(canBeNull = false)
        private boolean enabled;

        EitherConfigTable(EitherConfiguration eitherConfiguration) {
            this.id = eitherConfiguration.getId();
            this.enabled = eitherConfiguration.isEnabled();
        }

        public String getId() {
            return id;
        }

        public boolean getEnabled() {
            return enabled;
        }

    }

    @DatabaseTable(tableName = "numericalConfig")
    public static class NumericalConfigTable {
        NumericalConfigTable() {

        }

        @DatabaseField(canBeNull = false, id = true)
        private String id;
        @DatabaseField(canBeNull = false)
        private int numericValue;

        NumericalConfigTable(NumericalConfiguration numericalConfiguration) {
            this.id = numericalConfiguration.getId();
            this.numericValue = numericalConfiguration.getNumericValue();
        }

        public String getId() {
            return id;
        }

        public int getNumericValue() {
            return numericValue;
        }

    }


    private static final class ConfigurationStoragerHolder {
        private static final ConfigurationStorager instance = new ConfigurationStorager();
    }

    /**
     * どちらかを選択するような設定の情報を保持する列挙型
     */
    public static enum EitherConfigurations implements EitherConfiguration {
        DARK_THEME("DARK_THEME"),
        ADD_MENU_BUTTON("ADD_MENU_BUTTON"),
        SLEEPLESS("SLEEPLESS"),
        CONFIRMATION_LESS_GET_TWEET("CONFIRMATION_LESS_GET_TWEET");


        private String id;
        private boolean enabled = false;

        private EitherConfigurations(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * 数値的な値を持つ設定を保持する列挙型
     */
    public static enum NumericalConfigurations implements NumericalConfiguration {
        CHARACTER_SIZE("CHARACTER_SIZE");

        private String id;
        private int numericValue = 0;

        private NumericalConfigurations(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public int getNumericValue() {
            return numericValue;
        }

        @Override
        public void setNumericValue(int numericValue) {
            this.numericValue = numericValue;
        }

    }


}
