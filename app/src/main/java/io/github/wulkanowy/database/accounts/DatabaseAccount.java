package io.github.wulkanowy.database.accounts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class DatabaseAccount extends AccountAdapter {

    private String name = "name";
    private String email = "email";
    private String password = "password";
    private String county = "county";
    private String idText = "id";
    private String accounts = "accounts";

    public DatabaseAccount(Context context) {
        super(context);
    }

    public void put(AccountData accountData) throws SQLException {

        ContentValues newAccount = new ContentValues();
        newAccount.put(name, accountData.getName());
        newAccount.put(email, accountData.getEmail());
        newAccount.put(password, accountData.getPassword());
        newAccount.put(county, accountData.getCounty());

        Log.d(DatabaseHelper.DEBUG_TAG, "Put account into database");

        if (!database.isReadOnly()) {
            database.insertOrThrow(accounts, null, newAccount);
        }
    }

    public long update(AccountData accountData) {

        ContentValues updateAccount = new ContentValues();

        updateAccount.put(name, accountData.getName());
        updateAccount.put(email, accountData.getEmail());
        updateAccount.put(password, accountData.getPassword());
        updateAccount.put(county, accountData.getCounty());
        String args[] = {accountData.getId() + ""};

        Log.d(DatabaseHelper.DEBUG_TAG, "Update account into database");

        return database.update(accounts, updateAccount, "id=?", args);
    }

    public AccountData getAccount(int id) throws SQLException {

        AccountData accountData = new AccountData();

        String[] columns = {idText, name, email, password, county};
        String args[] = {id + ""};

        try {
            Cursor cursor = database.query(accounts, columns, "id=?", args, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                accountData.setId(cursor.getInt(0));
                accountData.setName(cursor.getString(1));
                accountData.setEmail(cursor.getString(2));
                accountData.setPassword(cursor.getString(3));
                accountData.setCounty(cursor.getString(4));
                cursor.close();
            }
        } catch (SQLException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw e;
        }

        Log.d(DatabaseHelper.DEBUG_TAG, "Extract account from base");

        return accountData;
    }
}
