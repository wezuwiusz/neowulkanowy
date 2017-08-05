package io.github.wulkanowy.database.accounts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.util.Log;

import io.github.wulkanowy.database.DatabaseAdapter;
import io.github.wulkanowy.database.DatabaseHelper;

public class AccountsDatabase extends DatabaseAdapter {

    private String name = "name";
    private String email = "email";
    private String password = "password";
    private String county = "county";
    private String idText = "id";
    private String accounts = "accounts";

    public AccountsDatabase(Context context) {
        super(context);
    }

    public long put(Account account) throws SQLException {

        ContentValues newAccount = new ContentValues();
        newAccount.put(name, account.getName());
        newAccount.put(email, account.getEmail());
        newAccount.put(password, account.getPassword());
        newAccount.put(county, account.getCounty());

        if (!database.isReadOnly()) {
            long newId = database.insertOrThrow(accounts, null, newAccount);
            Log.d(DatabaseHelper.DEBUG_TAG, "Put account " + newId + " into database");
            return newId;
        }

        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");
    }

    public long update(Account account) throws SQLException {

        ContentValues updateAccount = new ContentValues();
        updateAccount.put(name, account.getName());
        updateAccount.put(email, account.getEmail());
        updateAccount.put(password, account.getPassword());
        updateAccount.put(county, account.getCounty());
        String args[] = {account.getId() + ""};

        if (!database.isReadOnly()) {
            long updateId = database.update(accounts, updateAccount, "id=?", args);
            Log.d(DatabaseHelper.DEBUG_TAG, "Update account " + updateId + " into database");
            return updateId;
        }

        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");
    }

    public Account getAccount(long id) throws SQLException {

        Account account = new Account();

        String[] columns = {idText, name, email, password, county};
        String args[] = {id + ""};

        try {
            Cursor cursor = database.query(accounts, columns, "id=?", args, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                account.setId(cursor.getInt(0));
                account.setName(cursor.getString(1));
                account.setEmail(cursor.getString(2));
                account.setPassword(cursor.getString(3));
                account.setCounty(cursor.getString(4));
                cursor.close();
            }
        } catch (SQLException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw e;
        } catch (CursorIndexOutOfBoundsException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw new SQLException(e.getMessage());
        }

        Log.d(DatabaseHelper.DEBUG_TAG, "Extract account " + id + " from database");

        return account;
    }
}
