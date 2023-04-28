package ar.edu.unicen.isistan.asistan.tourwithme;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

//Clase que se utiliza para enviar info entre dispositivos
public class UserInfoDTO implements Serializable {

    private String name;
    private String lastName;
    private int age;
    private List<UserCategoryPreference> preferences;

    public UserInfoDTO(String name, String lastName, int age) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
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


