package ar.edu.unicen.isistan.asistan.storage.database.osm.categories;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;

public class CategoryMapper {

    private String[] KEYS = { "aeroway", "shop", "amenity", "craft", "leisure", "natural", "tourism", "landuse", "building" };
    private HashMap<String,SimpleMapper> mappers;

    public CategoryMapper() {
        this.mappers = new HashMap<>();
        this.mappers.put("aeroway",new SimpleMapper(PlaceCategory.AIRPORT));

        HashMap<String, PlaceCategory> naturalMap = new HashMap<>();
        naturalMap.put("wood",PlaceCategory.WOOD);
        naturalMap.put("scrub",PlaceCategory.WOOD);
        naturalMap.put("heath",PlaceCategory.WOOD);
        naturalMap.put("moor",PlaceCategory.WOOD);
        naturalMap.put("sand",PlaceCategory.WOOD);
        naturalMap.put("grassland",PlaceCategory.WOOD);
        naturalMap.put("shingle",PlaceCategory.WOOD);
        naturalMap.put("bare_rock",PlaceCategory.BARE_ROCK);
        naturalMap.put("fell",PlaceCategory.BARE_ROCK);
        naturalMap.put("beach",PlaceCategory.BEACH);
        this.mappers.put("natural",new SimpleMapper(PlaceCategory.UNSPECIFIED,naturalMap));

        HashMap<String, PlaceCategory> leisureMap = new HashMap<>();
        leisureMap.put("amusement_arcade",PlaceCategory.AMUSEMENT_ARCADE);
        leisureMap.put("bowling_alley",PlaceCategory.BOWLING);
        leisureMap.put("fitness_center",PlaceCategory.GYM);
        leisureMap.put("sports_centre",PlaceCategory.SPORT_CLUB);
        leisureMap.put("stadium",PlaceCategory.STADIUM);
        leisureMap.put("miniature_golf",PlaceCategory.MINIATURE_GOLF);
        leisureMap.put("dog_park",PlaceCategory.PARK);
        leisureMap.put("golf_course",PlaceCategory.GOLF);
        leisureMap.put("horse_riding",PlaceCategory.HORSE_RIDING);
        leisureMap.put("marina",PlaceCategory.PORT);
        leisureMap.put("nature_reserve",PlaceCategory.NATURE_RESERVE);
        leisureMap.put("water_park",PlaceCategory.WATER_PARK);
        leisureMap.put("park",PlaceCategory.PARK);
        leisureMap.put("garden",PlaceCategory.PARK);
        this.mappers.put("leisure",new SimpleMapper(PlaceCategory.UNSPECIFIED,leisureMap));

        HashMap<String, PlaceCategory> amenityMap = new HashMap<>();
        amenityMap.put("bar",PlaceCategory.CAFE);
        amenityMap.put("biergarten",PlaceCategory.RESTAURANT);
        amenityMap.put("cafe",PlaceCategory.CAFE);
        amenityMap.put("fast_food",PlaceCategory.FAST_FOOD);
        amenityMap.put("ice_cream",PlaceCategory.ICE_CREAM);
        amenityMap.put("pub",PlaceCategory.CAFE);
        amenityMap.put("restaurant",PlaceCategory.RESTAURANT);
        amenityMap.put("college",PlaceCategory.UNIVERSITY);
        amenityMap.put("kindergarten",PlaceCategory.KINDERGARTEN);
        amenityMap.put("library",PlaceCategory.LIBRARY);
        amenityMap.put("school",PlaceCategory.SCHOOL);
        amenityMap.put("music_school",PlaceCategory.MUSIC_SCHOOL);
        amenityMap.put("language_school",PlaceCategory.LANGUAGE_SCHOOL);
        amenityMap.put("driving_school",PlaceCategory.DRIVING_SCHOOL);
        amenityMap.put("university",PlaceCategory.UNIVERSITY);
        amenityMap.put("research_institute",PlaceCategory.RESEARCH_INSTITUTE);
        amenityMap.put("bus_station",PlaceCategory.TERMINAL);
        amenityMap.put("car_wash",PlaceCategory.CAR_WASH);
        amenityMap.put("car_rental",PlaceCategory.CAR_RENTAL);
        amenityMap.put("taxi",PlaceCategory.TAXI_STOP);
        amenityMap.put("bank",PlaceCategory.FINANCIAL_ENTITY);
        amenityMap.put("clinic",PlaceCategory.HOSPITAL);
        amenityMap.put("dentist",PlaceCategory.HOSPITAL);
        amenityMap.put("hospital",PlaceCategory.HOSPITAL);
        amenityMap.put("pharmacy",PlaceCategory.HEALTHY);
        amenityMap.put("social_facility",PlaceCategory.SOCIAL_FACILITY);
        amenityMap.put("veterinary",PlaceCategory.VETERINARY);
        amenityMap.put("arts_centre",PlaceCategory.ARTS_CENTRE);
        amenityMap.put("casino",PlaceCategory.CASINO);
        amenityMap.put("cinema",PlaceCategory.CINEMA);
        amenityMap.put("community_centre",PlaceCategory.COMMUNITY_CENTRE);
        amenityMap.put("nightclub",PlaceCategory.NIGHT_CLUB);
        amenityMap.put("planetarium",PlaceCategory.PLANETARIUM);
        amenityMap.put("theatre",PlaceCategory.THEATRE);
        amenityMap.put("courthouse",PlaceCategory.COURTHOUSE);
        amenityMap.put("crematorium",PlaceCategory.CREMATORIUM);
        amenityMap.put("dojo",PlaceCategory.DOJO);
        amenityMap.put("embassy",PlaceCategory.EMBASSY);
        amenityMap.put("fire_station",PlaceCategory.FIRE_STATION);
        amenityMap.put("gym",PlaceCategory.GYM);
        amenityMap.put("internet_cafe",PlaceCategory.CAFE);
        amenityMap.put("marketplace",PlaceCategory.MARKETPLACE);
        amenityMap.put("place_of_worship",PlaceCategory.CHURCH);
        amenityMap.put("police",PlaceCategory.POLICE);
        amenityMap.put("post_office",PlaceCategory.POST_OFFICE);
        amenityMap.put("prison",PlaceCategory.PRISON);
        amenityMap.put("townhall",PlaceCategory.PUBLIC_ORGANIZATION);
        amenityMap.put("fuel",PlaceCategory.FUEL);
        this.mappers.put("amenity",new SimpleMapper(PlaceCategory.UNSPECIFIED,amenityMap));

        HashMap<String, PlaceCategory> tourismMap = new HashMap<>();
        tourismMap.put("zoo",PlaceCategory.ZOO);
        tourismMap.put("viewpoint",PlaceCategory.VIEWPOINT);
        tourismMap.put("theme_park",PlaceCategory.THEME_PARK);
        tourismMap.put("museum",PlaceCategory.MUSEUM);
        tourismMap.put("motel",PlaceCategory.ACCOMMODATION);
        tourismMap.put("information",PlaceCategory.PUBLIC_ORGANIZATION);
        tourismMap.put("hotel",PlaceCategory.ACCOMMODATION);
        tourismMap.put("hostel",PlaceCategory.ACCOMMODATION);
        tourismMap.put("guest_house",PlaceCategory.ACCOMMODATION);
        tourismMap.put("gallery",PlaceCategory.MUSEUM);
        tourismMap.put("aquarium",PlaceCategory.AQUARIUM);
        tourismMap.put("caravan_site",PlaceCategory.CAMP_SITE);
        tourismMap.put("camp_site",PlaceCategory.CAMP_SITE);
        this.mappers.put("tourism",new SimpleMapper(PlaceCategory.UNSPECIFIED,tourismMap));

        HashMap<String, PlaceCategory> landuseMap = new HashMap<>();
        landuseMap.put("cementery",PlaceCategory.CEMETERY);
        landuseMap.put("forest",PlaceCategory.WOOD);
        landuseMap.put("farmland",PlaceCategory.RURAL_AREA);
        landuseMap.put("recreation_ground",PlaceCategory.PARK);
        this.mappers.put("landuse",new SimpleMapper(PlaceCategory.UNSPECIFIED,landuseMap));

        HashMap<String, PlaceCategory> buildingMap = new HashMap<>();
        buildingMap.put("hotel",PlaceCategory.ACCOMMODATION);
        buildingMap.put("commercial",PlaceCategory.COMMERCE);
        buildingMap.put("office",PlaceCategory.OFFICE);
        buildingMap.put("supermarket",PlaceCategory.SUPERMARKET);
        buildingMap.put("retail",PlaceCategory.SHOPPING);
        buildingMap.put("cathedral",PlaceCategory.CHURCH);
        buildingMap.put("church",PlaceCategory.CHURCH);
        buildingMap.put("temple",PlaceCategory.CHURCH);
        buildingMap.put("synagogue",PlaceCategory.CHURCH);
        buildingMap.put("kindergarten",PlaceCategory.KINDERGARTEN);
        buildingMap.put("hospital",PlaceCategory.HOSPITAL);
        buildingMap.put("civic",PlaceCategory.PUBLIC_ORGANIZATION);
        buildingMap.put("government",PlaceCategory.PUBLIC_ORGANIZATION);
        buildingMap.put("school",PlaceCategory.SCHOOL);
        buildingMap.put("stadium",PlaceCategory.STADIUM);
        buildingMap.put("train_station",PlaceCategory.TRAIN_STATION);
        buildingMap.put("university",PlaceCategory.UNIVERSITY);
        buildingMap.put("public",PlaceCategory.PUBLIC_ORGANIZATION);
        buildingMap.put("parking",PlaceCategory.PARKING);
        this.mappers.put("building",new SimpleMapper(PlaceCategory.UNSPECIFIED,buildingMap));

        HashMap<String, PlaceCategory> craftMap = new HashMap<>();
        craftMap.put("carpenter",PlaceCategory.CARPENTER);
        craftMap.put("car_repair",PlaceCategory.CAR_REPAIR);
        this.mappers.put("craft",new SimpleMapper(PlaceCategory.CRAFT,craftMap));

        HashMap<String, PlaceCategory> shopMap = new HashMap<>();
        shopMap.put("alcohol",PlaceCategory.WINERY);
        shopMap.put("bakery",PlaceCategory.BAKERY);
        shopMap.put("butcher",PlaceCategory.BUTCHER);
        shopMap.put("convenience",PlaceCategory.DAIRY_STORE);
        shopMap.put("deli",PlaceCategory.DAIRY_STORE);
        shopMap.put("dairy",PlaceCategory.DAIRY_STORE);
        shopMap.put("greengrocer",PlaceCategory.GREENGROCER);
        shopMap.put("health_food",PlaceCategory.SPICES);
        shopMap.put("ice_cream",PlaceCategory.ICE_CREAM);
        shopMap.put("pasta",PlaceCategory.PASTA);
        shopMap.put("pastry",PlaceCategory.BAKERY);
        shopMap.put("seafood",PlaceCategory.SEAFOOD);
        shopMap.put("spices",PlaceCategory.SPICES);
        shopMap.put("wine",PlaceCategory.WINERY);
        shopMap.put("department_store",PlaceCategory.SHOPPING);
        shopMap.put("general",PlaceCategory.DAIRY_STORE);
        shopMap.put("kiosk",PlaceCategory.KIOSK);
        shopMap.put("mall",PlaceCategory.SHOPPING);
        shopMap.put("supermarket",PlaceCategory.SUPERMARKET);
        shopMap.put("clothes",PlaceCategory.CLOTHES);
        shopMap.put("fashion",PlaceCategory.CLOTHES);
        shopMap.put("boutique",PlaceCategory.CLOTHES);
        shopMap.put("fashion_accessories",PlaceCategory.CLOTHES);
        shopMap.put("jewelry",PlaceCategory.JEWELRY);
        shopMap.put("leather",PlaceCategory.CLOTHES);
        shopMap.put("shoes",PlaceCategory.CLOTHES);
        shopMap.put("watches",PlaceCategory.JEWELRY);
        shopMap.put("second_hand",PlaceCategory.CLOTHES);
        shopMap.put("beauty",PlaceCategory.AESTHETIC);
        shopMap.put("cosmetics",PlaceCategory.AESTHETIC);
        shopMap.put("hairdresser",PlaceCategory.HAIRDRESSER);
        shopMap.put("hearing_aids",PlaceCategory.HAIRDRESSER);
        shopMap.put("medical_supply",PlaceCategory.HEALTHY);
        shopMap.put("optician",PlaceCategory.HEALTHY);
        shopMap.put("appliance",PlaceCategory.FURNITURE);
        shopMap.put("antiques",PlaceCategory.FURNITURE);
        shopMap.put("bed",PlaceCategory.FURNITURE);
        shopMap.put("furniture",PlaceCategory.FURNITURE);
        shopMap.put("computer",PlaceCategory.ELECTRONICS);
        shopMap.put("electronics",PlaceCategory.ELECTRICAL);
        shopMap.put("hifi",PlaceCategory.ELECTRONICS);
        shopMap.put("mobile_phone",PlaceCategory.ELECTRONICS);
        shopMap.put("radiotechnics",PlaceCategory.ELECTRICAL);
        shopMap.put("fireplace",PlaceCategory.FURNITURE);
        shopMap.put("vacuum_cleaner",PlaceCategory.ELECTRONICS);
        shopMap.put("pet",PlaceCategory.VETERINARY);
        shopMap.put("pet_grooming",PlaceCategory.VETERINARY);
        shopMap.put("tyres",PlaceCategory.CAR_REPAIR);
        this.mappers.put("shop",new SimpleMapper(PlaceCategory.SHOP,shopMap));
    }

    public CategoryMapper(boolean tourwithme){
        this.mappers = new HashMap<>();
        this.mappers.put("aeroway",new SimpleMapper(PlaceCategory.AIRPORT));

        HashMap<String, PlaceCategory> naturalMap = new HashMap<>();
        naturalMap.put("wood",PlaceCategory.WOOD);
        naturalMap.put("scrub",PlaceCategory.WOOD);
        naturalMap.put("heath",PlaceCategory.WOOD);
        naturalMap.put("moor",PlaceCategory.WOOD);
        naturalMap.put("sand",PlaceCategory.WOOD);
        naturalMap.put("grassland",PlaceCategory.WOOD);
        naturalMap.put("shingle",PlaceCategory.WOOD);
        naturalMap.put("bare_rock",PlaceCategory.BARE_ROCK);
        naturalMap.put("fell",PlaceCategory.BARE_ROCK);
        naturalMap.put("beach",PlaceCategory.BEACH);
        this.mappers.put("natural",new SimpleMapper(PlaceCategory.UNSPECIFIED,naturalMap));

        HashMap<String, PlaceCategory> leisureMap = new HashMap<>();
        leisureMap.put("amusement_arcade",PlaceCategory.AMUSEMENT_ARCADE);
        leisureMap.put("bowling_alley",PlaceCategory.BOWLING);
        leisureMap.put("fitness_center",PlaceCategory.GYM);
        leisureMap.put("sports_centre",PlaceCategory.SPORT_CLUB);
        leisureMap.put("stadium",PlaceCategory.STADIUM);
        leisureMap.put("miniature_golf",PlaceCategory.MINIATURE_GOLF);
        leisureMap.put("dog_park",PlaceCategory.PARK);
        leisureMap.put("golf_course",PlaceCategory.GOLF);
        leisureMap.put("horse_riding",PlaceCategory.HORSE_RIDING);
        leisureMap.put("marina",PlaceCategory.PORT);
        leisureMap.put("nature_reserve",PlaceCategory.NATURE_RESERVE);
        leisureMap.put("water_park",PlaceCategory.WATER_PARK);
        leisureMap.put("park",PlaceCategory.PARK);
        leisureMap.put("garden",PlaceCategory.PARK);
        this.mappers.put("leisure",new SimpleMapper(PlaceCategory.UNSPECIFIED,leisureMap));

        HashMap<String, PlaceCategory> amenityMap = new HashMap<>();
        amenityMap.put("bar",PlaceCategory.CAFE);
        amenityMap.put("biergarten",PlaceCategory.RESTAURANT);
        amenityMap.put("cafe",PlaceCategory.CAFE);
        amenityMap.put("fast_food",PlaceCategory.FAST_FOOD);
        amenityMap.put("ice_cream",PlaceCategory.ICE_CREAM);
        amenityMap.put("pub",PlaceCategory.CAFE);
        amenityMap.put("restaurant",PlaceCategory.RESTAURANT);
        amenityMap.put("library",PlaceCategory.LIBRARY);
        amenityMap.put("arts_centre",PlaceCategory.ARTS_CENTRE);
        amenityMap.put("casino",PlaceCategory.CASINO);
        amenityMap.put("cinema",PlaceCategory.CINEMA);
        amenityMap.put("nightclub",PlaceCategory.NIGHT_CLUB);
        amenityMap.put("planetarium",PlaceCategory.PLANETARIUM);
        amenityMap.put("theatre",PlaceCategory.THEATRE);
        amenityMap.put("internet_cafe",PlaceCategory.CAFE);
        amenityMap.put("marketplace",PlaceCategory.MARKETPLACE);

        this.mappers.put("amenity",new SimpleMapper(PlaceCategory.UNSPECIFIED,amenityMap));

        HashMap<String, PlaceCategory> tourismMap = new HashMap<>();
        tourismMap.put("zoo",PlaceCategory.ZOO);
        tourismMap.put("viewpoint",PlaceCategory.VIEWPOINT);
        tourismMap.put("theme_park",PlaceCategory.THEME_PARK);
        tourismMap.put("museum",PlaceCategory.MUSEUM);
        tourismMap.put("information",PlaceCategory.PUBLIC_ORGANIZATION);
        tourismMap.put("hotel",PlaceCategory.ACCOMMODATION);
        tourismMap.put("hostel",PlaceCategory.ACCOMMODATION);
        tourismMap.put("guest_house",PlaceCategory.ACCOMMODATION);
        tourismMap.put("gallery",PlaceCategory.MUSEUM);
        tourismMap.put("aquarium",PlaceCategory.AQUARIUM);
        tourismMap.put("caravan_site",PlaceCategory.CAMP_SITE);
        tourismMap.put("camp_site",PlaceCategory.CAMP_SITE);
        this.mappers.put("tourism",new SimpleMapper(PlaceCategory.UNSPECIFIED,tourismMap));

        HashMap<String, PlaceCategory> landuseMap = new HashMap<>();
        landuseMap.put("forest",PlaceCategory.WOOD);
        landuseMap.put("farmland",PlaceCategory.RURAL_AREA);
        landuseMap.put("recreation_ground",PlaceCategory.PARK);
        this.mappers.put("landuse",new SimpleMapper(PlaceCategory.UNSPECIFIED,landuseMap));

        HashMap<String, PlaceCategory> buildingMap = new HashMap<>();
        buildingMap.put("hotel",PlaceCategory.ACCOMMODATION);
        buildingMap.put("commercial",PlaceCategory.COMMERCE);
        buildingMap.put("office",PlaceCategory.OFFICE);
        buildingMap.put("supermarket",PlaceCategory.SUPERMARKET);
        buildingMap.put("retail",PlaceCategory.SHOPPING);
        buildingMap.put("cathedral",PlaceCategory.CHURCH);
        buildingMap.put("church",PlaceCategory.CHURCH);
        buildingMap.put("temple",PlaceCategory.CHURCH);
        buildingMap.put("synagogue",PlaceCategory.CHURCH);
        buildingMap.put("government",PlaceCategory.PUBLIC_ORGANIZATION);
        buildingMap.put("stadium",PlaceCategory.STADIUM);
        buildingMap.put("university",PlaceCategory.UNIVERSITY);
        buildingMap.put("public",PlaceCategory.PUBLIC_ORGANIZATION);
        this.mappers.put("building",new SimpleMapper(PlaceCategory.UNSPECIFIED,buildingMap));

        HashMap<String, PlaceCategory> craftMap = new HashMap<>();
        craftMap.put("carpenter",PlaceCategory.CARPENTER);
        craftMap.put("car_repair",PlaceCategory.CAR_REPAIR);
        this.mappers.put("craft",new SimpleMapper(PlaceCategory.CRAFT,craftMap));

        HashMap<String, PlaceCategory> shopMap = new HashMap<>();
        shopMap.put("alcohol",PlaceCategory.WINERY);
        shopMap.put("bakery",PlaceCategory.BAKERY);
        shopMap.put("butcher",PlaceCategory.BUTCHER);
        shopMap.put("convenience",PlaceCategory.DAIRY_STORE);
        shopMap.put("deli",PlaceCategory.DAIRY_STORE);
        shopMap.put("dairy",PlaceCategory.DAIRY_STORE);
        shopMap.put("greengrocer",PlaceCategory.GREENGROCER);
        shopMap.put("health_food",PlaceCategory.SPICES);
        shopMap.put("ice_cream",PlaceCategory.ICE_CREAM);
        shopMap.put("pasta",PlaceCategory.PASTA);
        shopMap.put("pastry",PlaceCategory.BAKERY);
        shopMap.put("seafood",PlaceCategory.SEAFOOD);
        shopMap.put("spices",PlaceCategory.SPICES);
        shopMap.put("wine",PlaceCategory.WINERY);
        shopMap.put("department_store",PlaceCategory.SHOPPING);
        shopMap.put("general",PlaceCategory.DAIRY_STORE);
        shopMap.put("kiosk",PlaceCategory.KIOSK);
        shopMap.put("mall",PlaceCategory.SHOPPING);
        shopMap.put("supermarket",PlaceCategory.SUPERMARKET);
        shopMap.put("clothes",PlaceCategory.CLOTHES);
        shopMap.put("fashion",PlaceCategory.CLOTHES);
        shopMap.put("boutique",PlaceCategory.CLOTHES);
        shopMap.put("fashion_accessories",PlaceCategory.CLOTHES);
        shopMap.put("jewelry",PlaceCategory.JEWELRY);
        shopMap.put("leather",PlaceCategory.CLOTHES);
        shopMap.put("shoes",PlaceCategory.CLOTHES);
        shopMap.put("watches",PlaceCategory.JEWELRY);
        shopMap.put("second_hand",PlaceCategory.CLOTHES);
        shopMap.put("beauty",PlaceCategory.AESTHETIC);
        shopMap.put("cosmetics",PlaceCategory.AESTHETIC);
        shopMap.put("hairdresser",PlaceCategory.HAIRDRESSER);
        shopMap.put("hearing_aids",PlaceCategory.HAIRDRESSER);
        shopMap.put("medical_supply",PlaceCategory.HEALTHY);
        shopMap.put("optician",PlaceCategory.HEALTHY);
        shopMap.put("appliance",PlaceCategory.FURNITURE);
        shopMap.put("antiques",PlaceCategory.FURNITURE);
        shopMap.put("bed",PlaceCategory.FURNITURE);
        shopMap.put("furniture",PlaceCategory.FURNITURE);
        shopMap.put("computer",PlaceCategory.ELECTRONICS);
        shopMap.put("electronics",PlaceCategory.ELECTRICAL);
        shopMap.put("hifi",PlaceCategory.ELECTRONICS);
        shopMap.put("mobile_phone",PlaceCategory.ELECTRONICS);
        shopMap.put("radiotechnics",PlaceCategory.ELECTRICAL);
        shopMap.put("fireplace",PlaceCategory.FURNITURE);
        shopMap.put("vacuum_cleaner",PlaceCategory.ELECTRONICS);
        shopMap.put("pet",PlaceCategory.VETERINARY);
        shopMap.put("pet_grooming",PlaceCategory.VETERINARY);
        this.mappers.put("shop",new SimpleMapper(PlaceCategory.SHOP,shopMap));
    }


    public PlaceCategory category(JsonObject element) {
        if (element.has("tags")) {
            JsonObject tags = element.get("tags").getAsJsonObject();
            for (String key : KEYS) {
                if (tags.has(key)) {
                    SimpleMapper mapper = this.mappers.get(key);
                    if (mapper != null)
                        return mapper.getCategory(tags.get(key).getAsString());
                }
            }
        }

        return PlaceCategory.UNSPECIFIED;
    }

    public ArrayList<PlaceCategory> getRelatedCategories(int category){
        ArrayList<PlaceCategory> relatedCategories = new ArrayList<>();

        for (int i = 1; i < KEYS.length; i++ ) {
                if (this.mappers.get(KEYS[i]).containsSubCategory(category)) {
                    relatedCategories.addAll(this.mappers.get(KEYS[i]).getMapValues());
                }
        }
        return relatedCategories;
    }
}
