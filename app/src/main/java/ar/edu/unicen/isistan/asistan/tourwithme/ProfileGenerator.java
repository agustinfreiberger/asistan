package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

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

    HashMap<PlaceCategory, Float> categoriesPreferenceList;

    public ProfileGenerator()
    {
        this.userPoiPreferenceList = new ArrayList();
        categoriesPreferenceList = new HashMap<>();
    }


    @Override
    protected Object doInBackground(Object[] objects) {

        //Todas las visitas a pois únicas (agrupadas por place_id)
        List<Visit> allUserVisits = Database.getInstance().mobility().selectVisits();

        if(allUserVisits != null)
        {
            float intTravelTime, userInterestInPlace2, averageVisitDuration = 0, cantVisitas = 0, averageTravelDuration = 0, cantViajes = 0;
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
                    intTravelTime = (averageTravelDuration/(averageTravelDuration + averageVisitDuration));

                    //Coeficiente estimado de interés en ese poi
                    userInterestInPlace2 = (averageVisitDuration + intTravelTime) / 2;

                    poi = Database.getInstance().mobility().selectPlace(visit.getPlaceId()); //Si no existe devuelve null
                    userPoiPreferenceList.add(new UserPoiPreference(poi, userInterestInPlace2));
                }
                averageVisitDuration = 0;
                cantVisitas = 0;
                averageTravelDuration = 0;
                cantViajes = 0;
            }

        }

        //TODO: agregar un emoticon de 'Listo' cuando termina
        return null;
    }


    //unas vez que tengo los intereses de cada lugar pondero por categorías

    @RequiresApi(api = Build.VERSION_CODES.N)
    public HashMap<PlaceCategory, Float> getUserPoiPreferences()
    {
        float interesTotal = 0;
        if(categoriesPreferenceList.size() >0){
            categoriesPreferenceList.clear();
        }

       if(userPoiPreferenceList != null && userPoiPreferenceList.size() > 0)
       {
           for (UserPoiPreference userPoiPreference:userPoiPreferenceList)
           {
               //filtro las categorías que no sirven
               if(PlaceCategory.get(userPoiPreference.getPlace().getPlaceCategory()) != PlaceCategory.HOME && PlaceCategory.get(userPoiPreference.getPlace().getPlaceCategory()) != PlaceCategory.OTHERS)
               {
                   if(!categoriesPreferenceList.containsKey(PlaceCategory.get(userPoiPreference.getPlace().getPlaceCategory()).getName()))
                   {
                       //cargo el hashmap de categorías con la suma de intereses
                        categoriesPreferenceList.put(PlaceCategory.get(userPoiPreference.getPlace().getPlaceCategory()), userPoiPreference.getPreference());
                        interesTotal = interesTotal + userPoiPreference.getPreference();
                   }
                   else
                   {
                       categoriesPreferenceList.computeIfPresent(PlaceCategory.get(userPoiPreference.getPlace().getPlaceCategory()),
                               (key, val) -> val + userPoiPreference.getPreference());
                   }
               }
           }
       }

       //hago la ponderacion y la guardo
       final float interesTotalFinal = interesTotal;
        for (PlaceCategory key:categoriesPreferenceList.keySet()) {
            categoriesPreferenceList.put(key, (categoriesPreferenceList.get(key).floatValue()/interesTotal));
        }

       //categoriesPreferenceList.forEach((key,val) -> val = val.floatValue()/interesTotalFinal); //esta division no la hace

        return  categoriesPreferenceList;
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

