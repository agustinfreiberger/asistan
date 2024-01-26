package ar.edu.unicen.isistan.asistan.tourwithme.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;

//Clase que se utiliza para enviar info entre dispositivos
public class UserInfoDTO implements Serializable {

    private UUID id;
    private String name;
    private String lastName;
    private int age;
    private double latitud;
    private double longitud;
    private List<UserCategoryPreference> preferences;


    public UserInfoDTO(){
    }
    public UserInfoDTO(String name, String lastName, int age, double x, double y) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.setLatitud(x);
        this.setLongitud(y);
        this.preferences = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public void setLocation(double latitud, double longitud){
         this.latitud = latitud;
         this.longitud = longitud;
    }

    public void setLatitud(double latitud){
        this.latitud = latitud;
    }

    public void setLongitud(double longitud){
        this.longitud = longitud;
    }

    public double getLatitud(){
        return this.latitud;
    }

    public double getLongitud(){
        return this.longitud;
    }

    public List<UserCategoryPreference> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<UserCategoryPreference> preferences) {
        this.preferences = preferences;
    }

    public void addPreference(int category, float preference){
        this.preferences.add(new UserCategoryPreference(category, preference));
    }
}



