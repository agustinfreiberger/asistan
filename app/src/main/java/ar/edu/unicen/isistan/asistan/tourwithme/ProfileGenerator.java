package ar.edu.unicen.isistan.asistan.tourwithme;

import java.util.HashMap;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;

public class ProfileGenerator {

    //por ahi conviene tener una Hash lugar/interes e ir actualizandolá
    HashMap <Place, Float> userPlacePreference;

    HashMap <PlaceCategory, Float> userCategoryPreference;

    public ProfileGenerator(){
        this.userPlacePreference = new HashMap <Place, Float> ();
        this.userCategoryPreference = new HashMap <PlaceCategory, Float> ();
    }

    //Este método debería llamarse cada vez que se genera un VISIT a un Place
    //Interes de usuario en POI
    public void UserPoiInterest(Place place, Visit visit, Commute commute) {
        long TotalVisitTime = 0;
        int cantVisitas = 0;

        //visitas a ese lugar
        List<Visit> visits = Database.getInstance().mobility().selectVisits(place.getId());

        for (Visit v: visits) {
            TotalVisitTime =+ v.duration();
            cantVisitas++;
        }

        float VisitTimeAverage = TotalVisitTime / cantVisitas;

        float IntTravelTime = commute.duration() / (commute.duration() + visit.duration());

        float userInterestInPlace = (VisitTimeAverage + IntTravelTime) / 2;

        userPlacePreference.put(place, userInterestInPlace);
    }

    //Interés de usuario en una categoría. ej: "pub"
    public float UserCategoryInterest(PlaceCategory category){
        //Por cada poi de la zona de categoría "pub" llamar a UserPoiInterest y sumarlos
        //float result = totalUserPoiInterest / cantCategories;
        //userCategoryPreference.put(category, result);
        return 0;
    }

    //Interés de usuario en una categoría ingresada explícitamente.
    public float UserCategoryInterestExplicit(PlaceCategory category){
        //Por cada poi de la zona de categoría "pub" llamar a UserPoiInterest y sumarlos
        //float result = (1 + (totalUserPoiInterest / cantCategories))/2;
        return 0;
    }
}

