package ar.edu.unicen.isistan.asistan.storage.preferences.user.profile;

public enum Income {

    UNSPECIFIED(0,"Sin especificar"),
    UPPER_CLASS(1,"Mas de $130.000"),
    UPPER_MIDDLE_CLASS(2,"Entre $50.000 y $130.000"),
    MIDDLE_CLASS(3,"Entre $30.000 y $50.000"),
    UPPER_LOW_CLASS(4,"Entre $20.000 y $30.000"),
    LOWER_CLASS(5,"Menos de $20.000");

    private int code;
    private String name;

    Income(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Income get(int code) {
        for (Income income: Income.values()) {
            if (income.getCode() == code)
                return income;
        }
        return Income.UNSPECIFIED;
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