package ar.edu.unicen.isistan.asistan.tourwithme;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;

public class UserPoiPreference {
    private long placeId;
    private float preference;

    public UserPoiPreference(long placeId, float pref){
        this.placeId = placeId;
        this.preference = pref;
    }

    public long getPlaceId(){
        return placeId;
    }

    public String getPreference(){
        return String.valueOf(preference);
    }
}
