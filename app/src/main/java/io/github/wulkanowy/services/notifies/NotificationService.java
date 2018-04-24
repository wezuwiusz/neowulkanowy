package io.github.wulkanowy.services.notifies;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

public class NotificationService {

    private NotificationManager manager;

    private Context context;

    public NotificationService(Context context) {
        this.context = context;
    }

    public void notify(Notification notification) {
        getManager().notify(new Random().nextInt(1000), notification);
    }

    public NotificationCompat.Builder notificationBuilder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        return new NotificationCompat.Builder(context, getChannelId());
    }

    public void cancelAll() {
        getManager().cancelAll();
    }

    NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    String getString(@StringRes int stringId) {
        return context.getString(stringId);
    }

    @TargetApi(26)
    void createChannel() {
    }

    String getChannelId() {
        return null;
    }
}