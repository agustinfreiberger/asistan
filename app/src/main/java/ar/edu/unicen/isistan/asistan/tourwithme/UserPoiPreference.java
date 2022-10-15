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

    public String getPlaceName(){
        return poi.getName();
    }

    public String getPreference(){
        return String.valueOf(userPreference);
    }

    public String getCategoryName() { return PlaceCategory.get(poi.getPlaceCategory()).getName(); }
}
