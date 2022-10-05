package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.AsyncTask;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;

public class ProfileGenerator extends AsyncTask {

    HashMap<Place, Float> userPlacePreference;
    HashMap<Integer, Float> userCategoryPreference;
    List<UserPoiPreference> userPoiPreferenceList;

    public ProfileGenerator()
    {
        this.userPlacePreference = new HashMap <Place, Float> ();
        this.userCategoryPreference = new HashMap <Integer , Float> ();
        this.userPoiPreferenceList = new ArrayList();
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        //visitas a lugares agrupadas por place_id
        List<Visit> allUserVisits = Database.getInstance().mobility().selectVisits();

        if(allUserVisits != null){
            //por cada lugar visitado calculo su interés
            for (Visit visit : allUserVisits) {
                if(visit != null){

                    long intTravelTime, userInterestInPlace, averageVisitDuration = 0, cantVisitas = 0, averageTravelDuration = 0, cantViajes = 0;
                    Commute loadedCommute;

                    //visitas a ese lugar
                    List<Visit> visits = Database.getInstance().mobility().selectVisits(visit.getPlaceId());

                    for (Visit v: visits) {
                        averageVisitDuration += v.duration();
                        cantVisitas++;
                    }

                    //promedio de duración de la visita
                    averageVisitDuration = averageVisitDuration / cantVisitas;

                    //viajes
                    List<Commute> commutes = Database.getInstance().mobility().selectCommutes();

                    if(commutes != null && commutes.size() > 0){
                        for (Commute commute:commutes) {
                            //cargo origen y destino del viaje
                            loadedCommute = Database.getInstance().mobility().selectCommuteAndContext(commute.getId());
                            //viajó a ese poi?
                            if (loadedCommute.getDestination() != null && loadedCommute.getDestination().getPlaceId() == visit.getPlaceId()) {
                                averageTravelDuration += commute.duration();
                                cantViajes++;
                            }
                        }
                        //promedio de duración de viaje a ese destino
                        if(cantViajes != 0){
                            averageTravelDuration = averageVisitDuration/cantViajes;
                        }
                    }

                    //interés basado en tiempo de viaje
                    intTravelTime =  (averageTravelDuration/ (averageTravelDuration + averageVisitDuration));

                    //coeficiente estimado de interés de ese poi
                    userInterestInPlace = (averageVisitDuration + intTravelTime) / 2;

                    userPoiPreferenceList.add(new UserPoiPreference(visit.getPlaceId(), userInterestInPlace));
                }
            }
        }

        return userPoiPreferenceList.size() == 0;
    }


    public ArrayList<String> getUserPoiPreferences(){
       ArrayList<String> aux = new ArrayList<>();

       if(userPoiPreferenceList != null && userPoiPreferenceList.size() > 0){
           for (UserPoiPreference userPoiPreference:userPoiPreferenceList) {
               aux.add("Lugar: "+userPoiPreference.getPlaceId()+" Preferencia:"+ userPoiPreference.getPreference());
           }
       }

        return aux;
    }


    //Interés de usuario en una categoría. ej: "pub"
//    public float UserCategoryInterest(PlaceCategory category){
//        //Por cada poi de la zona de categoría "pub" llamar a UserPoiInterest y sumarlos
//        //float result = totalUserPoiInterest / cantCategories;
//        //userCategoryPreference.put(category, result);
//        return 0;
//    }
//
//    //Interés de usuario en una categoría ingresada explícitamente.
//    public float UserCategoryInterestExplicit(PlaceCategory category){
//        //Por cada poi de la zona de categoría "pub" llamar a UserPoiInterest y sumarlos
//        //float result = (1 + (totalUserPoiInterest / cantCategories))/2;
//        return 0;
//    }
}

