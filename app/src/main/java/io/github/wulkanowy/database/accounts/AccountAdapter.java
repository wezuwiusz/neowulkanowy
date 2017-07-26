package io.github.wulkanowy.database.accounts;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AccountAdapter {

    private final String DATABASE_NAME = "accountdatabase.db";
    private final int DATABASE_VERSION = 1;
    public SQLiteDatabase database;
    private DatabaseHelper databaseHelper;
    private Context context;

    AccountAdapter(Context context) {
        this.context = context;
    }

    public AccountAdapter open() {

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

    public boolean checkExist() {

        open();

        Log.d(DatabaseHelper.DEBUG_TAG, "Check exist table");

        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM accounts", null);

        if (cursor != null) {
            cursor.moveToFirst();

            int count = cursor.getInt(0);

            if (count > 0) {
                return true;
            }

            cursor.close();
            close();
        }

        return false;
    }
}
