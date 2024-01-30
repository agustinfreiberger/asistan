package ar.edu.unicen.isistan.asistan.tourwithme.generators;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMPlace;
import ar.edu.unicen.isistan.asistan.storage.database.osm.categories.CategoryMapper;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserCategoryPreference;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;

public class TourGenerator extends AsyncTask{

    private List<OSMPlace> placesList;
    public ArrayList<Place> tourList;
    private int tamanoMaximo = 5;

    public TourGenerator(){
        placesList = new ArrayList<>();
        tourList = new ArrayList<>();
        this.execute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        placesList = Database.getInstance().mobility().allOSMPlaces2();
        return null;
    }

    //Genero el tour para una persona
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Place> GenerateTour(List<UserCategoryPreference> categoriesPreferenceList){
        int tamano = 0;
        Place aux;
        CategoryMapper categoryMapper = new CategoryMapper(true);
        ArrayList<String> agregados = new ArrayList<>();

        if(tourList.size() == 5){
            return tourList;
        }

        if(!categoriesPreferenceList.isEmpty() && !placesList.isEmpty())
        {
            for (OSMPlace lugar : placesList) {
                for (UserCategoryPreference categoria : categoriesPreferenceList) {
                    for (PlaceCategory placeCategory : categoryMapper.getRelatedCategories(categoria.getPlacecategory())) {
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


    //Genero el tour para el grupo
    public ArrayList<Place> GenerateGroupTour(List<UserInfoDTO> groupUsers){
        if(tourList.size() >0){
            tourList.clear();
        }

        int tamano = 0;
        Place aux;
        CategoryMapper categoryMapper = new CategoryMapper(true);
        ArrayList<String> agregados = new ArrayList<>();

        List<PlaceCategory> bestCategories = getMorePreferedCategories(groupUsers);

        if(!bestCategories.isEmpty() && !placesList.isEmpty())
        {
            for (OSMPlace lugar : placesList) {
                for (PlaceCategory categoria : bestCategories) {
                    for (PlaceCategory placeCategory : categoryMapper.getRelatedCategories(categoria.getCode())) {
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

    private List<PlaceCategory> getMorePreferedCategories(List<UserInfoDTO> groupUsers){
        List<UserCategoryPreference> allCategories = new ArrayList<>();

        //Obtengo el listado de categorias unicas
        for (UserInfoDTO user: groupUsers) {
            for (UserCategoryPreference userPreference: user.getPreferences()) {
                if(!allCategories.contains(userPreference)){
                    allCategories.add(userPreference);
                }
            }
        }

        float minPreference = Integer.MAX_VALUE;
        List<PlaceCategory> bestCategories = new ArrayList<>();

        //Filtro para obtener las que tienen menor diferencia entre los miembros
        for (UserCategoryPreference category: allCategories) {
            if(calculateDifference(groupUsers, category.getPlacecategory()) < minPreference){
                bestCategories.add(PlaceCategory.get(category.getPlacecategory()));
            }
        }

        return bestCategories;
    }

    private static float calculateDifference(List<UserInfoDTO> users, int category) {
        if (users == null || users.isEmpty()) {
            // Handle empty input
            return 0;
        }

        float maxPreference = Integer.MIN_VALUE;
        float minPreference = Integer.MAX_VALUE;

        for (UserInfoDTO user : users) {
            if (user.getPreferences() != null && user.getPreferences().contains(category)) {
                float preference = user.getPreferences().get(category).getPreference();
                maxPreference = Math.max(maxPreference, preference);
                minPreference = Math.min(minPreference, preference);
            }
        }

        // Calculate the difference between the max and min preferences
        return maxPreference - minPreference;
    }
}
