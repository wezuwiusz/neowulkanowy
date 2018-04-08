package io.github.wulkanowy.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import io.github.wulkanowy.R;

class GradeNotify extends NotificationService {

    private static final String CHANNEL_ID = "Grade_Notify";

    GradeNotify(Context context) {
        super(context);
    }

    @Override
    @TargetApi(26)
    void createChannel() {
        String channelName = getString(R.string.notify_grade_channel);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName,
                NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);
    }

    @Override
    String getChannelId() {
        return CHANNEL_ID;
    }
}
