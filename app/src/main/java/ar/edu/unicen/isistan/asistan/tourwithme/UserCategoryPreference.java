package ar.edu.unicen.isistan.asistan.tourwithme;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;

public class UserCategoryPreference {
    private PlaceCategory category;
    private Float preference;

    public UserCategoryPreference(PlaceCategory category, float preference){
        this.category = category;
        this.preference = preference;
    }

    public PlaceCategory getCategory() {
        return category;
    }

    public Float getPreference() {
        return preference;
    }

}
