package ar.edu.unicen.isistan.asistan.storage.preferences.user.profile;

public enum CivilStatus {

    UNSPECIFIED(0,"Sin especificar"),
    SINGLE(1,"Soltero/a"),
    IN_RELATIONSHIP(2, "En pareja"),
    LIVING_TOGETHER(3, "Conviviendo juntos"),
    SEPARATED_OR_DIVORCED(4, "Separada/o"),
    WIDOWER(5,"Viuda/o");

    private int code;
    private String name;

    CivilStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CivilStatus get(int code) {
        for (CivilStatus civilStatus: CivilStatus.values()) {
            if (civilStatus.getCode() == code)
                return civilStatus;
        }
        return CivilStatus.UNSPECIFIED;
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
