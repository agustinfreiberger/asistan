package ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.movement;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;

public class UserVisit extends UserMovement {

    private String placeName;
    private int placeCategory;
    private Coordinate center;

    public UserVisit() {
        super(Movement.MovementType.VISIT);
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public int getPlaceCategory() {
        return placeCategory;
    }

    public void setPlaceCategory(int placeCategory) {
        this.placeCategory = placeCategory;
    }

    public Coordinate getCenter() {
        return center;
    }

    public void setCenter(Coordinate center) {
        this.center = center;
    }
}
