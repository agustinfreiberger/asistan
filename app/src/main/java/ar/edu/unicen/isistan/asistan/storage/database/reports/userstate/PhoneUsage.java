package ar.edu.unicen.isistan.asistan.storage.database.reports.userstate;

public class PhoneUsage {

    private Long lastScreenOn;
    private Long lastScreenOff;
    private Long lastUserPresent;

    public Long getLastScreenOn() {
        return lastScreenOn;
    }

    public void setLastScreenOn(Long lastScreenOn) {
        this.lastScreenOn = lastScreenOn;
    }

    public Long getLastScreenOff() {
        return lastScreenOff;
    }

    public void setLastScreenOff(Long lastScreenOff) {
        this.lastScreenOff = lastScreenOff;
    }

    public Long getLastUserPresent() {
        return lastUserPresent;
    }

    public void setLastUserPresent(Long lastUserPresent) {
        this.lastUserPresent = lastUserPresent;
    }
}
