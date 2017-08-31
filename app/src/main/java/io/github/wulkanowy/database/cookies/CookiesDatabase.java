package io.github.wulkanowy.database.cookies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import io.github.wulkanowy.database.DatabaseAdapter;
import io.github.wulkanowy.database.DatabaseHelper;

public class CookiesDatabase extends DatabaseAdapter {

    private String cookies = "cookies";

    public CookiesDatabase(Context context) {
        super(context);
    }

    public long put(String serializableCookiesMap) {

        ContentValues newCookie = new ContentValues();
        newCookie.put(cookies, serializableCookiesMap);

        if (!database.isReadOnly()) {
            if (!checkExist(cookies)) {
                long newId = database.insertOrThrow(cookies, null, newCookie);
                Log.d(DatabaseHelper.DEBUG_TAG, "Put cookies into database");
                return newId;
            } else {
                deleteAndCreate(cookies);
                long newId = database.insertOrThrow(cookies, null, newCookie);
                Log.d(DatabaseHelper.DEBUG_TAG, "Put cookies into database");
                return newId;
            }
        } else {
            Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
            throw new SQLException("Attempt to write on read-only database");
        }
    }

    public String getCookies() {

        String exec = "SELECT " + cookies + " FROM " + cookies;

        Cursor cursor = database.rawQuery(exec, null);

        cursor.moveToFirst();

        String cookie = cursor.getString(0);

        cursor.close();

        Log.d(DatabaseHelper.DEBUG_TAG, "Extract cookies from database");

        return cookie;
    }
}
