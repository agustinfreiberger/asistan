package ar.edu.unicen.isistan.asistan.storage.database.osm.categories;

import java.util.HashMap;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;

public class SimpleMapper {

    private PlaceCategory defaultCategory;
    private HashMap<String, PlaceCategory> map;

    public SimpleMapper(PlaceCategory defaultCategory) {
        this.defaultCategory = defaultCategory;
        this.map = null;
    }

    public SimpleMapper(HashMap<String, PlaceCategory> map) {
        this.defaultCategory = PlaceCategory.UNSPECIFIED;
        this.map = map;
    }

    public SimpleMapper(PlaceCategory defaultCategory, HashMap<String, PlaceCategory> map) {
        this.defaultCategory = defaultCategory;
        this.map = map;
    }

    public PlaceCategory getCategory(String value) {
        PlaceCategory result = null;
        if (this.map != null)
            result = this.map.get(value);
        if (result == null)
            result = this.defaultCategory;
        return result;
    }

}
