package View.Activity;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

public class ChannelNotification extends Application {
    public static final String CHANNEL_ID = "channel1";
    private static NotificationManager notificationManager;
    private Notification notification;
    private Button button;


    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel("channel", "Channel pour créer des notifications", NotificationManager.IMPORTANCE_DEFAULT);
    }

    private void createNotificationChannel(String nameOfChannel, String description, int importanceDefault) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, description, importanceDefault);
            channel.setDescription(description);
            notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);

        }
    }

    void createNotification(){

            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Attention!")
                    .setContentText("Il y a un nouveau incident!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);



    }
}