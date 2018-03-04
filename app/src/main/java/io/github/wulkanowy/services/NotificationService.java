package io.github.wulkanowy.services;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

class NotificationService {

    private static final String CHANNEL_ID = "Wulkanowy_New_Grade_Channel";

    private static final String CHANNEL_NAME = "New Grade Channel";

    private NotificationManager manager;

    private Context context;

    NotificationService(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    void notify(Notification notification) {
        getManager().notify(new Random().nextInt(1000), notification);
    }

    NotificationCompat.Builder notificationBuilder() {
        return new NotificationCompat.Builder(context, CHANNEL_ID);
    }

    @TargetApi(26)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}
