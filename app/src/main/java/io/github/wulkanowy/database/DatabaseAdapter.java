package io.github.wulkanowy.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseAdapter {

    private final String DATABASE_NAME = "accountdatabase.db";

    private final int DATABASE_VERSION = 3;

    public static SQLiteDatabase database;

    private DatabaseHelper databaseHelper;

    public Context context;

    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    public DatabaseAdapter open() {

        databaseHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

        try {
            database = databaseHelper.getWritableDatabase();
        } catch (SQLException e) {
            database = databaseHelper.getReadableDatabase();
            Log.w(DatabaseHelper.DEBUG_TAG, "Database in read-only");
        }

        Log.d(DatabaseHelper.DEBUG_TAG, "Open database");

        return this;
    }

    public void close() {
        databaseHelper.close();

        Log.d(DatabaseHelper.DEBUG_TAG, "Close database");
    }

    public boolean checkExist(String tableName, String dbfield, String fieldValue) {

        Cursor cursor;

        if (dbfield == null && fieldValue == null && tableName != null) {
            cursor = database.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            Log.d(DatabaseHelper.DEBUG_TAG, "Check exist " + tableName + " table");
        } else if (dbfield != null && fieldValue != null && tableName != null) {
            cursor = database.rawQuery("SELECT COUNT(*) FROM " + tableName + " WHERE " + dbfield + "=?", new String[]{fieldValue});
            Log.d(DatabaseHelper.DEBUG_TAG, "Check exist " + fieldValue + " row");
        } else {
            cursor = null;
        }

        if (cursor != null) {
            cursor.moveToFirst();

            int count = cursor.getInt(0);

            if (count > 0) {
                return true;
            }

            cursor.close();
        }

        return false;
    }

    public boolean checkExist(String tableName) {
        return checkExist(tableName, null, null);
    }

    public void deleteAndCreate(String tableName) {

        database.execSQL(databaseHelper.DROP_TABLE + tableName);
        database.execSQL(databaseHelper.SUBJECT_TABLE);
        database.execSQL(databaseHelper.ACCOUNT_TABLE);
        database.execSQL(databaseHelper.GRADE_TABLE);
        database.execSQL(databaseHelper.COOKIES_TABLE);

        Log.d(DatabaseHelper.DEBUG_TAG, "Recreate table " + tableName);

    }
}
