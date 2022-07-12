package ar.edu.unicen.isistan.asistan.storage.database.mobility.places;

public class FilterLeaf extends Filter {

    private boolean leaf;

    public FilterLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    @Override
    public boolean check(PlaceCategory category) {
        return (category.getSubTypes().isEmpty() == this.leaf);
    }

}
