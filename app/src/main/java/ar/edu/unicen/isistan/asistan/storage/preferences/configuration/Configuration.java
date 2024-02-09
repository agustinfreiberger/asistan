package ar.edu.unicen.isistan.asistan.storage.preferences.configuration;

import ar.edu.unicen.isistan.asistan.utils.time.Time;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

public class Configuration {

    private boolean running;
    private boolean programmedTime;
    private int mapView;
    private Time startTime;
    private Time endTime;

    public Configuration() {
        this.running = false;
        this.programmedTime = false;
        this.startTime = new Time(10,0);
        this.endTime= new Time(22,0);
        this.mapView = MapController.Map.OPEN_STREET_MAP.getCode();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isProgrammedTime() {
        return programmedTime;
    }

    public void setProgrammedTime(boolean programmedTime) {
        this.programmedTime = programmedTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getMapView() {
        return mapView;
    }

    public void setMapView(int mapView) {
        this.mapView = mapView;
    }

}
