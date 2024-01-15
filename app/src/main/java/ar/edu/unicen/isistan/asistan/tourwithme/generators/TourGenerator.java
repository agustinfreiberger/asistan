package ar.edu.unicen.isistan.asistan.tourwithme.generators;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMPlace;
import ar.edu.unicen.isistan.asistan.storage.database.osm.categories.CategoryMapper;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserCategoryPreference;

public class TourGenerator extends AsyncTask{

    private List<OSMPlace> placesList;
    public ArrayList<Place> tourList;
    private int tamanoMaximo = 5;

    public TourGenerator(){
        placesList = new ArrayList<OSMPlace>();
        tourList = new ArrayList<Place>();
        this.execute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        //placesList = Database.getInstance().mobility().allOSMPlaces(-37.30181903359261,-37.38248010959719,-59.02130126953124,-59.18196132421875);
        placesList = Database.getInstance().mobility().allOSMPlaces2();
        return null;
    }

    //Genero el tour
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Place> GenerateTour(List<UserCategoryPreference> categoriesPreferenceList){
        int tamano = 0;
        Place aux;
        CategoryMapper categoryMapper = new CategoryMapper(true);
        ArrayList<String> agregados = new ArrayList<>();

        if(tourList.size() >0){
            tourList.clear();
        }

        if(!categoriesPreferenceList.isEmpty() && !placesList.isEmpty())
        {
            for (OSMPlace lugar : placesList) {
                for (UserCategoryPreference categoria : categoriesPreferenceList) {
                    for (PlaceCategory placeCategory : categoryMapper.getRelatedCategories(categoria.getCategory())) {
                        if (lugar.getCategory() == placeCategory.getCode()) {
                            if (tamano < tamanoMaximo) {
                                if (!agregados.contains(lugar.getName())) {
                                    aux = new Place();
                                    lugar.export(aux);
                                    tourList.add(aux);
                                    agregados.add(aux.getName());
                                    tamano++;
                                }
                            } else {
                                return tourList;
                            }
                        }
                    }
                }
            }
        }
        return tourList;
    }
}
