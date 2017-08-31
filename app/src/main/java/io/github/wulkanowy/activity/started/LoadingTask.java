package io.github.wulkanowy.activity.started;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.main.LoginTask;
import io.github.wulkanowy.activity.main.MainActivity;
import io.github.wulkanowy.database.accounts.Account;
import io.github.wulkanowy.database.accounts.AccountsDatabase;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;

public class LoadingTask extends AsyncTask<Void, Void, Void> {

    private final boolean SAVE_DATA = true;
    private Activity activity;
    private boolean isOnline;

    LoadingTask(Activity main) {
        activity = main;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isOnline = isOnline();

        return null;
    }

    protected void onPostExecute(Void result) {

        if (isOnline) {
            signIn();
        } else {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);

            Toast.makeText(activity, R.string.noInternet_text, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress address = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(address, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean signIn() {

        if (SAVE_DATA) {
            AccountsDatabase accountsDatabase = new AccountsDatabase(activity);
            accountsDatabase.open();

            if (accountsDatabase.checkExist("accounts")) {
                try {
                    Account account = accountsDatabase.getAccount(activity.getSharedPreferences("LoginData", activity.MODE_PRIVATE).getLong("isLogin", 0));
                    accountsDatabase.close();

                    if (account != null) {

                        Safety safety = new Safety(activity);

                        new LoginTask(activity, false).execute(
                                account.getEmail(),
                                safety.decrypt(account.getEmail(), account.getPassword()),
                                account.getSymbol()
                        );

                        return true;
                    }
                } catch (SQLException e) {
                    Toast.makeText(activity, R.string.SQLite_ioError_text,
                            Toast.LENGTH_LONG).show();
                } catch (CryptoException e) {
                    Toast.makeText(activity, R.string.decrypt_failed_text, Toast.LENGTH_LONG).show();
                }
            }
            accountsDatabase.close();
        }

        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);

        return false;
    }
}
