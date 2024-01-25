package ar.edu.unicen.isistan.asistan.tourwithme.models;


public class UserCategoryPreference implements Comparable<UserCategoryPreference> {
    private int placecategory;
    private Float preference;

    public UserCategoryPreference(int category, float preference){
        this.placecategory = category;
        this.preference = preference;
    }

    public int getPlacecategory() {
        return placecategory;
    }

    public Float getPreference() {
        return preference;
    }


    @Override
    public int compareTo(UserCategoryPreference userCategoryPreference) {
        return Integer.compare(this.placecategory, userCategoryPreference.placecategory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCategoryPreference that = (UserCategoryPreference) o;

        return placecategory == that.placecategory;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(placecategory);
    }
}
