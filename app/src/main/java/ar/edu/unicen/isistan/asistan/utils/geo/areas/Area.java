package ar.edu.unicen.isistan.asistan.utils.geo.areas;

import com.google.gson.annotations.JsonAdapter;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

@JsonAdapter(AreaAdapter.class)
public abstract class Area implements MapController.Mappable {

    public enum AreaType {

        CIRCLE(Circle.class), POLYGON(Polygon.class), UNION_AREA(UnionArea.class), INTERSECTION_AREA(IntersectionArea.class);

        private Class<? extends Area> type;

        AreaType(Class<? extends Area> type) {
            this.type = type;
        }

        public Class<? extends Area> getAreaClass() {
            return this.type;
        }

        public static AreaType getType(Class<? extends Area> areaClass) {
            for (AreaType type: AreaType.values()) {
                if (type.getAreaClass().equals(areaClass))
                    return type;
            }
            return null;
        }
    }

    protected AreaType type;

    protected Area() {
        this.type = AreaType.getType(this.getClass());
    }

    public AreaType getType() {
        return this.type;
    }

    public void setType(AreaType type) {
        this.type = type;
    }

    public abstract double getSurface();

    public abstract Coordinate getCenter();

    public abstract boolean contains(Coordinate location);

    public abstract double distance(Coordinate coordinate);

    public abstract Area copy();

    public abstract Bound getBound();

    public abstract Area getSimplified();


}
