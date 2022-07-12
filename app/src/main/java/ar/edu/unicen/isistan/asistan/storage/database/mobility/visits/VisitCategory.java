package ar.edu.unicen.isistan.asistan.storage.database.mobility.visits;

import ar.edu.unicen.isistan.asistan.R;

public enum VisitCategory {

    ERROR(1,"Error", R.drawable.ic_problem_black_24dp),
    UNCONFIRMED(2,"Sin verificar", R.drawable.ic_unknown_black_24dp),
    VISIT(3,"Visita", R.drawable.ic_place_black_24dp),
    STOP(4,"Detención casual",R.drawable.ic_stop_black_24dp),
    FORGOTTEN_MOBILE(5,"Me olvidé el celular",R.drawable.ic_phone_black_24dp);

    private int code;
    private int iconSrc;
    private String name;

    VisitCategory(int code, String name, int iconSrc) {
        this.code = code;
        this.iconSrc = iconSrc;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }

    public int getIconSrc() {
        return this.iconSrc;
    }

    public String getName() {
        return this.name;
    }

    public static VisitCategory get(int code) {
        for (VisitCategory category: VisitCategory.values())
            if (category.getCode() == code)
                return category;
        return VisitCategory.VISIT;
    }

}
