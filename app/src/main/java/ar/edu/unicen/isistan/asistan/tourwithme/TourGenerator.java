package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMPlace;
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

    public void GenerateTour(List<UserCategoryPreference> categoriesPreferenceList){
        int tamano = 0;
        Place aux;
        ArrayList<String> agregados = new ArrayList<>();

        for (UserCategoryPreference categoria: categoriesPreferenceList) {
            for (OSMPlace lugar: placesList) {
                if(tamano < tamanoMaximo && !agregados.contains(lugar.getName())){
                    if(lugar.getCategory() == categoria.getCategory().getCode()){
                        aux = new Place();
                        lugar.export(aux);
                        tourList.add(aux);
                        agregados.add(aux.getName());
                        tamano++;
                    }
                }
            }
        }
        return;
    }
}
