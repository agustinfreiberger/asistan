package ar.edu.unicen.isistan.asistan.tracker.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.jetbrains.annotations.NotNull;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.wifi.WiFiData;
import ar.edu.unicen.isistan.asistan.storage.database.wifi.WiFiScan;
import ar.edu.unicen.isistan.asistan.utils.receivers.AsyncBroadcastReceiver;

public class WiFiReceiver extends AsyncBroadcastReceiver {

    @Override
    public void process(@NotNull Context context, @NotNull Intent intent) {
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WiFiScan scanEvent = new WiFiScan();
                List<ScanResult> results = wifiManager.getScanResults();

                for (ScanResult scan : results) {
                    WiFiData data = new WiFiData(scan);
                    scanEvent.addWiFiData(data.getBssid(), data);
                }

                Database.getInstance().wifi().insert(scanEvent);
            }
        }
    }

}
