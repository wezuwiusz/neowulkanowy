package io.github.wulkanowy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DEBUG_TAG = "SQLiteWulkanowyDatabase";
    public final String ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS accounts( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "email TEXT," +
            "password TEXT, " +
            "county TEXT );";
    public final String SUBJECT_TABLE = "CREATE TABLE IF NOT EXISTS subjects( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "predictedRating1 TEXT, " +
            "finalRating1 TEXT, " +
            "predictedRating2 TEXT, " +
            "finalRating2 TEXT );";
    public final String GRADE_TABLE = "CREATE TABLE IF NOT EXISTS grades( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userID INTEGER, " +
            "subjectID INTEGER, " +
            "subject TEXT, " +
            "value TEXT, " +
            "color TEXT, " +
            "symbol TEXT, " +
            "description TEXT, " +
            "weight TEXT, " +
            "date TEXT, " +
            "teacher TEXT, " +
            "semester INTEGER, " +
            "isNew INTEGER );";

    public final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ACCOUNT_TABLE);
        db.execSQL(SUBJECT_TABLE);
        db.execSQL(GRADE_TABLE);
        Log.d(DEBUG_TAG, "Create database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + "accounts");
        db.execSQL(DROP_TABLE + "subjects");
        db.execSQL(DROP_TABLE + "grades");
        onCreate(db);
        Log.d(DEBUG_TAG, "Database upgrade from ver." + oldVersion + " to ver." + newVersion);
    }
}
