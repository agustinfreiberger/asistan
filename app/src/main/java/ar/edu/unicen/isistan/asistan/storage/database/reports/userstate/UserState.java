package ar.edu.unicen.isistan.asistan.storage.database.reports.userstate;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.reports.Report;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.movement.UserMovement;

public class UserState extends Report {

    @NotNull
    private Coordinate location;
    private UserMovement currentMovement;
    private BatteryStatus batteryStatus;
    private RingMode ringMode;
    private PhoneUsage phoneUsage;

    public UserState() {
        super(ReportType.USER_STATE);
        this.location = new Coordinate();
        this.currentMovement = null;
        this.ringMode = null;
        this.batteryStatus = null;
        this.phoneUsage = null;
    }

    @NotNull
    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(@NotNull Coordinate location) {
        this.location = location;
    }

    public UserMovement getCurrentMovement() {
        return currentMovement;
    }

    public void setCurrentMovement(UserMovement currentMovement) {
        this.currentMovement = currentMovement;
    }

    public PhoneUsage getPhoneUsage() {
        return phoneUsage;
    }

    public void setPhoneUsage(PhoneUsage phoneUsage) {
        this.phoneUsage = phoneUsage;
    }

    public RingMode getRingMode() {
        return ringMode;
    }

    public void setRingMode(RingMode ringMode) {
        this.ringMode = ringMode;
    }

    public BatteryStatus getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(BatteryStatus batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public void updatePhoneUsage(PhoneUsage phoneUsage) {
        if (this.phoneUsage == null)
            this.phoneUsage = phoneUsage;
        else {
            if (phoneUsage.getLastScreenOff() != null)
                this.phoneUsage.setLastScreenOff(phoneUsage.getLastScreenOff());
            if (phoneUsage.getLastScreenOn() != null)
                this.phoneUsage.setLastScreenOn(phoneUsage.getLastScreenOn());
            if (phoneUsage.getLastUserPresent() != null)
                this.phoneUsage.setLastUserPresent(phoneUsage.getLastUserPresent());
        }
    }
}
