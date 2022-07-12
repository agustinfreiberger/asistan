package ar.edu.unicen.isistan.asistan.storage.database.reports.userstate;

public class RingMode {

    public enum RingModeType { SILENT, VIBRATE, SOUND }

    private RingModeType ringMode;
    private Float level;

    public RingModeType getRingModeType() {
        return ringMode;
    }

    public void setRingModeType(RingModeType ringMode) {
        this.ringMode = ringMode;
    }

    public Float getLevel() {
        return level;
    }

    public void setLevel(Float level) {
        this.level = level;
    }

}
