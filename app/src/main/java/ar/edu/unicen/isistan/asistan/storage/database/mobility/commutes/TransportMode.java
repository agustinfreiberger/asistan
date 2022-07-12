package ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.R;

public enum TransportMode {

    UNSPECIFIED("UNSPECIFIED", -1, "Sin especificar", R.drawable.ic_unknown_black_24dp),
    FOOT("FOOT",0,"A pie", R.drawable.ic_walking_black_24dp),
    BICYCLE("BICYCLE",1,"En bicicleta", R.drawable.ic_on_bike_24dp),
    VEHICLE("VEHICLE",2,"En vehículo", R.drawable.ic_in_vehicle_24dp),
    MOTORCYCLE("MOTORCYCLE",3,"En moto", R.drawable.ic_motorcycle_black_24dp),
    TAXI("TAXI",4,"En taxi", R.drawable.ic_taxi_black_24dp),
    BUS("BUS",5,"En colectivo",R.drawable.ic_bus_black_24dp),
    SUBWAY("SUBWAY",6,"En subterraneo", R.drawable.ic_subway_black_24dp),
    RAILWAY("RAILWAY",7,"En tren", R.drawable.ic_railway_black_24dp),
    TRAM("TRAM",8,"En tranvía", R.drawable.ic_railway_black_24dp),
    PLANE("PLANE",9,"En avión", R.drawable.ic_plane_black_24dp),
    SHIP("SHIP",10,"En barco", R.drawable.ic_ship_black_24dp),
    MIXED("MIXED",98, "Mixto", R.drawable.ic_others_black_25dp),
    OTHER("OTHER",99, "Otro", R.drawable.ic_others_black_25dp);


    private int code;
    @NotNull
    private String name;
    @NotNull
    private String description;
    private int iconSrc;

    TransportMode(@NotNull String name, int code, @NotNull String description, int iconSrc) {
        this.name = name;
        this.code = code;
        this.iconSrc = iconSrc;
        this.description = description;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @NotNull
    public static TransportMode get(int code) {
        for (TransportMode mode: TransportMode.values())
            if (mode.getCode() == code)
                return mode;
        return TransportMode.UNSPECIFIED;
    }

    public int getIconSrc() {
        return iconSrc;
    }

    public void setIconSrc(int iconSrc) {
        this.iconSrc = iconSrc;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public boolean similarWays(TransportMode mode) {
        if (mode == null)
            return false;

        if (mode == this)
            return true;

        return ((this == TransportMode.BUS || this == TransportMode.VEHICLE || this == TransportMode.MOTORCYCLE || this == TransportMode.TAXI) && (mode == TransportMode.BUS || mode == TransportMode.VEHICLE || mode == TransportMode.MOTORCYCLE || mode == TransportMode.TAXI));
    }
}
