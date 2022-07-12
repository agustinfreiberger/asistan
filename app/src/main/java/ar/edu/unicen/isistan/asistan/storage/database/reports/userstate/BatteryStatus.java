package ar.edu.unicen.isistan.asistan.storage.database.reports.userstate;

public class BatteryStatus {

    private float batteryLevel;
    private boolean charging;

    public BatteryStatus(float batteryLevel, boolean charging) {
        this.batteryLevel = batteryLevel;
        this.charging = charging;
    }

    public float getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

}
