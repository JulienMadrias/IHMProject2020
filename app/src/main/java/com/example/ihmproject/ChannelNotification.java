package com.example.ihmproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class ChannelNotification extends Application {
    public static final String CHANNEL_ID="channel1";
    private static NotificationManager notificationManager;

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel("channel","Channel pour créer des notifications", NotificationManager.IMPORTANCE_DEFAULT);
    }

    private void createNotificationChannel(String nameOfChannel, String description, int importanceDefault) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, description, importanceDefault);
            channel.setDescription(description);
            notificationManager= getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);

        }
    }


}
