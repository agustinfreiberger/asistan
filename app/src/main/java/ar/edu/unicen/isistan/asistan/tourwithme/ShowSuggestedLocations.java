package ar.edu.unicen.isistan.asistan.tourwithme;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;

public class ShowSuggestedLocations extends AppCompatActivity {

    //lista de recomendaciónes que se va a mostrar por pantalla
    ListView lv_suggestedLocations;

    Database db;
    HashMap<Place, Float> userPlacePreference;
    HashMap <PlaceCategory, Float> userCategoryPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Database.getInstance();

        setContentView(R.layout.list_suggestedlocations);

        lv_suggestedLocations = findViewById(R.id.lv_suggestedLocations);

        this.userPlacePreference = new HashMap <Place, Float> ();
        this.userCategoryPreference = new HashMap <PlaceCategory, Float> ();
    }

    //Interes de usuario en POI
    public void UserPoiInterest(Place place, Visit visit, Commute commute) {
        long TotalVisitTime = 0;
        int cantVisitas = 0;

        //visitas del CURRENT USER a ese lugar
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
