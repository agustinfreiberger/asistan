package ar.edu.unicen.isistan.asistan.storage.database.mobility.visits;

import java.util.ArrayList;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.movement.UserVisit;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

@Entity(tableName = Visit.TABLE_NAME)
public class Visit extends Movement {

    public static final String TABLE_NAME = "visit";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @Embedded
    private Coordinate center;
    @ColumnInfo(name = "category")
    private int category;
    @ColumnInfo(name = "place_id")
    private long placeId;
    @Ignore
    @Nullable
    private Place place;
    @Ignore
    @Nullable
    private transient Commute previous;
    @Ignore
    @Nullable
    private transient Commute next;

    public Visit() {
        super(MovementType.VISIT);
        this.category = VisitCategory.VISIT.getCode();
        this.center = new Coordinate(0,0);
    }

    public Visit(@NotNull Visit origin, @NotNull Visit destination) {
        this();

        this.setStartTime(origin.getStartTime());
        this.setEndTime(destination.getEndTime());
        this.setCategory(VisitCategory.VISIT.getCode());
        this.setPlaceId(origin.getPlaceId());

        double duration = origin.duration() + destination.duration();
        if (this.duration() == 0.0D) { // This should never happened
            this.setCenter(origin.getCenter());
        } else {
            double originWeight = origin.duration() / duration;
            double destinationWeight = destination.duration() / duration;
            double lat = origin.getCenter().getLatitude() * originWeight + destination.getCenter().getLatitude() * destinationWeight;
            double lng = origin.getCenter().getLongitude() * originWeight + destination.getCenter().getLongitude() * destinationWeight;
            this.setCenter(new Coordinate(lat,lng));
        }

        this.labels.addAll(origin.getLabels());
        for (Integer label: destination.getLabels()) {
            if (!this.labels.contains(label))
                this.labels.add(label);
        }

        if (destination.isClosed())
            this.close();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean hasId() {
        return this.id != 0;
    }

    @NotNull
    public Coordinate getCenter() {
        return this.center;
    }

    public void setCenter(@NotNull Coordinate coordinate) {
        this.center = new Coordinate(coordinate.getLatitude(),coordinate.getLongitude());
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public int getCategory() {
        return this.category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean hasPlace() {
        return this.placeId != 0;
    }

    @Nullable
    public Place getPlace() {
        return place;
    }

    public void setPlace(@Nullable Place place) {
        this.place = place;
        if (place != null)
            this.placeId = place.getId();
        else
            this.placeId = 0;
    }

    @Nullable
    public Commute getPrevious() {
        return previous;
    }

    public void setPrevious(@Nullable Commute previous) {
        this.previous = previous;
    }

    @Nullable
    public Commute getNext() {
        return next;
    }

    public void setNext(@Nullable Commute next) {
        this.next = next;
    }

    @NotNull
    public Visit copy() {
        Visit visit = new Visit();
        visit.setId(this.getId());
        visit.setCategory(this.getCategory());
        visit.setCenter(this.getCenter());
        visit.setStartTime(this.getStartTime());
        visit.setEndTime(this.getEndTime());
        visit.setPlaceId(this.getPlaceId());
        visit.setClosed(this.isClosed());
        visit.setUpload(this.isUpload());
        visit.setLabels(new ArrayList<>(this.getLabels()));
        visit.setPlace(this.getPlace());
        visit.setPrevious(this.getPrevious());
        visit.setNext(this.getNext());
        return visit;
    }

    public void load(@NotNull Visit visit) {
        this.setId(visit.getId());
        this.setStartTime(visit.getStartTime());
        this.setEndTime(visit.getEndTime());
        this.setCategory(visit.getCategory());
        this.setCenter(visit.getCenter().copy());
        this.setUpload(visit.isUpload());
        this.setPlaceId(visit.getPlaceId());
        this.setClosed(visit.isClosed());
        this.setUpload(visit.isUpload());
        this.setLabels(new ArrayList<>(visit.getLabels()));
        this.setPlace(visit.getPlace());
        this.setPrevious(visit.getPrevious());
        this.setNext(visit.getNext());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof Visit) {
            Visit visit = (Visit) obj;
            return (this.hasId() && visit.hasId() && this.getId() == visit.getId());
        }

        return false;
    }

    public boolean different(Visit visit) {
        return (this.getId() != visit.getId()) ||
                (this.getCategory() != visit.getCategory()) ||
                (!this.getCenter().equals(visit.getCenter())) ||
                (this.getPlaceId() != visit.getPlaceId()) ||
                (this.getStartTime() != visit.getStartTime()) ||
                (this.getEndTime() != visit.getEndTime()) ||
                (this.isClosed() != visit.isClosed()) ||
                (!this.getLabels().equals(visit.getLabels()));
    }

    public Bound getBound() {
        if (this.place != null)
            return this.place.getBound();
        else
            return new Bound(this.getCenter(),30);
    }

    @Override
    public UserVisit simplify() {
        UserVisit visit = new UserVisit();
        visit.setStartTime(this.getStartTime());
        visit.setEndTime(this.getEndTime());
        visit.setCenter(this.getCenter());
        if (this.getPlace() != null) {
            visit.setPlaceName(this.getPlace().getName());
            visit.setPlaceCategory(this.getPlace().getPlaceCategory());
        }
        visit.setClosed(this.isClosed());
        return visit;
    }
}
