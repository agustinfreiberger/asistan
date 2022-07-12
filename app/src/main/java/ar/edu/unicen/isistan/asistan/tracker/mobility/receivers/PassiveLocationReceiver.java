package ar.edu.unicen.isistan.asistan.tracker.mobility.receivers;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.utils.receivers.AsyncBroadcastReceiver;
import ar.edu.unicen.isistan.asistan.tracker.mobility.MobilityTracker;

public class PassiveLocationReceiver extends AsyncBroadcastReceiver {

    @Override
    public void process(@NotNull Context context, @NotNull Intent intent) {
        if (intent.getExtras() != null) {
            Object extra = intent.getExtras().get(android.location.LocationManager.KEY_LOCATION_CHANGED);
            if (extra instanceof Location) {
                Location location = (Location) extra;
                if (LocationManager.GPS_PROVIDER.equals(location.getProvider()))
                    MobilityTracker.updateGps(context, location);
                else if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider()))
                    MobilityTracker.updateNetwork(context, location);
            }
        }
    }

}
