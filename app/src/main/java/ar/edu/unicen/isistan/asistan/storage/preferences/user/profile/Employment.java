package ar.edu.unicen.isistan.asistan.storage.preferences.user.profile;

public enum Employment {

    UNSPECIFIED(0,"Sin especificar"),
    EMPLOYEE(1,"Empleado asalariado"),
    SELF_EMPLOYEE(2,"Trabajador por cuenta propia"),
    UNEMPLOYED(3,"Desempleado"),
    RETIRED(4,"Jubilado"),
    UNABLE(5,"Incapacitado para trabajar"),
    YOUNG(6,"Menor de edad"),
    HOUSEKEEPER(7,"Ama/o de casa"),
    STUDENT_AND_EMPLOYEE(8,"Estudiante y empleado asalariado"),
    STUDENT_AND_SELF_EMPLOYEE(9,"Estudiante y trabajador por cuenta propia"),
    STUDENT(10,"Estudiante");

    private int code;
    private String name;

    Employment(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Employment get(int code) {
        for (Employment employment: Employment.values()) {
            if (employment.getCode() == code)
                return employment;
        }
        return Employment.UNSPECIFIED;
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
