package ar.edu.unicen.isistan.asistan.tracker.mobility.receivers;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.activity.Activity;
import ar.edu.unicen.isistan.asistan.utils.receivers.AsyncBroadcastReceiver;
import ar.edu.unicen.isistan.asistan.tracker.mobility.MobilityTracker;

public class ActivityReceiver extends AsyncBroadcastReceiver {

    @Override
    public void process(@NotNull Context context, @NotNull Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            MobilityTracker.updateActivity(context, result);
        }
    }

}
