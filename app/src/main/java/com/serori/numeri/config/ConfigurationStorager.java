package com.serori.numeri.config;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.application.Application;
import com.serori.numeri.util.database.DataBaseHelper;

import java.sql.SQLException;

/**
 *
 */
public class ConfigurationStorager {
    public static final String THEME = "THEME";

    public static ConfigurationStorager getInstance() {
        return ConfigurationStoragerHolder.instance;
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
            Dao<EitherConfigTable, String> dao = helper.getDao(EitherConfigTable.class);
            for (EitherConfigurations eitherConfiguration : EitherConfigurations.values()) {
                EitherConfigTable table = dao.queryForId(eitherConfiguration.getId());
                if (table != null) {
                    eitherConfiguration.setEnabled(table.getEnabled());
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

    private static final class ConfigurationStoragerHolder {
        private static final ConfigurationStorager instance = new ConfigurationStorager();
    }

    /**
     * Created by serioriKETC on 2015/01/22.
     */
    public static enum EitherConfigurations implements EitherConfiguration {
        DARK_THEME("DARK_THEME");


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
}
