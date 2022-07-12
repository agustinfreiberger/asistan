package ar.edu.unicen.isistan.asistan.views.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.IntersectionArea;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Polygon;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.UnionArea;

public abstract class MapController {

    public enum Map {
        OPEN_STREET_MAP(0,"OpenStreetMap", R.drawable.osm),
        GOOGLE_MAPS(1,"GoogleMaps",R.drawable.gmaps);

        private int code;
        private String name;
        private int src;

        Map(int code, String name, int src) {
            this.code = code;
            this.name = name;
            this.src = src;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static Map get(int code) {
            for (Map map: Map.values())
                if (map.getCode() == code)
                    return map;
            return OPEN_STREET_MAP;
        }

        public int getSrc() {
            return src;
        }

        public void setSrc(int src) {
            this.src = src;
        }
    }

    public abstract PlaceController draw(@NotNull Place place, @NotNull PlaceChangeListener placeChangeListener);

    public abstract void draw(@NotNull Place... places);

    public abstract void draw(@NotNull Commute commute);

    public abstract void draw(@NotNull Visit visit);

    public abstract void drawVisits(@NotNull ArrayList<Visit> visits);

    public abstract void setLocation(@NotNull GeoLocation location);

    public abstract void clear();

    public abstract class PlaceController {

        private PlaceChangeListener listener;

        protected PlaceController() {
            this.listener = null;
        }

        protected PlaceController(@NotNull PlaceChangeListener listener) {
            this.listener = listener;
        }

        protected void onPlaceChange(@NotNull Coordinate coordinate) {
            if (this.listener != null)
                this.listener.onPlaceChange(coordinate);
        }

        public abstract void setRadius(float radius);

        public abstract void setDraggable(boolean draggable);

    }

    public interface PlaceChangeListener {

        void onPlaceChange(Coordinate coordinate);

    }

    @Nullable
    public abstract Object drawArea(Circle circle);

    @Nullable
    public abstract Object drawArea(Polygon polygon);

    @Nullable
    public abstract Object drawArea(UnionArea union);

    @Nullable
    public abstract Object drawArea(IntersectionArea intersectionArea);

    public abstract void highlight(Coordinate coordinate);

    public abstract void onPause();

    public abstract void onResume();

    public abstract void onDestroy();

    public interface Mappable {

        Object map(MapController mapController);

    }


}
