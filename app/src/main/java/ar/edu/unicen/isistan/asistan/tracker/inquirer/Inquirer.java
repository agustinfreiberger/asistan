package ar.edu.unicen.isistan.asistan.tracker.inquirer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.application.notifications.NotificationManager;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.Configuration;
import ar.edu.unicen.isistan.asistan.storage.preferences.configuration.ConfigurationManager;
import ar.edu.unicen.isistan.asistan.utils.time.Time;
import ar.edu.unicen.isistan.asistan.views.asistan.MainActivity;

public class Inquirer {

    private static final long MIN_INTERVAL = 10800000L;
    private static final long FIVE_MINUTES = 300000L;

    private static final String NOTIFICATION_ID = "ar.edu.unicen.isistan.asistan-inquirer-notification";

    private static final String PREFERENCES = "ar.edu.unicen.isistan.asistan-inquirer";
    private static final String STATE = "state";

    private static Long lastQuestionInstance;

    private static long load(@NotNull Context context) {
        if (Inquirer.lastQuestionInstance == null) {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            Inquirer.lastQuestionInstance = preferences.getLong(STATE, 0L);
        }
        return Inquirer.lastQuestionInstance;
    }

    private static void store(@NotNull Context context, long lastQuestion) {
        Inquirer.lastQuestionInstance = lastQuestion;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(STATE,lastQuestion);
        editor.apply();
    }

    public synchronized static void clean() {
        Inquirer.lastQuestionInstance = null;
    }

    public synchronized static void visitUpdated(@NotNull Context context, @NotNull Visit visit) {
        context = context.getApplicationContext();
        if (!visit.isClosed() && visit.duration() > FIVE_MINUTES) {
            long lastQuestion = load(context);
            long current = visit.getEndTime();
            if (current - lastQuestion > Inquirer.MIN_INTERVAL) {
                Configuration configuration = ConfigurationManager.load(context);
                if (!configuration.isProgrammedTime()) {
                    checkNotification(context);
                } else {
                    Time time = new Time(current);
                    if (time.between(configuration.getStartTime(), configuration.getEndTime()))
                        checkNotification(context);
                }
            }
        }
    }

    private static void checkNotification(@NotNull Context context) {
        if (Database.getInstance().mobility().areThereVisitsToReview()) {

            long lastQuestion = System.currentTimeMillis();
            store(context, lastQuestion);

            NotificationCompat.Builder builder = NotificationManager.getInstance(context).getNotification("AsisTan tiene preguntas","Queremos saber mas sobre tus visitas", NotificationManager.STANDARD_CHANNEL_ID, true);
            Intent openIntent = new Intent(context, MainActivity.class);
            openIntent.putExtra(MainActivity.PARAMETER, MainActivity.INQUIRER_CODE);
            PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(openPendingIntent);
            NotificationManager.getInstance(context).notify(NOTIFICATION_ID.hashCode(), builder);
        }
    }

}
