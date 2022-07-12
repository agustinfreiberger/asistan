package ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes;

public enum CommuteCategory {

    DELETED(1,"Borrado"),
    COMMUTE(2,"Viaje");

    private int code;
    private String name;

    CommuteCategory(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
