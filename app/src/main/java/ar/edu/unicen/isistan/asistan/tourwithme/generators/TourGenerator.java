package ar.edu.unicen.isistan.asistan.tourwithme.generators;

import static ar.edu.unicen.isistan.asistan.tourwithme.TourWithMeActivity.myPreferences;
import static ar.edu.unicen.isistan.asistan.tourwithme.TourWithMeActivity.tourPlaces;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMArea;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMPlace;
import ar.edu.unicen.isistan.asistan.storage.database.osm.categories.CategoryMapper;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserCategoryPreference;
import ar.edu.unicen.isistan.asistan.tourwithme.models.UserInfoDTO;

public class TourGenerator extends AsyncTask{

    Coordinate currentLocation;
    private List<OSMPlace> placesList;
    private List<OSMArea> areasList;
    public ArrayList<Place> tourList;
    private int tamanoMaximo = 5;


    public TourGenerator (){
        placesList = new ArrayList<>();
        tourList = new ArrayList<>();
        currentLocation = new Coordinate();
    }

    public TourGenerator(double latitud, double longitud){
        placesList = new ArrayList<>();
        tourList = new ArrayList<>();
        this.currentLocation = new Coordinate(latitud,longitud);
        this.execute();
    }


    public Coordinate getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(double latitud, double longitud) {
        this.currentLocation = new Coordinate(latitud, longitud);
        this.execute();
    }
    @Override
    protected Object doInBackground(Object[] objects) {
        placesList = Database.getInstance().mobility().allOSMPlaces2();
        areasList = Database.getInstance().openStreetMap().nearAreas(currentLocation, 4000);
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
            for (OSMArea area: areasList) {                    //Reviso primero las áreas y luego los lugares
                for (UserCategoryPreference categoria : categoriesPreferenceList) {
                    for (PlaceCategory placeCategory : categoryMapper.getRelatedCategories(categoria.getPlacecategory())) {
                        if (area.getCategory() == placeCategory.getCode()) {
                            if (tamano < tamanoMaximo) {
                                if (!agregados.contains(area.getName())) {
                                    aux = new Place();
                                    aux.setArea(area.getArea());
                                    area.export(aux);
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

            if(tamano < tamanoMaximo){
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
            }
        return tourList;
    }

    //Genero el tour para el grupo
    public ArrayList<Place> GenerateGroupTour(List<UserInfoDTO> groupUsers){

        //Si el usuario encontrado no tiene preferencias, el tour es el generado para una persona.
        if(groupUsers.size() == 1 && (groupUsers.get(0).getPreferences()  == null || groupUsers.get(0).getPreferences().isEmpty())){
            return tourPlaces.getValue();
        }

        if(!tourList.isEmpty()){
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

        //Agrego mis preferencias
        allCategories.addAll(myPreferences);

        //Obtengo el listado de categorias unicas de los usuarios cercanos
        for (UserInfoDTO user: groupUsers) {
            for (UserCategoryPreference userPreference: user.getPreferences()) {
                if(!allCategories.contains(userPreference)){
                    allCategories.add(userPreference);
                }
            }
        }

        List<UserCategoryPreference> categoryDifferenceArray = new ArrayList<>();
        List<PlaceCategory> bestCategories = new ArrayList<>();

        //Armo el arreglo de diferencias
        for (UserCategoryPreference category: allCategories) {
            categoryDifferenceArray.add(new UserCategoryPreference(category.getPlacecategory(),calculateDifference(groupUsers,category.getPlacecategory())));
        }

        //Ordeno las diferencias de mayor a menor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            categoryDifferenceArray.sort(Comparator.comparing(UserCategoryPreference::getPreference).reversed());
        }

        for (UserCategoryPreference userCategoryPreference: categoryDifferenceArray) {
            bestCategories.add(PlaceCategory.get(userCategoryPreference.getPlacecategory()));
        }

        return bestCategories;
    }

    private static float calculateDifference(List<UserInfoDTO> users, int category) {
        float maxPreference = Integer.MIN_VALUE;
        float minPreference = Integer.MAX_VALUE;

        for (UserInfoDTO user : users) {
            if (user.getPreferences() != null && user.getPreferences().contains(category)) {
                float preference = user.getPreferences().get(category).getPreference();
                maxPreference = Math.max(maxPreference, preference);
                minPreference = Math.min(minPreference, preference);
            }
        }

        return maxPreference - minPreference;
    }

}
