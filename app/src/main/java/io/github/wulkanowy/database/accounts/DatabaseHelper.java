package io.github.wulkanowy.database.accounts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DEBUG_TAG = "SQLiteAccountsDatabase";
    private final String ACCOUNT_TABLE = "CREATE TABLE accounts( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "email TEXT," +
            "password TEXT, " +
            "county TEXT );";
    private final String DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS accounts";

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ACCOUNT_TABLE);
        Log.d(DEBUG_TAG, "Create database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_ACCOUNT_TABLE);
        onCreate(db);
        Log.d(DEBUG_TAG, "Upgrade database");
    }
}
