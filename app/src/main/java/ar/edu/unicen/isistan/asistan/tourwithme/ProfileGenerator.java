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

    List<UserPoiPreference> userPoiPreferenceList;

    public ProfileGenerator()
    {
        this.userPoiPreferenceList = new ArrayList();
    }


    @Override
    protected Object doInBackground(Object[] objects) {

        //Todas las visitas a pois únicas (agrupadas por place_id)
        List<Visit> allUserVisits = Database.getInstance().mobility().selectVisits();

        if(allUserVisits != null)
        {
            long intTravelTime, userInterestInPlace, averageVisitDuration = 0, cantVisitas = 0, averageTravelDuration = 0, cantViajes = 0;
            Commute loadedCommute;
            Place poi;

            //Por cada lugar visitado calculo su interés
            for (Visit visit : allUserVisits)
            {
                if(visit != null)
                {
                    //Todas las visitas a ese lugar
                    List<Visit> allPoiVisits = Database.getInstance().mobility().selectVisits(visit.getPlaceId());

                    for (Visit v: allPoiVisits)
                    {
                        averageVisitDuration += v.duration();
                        cantVisitas++;
                    }

                    //Promedio de duración de la visita
                    averageVisitDuration = averageVisitDuration / cantVisitas;

                    //Todos los viajes
                    List<Commute> commutes = Database.getInstance().mobility().selectCommutes();

                    if(commutes != null && commutes.size() > 0)
                    {
                        for (Commute commute:commutes)
                        {
                            //Cargo origen y destino del viaje
                            loadedCommute = Database.getInstance().mobility().selectCommuteAndContext(commute.getId());

                            //Tomo sólo el viaje con destino al POI
                            //TODO: Tomar solo los lugares turísticos (no Casa, ni hospitales, etc...)
                            if (loadedCommute.getDestination() != null && loadedCommute.getDestination().getPlaceId() == visit.getPlaceId())
                            {
                                averageTravelDuration += commute.duration();
                                cantViajes++;
                            }
                        }
                        //Promedio de duración de viaje a ese destino
                        if(cantViajes != 0)
                        {
                            averageTravelDuration = averageVisitDuration/cantViajes;
                        }
                    }



                    //Interés basado en tiempo de viaje
                    intTravelTime = (averageTravelDuration / (averageTravelDuration + averageVisitDuration));

                    //Coeficiente estimado de interés de ese poi
                    userInterestInPlace = (averageVisitDuration + intTravelTime) / 2;

                    poi = Database.getInstance().mobility().selectPlace(visit.getPlaceId()); //Si no existe devuelve null
                    userPoiPreferenceList.add(new UserPoiPreference(poi, userInterestInPlace));
                }
                averageVisitDuration = 0;
                cantVisitas = 0;
                averageTravelDuration = 0;
                cantViajes = 0;
            }

        }

        return null;
    }


    public ArrayList<String> getUserPoiPreferences()
    {
       ArrayList<String> aux = new ArrayList<>();

       if(userPoiPreferenceList != null && userPoiPreferenceList.size() > 0)
       {
           for (UserPoiPreference userPoiPreference:userPoiPreferenceList)
           {
               aux.add("Lugar: "+userPoiPreference.getPlaceName()+" Categoria: "+userPoiPreference.getCategoryName()+" Preferencia:"+ userPoiPreference.getPreference());
           }
       }
       else
       {
           aux.add("No se han encontrado visitas");
       }
       return aux;
    }


    public ArrayList<String> getTourReco(){
        ArrayList<String> aux = new ArrayList<>();
        for (UserPoiPreference userPoiPreference:userPoiPreferenceList)
        {
            //TODO: SUMAR TODAS LAS PREFERENCIAS Y CALCULAR SU PORCENTAJE EJ: <82% OUTDOOR, 18% NIGHT>
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

