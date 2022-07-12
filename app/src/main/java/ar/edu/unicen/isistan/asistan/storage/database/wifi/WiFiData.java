package ar.edu.unicen.isistan.asistan.storage.database.wifi;

import android.net.wifi.ScanResult;
import android.os.Build;

import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ar.edu.unicen.isistan.asistan.utils.queues.Reusable;

public class WiFiData implements Reusable<WiFiData> {

    private static final int NO_DATA = Integer.MIN_VALUE;

    private String bssid;
    private String ssid;
    private String capabilities;
    private int centerFreq0;
    private int centerFreq1;
    private int channelWidth;
    private int frequency;
    private String operatorFriendlyName;
    private String venueName;
    private boolean is80211mcResponder;
    private boolean isPasspointNetwork;
    private int level;
    private long sinceBoot;

    public WiFiData() {
        this.init();
    }

    @Ignore
    public WiFiData(@Nullable ScanResult scan) {
        this.init(scan);
    }

    @Override
    public void init() {
        this.bssid = null;
        this.ssid = null;
        this.level = NO_DATA;
        this.capabilities = null;
        this.frequency = NO_DATA;
        this.centerFreq0 = NO_DATA;
        this.centerFreq1 = NO_DATA;
        this.operatorFriendlyName = null;
        this.channelWidth = NO_DATA;
        this.venueName = null;
        this.sinceBoot = NO_DATA;
        this.is80211mcResponder = false;
        this.isPasspointNetwork = false;
    }

    public void init(@Nullable ScanResult scan) {
        this.init();
        if (scan != null) {
            this.bssid = scan.BSSID;
            this.ssid = scan.SSID;
            this.level = scan.level;
            this.capabilities = scan.capabilities;
            this.frequency = scan.frequency;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.centerFreq0 = scan.centerFreq0;
                this.centerFreq1 = scan.centerFreq1;
                this.operatorFriendlyName = scan.operatorFriendlyName.toString();
                this.channelWidth = scan.channelWidth;
                this.venueName = scan.venueName.toString();
                this.is80211mcResponder = scan.is80211mcResponder();
                this.isPasspointNetwork = scan.isPasspointNetwork();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                this.sinceBoot = (scan.timestamp/1000);
        }
    }

    @Override
    public void init(@Nullable WiFiData wifiData) {
        this.init();
        if (wifiData != null) {
            this.bssid = wifiData.bssid;
            this.ssid = wifiData.ssid;
            this.level = wifiData.level;
            this.capabilities = wifiData.capabilities;
            this.frequency = wifiData.frequency;
            this.centerFreq0 = wifiData.centerFreq0;
            this.centerFreq1 = wifiData.centerFreq1;
            this.operatorFriendlyName = wifiData.operatorFriendlyName;
            this.channelWidth = wifiData.channelWidth;
            this.venueName = wifiData.venueName;
            this.sinceBoot = wifiData.sinceBoot;
            this.is80211mcResponder = wifiData.is80211mcResponder;
            this.isPasspointNetwork = wifiData.isPasspointNetwork;
        }
    }

    @Override
    public boolean isEmpty() {
        return level == NO_DATA;
    }

    @NotNull
    @Override
    public WiFiData copy() {
        WiFiData wifiData = new WiFiData();
        wifiData.init(this);
        return wifiData;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getCenterFreq0() {
        return centerFreq0;
    }

    public void setCenterFreq0(int centerFreq0) {
        this.centerFreq0 = centerFreq0;
    }

    public int getCenterFreq1() {
        return centerFreq1;
    }

    public void setCenterFreq1(int centerFreq1) {
        this.centerFreq1 = centerFreq1;
    }

    public int getChannelWidth() {
        return channelWidth;
    }

    public void setChannelWidth(int channelWidth) {
        this.channelWidth = channelWidth;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getOperatorFriendlyName() {
        return operatorFriendlyName;
    }

    public void setOperatorFriendlyName(String operatorFriendlyName) {
        this.operatorFriendlyName = operatorFriendlyName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public boolean isIs80211mcResponder() {
        return is80211mcResponder;
    }

    public void setIs80211mcResponder(boolean is80211mcResponder) {
        this.is80211mcResponder = is80211mcResponder;
    }

    public boolean isPasspointNetwork() {
        return isPasspointNetwork;
    }

    public void setPasspointNetwork(boolean isPasspointNetwork) {
        this.isPasspointNetwork = isPasspointNetwork;
    }

    public long getSinceBoot() {
        return sinceBoot;
    }

    public void setSinceBoot(long sinceBoot) {
        this.sinceBoot = sinceBoot;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

}
