package io.github.wulkanowy.activity.started;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.main.Login;
import io.github.wulkanowy.activity.main.MainActivity;

public class LoadingTask extends AsyncTask<Void, Void, Void> {

    Activity activity;
    boolean isOnline;
    String idAccount;
    String email;
    String password;
    String county;

    final boolean SAVE_DATA = true;

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
           SharedPreferences sharedPreferences = activity.getSharedPreferences("io.github.wulkanowy", Context.MODE_PRIVATE);

           if (SAVE_DATA) {

               if (sharedPreferences.contains("wulkanowy")) {

                   idAccount = sharedPreferences.getString("wulkanowy", "");
                   email = sharedPreferences.getString(idAccount, "");
                   password = sharedPreferences.getString("sandi" + email, "");
                   county = sharedPreferences.getString("county" + email, "");

                   if (!email.isEmpty() || !password.isEmpty() || !county.isEmpty()) {
                       new Login(email, password, county, activity, 1).execute();
                   } else if (password.isEmpty() || email.isEmpty() || county.isEmpty()) {
                       Toast.makeText(activity, R.string.data_text, Toast.LENGTH_SHORT).show();

                   }

               } else {
                   Intent intent = new Intent(activity, MainActivity.class);
                   activity.startActivity(intent);
               }
           }
           else{
               Intent intent = new Intent(activity, MainActivity.class);
               activity.startActivity(intent);
           }
       }
       else{
           Intent intent = new Intent(activity, MainActivity.class);
           activity.startActivity(intent);

           Toast.makeText(activity,"Brak połączenia z internetem",Toast.LENGTH_SHORT ).show();
       }
    }

    public boolean isOnline() {
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
}
