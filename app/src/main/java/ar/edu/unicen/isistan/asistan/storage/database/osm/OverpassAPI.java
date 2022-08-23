package ar.edu.unicen.isistan.asistan.storage.database.osm;

import android.util.LongSparseArray;
import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.osm.categories.CategoryMapper;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Polygon;
import ar.edu.unicen.isistan.asistan.utils.geo.paths.Path;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.UnionArea;

public class OverpassAPI {

    // TODO HOW-TO FIX En algunos lugares (como en europa) estos queries pueden superar el uso de recursos máximo permitidos por la API, y retornar null.
    private static final String OVERPASS_API = "http://www.overpass-api.de/api/interpreter";
    private static final String NEARBY_CITIES_QUERY = "[timeout:30][out:json]; (node['place'~'city|town|village'](%f,%f,%f,%f);); out body;";
    private static final String NEARBY_PLACES = "[timeout:30][out:json][bbox:%f,%f,%f,%f];(way(if:is_closed())['aeroway'='aerodrome'];relation(if:is_closed())['aeroway'='aerodrome'];node['leisure'~'amusement_arcade|bowling_alley|fitness_center|sports_centre']['name'];way(if:is_closed())['leisure'~'amusement_arcade|bowling_alley|fitness_center|miniature_golf|sports_centre|stadium']['name']['building'];relation(if:is_closed())['leisure'~'amusement_arcade|bowling_alley|fitness_center|miniature_golf|sports_centre|stadium']['name']['building'];way(if:is_closed())['leisure'~'dog_park|golf_course|horse_riding|marina|nature_reserve|stadium|water_park|park|garden|sports_centre|stadium']['building'!~'.'];relation(if:is_closed())['leisure'~'dog_park|golf_course|horse_riding|marina|nature_reserve|stadium|water_park|park|garden|sports_centre|stadium']['building'!~'.'];node['amenity'~'bar|biergarten|cafe|fast_food|ice_cream|pub|restaurant|college|kindergarten|library|school|music_school|language_school|driving_school|university|research_institute|bus_station|car_wash|car_rental|taxi|bank|clinic|dentist|hospital|pharmacy|social_facility|veterinary|arts_centre|casino|cinema|community_centre|nightclub|planetarium|theatre|courthouse|crematorium|dojo|embassy|fire_station|gym|internet_cafe|marketplace|place_of_worship|police|post_office|prison|townhall']['name'];way(if:is_closed())['amenity'~'bar|biergarten|cafe|fast_food|ice_cream|pub|restaurant|college|kindergarten|library|school|music_school|language_school|driving_school|university|research_institute|bus_station|car_wash|car_rental|taxi|bank|clinic|dentist|hospital|pharmacy|social_facility|veterinary|arts_centre|casino|cinema|community_centre|nightclub|planetarium|theatre|courthouse|crematorium|dojo|embassy|fire_station|gym|internet_cafe|marketplace|place_of_worship|police|post_office|prison|townhall']['name']['building'];relation(if:is_closed())['amenity'~'bar|biergarten|cafe|fast_food|ice_cream|pub|restaurant|college|kindergarten|library|school|music_school|language_school|driving_school|university|research_institute|bus_station|car_wash|car_rental|taxi|bank|clinic|dentist|hospital|pharmacy|social_facility|veterinary|arts_centre|casino|cinema|community_centre|nightclub|planetarium|theatre|courthouse|crematorium|dojo|embassy|fire_station|gym|internet_cafe|marketplace|place_of_worship|police|post_office|prison|townhall']['name']['building'];node['amenity'~'fuel']['brand'];way(if:is_closed())['amenity'~'fuel']['brand']['building'];relation(if:is_closed())['amenity'~'fuel']['brand']['building'];way(if:is_closed())['natural'~'wood|scrub|heath|moor|bare_rock|beach|sand|shingle|grassland|fell'];relation(if:is_closed())['natural'~'wood|scrub|heath|moor|bare_rock|beach|sand|shingle|grassland|fell'];node['shop']['name'];way(if:is_closed())['shop']['name']['building'];relation(if:is_closed())['shop']['name']['building'];node['building'~'hotel|commercial|office|supermarket|retail|warehouse|cathedral|church|temple|synagogue|kindergarten|hospital|civic|government|school|stadium|train_station|university|public|parking']['name'];way(if:is_closed())['building'~'hotel|commercial|office|supermarket|retail|warehouse|cathedral|church|temple|synagogue|kindergarten|hospital|civic|government|school|stadium|train_station|university|public|parking']['name']['building'];relation(if:is_closed())['building'~'hotel|commercial|office|supermarket|retail|warehouse|cathedral|church|temple|synagogue|kindergarten|hospital|civic|government|school|stadium|train_station|university|public|parking']['name']['building'];node['tourism'~'zoo|viewpoint|theme_park|museum|motel|information|hotel|hostel|guest_house|gallery|aquarium']['name'];way(if:is_closed())['tourism'~'zoo|viewpoint|theme_park|museum|motel|information|hotel|hostel|guest_house|gallery|aquarium']['name']['building'];relation(if:is_closed())['tourism'~'zoo|viewpoint|theme_park|museum|motel|information|hotel|hostel|guest_house|gallery|aquarium']['name']['building'];way(if:is_closed())['tourism'~'caravan_site|camp_site']['name']['building'!~'.*'];relation(if:is_closed())['tourism'~'caravan_site|camp_site']['name']['building'!~'.*'];way(if:is_closed())['landuse'~'cemetery|forest|farmland|recreation_ground']['name']['building'!~'.*'];relation(if:is_closed())['landuse'~'cemetery|forest|farmland|recreation_ground']['name']['building'!~'.*'];node['craft']['name'];way(if:is_closed())['craft']['name']['building'];relation(if:is_closed())['craft']['name']['building'];);(._;>;);out body;";
    private static final String CITY_BUS_ROUTES = "[timeout:30][out:json][bbox:%f,%f,%f,%f];(relation['route'='bus'];);(._;>;);out body;";


    private static final String TANDIL_AMENITIES_QUERY = "[timeout:30][out:json]; (node['amenity'~'pub|cafe|restaurant'](-37.35160114495477,-59.163780212402344,-37.29754420029534,-59.08927917480469);); out body;";


    private enum CityType {
        CITY("city",10000), TOWN("town",5000), VILLAGE("village",3250);

        private String type;
        private int radius;

        CityType(String type,int radius) {
            this.type = type;
            this.radius = radius;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static CityType get(String type) {
            for (CityType city_type: CityType.values())
                if (city_type.getType().equalsIgnoreCase(type))
                    return city_type;
            return TOWN;
        }
    }

    @Nullable
    public static List<OSMCity> queryCities(double south, double west, double north, double east) {
        String query = String.format(Locale.US, NEARBY_CITIES_QUERY, south, west, north, east);

        List<JsonObject> elements = getElements(query);

        if (elements == null)
            return null;

        ArrayList<OSMCity> cities = new ArrayList<>();

        for (JsonObject element: elements) {
            try {
                long id = (element.get("id").getAsLong());
                double lat = (element.get("lat").getAsDouble());
                double lng = (element.get("lon").getAsDouble());
                OSMCity city = new OSMCity(id);
                city.setCoordinate(new Coordinate(lat,lng));
                JsonObject tags = element.getAsJsonObject("tags");
                city.setName(tags.get("name").getAsString());
                city.setType(tags.get("place").getAsString());
                city.setRadius(CityType.get(city.getType()).getRadius());
                cities.add(city);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cities;
    }

    public static Pair<List<OSMPlace>,List<OSMArea>> queryPlaces(OSMCity city) {

        Coordinate location = city.getCoordinate();
        GeodesicData data = Geodesic.WGS84.Inverse(location.getLatitude(), location.getLongitude(), location.getLatitude(), location.getLongitude()+1, GeodesicMask.DISTANCE);
        double long_degree_to_meters = data.s12;

        double lat_degrees = (double) city.getRadius() / (double) Coordinate.DEGREE_TO_METERS;
        double long_degrees = (double) city.getRadius() / long_degree_to_meters;
        double south = Math.max(location.getLatitude() - lat_degrees, Coordinate.MIN_LAT);
        double west = Math.max(location.getLongitude() - long_degrees, Coordinate.MIN_LNG);
        double north = Math.min(location.getLatitude() + lat_degrees, Coordinate.MAX_LAT);
        double east = Math.min(location.getLongitude() + long_degrees, Coordinate.MAX_LNG);

        String query = String.format(Locale.US, NEARBY_PLACES, south, west, north, east);

        List<JsonObject> elements = getElements(query);

        if (elements == null)
            return null;

        LongSparseArray<Coordinate> coordinates = new LongSparseArray<>();
        LongSparseArray<Polygon> surfaces = new LongSparseArray<>();

        CategoryMapper mapper = new CategoryMapper();
        List<OSMPlace> places = new ArrayList<>();
        List<OSMArea> areas = new ArrayList<>();
        for (JsonObject element: elements) {
            try {
                String type = element.get("type").getAsString();
                long nid = element.get("id").getAsLong();
                JsonObject tags = element.has("tags") ? element.get("tags").getAsJsonObject() : null;
                PlaceCategory category = mapper.category(element);
                String name = null;
                boolean building = false;
                if (tags != null) {
                    name = tags.has("name") ? tags.get("name").getAsString() : null;
                    building = tags.has("building");
                }

                String id = type.substring(0,1) + nid;

                switch(type) {
                    case "node":
                        Coordinate coordinate = readCoordinate(element);
                        coordinates.put(nid, coordinate);

                        if (name != null)
                            places.add(new OSMPlace(id,name,category.getCode(),coordinate,building));
                        break;
                    case "way":
                        JsonArray nodes = element.get("nodes").getAsJsonArray();
                        ArrayList<Coordinate> aux = new ArrayList<>();
                        for (JsonElement nodeId: nodes)
                            aux.add(coordinates.get(nodeId.getAsLong()));

                        Polygon surface = new Polygon();
                        surface.setCoordinates(aux);

                        surfaces.put(nid,surface);
                        surface.getSimplified();
                        if (name != null || surface.getSurface() >= 4500)
                            areas.add(new OSMArea(id, name, category.getCode(), surface, building));
                        break;
                    case "relation":

                        JsonArray members = element.get("members").getAsJsonArray();
                        ArrayList<ArrayList<Coordinate>> outers = new ArrayList<>();

                        for (JsonElement memberEl: members) {
                            JsonObject member = memberEl.getAsJsonObject();
                            if (member.get("type").getAsString().equals("way") && member.get("role").getAsString().equals("outer")) {
                                Polygon outer = surfaces.get(member.get("ref").getAsLong());
                                if (outer != null)
                                    outers.add(new ArrayList<>(outer.getCoordinates()));
                            }
                        }

                        OverpassAPI.join(outers);
                        OverpassAPI.clean(outers);

                        if (outers.size() == 1) {
                            surface = new Polygon();
                            surface.setCoordinates(outers.get(0));
                            surface.getSimplified();
                            if (name != null || surface.getSurface() >= 4500)
                                areas.add(new OSMArea(id,name,category.getCode(),surface,building));
                        } else if (!outers.isEmpty()) {
                            UnionArea multiSurface = new UnionArea();
                            for (ArrayList<Coordinate> subArea: outers) {
                                surface = new Polygon();
                                surface.setCoordinates(subArea);
                                multiSurface.addSurface(surface);
                            }

                            multiSurface.getSimplified();
                            if (name != null || multiSurface.getSurface() >= 4500)
                                areas.add(new OSMArea(id,name,category.getCode(), multiSurface,building));
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Pair<>(places,areas);
    }

    public static List<OSMBusLine> queryBuses(OSMCity city) {
        Coordinate location = city.getCoordinate();
        GeodesicData data = Geodesic.WGS84.Inverse(location.getLatitude(), location.getLongitude(), location.getLatitude(), location.getLongitude()+1, GeodesicMask.DISTANCE);
        double long_degree_to_meters = data.s12;

        double lat_degrees = (double) city.getRadius() / (double) Coordinate.DEGREE_TO_METERS;
        double long_degrees = (double) city.getRadius() / long_degree_to_meters;
        double south = Math.max(location.getLatitude() - lat_degrees, Coordinate.MIN_LAT);
        double west = Math.max(location.getLongitude() - long_degrees, Coordinate.MIN_LNG);
        double north = Math.min(location.getLatitude() + lat_degrees, Coordinate.MAX_LAT);
        double east = Math.min(location.getLongitude() + long_degrees, Coordinate.MAX_LNG);

        String query = String.format(Locale.US, CITY_BUS_ROUTES, south, west, north, east);

        List<JsonObject> elements = getElements(query);

        if (elements == null)
            return null;

        LongSparseArray<Coordinate> coordinates = new LongSparseArray<>();
        LongSparseArray<Path> paths = new LongSparseArray<>();
        HashMap<String,ArrayList<Path>> lines= new HashMap<>();
        HashMap<String,Long> ids = new HashMap<>();

        for (JsonObject element : elements) {
            try {
                String type = element.get("type").getAsString();
                long id = element.get("id").getAsLong();
                JsonObject tags = element.has("tags") ? element.get("tags").getAsJsonObject() : null;
                String ref = null;
                if (tags != null)
                    ref = tags.has("ref") ? tags.get("ref").getAsString() : null;

                switch (type) {
                    case "node":
                        Coordinate coordinate = readCoordinate(element);
                        coordinates.put(id, coordinate);
                        break;
                    case "way":
                        Path path = new Path();
                        JsonArray nodes = element.get("nodes").getAsJsonArray();
                        for (JsonElement nodeId : nodes)
                            path.addCoordinate(coordinates.get(nodeId.getAsLong()));
                        paths.put(id, path);
                        break;
                    case "relation":
                        JsonArray members = element.get("members").getAsJsonArray();
                        ArrayList<ArrayList<Coordinate>> lists = new ArrayList<>();

                        for (JsonElement memberEl : members) {
                            JsonObject member = memberEl.getAsJsonObject();
                            Path aux = paths.get(member.get("ref").getAsLong());
                            if (aux != null)
                                lists.add(new ArrayList<>(aux.getCoordinates()));
                        }

                        OverpassAPI.join(lists);
                        OverpassAPI.clean(lists);

                        ArrayList<Coordinate> longest = OverpassAPI.longest(lists);

                        if (longest != null) {
                            path = new Path();
                            path.setCoordinates(longest);

                            ArrayList<Path> aux = lines.get(ref);
                            if (aux != null) {

                                lists = new ArrayList<>();
                                lists.add(path.getCoordinates());
                                for (Path auxPath:  aux)
                                    lists.add(auxPath.getCoordinates());

                                OverpassAPI.join(lists);
                                OverpassAPI.clean(lists);

                                ArrayList<Path> newPaths = new ArrayList<>();
                                for (ArrayList<Coordinate> list: lists)
                                    newPaths.add(new Path(list));

                                lines.put(ref,newPaths);
                            } else {
                                aux = new ArrayList<>();
                                aux.add(path);
                                lines.put(ref,aux);
                                ids.put(ref,id);
                            }
                        }

                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayList<OSMBusLine> busLines = new ArrayList<>();

        for (String ref: lines.keySet()) {
            Long id = ids.get(ref);
            ArrayList<Path> busLinesPaths = lines.get(ref);
            if (id != null && busLinesPaths != null) {
                OSMBusLine busLine = new OSMBusLine(id, busLinesPaths);
                busLine.setLine(ref);
                busLines.add(busLine);
            }
        }

        return busLines;
    }

    private static ArrayList<Coordinate> longest(ArrayList<ArrayList<Coordinate>> lists) {
        ArrayList<Coordinate> max = null;
        for (ArrayList<Coordinate> list : lists) {
            if (max == null || max.size() < list.size())
                max = list;
        }
        return max;
    }

    private static void clean(ArrayList<ArrayList<Coordinate>> lists) {

        ArrayList<Coordinate> aux = new ArrayList<>();
        for (ArrayList<Coordinate> list: lists) {
            for (Coordinate coordinate: list)
                if (!aux.contains(coordinate))
                    aux.add(coordinate);
            list.clear();
            list.addAll(aux);
            aux.clear();
        }

    }

    private static void join(ArrayList<ArrayList<Coordinate>> lists) {

        boolean modified = true;
        while (lists.size() > 1 && modified) {
            modified = false;
            for (int index = 0; index < lists.size(); index++) {
                ArrayList<Coordinate> list = lists.get(index);
                for (int index2 = 0; index2 < lists.size(); index2++) {
                    if (index != index2) {
                        ArrayList<Coordinate> list2 = lists.get(index2);
                        if (list2.get(0).equals(list.get(list.size()-1))) {
                            list.addAll(list2);
                            lists.remove(index2);
                            modified = true;
                        } else if (list2.get(list2.size()-1).equals(list.get(0))) {
                            list.addAll(0, list2);
                            lists.remove(index2);
                            modified = true;
                        } else if (list2.get(list2.size()-1).equals(list.get(list.size()-1))) {
                            Collections.reverse(list2);
                            list.addAll(list2);
                            lists.remove(index2);
                            modified = true;
                        } else if (list2.get(0).equals(list.get(0))) {
                            Collections.reverse(list);
                            list.addAll(list2);
                            lists.remove(index2);
                            modified = true;
                        }
                    }
                }
            }
        }
    }

    private static List<JsonObject> getElements(String query)  {
        try {
            URL osm = new URL(OVERPASS_API);
            HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setRequestProperty("Content-Type", "x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestMethod("POST");

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            printout.writeBytes("data=" + URLEncoder.encode(query, "utf-8"));
            printout.flush();
            printout.close();

            BufferedReader buffered = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = buffered.readLine()) != null) {
                builder.append(line);
            }

            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(builder.toString()).getAsJsonObject();
            JsonArray elements = root.getAsJsonArray("elements");
            ArrayList<JsonObject> out = new ArrayList<>();

            for (JsonElement element: elements) {
                out.add(element.getAsJsonObject());
            }

            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<JsonObject> getPois(String query)  {
        try {
            URL osm = new URL(OVERPASS_API);
            HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setRequestProperty("Content-Type", "x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestMethod("POST");

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            printout.writeBytes("data=" + URLEncoder.encode(query, "utf-8"));
            printout.flush();
            printout.close();

            BufferedReader buffered = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = buffered.readLine()) != null) {
                builder.append(line);
            }

            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(builder.toString()).getAsJsonObject();
            JsonArray elements = root.getAsJsonArray("elements");
            ArrayList<JsonObject> out = new ArrayList<>();

            for (JsonElement element: elements) {
                out.add(element.getAsJsonObject());
            }

            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static Coordinate readCoordinate(JsonObject element) {
        double lat = element.get("lat").getAsDouble();
        double lng = element.get("lon").getAsDouble();
        return new Coordinate(lat,lng);
    }

}
