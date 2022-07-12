package ar.edu.unicen.isistan.asistan.views.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Step;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.IntersectionArea;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Polygon;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.UnionArea;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;
import ar.edu.unicen.isistan.asistan.views.utils.BitmapUtils;

public class GoogleMapController extends MapController {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm",Locale.US);
    private static final List<PatternItem> DASHED_LINE = Arrays.asList(new Gap(10), new Dash(10), new Gap(10));

    private GoogleMap map;
    private Context context;
    private Marker locationMarker;
    private com.google.android.gms.maps.model.Circle locationCircle;

    public GoogleMapController(@NotNull Context context, @NotNull GoogleMap map) {
        this.map = map;
        this.context = context;
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            @Nullable
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(GoogleMapController.this.context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(GoogleMapController.this.context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(GoogleMapController.this.context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
     }

    public static void prepare(@NotNull FragmentManager manager, OnMapReadyCallback callback) {
        SupportMapFragment mapFragment =  SupportMapFragment.newInstance();
        manager.beginTransaction().replace(R.id.mapContainer, mapFragment).commit();
        mapFragment.getMapAsync(callback);
    }

    @Override
    public GooglePlaceController draw(@NotNull Place place, @NotNull final PlaceChangeListener placeChangeListener) {
        Marker marker = this.drawPlace(place);
        this.centerMap(place.getBound());
        final GooglePlaceController controller = new GooglePlaceController(marker,placeChangeListener);
        this.map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Object obj = marker.getTag();
                if (obj instanceof com.google.android.gms.maps.model.Circle) {
                    com.google.android.gms.maps.model.Circle circle = (com.google.android.gms.maps.model.Circle) obj;
                    circle.setCenter(marker.getPosition());
                }
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                controller.onPlaceChange(new Coordinate(marker.getPosition().latitude,marker.getPosition().longitude));
            }

        });
        return controller;
    }

    public void draw(@NotNull Place... places) {
        if (places.length > 0) {
            Bound bound = places[0].getBound();
            for (Place place : places) {
                this.drawPlace(place);
                bound.increase(place.getBound());
            }
            this.centerMap(bound);
        }
    }

    public void draw(@NotNull Commute commute) {
        this.drawCommute(commute);
        Bound bound = commute.getBound();
        ArrayList<Visit> visits = new ArrayList<>();
        if (commute.getOrigin() != null) {
            bound.increase(commute.getOrigin().getBound());
            visits.add(commute.getOrigin());
        }
        if (commute.getDestination() != null) {
            bound.increase(commute.getDestination().getBound());
            visits.add(commute.getDestination());
        }
        this.drawVisits(visits);
        this.centerMap(bound);
    }

    public void draw(@NotNull Visit visit) {
        ArrayList<Visit> visits = new ArrayList<>();
        visits.add(visit);
        Bound bound = visit.getBound();
        if (visit.getPrevious() != null && visit.getPrevious().getOrigin() != null) {
            bound.increase(visit.getPrevious().getBound());
            bound.increase(visit.getPrevious().getOrigin().getBound());
            visits.add(visit.getPrevious().getOrigin());
        }
        if (visit.getNext() != null && visit.getNext().getDestination() != null) {
            bound.increase(visit.getNext().getBound());
            bound.increase(visit.getNext().getDestination().getBound());
            visits.add(visit.getNext().getDestination());
        }

        this.drawVisits(visits);
        this.centerMap(bound);
    }

    public void drawVisits(@NotNull ArrayList<Visit> visits) {
        if (!visits.isEmpty()) {
            ArrayList<Place> places = new ArrayList<>();
            ArrayList<ArrayList<Visit>> visitsPerPlace = new ArrayList<>();
            ArrayList<Commute> commutes = new ArrayList<>();

            Bound bound = visits.get(0).getBound();
            for (Visit visit : visits) {
                if (visit.getPlace() != null) {
                    if (!places.contains(visit.getPlace())) {
                        places.add(visit.getPlace());
                        ArrayList<Visit> visitsInPlace = new ArrayList<>();
                        visitsInPlace.add(visit);
                        visitsPerPlace.add(visitsInPlace);
                    } else {
                        visitsPerPlace.get(places.indexOf(visit.getPlace())).add(visit);
                    }
                } else {
                    this.drawVisit(visit);
                }

                bound.increase(visit.getBound());

                if (visit.getPrevious() != null && !commutes.contains(visit.getPrevious())) {
                    bound.increase(visit.getPrevious().getBound());
                    commutes.add(visit.getPrevious());
                }

                if (visit.getNext() != null && !commutes.contains(visit.getNext())) {
                    bound.increase(visit.getNext().getBound());
                    commutes.add(visit.getNext());
                }
            }

            this.draw(visitsPerPlace);
            this.draw(commutes.toArray(new Commute[0]));

            this.centerMap(bound);
        }
    }

    private void drawCommute(Commute commute) {
        this.drawCommute(commute,this.context.getResources().getColor(R.color.colorAccent));
    }

    private void drawCommute(Commute commute, int color) {
        for (int index = 0; index < commute.getSteps().size(); index++) {
            Step step = commute.getSteps().get(index);

            ArrayList<LatLng> points = new ArrayList<>();
            boolean first = true;
            if (index == 0 && commute.getOrigin() != null) {
                first = false;
                if (commute.getOrigin().getPlace() != null)
                    points.add(commute.getOrigin().getPlace().getArea().getCenter().toLatLng());
                else
                    points.add(commute.getOrigin().getCenter().toLatLng());
            }


            for (Event event : step.getEvents())
                    points.add(event.getLocation().toLatLng());

            boolean last = true;
            if (index == commute.getSteps().size() - 1 && commute.getDestination() != null) {
                last = false;
                if (commute.getDestination().getPlace() != null)
                    points.add(commute.getDestination().getPlace().getArea().getCenter().toLatLng());
                else
                    points.add(commute.getDestination().getCenter().toLatLng());
            }


            ArrayList<PolylineOptions> optionsList = new ArrayList<>();
            for (int pindex = 0; pindex < points.size()-1; pindex++) {
                PolylineOptions options = new PolylineOptions();
                options.add(points.get(pindex));
                options.add(points.get(pindex+1));
                if (step.transportMode().equals(TransportMode.FOOT))
                    options.pattern(DASHED_LINE);
                options.width(7);
                options.color(color);
                optionsList.add(options);
            }

            Bitmap eventBitMap = BitmapFactory.decodeResource(context.getResources(),R.drawable.event);
            eventBitMap = BitmapUtils.replaceColor(eventBitMap,Color.BLACK, color);
            CustomCap cap = new CustomCap(BitmapDescriptorFactory.fromBitmap(eventBitMap),28);
            int size = optionsList.size();
            for (int optIndex = 0; optIndex < size; optIndex++) {
                PolylineOptions options = optionsList.get(optIndex);
                if (optIndex != 0 || first)
                    options.startCap(cap);
                if (optIndex == size-1 && last)
                    options.endCap(cap);
                this.map.addPolyline(options);
            }
        }

    }

    private void draw(Commute[] commutes) {
        for (int index = 0; index < commutes.length; index++) {
            Commute commute = commutes[index];
            float value = ((float) index / commutes.length) * 360;
            int color = Color.HSVToColor(170, new float[]{value, 0.7F, 0.7F});
            drawCommute(commute, color);
        }
    }

    private void drawVisit(@NotNull Visit visit) {
        MarkerOptions options = new MarkerOptions();
        options.title(VisitCategory.get(visit.getCategory()).getName());
        if (visit.getPlace() == null)
            options.position(visit.getCenter().toLatLng());
        else
            options.position(visit.getPlace().getArea().getCenter().toLatLng());

        if (visit.getPlace() != null)
            options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(),PlaceCategory.get(visit.getPlace().getPlaceCategory()).getMarkerSrc())));
        else if (visit.getCategory() == VisitCategory.VISIT.getCode())
            options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.new_icon)));
        else
            options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.clock)));

        this.map.addMarker(options);
    }

    private void draw(ArrayList<ArrayList<Visit>> visitsPerPlace) {
        for (ArrayList<Visit> visits : visitsPerPlace) {
            Place place = visits.get(0).getPlace();
            this.draw(place, visits);
        }
    }

    private void draw(Place place, ArrayList<Visit> visits) {
        Marker marker = this.drawPlace(place);
        StringBuilder builder = new StringBuilder();
        int size = visits.size();
        for (int index = 0; index < size; index++) {
            Visit visit = visits.get(index);
            if (index > 0)
                builder.append("\n");
            builder.append("Desde ");
            builder.append(DATE_FORMAT.format(new java.util.Date(visit.getStartTime())));
            builder.append(" hasta ");
            builder.append(DATE_FORMAT.format(new java.util.Date(visit.getEndTime())));
        }
        String info = builder.toString();
        marker.setSnippet(info);
    }

    private Marker drawPlace(Place place) {
        MarkerOptions options = new MarkerOptions();
        options.title(place.getShowName());
        options.position(place.getArea().getCenter().toLatLng());
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(),PlaceCategory.get(place.getPlaceCategory()).getMarkerSrc())));
        Marker marker = this.map.addMarker(options);
        marker.setTag(place.getArea().map(this));
        return marker;
    }

    public com.google.android.gms.maps.model.Circle drawArea(Circle circle) {
        CircleOptions options = new CircleOptions();
        options.center(circle.getCenter().toLatLng());
        options.radius(circle.getRadius());
        options.fillColor(0x12121212);
        options.strokeColor(Color.RED);
        options.strokeWidth(2);
        return this.map.addCircle(options);
    }

    public com.google.android.gms.maps.model.Polygon drawArea(Polygon polygon) {
        PolygonOptions options = new PolygonOptions();
        for (Coordinate coordinate: polygon.getCoordinates())
            options.add(coordinate.toLatLng());
        options.fillColor(0x12121212);
        options.strokeColor(Color.RED);
        options.strokeWidth(2);
        return this.map.addPolygon(options);
    }

    @Override
    public Object drawArea(UnionArea union) {
        for (Area area: union.getAreas()) {
            area.map(this);
        }
        return null;
    }

    @Override
    public Object drawArea(IntersectionArea intersection) {
        for (Area area: intersection.getAreas()) {
            area.map(this);
        }
        return null;
    }

    @Override
    public void highlight(Coordinate coordinate) {
        MarkerOptions options = new MarkerOptions();
        Bitmap arrowBitMap = BitmapFactory.decodeResource(context.getResources(),R.drawable.arrow);
        options.icon(BitmapDescriptorFactory.fromBitmap(arrowBitMap));
        options.anchor(0,0);
        options.position(coordinate.toLatLng());
        this.map.addMarker(options);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    private void centerMap(@NotNull Bound bound) {
        bound.increase(0.2F);
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(bound.getCenter().toLatLng(),this.getZoom(bound)));
    }

    private float getZoom(@NotNull Bound bound) {
        double distance = bound.getNorthEast().distance(bound.getSouthWest());
        double val = 55;
        int zoom = 0;
        while (val < distance) {
            zoom++;
            val *= 2;
        }
        return Math.max(this.map.getMaxZoomLevel() - zoom - 1,0);
    }

    public void clear() {
        this.map.clear();
    }

    public void setLocation(@NotNull GeoLocation location) {
        if (this.locationMarker == null) {
            MarkerOptions options = new MarkerOptions();
            options.position(location.getCoordinate().toLatLng());
            this.locationMarker = this.map.addMarker(options);
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(this.locationMarker.getPosition());
            circleOptions.radius(location.getAccuracy());
            circleOptions.fillColor(0x12121212);
            circleOptions.strokeColor(Color.RED);
            circleOptions.strokeWidth(2);
            this.locationCircle = this.map.addCircle(circleOptions);
            this.centerMap(new Bound(location.getCoordinate(),location.getAccuracy()));
        } else {
            this.locationMarker.setPosition(location.getCoordinate().toLatLng());
            this.locationCircle.setCenter(this.locationMarker.getPosition());
            this.locationCircle.setRadius(location.getAccuracy());
        }
    }

    public class GooglePlaceController extends PlaceController {

        @NotNull
        private Marker marker;

        public GooglePlaceController(@NotNull Marker marker, @NotNull PlaceChangeListener placeChangeListener) {
            super(placeChangeListener);
            this.marker = marker;
        }

        public void setRadius(float radius) {
            Object object = this.marker.getTag();
            if (object instanceof com.google.android.gms.maps.model.Circle) {
                com.google.android.gms.maps.model.Circle circle = (com.google.android.gms.maps.model.Circle) object;
                circle.setRadius(radius);
            }
        }

        @Override
        public void setDraggable(boolean draggable) {
            this.marker.setDraggable(draggable);
        }

    }
}
