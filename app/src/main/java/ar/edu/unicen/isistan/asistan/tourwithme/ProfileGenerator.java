package ar.edu.unicen.isistan.asistan.tourwithme;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;

public class ProfileGenerator {

    //ver como setear -> user = logged user
    //por ahi conviene tener una lista de intereses por cada POI (Place) e ir actualizandolá

    public ProfileGenerator(){}


    //Interes de usuario en POI
    public void UserPoiInterest(Place place, Visit visit, int travelid) {
        float result = VisitTimeAverage(place) + IntTravelTime(travelid, visit);

    }

    //Promedio de estadia en POI
    private float VisitTimeAverage(Place place) {
        //for each visit in place { TotalVisitTime =+ visit.VisitTime ; cantVisitas++; }
        //result = TotalVisitTime / cantVisitas
        return 0;
    }

    //Métrica asociada al tiempo de viaje y estadía
    private float IntTravelTime(int travelid, Visit visit) {
        // float result = traveltime / (traveltime + visit.VisitTime);
        return 0;
    }

    //Interés de usuario en una categoría. ej: "pub"
    public float UserCategoryInterest(PlaceCategory category){
        //Por cada poi de la zona de categoría "pub" llamar a UserPoiInterest y sumarlos
        //float result = totalUserPoiInterest / cantCategories;
        return 0;
    }

    //Interés de usuario en una categoría ingresada explícitamente.
    public float UserCategoryInterestExplicit(PlaceCategory category){
        //Por cada poi de la zona de categoría "pub" llamar a UserPoiInterest y sumarlos
        //float result = (1 + (totalUserPoiInterest / cantCategories))/2;
        return 0;
    }
}

