package ar.edu.unicen.isistan.asistan.tourwithme;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;

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
        this.location.setLatitude(x);
        this.location.setLongitude(y);
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


    public byte[] ToByteArray(){
        Gson userInfoGSON = new Gson();
        String userJson = userInfoGSON.toJson(this);
        return userJson.getBytes();
    }
}



