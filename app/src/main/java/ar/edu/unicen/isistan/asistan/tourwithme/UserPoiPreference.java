package ar.edu.unicen.isistan.asistan.tourwithme;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;

public class UserPoiPreference {
    private Place poi;
    private float userPreference;

    public UserPoiPreference(Place poi, float pref){
        this.poi = poi;
        this.userPreference = pref;
    }

    public Place getPlace() { return poi; }

    public float getPreference() { return userPreference; }
}
