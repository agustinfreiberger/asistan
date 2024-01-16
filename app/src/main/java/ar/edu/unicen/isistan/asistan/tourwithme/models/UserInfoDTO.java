package ar.edu.unicen.isistan.asistan.tourwithme.models;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserCategoryPreference;

//Clase que se utiliza para enviar info entre dispositivos
public class UserInfoDTO implements Serializable {

    private String name;
    private String lastName;
    private int age;
    private Coordinate location;
    private List<UserCategoryPreference> preferences;

    public UserInfoDTO(String name, String lastName, int age, double x, double y) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        location = new Coordinate();
        this.location.setLatitude(x);
        this.location.setLongitude(y);
        this.preferences = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public List<UserCategoryPreference> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<UserCategoryPreference> preferences) {
        this.preferences = preferences;
    }

    public void addPreference(int category, float preference){
        this.preferences.add(new UserCategoryPreference(PlaceCategory.get(category), preference));
    }
}



