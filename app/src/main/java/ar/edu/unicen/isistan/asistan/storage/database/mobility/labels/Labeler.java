package ar.edu.unicen.isistan.asistan.storage.database.mobility.labels;

import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;

public class Labeler {

    private LongSparseArray<List<Integer>> predefinedLabels;

    public Labeler() {
        this.predefinedLabels = new LongSparseArray<>();

        ArrayList<Integer> labelsWork = new ArrayList<>();
        labelsWork.add(Label.WORK.getCode());
        labelsWork.add(Label.WITH_WORK_PARTNERS.getCode());
        this.predefinedLabels.put(PlaceCategory.WORK_CATEGORY.getCode(), labelsWork);

        ArrayList<Integer> labelHome = new ArrayList<>();
        labelHome.add(Label.HOME.getCode());
        labelHome.add(Label.WITH_FAMILY.getCode());
        this.predefinedLabels.put(PlaceCategory.HOME.getCode(), labelHome);

        ArrayList<Integer> labelAccommodation = new ArrayList<>();
        labelAccommodation.add(Label.ACCOMMODATION.getCode());
        labelAccommodation.add(Label.WITH_FAMILY.getCode());
        this.predefinedLabels.put(PlaceCategory.ACCOMMODATION.getCode(), labelAccommodation);

        ArrayList<Integer> labelDailyShop = new ArrayList<>();
        labelDailyShop.add(Label.DAILY_SHOP.getCode());
        this.predefinedLabels.put(PlaceCategory.DAILY_STORE.getCode(), labelDailyShop);

        ArrayList<Integer> labelShopping = new ArrayList<>();
        labelShopping.add(Label.SHOPPING.getCode());
        this.predefinedLabels.put(PlaceCategory.SHOP_CATEGORY.getCode(), labelShopping);

        ArrayList<Integer> labelCare = new ArrayList<>();
        labelCare.add(Label.PERSONAL_CARE.getCode());
        this.predefinedLabels.put(PlaceCategory.HAIRDRESSER.getCode(), labelCare);
        this.predefinedLabels.put(PlaceCategory.AESTHETIC.getCode(), labelCare);

        ArrayList<Integer> labelPharmacy = new ArrayList<>();
        labelPharmacy.add(Label.DAILY_SHOP.getCode());
        labelPharmacy.add(Label.HEALTHY.getCode());
        this.predefinedLabels.put(PlaceCategory.HEALTHY.getCode(), labelPharmacy);

        ArrayList<Integer> labelCraft = new ArrayList<>();
        labelCraft.add(Label.REPAIR.getCode());
        this.predefinedLabels.put(PlaceCategory.CRAFT.getCode(), labelCraft);
        this.predefinedLabels.put(PlaceCategory.CAR_REPAIR.getCode(), labelCraft);

        ArrayList<Integer> labelPayPlace = new ArrayList<>();
        labelPayPlace.add(Label.PAPERWORK.getCode());
        this.predefinedLabels.put(PlaceCategory.PAY_PLACE.getCode(), labelPayPlace);
        this.predefinedLabels.put(PlaceCategory.ENTITIES_CATEGORY.getCode(), labelPayPlace);

        ArrayList<Integer> labelHealth = new ArrayList<>();
        labelHealth.add(Label.HEALTHY.getCode());
        this.predefinedLabels.put(PlaceCategory.HEALTH_CATEGORY.getCode(), labelHealth);

        ArrayList<Integer> labelEducation = new ArrayList<>();
        labelEducation.add(Label.STUDY.getCode());
        this.predefinedLabels.put(PlaceCategory.EDUCATION_CATEGORY.getCode(), labelEducation);

        ArrayList<Integer> labelReligion = new ArrayList<>();
        labelReligion.add(Label.RELIGION.getCode());
        this.predefinedLabels.put(PlaceCategory.RELIGION_CATEGORY.getCode(), labelReligion);

        ArrayList<Integer> labelTransport = new ArrayList<>();
        labelTransport.add(Label.WAIT.getCode());
        this.predefinedLabels.put(PlaceCategory.TRANSPORT_CATEGORY.getCode(), labelTransport);

        ArrayList<Integer> labelParks = new ArrayList<>();
        labelParks.add(Label.RECREATION.getCode());
        labelParks.add(Label.OUTDOOR.getCode());
        this.predefinedLabels.put(PlaceCategory.OUTDOOR_CATEGORY.getCode(), labelParks);

        ArrayList<Integer> labelSport = new ArrayList<>();
        labelSport.add(Label.SPORT.getCode());
        this.predefinedLabels.put(PlaceCategory.SPORT_CATEGORY.getCode(), labelSport);

        ArrayList<Integer> labelFood = new ArrayList<>();
        labelFood.add(Label.EAT.getCode());
        labelFood.add(Label.RECREATION.getCode());
        this.predefinedLabels.put(PlaceCategory.FOOD_CATEGORY.getCode(), labelFood);

        ArrayList<Integer> labelRecreation = new ArrayList<>();
        labelRecreation.add(Label.RECREATION.getCode());
        this.predefinedLabels.put(PlaceCategory.ART_AND_ENTERTAINMENT_CATEGORY.getCode(), labelRecreation);

        ArrayList<Integer> labelCultural = new ArrayList<>();
        labelCultural.add(Label.CULTURAL.getCode());
        this.predefinedLabels.put(PlaceCategory.MUSEUM.getCode(), labelCultural);
        this.predefinedLabels.put(PlaceCategory.LIBRARY.getCode(), labelCultural);
        this.predefinedLabels.put(PlaceCategory.THEATER.getCode(), labelCultural);
        this.predefinedLabels.put(PlaceCategory.CINEMA.getCode(), labelCultural);
        this.predefinedLabels.put(PlaceCategory.AMPHITHEATER.getCode(), labelCultural);
        this.predefinedLabels.put(PlaceCategory.ARTS_CENTRE.getCode(), labelCultural);
        this.predefinedLabels.put(PlaceCategory.PLANETARIUM.getCode(), labelCultural);

        ArrayList<Integer> labelSocial = new ArrayList<>();
        labelSocial.add(Label.WITH_FRIENDS.getCode());
        labelSocial.add(Label.RECREATION.getCode());
        this.predefinedLabels.put(PlaceCategory.SOCIAL_CATEGORY.getCode(), labelSocial);

        ArrayList<Integer> labelFriends = new ArrayList<>();
        labelFriends.add(Label.WITH_FRIENDS.getCode());
        this.predefinedLabels.put(PlaceCategory.FRIEND_HOME.getCode(), labelFriends);

        ArrayList<Integer> labelLove = new ArrayList<>();
        labelLove.add(Label.WITH_LOVE.getCode());
        this.predefinedLabels.put(PlaceCategory.LOVE_HOME.getCode(), labelLove);

        ArrayList<Integer> labelRelative = new ArrayList<>();
        labelRelative.add(Label.WITH_FAMILY.getCode());
        this.predefinedLabels.put(PlaceCategory.RELATIVE_HOME.getCode(), labelRelative);

    }

    public void label(Visit visit) {
        if (visit.getCategory() == VisitCategory.STOP.getCode()) {
            visit.addLabel(Label.WAIT.getCode());
        } else if (visit.getCategory() == VisitCategory.VISIT.getCode()) {
            Database database = Database.getInstance();
            database.mobility().populatePlace(visit);
            if (visit.getPlace() != null) {
                Place place = visit.getPlace();
                Visit lastVisit = database.mobility().lastVisitBefore(visit.getStartTime(), place.getId());
                if (lastVisit == null || lastVisit.getLabels().isEmpty()) {
                    PlaceCategory category = PlaceCategory.get(place.getPlaceCategory());
                    while (category != null) {
                        List<Integer> labels = this.predefinedLabels.get(category.getCode(),new ArrayList<Integer>());
                        for (Integer label: labels)
                            visit.addLabel(label);
                        category = category.getParent();
                    }
                } else {
                    for (Integer label: lastVisit.getLabels())
                        visit.addLabel(label);
                }
            }
        }
    }

    public void label(Commute commute) {
        // TODO ¿ACA SE PODRÍA HACER UN MEJOR ANALISIS NO? VER VIAJES ANTERIORES DESDE ESE LUGAR A ESA HORA
        // TODO VER LA CANTIDAD DE VUELTAS QUE SE DIO PARA DETERMINAR SI FUE O NO UN PASEO, ETC
        // TODO PARA ESO TENDRIA QUE RECIBIR
        commute.addLabel(Label.MOVE.getCode());
    }


    public void simplyLabel(Visit visit) {
        if (visit.getCategory() == VisitCategory.STOP.getCode()) {
            visit.addLabel(Label.WAIT.getCode());
        } else if (visit.getCategory() == VisitCategory.VISIT.getCode()) {
            Place place = visit.getPlace();
            if (place != null) {
                visit.setLabels(new ArrayList<Integer>());
                PlaceCategory category = PlaceCategory.get(place.getPlaceCategory());
                while (category != null) {
                    List<Integer> labels = this.predefinedLabels.get(category.getCode(), new ArrayList<Integer>());
                    for (Integer label : labels)
                        visit.addLabel(label);
                    category = category.getParent();
                }
            }
        }
    }
}
