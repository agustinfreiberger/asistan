package ar.edu.unicen.isistan.asistan.views.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import ar.edu.unicen.isistan.asistan.utils.geo.areas.UnionArea;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

public class OsmMapController extends MapController {

    private static final DashPathEffect DASHED_LINE = new DashPathEffect(new float[]{10, 10}, 0);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm",Locale.US);

    private MapView mapView;
    private Marker locationMarker;
    private Polygon locationCircle;
    private Comparator<Marker> comparator;

    public OsmMapController(@NotNull Context context, @NotNull MapView mapView) {
        org.osmdroid.config.Configuration.getInstance().load(context.getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        this.mapView = mapView;
        this.mapView.setTileSource(TileSourceFactory.MAPNIK);
        this.mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        this.mapView.setMultiTouchControls(true);
        this.mapView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        IMapController mapController = this.mapView.getController();
        mapController.setZoom(13.0);
        this.mapView.invalidate();
        this.locationMarker = null;
        this.locationCircle = null;
        this.comparator = (Marker o1, Marker o2) -> -Double.compare(o1.getPosition().getLatitude(),o2.getPosition().getLatitude());
    }

    public static OsmMapController prepare(@NotNull Context context, @NotNull View view) {
        MapView mapView = new MapView(context);
        FrameLayout layout = view.findViewById(R.id.mapContainer);
        layout.addView(mapView, new org.osmdroid.views.MapView.LayoutParams(
                org.osmdroid.views.MapView.LayoutParams.MATCH_PARENT,
                org.osmdroid.views.MapView.LayoutParams.MATCH_PARENT,
                null, 0, 0, 0));
        return new OsmMapController(context,mapView);
    }

    @Override
    public PlaceController draw(@NotNull Place place, @NotNull PlaceChangeListener placeChangeListener) {
        Marker marker = this.drawPlace(place);
        this.centerMap(place.getBound());
        final OsmPlaceController controller;
        if (place.getArea().getType().equals(Area.AreaType.CIRCLE))
            controller = new OsmPlaceController(marker,placeChangeListener, (float) ((Circle)place.getArea()).getRadius());
        else
            controller = new OsmPlaceController(marker,placeChangeListener);
        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                controller.onPlaceChange(new Coordinate(marker.getPosition().getLatitude(),marker.getPosition().getLongitude()));
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                Object object = marker.getRelatedObject();
                if (object instanceof Polygon)
                    OsmMapController.this.mapView.getOverlays().remove(object);
                OsmMapController.this.mapView.invalidate();
            }
        });

        return controller;
    }

    public void draw(@NotNull Visit visit) {
        Bound bound = visit.getBound();
        ArrayList<Visit> visits = new ArrayList<>();
        if (visit.getPrevious() != null && visit.getPrevious().getOrigin() != null) {
            visits.add(visit.getPrevious().getOrigin());
            bound.increase(visit.getPrevious().getBound());
            bound.increase(visit.getPrevious().getOrigin().getBound());
        }
        if (visit.getNext() != null && visit.getNext().getDestination() != null) {
            visits.add(visit.getNext().getDestination());
            bound.increase(visit.getNext().getBound());
            bound.increase(visit.getNext().getDestination().getBound());
        }
        visits.add(visit);

        this.drawVisits(visits);

        this.centerMap(bound);

        this.mapView.invalidate();
    }

    public void draw(@NotNull Commute commute) {
        this.drawCommute(commute);
        ArrayList<Visit> visits = new ArrayList<>();
        if (commute.getOrigin() != null)
            visits.add(commute.getOrigin());
        if (commute.getDestination() != null)
            visits.add(commute.getDestination());
        this.drawVisits(visits);
        Bound bound = commute.getBound();
        if (commute.getOrigin() != null)
            bound.increase(commute.getOrigin().getBound());
        if (commute.getDestination() != null)
            bound.increase(commute.getDestination().getBound());
        this.centerMap(bound);
        this.mapView.invalidate();
    }

    public void draw(@NotNull Place... places) {
        ArrayList<Marker> markers = new ArrayList<>();
        for (Place place : places)
            markers.add(this.drawPlace(place));
        IMapController mapController = this.mapView.getController();
        mapController.setZoom(12.0);
        if (places.length > 0)
            mapController.setCenter(places[0].getArea().getCenter().toGeoPoint());
        Collections.sort(markers,this.comparator);
        for (Marker marker: markers) {
            this.mapView.getOverlays().remove(marker);
            this.mapView.getOverlays().add(marker);
        }
        this.mapView.invalidate();
    }

    private Marker drawPlace(Place place) {
        Marker marker = new Marker(this.mapView);
        marker.setTitle(place.getShowName());
        marker.setPosition(place.getArea().getCenter().toGeoPoint());
        marker.setIcon(this.mapView.getContext().getResources().getDrawable(PlaceCategory.get(place.getPlaceCategory()).getMarkerSrc()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Polygon polygon = (Polygon) place.getArea().map(this);
        this.mapView.getOverlays().add(marker);
        marker.setRelatedObject(polygon);
        return marker;
    }

    public void drawVisits(@NotNull ArrayList<Visit> visits) {
        if (!visits.isEmpty()) {
            ArrayList<Place> places = new ArrayList<>();
            ArrayList<ArrayList<Visit>> visitsPerPlace = new ArrayList<>();
            ArrayList<Commute> commutes = new ArrayList<>();

            ArrayList<Marker> markers = new ArrayList<>();

            Bound bound = visits.get(0).getBound();
            for (Visit visit : visits) {
                bound.increase(visit.getBound());
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
                    markers.add(this.drawVisit(visit));
                }

                if (visit.getPrevious() != null && !commutes.contains(visit.getPrevious())) {
                    bound.increase(visit.getPrevious().getBound());
                    commutes.add(visit.getPrevious());
                }

                if (visit.getNext() != null && !commutes.contains(visit.getNext())) {
                    bound.increase(visit.getNext().getBound());
                    commutes.add(visit.getNext());
                }
            }

            this.draw(commutes.toArray(new Commute[0]));
            markers.addAll(this.draw(visitsPerPlace));
            Collections.sort(markers,this.comparator);
            for (Marker marker : markers) {
                this.mapView.getOverlays().remove(marker);
                this.mapView.getOverlays().add(marker);
            }

            this.centerMap(bound);
            this.mapView.invalidate();
        }
    }

    @Nullable
    @Override
    public Object drawArea(Circle circle) {
        Polygon overlay = new Polygon();

        overlay.setPoints(Polygon.pointsAsCircle(circle.getCenter().toGeoPoint(), circle.getRadius()));
        this.drawPolygon(overlay);
        return overlay;
    }

    @Nullable
    @Override
    public Object drawArea(ar.edu.unicen.isistan.asistan.utils.geo.areas.Polygon polygon) {
        org.osmdroid.views.overlay.Polygon overlay = new org.osmdroid.views.overlay.Polygon();
        ArrayList<GeoPoint> geoPoints = new ArrayList<>();
        for (Coordinate coordinate: polygon.getCoordinates())
            geoPoints.add(coordinate.toGeoPoint());
        overlay.setPoints(geoPoints);
        this.drawPolygon(overlay);
        return overlay;
    }

    @Nullable
    @Override
    public Object drawArea(UnionArea union) {
        for (Area area: union.getAreas())
            area.map(this);
        return null;
    }

    @Nullable
    @Override
    public Object drawArea(IntersectionArea intersection) {
        for (Area area: intersection.getAreas())
            area.map(this);
        return null;
    }

    @Override
    public void onPause() {
        this.mapView.onPause();
    }

    @Override
    public void onResume() {
        this.mapView.onResume();
    }

    @Override
    public void onDestroy() {
        this.mapView.onDetach();
    }

    private Marker drawVisit(Visit visit) {
        Marker marker = new Marker(this.mapView);
        marker.setTitle(VisitCategory.get(visit.getCategory()).getName());
        if (visit.getPlace() == null)
            marker.setPosition(visit.getCenter().toGeoPoint());
        else
            marker.setPosition(visit.getPlace().getArea().getCenter().toGeoPoint());

        if (visit.getPlace() != null)
            marker.setIcon(this.mapView.getContext().getResources().getDrawable(PlaceCategory.get(visit.getPlace().getPlaceCategory()).getMarkerSrc()));
        else if (visit.getCategory() == VisitCategory.VISIT.getCode())
            marker.setIcon(this.mapView.getContext().getResources().getDrawable(R.drawable.new_icon));
        else
            marker.setIcon(this.mapView.getContext().getResources().getDrawable(R.drawable.clock));

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        this.mapView.getOverlays().add(marker);
        return marker;
    }

    private ArrayList<Marker> draw(ArrayList<ArrayList<Visit>> visitsPerPlace) {
        ArrayList<Marker> markers = new ArrayList<>();
        for (ArrayList<Visit> visits : visitsPerPlace) {
            Place place = visits.get(0).getPlace();
            Marker marker = this.draw(place, visits);
            markers.add(marker);
        }
        return markers;
    }

    private Marker draw(Place place, ArrayList<Visit> visits) {
        Marker marker = this.drawPlace(place);
        StringBuilder builder = new StringBuilder();
        for (Visit visit : visits) {
            builder.append("\nDesde ");
            builder.append(DATE_FORMAT.format(new java.util.Date(visit.getStartTime())));
            builder.append(" hasta ");
            builder.append(DATE_FORMAT.format(new java.util.Date(visit.getEndTime())));
        }
        marker.setSnippet(builder.toString());
        return marker;
    }

    private void draw(Commute[] commutes) {
        for (int index = 0; index < commutes.length; index++) {
            Commute commute = commutes[index];
            float value = ((float) index / commutes.length) * 360;
            int color = Color.HSVToColor(170, new float[]{value, 0.7F, 0.7F});
            drawCommute(commute, color);
        }
    }

    private void drawCommute(Commute commute) {
        this.drawCommute(commute, this.mapView.getContext().getResources().getColor(R.color.colorAccent));
    }

    private void drawCommute(Commute commute, int color) {
        for (int index = 0; index < commute.getSteps().size(); index++) {
            Step step = commute.getSteps().get(index);

            ArrayList<GeoPoint> points = new ArrayList<>();

            if (index == 0 && commute.getOrigin() != null) {
                if (commute.getOrigin().getPlace() != null)
                    points.add(commute.getOrigin().getPlace().getArea().getCenter().toGeoPoint());
                else
                    points.add(commute.getOrigin().getCenter().toGeoPoint());
            }

            for (Event event : step.getEvents()) {
                points.add(event.getLocation().toGeoPoint());
            }

            if (index == commute.getSteps().size() - 1 && commute.getDestination() != null) {
                if (commute.getDestination().getPlace() != null)
                    points.add(commute.getDestination().getPlace().getArea().getCenter().toGeoPoint());
                else
                    points.add(commute.getDestination().getCenter().toGeoPoint());
            }

            Polyline polyline = new Polyline();
            polyline.setPoints(points);

            if (step.transportMode().equals(TransportMode.FOOT)) {
                polyline.getPaint().setPathEffect(DASHED_LINE);
                //this.mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            polyline.setWidth(7);
            polyline.setColor(color);

            this.mapView.getOverlayManager().add(polyline);
        }

    }

    private void drawPolygon(Polygon polygon) {
        polygon.setFillColor(0x12121212);
        polygon.setStrokeColor(Color.RED);
        polygon.setStrokeWidth(2);
        this.mapView.getOverlays().add(polygon);
    }

    private void centerMap(@NotNull Bound bound) {
        bound.increase(0.2F);
        IMapController mapController = this.mapView.getController();
        mapController.setCenter(bound.getCenter().toGeoPoint());
        mapController.setZoom(getZoom(bound));
    }

    private double getZoom(@NotNull Bound bound) {
        double distance = bound.getNorthEast().distance(bound.getSouthWest());
        double val = 1;
        int zoom = 0;
        while (val < distance) {
            zoom++;
            val *= 2;
        }
        return Math.max(this.mapView.getMaxZoomLevel() - zoom - 2,0);
    }

    public void clear() {
        this.mapView.getOverlayManager().clear();
        this.mapView.getOverlays().clear();
        //this.mapView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    public void setLocation(@NotNull GeoLocation location) {
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (this.locationMarker == null) {
            this.locationMarker = new Marker(this.mapView);
            this.locationMarker.setPosition(geoPoint);
            this.locationCircle = new Polygon();
            this.locationCircle.setPoints(Polygon.pointsAsCircle(geoPoint,location.getAccuracy()));
            this.drawPolygon(this.locationCircle);
            IMapController mapController = this.mapView.getController();
            mapController.setZoom(18D);
            mapController.setCenter(geoPoint);
            this.mapView.getOverlays().add(this.locationMarker);
        } else {
            this.locationMarker.setPosition(geoPoint);
            this.mapView.getOverlays().remove(this.locationCircle);
            this.locationCircle = new Polygon();
            this.locationCircle.setPoints(Polygon.pointsAsCircle(geoPoint,location.getAccuracy()));
            this.drawPolygon(this.locationCircle);
            this.mapView.getOverlays().remove(this.locationMarker);
            this.mapView.getOverlays().add(this.locationMarker);
        }
        this.mapView.invalidate();
    }

    public class OsmPlaceController extends PlaceController {

        @NotNull
        private Marker marker;
        private float radius;

        public OsmPlaceController(@NotNull Marker marker, @NotNull PlaceChangeListener placeChangeListener) {
            super(placeChangeListener);
            this.marker = marker;
        }

        public OsmPlaceController(@NotNull Marker marker, @NotNull PlaceChangeListener placeChangeListener, float radius) {
            super(placeChangeListener);
            this.marker = marker;
            this.radius = radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
            Object object = this.marker.getRelatedObject();
            if (object instanceof Polygon)
                OsmMapController.this.mapView.getOverlays().remove(object);
            Polygon overlay = new Polygon();
            overlay.setPoints(Polygon.pointsAsCircle(marker.getPosition(), radius));
            OsmMapController.this.drawPolygon(overlay);
            this.marker.setRelatedObject(overlay);
            OsmMapController.this.mapView.invalidate();
        }

        @Override
        public void setDraggable(boolean draggable) {
            this.marker.setDraggable(draggable);
        }

        public float getRadius() {
            return this.radius;
        }

    }

    @Override
    public void highlight(Coordinate coordinate) {
        Marker marker = new Marker(this.mapView);
        marker.setPosition(coordinate.toGeoPoint());
        marker.setIcon(this.mapView.getContext().getResources().getDrawable(R.drawable.arrow));
        marker.setAnchor(Marker.ANCHOR_LEFT, Marker.ANCHOR_TOP);
        this.mapView.getOverlays().add(marker);
        this.mapView.invalidate();
    }


}