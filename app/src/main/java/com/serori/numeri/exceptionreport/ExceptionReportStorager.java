package com.serori.numeri.exceptionreport;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.net.Uri;
import android.os.Build;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.serori.numeri.temp.activity.NumeriActivity;
import com.serori.numeri.main.Global;
import com.serori.numeri.util.database.DataBaseHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

/**
 */
public class ExceptionReportStorager {
    private ExceptionReportStorager() {

    }

    private static final String ID = "report";

    public static ExceptionReportStorager getInstance() {
        return ExceptionReportStoragerHolder.instance;
    }

    public void savExceptionReportTableTable(Exception e) {
        ConnectionSource connectionSource = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ExceptionReportTable.class);
            Dao<ExceptionReportTable, String> dao = helper.getDao(ExceptionReportTable.class);
            ExceptionReportTable table = new ExceptionReportTable(e);
            dao.createOrUpdate(table);
        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (SQLException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public String loadExceptionReport() {
        ConnectionSource connectionSource = null;
        String exceptionReport = null;
        try {
            DataBaseHelper helper = new DataBaseHelper(Global.getInstance().getApplicationContext());
            connectionSource = helper.getConnectionSource();
            TableUtils.createTableIfNotExists(connectionSource, ExceptionReportTable.class);
            Dao<ExceptionReportTable, String> dao = helper.getDao(ExceptionReportTable.class);
            if (!dao.queryForAll().isEmpty()) {
                exceptionReport = dao.queryForId(ID).getExceptionReport();
                dao.deleteById(ID);
            }
            return exceptionReport;
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

    public static void sendReport(NumeriActivity activity) {
        String exceptionReport = ExceptionReportStorager.getInstance().loadExceptionReport();
        if (!(exceptionReport == null)) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).setMessage("前回の起動で予期しないエラーが発生しました。\nバグレポートを送信しますか？")
                    .setPositiveButton("はい", (dialog, which) -> {
                        String info = "ブランド名 : " + Build.BRAND;
                        info += "\n" + "デバイス : " + Build.DEVICE;
                        info += "\n" + "プロダクト名 : " + Build.PRODUCT;
                        info += "\n" + "APIバージョン : " + Build.VERSION.SDK_INT + " : " + Build.VERSION.CODENAME + "\n";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + "numerical.developer@gmail.com"));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "【report】");
                        intent.putExtra(Intent.EXTRA_TEXT, info + exceptionReport);
                        activity.startActivity(intent);
                    }).setNegativeButton("キャンセル", null).create();
            activity.setCurrentShowDialog(alertDialog);
        }
    }

    @DatabaseTable
    private static class ExceptionReportTable {
        @DatabaseField(canBeNull = false)
        private String exceptionReport = "";
        @DatabaseField(canBeNull = false, id = true)
        private String id = ID;

        public ExceptionReportTable() {

        }

        public ExceptionReportTable(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.flush();
            exceptionReport = stringWriter.toString();
        }

        public String getExceptionReport() {
            return exceptionReport;
        }

    }

    private static class ExceptionReportStoragerHolder {
        private static final ExceptionReportStorager instance = new ExceptionReportStorager();
    }

}
