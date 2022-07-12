package ar.edu.unicen.isistan.asistan.storage.preferences.user.profile;

public enum Gender {

    UNSPECIFIED(0,"Sin especificar"),
    MALE(1,"Hombre"),
    FEMALE(2,"Mujer"),
    OTHER(3,"Otro");

    private int code;
    private String value;

    Gender(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static Gender get(int code) {
        for (Gender gender: Gender.values()) {
            if (gender.getCode() == code)
                return gender;
        }
        return Gender.UNSPECIFIED;
    }

    public static Gender getGender(String value) {
        for (Gender gender: Gender.values()) {
            if (gender.getValue().equalsIgnoreCase(value))
                return gender;
        }
        return Gender.UNSPECIFIED;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}