package cz.pikadorama.uome.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHelper.class.getName();

    public static final String DATABASE_NAME = "uome.db";

    private static final int DATABASE_VERSION = 4;

    private static final String ENABLE_FOREIGN_KEYS_SQL = "PRAGMA foreign_keys = ON;";

    private static final List<String> UPGRADE_SCRIPT_TO_VERSION_4 = ImmutableList.of(
            Person.Sql.RENAME_TABLE_TO_TEMP,
            Person.Sql.CREATE_TABLE,
            Person.Sql.COPY_FROM_TEMP_TABLE,
            Person.Sql.DROP_TEMP_TABLE,
            Transaction.Sql.RENAME_TABLE_TO_TEMP,
            Transaction.Sql.CREATE_TABLE,
            Transaction.Sql.COPY_FROM_TEMP_TABLE,
            Transaction.Sql.DROP_TEMP_TABLE);

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase database) {
        database.execSQL(ENABLE_FOREIGN_KEYS_SQL);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i(TAG, "Creating new database");

        database.beginTransaction();
        try {
            execSqlVerbose(database, Group.Sql.CREATE_TABLE);
            execSqlVerbose(database, Person.Sql.CREATE_TABLE);
            execSqlVerbose(database, Transaction.Sql.CREATE_TABLE);

            execSqlVerbose(database, Group.Sql.INSERT_SIMPLE_DEBTS_GROUP);

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        database.beginTransaction();
        try {
            if (oldVersion < 2) {
                upgradeToVersion2(database);
            }
            if (oldVersion < 3) {
                upgradeToVersion3(database);
            }
            if (oldVersion < 4) {
                upgradeToVersion4(database);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private static void upgradeToVersion2(SQLiteDatabase database) {
        execSqlVerbose(database, Person.Sql.ADD_EMAIL_COLUMN);
        Log.i(TAG, "Database was upgraded to version 2");
    }

    private static void upgradeToVersion3(SQLiteDatabase database) {
        execSqlVerbose(database, Person.Sql.ADD_IMAGE_URI_COLUMN);
        Log.i(TAG, "Database was upgraded to version 3");
    }

    private static void upgradeToVersion4(SQLiteDatabase database) {
        long start = System.currentTimeMillis();

        for (String statement : UPGRADE_SCRIPT_TO_VERSION_4) {
            execSqlVerbose(database, statement);
        }

        long duration = System.currentTimeMillis() - start;
        Log.i(TAG, "Database was upgraded to version 4 in " + duration + " ms");
    }

    private static void execSqlVerbose(SQLiteDatabase database, String sql) {
        database.execSQL(sql);
        Log.i(TAG, sql);
    }

}