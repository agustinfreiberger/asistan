package ar.edu.unicen.isistan.asistan.tracker.mobility.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.utils.receivers.AsyncBroadcastReceiver;
import ar.edu.unicen.isistan.asistan.tracker.mobility.MobilityTracker;

public class AirPlaneReceiver extends AsyncBroadcastReceiver {

    @Override
    public void process(@NotNull Context context, @NotNull Intent intent) {
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            boolean airPlaneMode = intent.getBooleanExtra("state", false);
            MobilityTracker.updateAirPlaneMode(context, airPlaneMode);
        }
    }

}
