package ar.edu.unicen.isistan.asistan.storage.preferences.user.profile;

public enum Education {

    UNSPECIFIED(0,"Sin especificar"),
    WITHOUT_STUDIES(1,"Sin estudios"),
    INCOMPLETE_ELEMENTARY(2,"Primario incompleto"),
    ELEMENTARY(3,"Primario completo"),
    INCOMPLETE_HIGH(4,"Secundario incompleto"),
    HIGH(5,"Secundario completo"),
    INCOMPLETE_TERTIARY(6,"Terciario incompleto"),
    INCOMPLETE_UNIVERSITY(7,"Universitario incompleto"),
    TERTIARY(8,"Terciario completo"),
    UNIVERSITY(9,"Universitario completo");

    private int code;
    private String name;

    Education(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Education get(int code) {
        for (Education education: Education.values()) {
            if (education.getCode() == code)
                return education;
        }
        return Education.UNSPECIFIED;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
