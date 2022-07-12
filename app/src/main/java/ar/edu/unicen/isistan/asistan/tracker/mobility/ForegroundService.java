package ar.edu.unicen.isistan.asistan.tracker.mobility;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import ar.edu.unicen.isistan.asistan.application.notifications.NotificationManager;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.tracker.Tracker;
import ar.edu.unicen.isistan.asistan.views.asistan.MainActivity;

public class ForegroundService extends Service {

    private static final String CLASS_TAG = "ForegroundService";

    private static final String NOTIFICATION_ID = "ar.edu.unicen.isistan.asistan.tracker.notification";

    private static final String SERVICE_FOREGROUND = "SERVICE_SET_AS_FOREGROUND";
    private static final String SERVICE_DESTROYED = "SERVICE_DESTROYED";
    private static final long SEVEN_SECONDS = 7000L;

    private static long creationTime;
    private static Handler handler;
    private static Runnable stopRunnable;

    private boolean firstTime;

    @Override
    public void onCreate() {
        super.onCreate();
        prepare(this.getApplicationContext());
        ForegroundService.createMinimizedNotification(this);
        this.createForegroundNotification();
        this.firstTime = true;
        creationTime = SystemClock.elapsedRealtime();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        ForegroundService.handler.removeCallbacks(ForegroundService.stopRunnable);
        if (this.firstTime){
            Context appContext = this.getApplicationContext();
            Runnable minimizeNotification = () -> ForegroundService.createMinimizedNotification(appContext);
            ForegroundService.handler.postDelayed(minimizeNotification,0);
            ForegroundService.handler.postDelayed(minimizeNotification,500);
            ForegroundService.handler.postDelayed(minimizeNotification,4000);
            this.firstTime = false;
        }
        return Service.START_STICKY;
    }

    private void createForegroundNotification() {
        NotificationCompat.Builder builder = NotificationManager.getInstance(this).getNotification("Activado", null, NotificationManager.FOREGROUND_CHANNEL_ID, (Build.VERSION.SDK_INT < Build.VERSION_CODES.O));
        builder.setOnlyAlertOnce(true);

        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(openPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText("Mantene presionado para minimizar");
            builder.setStyle(bigTextStyle);
        }

        builder.setCategory(Notification.CATEGORY_SERVICE);

        this.startForeground(NOTIFICATION_ID.hashCode(), builder.build());

        Database.getInstance().asistan().asyncInsert(new AsistanEvent(CLASS_TAG, SERVICE_FOREGROUND));
    }

    public static void createMinimizedNotification(@NonNull Context context) {
        if (Tracker.hasRequiredPermissions(context)) {
            NotificationCompat.Builder builder = NotificationManager.getInstance(context).getNotification("Activado", null, NotificationManager.FOREGROUND_MINIMIZED_CHANNEL_ID, (Build.VERSION.SDK_INT < Build.VERSION_CODES.O));
            builder.setOnlyAlertOnce(true);

            Intent openIntent = new Intent(context, MainActivity.class);
            PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(openPendingIntent);
            builder.setCategory(Notification.CATEGORY_SERVICE);

            NotificationManager.getInstance(context).notify(NOTIFICATION_ID.hashCode(), builder);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Database.getInstance().asistan().asyncInsert(new AsistanEvent(CLASS_TAG, SERVICE_DESTROYED));
    }


    public static void startService(@NonNull Context context) {
        context = context.getApplicationContext();
        prepare(context);
        ForegroundService.handler.removeCallbacks(ForegroundService.stopRunnable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent startServiceIntent = new Intent(context.getApplicationContext(), ForegroundService.class);
            context.getApplicationContext().startForegroundService(startServiceIntent);
        }
    }

    public static void stopService(@NonNull Context context) {
        context = context.getApplicationContext();

        prepare(context);

        if (creationTime != 0) {
            long time = SystemClock.elapsedRealtime();
            long diff = time - creationTime;
            if (diff > SEVEN_SECONDS) {
                Intent intent = new Intent(context, ForegroundService.class);
                context.stopService(intent);
            } else {
                ForegroundService.handler.postDelayed(ForegroundService.stopRunnable, SEVEN_SECONDS - diff);
            }
        }
    }

    private static void prepare(@NonNull Context context) {
        final Context appContext = context.getApplicationContext();

        if (ForegroundService.handler == null) {
            ForegroundService.handler = new Handler(Looper.getMainLooper());
        }

        if (ForegroundService.stopRunnable == null) {
            ForegroundService.stopRunnable = () -> {
                Intent intent = new Intent(appContext, ForegroundService.class);
                appContext.stopService(intent);
            };
        }
    }

}
