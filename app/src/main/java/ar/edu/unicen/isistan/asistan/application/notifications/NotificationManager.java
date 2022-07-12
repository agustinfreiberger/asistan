package ar.edu.unicen.isistan.asistan.application.notifications;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import ar.edu.unicen.isistan.asistan.R;

public class NotificationManager extends ContextWrapper {

    public static final String FOREGROUND_CHANNEL_ID = "ar.edu.unicen.isistan.asistan.notification.foreground";
    public static final String FOREGROUND_MINIMIZED_CHANNEL_ID = "ar.edu.unicen.isistan.asistan.notification.foreground_minimized";
    public static final String STANDARD_CHANNEL_ID = "ar.edu.unicen.isistan.asistan.notification.standard";
    public static final String ERRORS_CHANNEL_ID = "ar.edu.unicen.isistan.asistan.notification.errors";

    private static NotificationManager INSTANCE = null;

    private android.app.NotificationManager manager;

    public static NotificationManager getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new NotificationManager(context.getApplicationContext());
        return INSTANCE;
    }

    private NotificationManager(Context context) {
        super(context.getApplicationContext());
        this.manager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel minChannel = new NotificationChannel(FOREGROUND_MINIMIZED_CHANNEL_ID,
                    getString(R.string.foreground_silence_channel),
                    android.app.NotificationManager.IMPORTANCE_MIN);
            this.manager.createNotificationChannel(minChannel);

            NotificationChannel standardChannel = new NotificationChannel(STANDARD_CHANNEL_ID,
                    getString(R.string.standard_channel),
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            this.manager.createNotificationChannel(standardChannel);

            NotificationChannel foregroundChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID,
                    getString(R.string.foreground_channel),
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            this.manager.createNotificationChannel(foregroundChannel);

            NotificationChannel errorsChannel = new NotificationChannel(ERRORS_CHANNEL_ID,
                    getString(R.string.errors_channel),
                    android.app.NotificationManager.IMPORTANCE_HIGH);
            this.manager.createNotificationChannel(errorsChannel);

        }
    }

    public void notify(int id, NotificationCompat.Builder notification) {
        this.manager.notify(id, notification.build());
    }

    public NotificationCompat.Builder getNotification(String title, String body, String channel_id) {
        return this.getNotification(title,body,channel_id, NotificationCompat.PRIORITY_MIN, false);
    }

    public NotificationCompat.Builder getNotification(String title, String body, String channel_id, boolean showIcon) {
        return this.getNotification(title,body,channel_id, NotificationCompat.PRIORITY_MIN, showIcon);
    }

    public NotificationCompat.Builder getNotification(String title, String body, String channel_id, int priority) {
        return this.getNotification(title,body,channel_id,priority,false);
    }

    public NotificationCompat.Builder getNotification(String title, String body, String channel_id, int priority, boolean showIcon) {
        return new NotificationCompat.Builder(getApplicationContext(), channel_id)
            .setContentTitle(title)
            .setContentText(body)
            .setLargeIcon(showIcon ? BitmapFactory.decodeResource(getResources(), R.mipmap.asistan_icon_foreground) : null)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setAutoCancel(true)
            .setPriority(priority);
    }

}