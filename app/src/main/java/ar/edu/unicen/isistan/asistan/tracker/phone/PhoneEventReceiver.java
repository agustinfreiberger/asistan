package ar.edu.unicen.isistan.asistan.tracker.phone;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.phone.PhoneEvent;
import ar.edu.unicen.isistan.asistan.utils.receivers.AsyncBroadcastReceiver;

public class PhoneEventReceiver extends AsyncBroadcastReceiver {

    @Override
    public void process(@NotNull Context context, @NotNull Intent intent) {
        PhoneEvent phoneEvent = new PhoneEvent();
        phoneEvent.setType(intent.getAction());

        if (intent.getExtras() != null)
            for (String key : intent.getExtras().keySet())
                phoneEvent.addExtra(key, intent.getExtras().get(key));

        if (LocationManager.PROVIDERS_CHANGED_ACTION.equalsIgnoreCase(intent.getAction())) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                phoneEvent.addExtra(LocationManager.GPS_PROVIDER, locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                phoneEvent.addExtra(LocationManager.NETWORK_PROVIDER, locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
            }
        }

        Database.getInstance().phoneEvent().insert(phoneEvent);
    }
}
