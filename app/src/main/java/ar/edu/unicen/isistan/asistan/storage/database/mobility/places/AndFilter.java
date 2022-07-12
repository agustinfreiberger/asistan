package ar.edu.unicen.isistan.asistan.storage.database.mobility.places;

import java.util.ArrayList;
import java.util.Arrays;

public class AndFilter extends Filter {

    private ArrayList<Filter> filters;

    public AndFilter(Filter... filters) {
        this.filters = new ArrayList<>(Arrays.asList(filters));
    }

    @Override
    public boolean check(PlaceCategory category) {
        for (Filter filter: this.filters) {
            if (!filter.check(category))
                return false;
        }
        return true;
    }

}
