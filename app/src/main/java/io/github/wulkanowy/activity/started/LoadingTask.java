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
import io.github.wulkanowy.database.accounts.AccountData;
import io.github.wulkanowy.database.accounts.DatabaseAccount;

public class LoadingTask extends AsyncTask<Void, Void, Void> {

    private Activity activity;
    private boolean isOnline;

    private final boolean SAVE_DATA = true;

    LoadingTask(Activity main) {
        activity = main;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (!SAVE_DATA) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        isOnline = isOnline();

        return null;
    }

    protected void onPostExecute(Void result) {

       if (isOnline) {
           signIn();
       } else{
           Intent intent = new Intent(activity, MainActivity.class);
           activity.startActivity(intent);

           Toast.makeText(activity,R.string.noInternet_text,Toast.LENGTH_SHORT ).show();
       }
    }

    private boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean signIn(){

        if (SAVE_DATA) {

            DatabaseAccount databaseAccount = new DatabaseAccount(activity);
            if (databaseAccount.checkExist()) {
                try {
                    AccountData accountData = databaseAccount.getAccount(1);
                    databaseAccount.close();

                    if (accountData != null) {
                        new LoginTask(activity, false).execute(accountData.getEmail(), accountData.getPassword(), accountData.getCounty());
                        return true;
                    }
                } catch (SQLException e){
                    Toast.makeText(activity,R.string.SQLite_ioError_text,Toast.LENGTH_LONG ).show();
                }
            }
        }

        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);

        return false;
    }
}
