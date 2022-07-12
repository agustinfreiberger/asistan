package ar.edu.unicen.isistan.asistan.storage.database.mobility.places;

import java.util.ArrayList;

public class FilterByWord extends Filter {

    private ArrayList<String> words;

    public FilterByWord(String... words) {
        this.words = new ArrayList<>();
        for (String word: words)
            this.words.add(word.toLowerCase());
    }

    @Override
    public boolean check(PlaceCategory category) {
        for (String word: this.words) {
            if (category.getName().toLowerCase().contains(word) || category.getDescription().toLowerCase().contains(word))
                return true;
        }
        return false;
    }
}
