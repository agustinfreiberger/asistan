package ar.edu.unicen.isistan.asistan.synchronizer.data;

import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.activity.Activity;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.phone.PhoneEvent;
import ar.edu.unicen.isistan.asistan.storage.database.wifi.WiFiScan;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;

public class Data {

    private String version;
    private User user;
    private List<GeoLocation> geolocations;
    private List<Activity> activities;
    private List<AsistanEvent> asistan_events;
    private List<PhoneEvent> phone_events;
    private List<WiFiScan> wifi_scans;
    private List<Place> places;
    private List<Visit> visits;
    private List<Commute> commutes;
    private List<Event> events;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<GeoLocation> getGeolocations() {
        return geolocations;
    }

    public void setGeolocations(List<GeoLocation> geolocations) {
        this.geolocations = geolocations;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<AsistanEvent> getAsistanEvents() {
        return asistan_events;
    }

    public void setAsistanEvents(List<AsistanEvent> asistan_events) {
        this.asistan_events = asistan_events;
    }

    public List<PhoneEvent> getPhoneEvents() {
        return phone_events;
    }

    public void setPhoneEvents(List<PhoneEvent> phone_events) {
        this.phone_events = phone_events;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public List<Commute> getCommutes() {
        return commutes;
    }

    public void setCommutes(List<Commute> commutes) {
        this.commutes = commutes;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<WiFiScan> getWifiScans() {
        return wifi_scans;
    }

    public void setWifiScans(List<WiFiScan> wifi_scans) {
        this.wifi_scans = wifi_scans;
    }
}
