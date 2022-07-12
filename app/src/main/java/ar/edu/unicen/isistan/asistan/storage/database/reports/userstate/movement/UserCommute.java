package ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.movement;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Step;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;

public class UserCommute extends UserMovement {

    private ArrayList<Step> steps;

    public UserCommute() {
        super(Movement.MovementType.COMMUTE);
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }
}
