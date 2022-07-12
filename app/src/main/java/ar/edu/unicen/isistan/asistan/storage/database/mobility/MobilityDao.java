package ar.edu.unicen.isistan.asistan.storage.database.mobility;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.jetbrains.annotations.Nullable;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.CommuteCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;
import ar.edu.unicen.isistan.asistan.map.MapManager;
import ar.edu.unicen.isistan.asistan.tracker.mobility.MobilityTracker;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.StateMachineTracker;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

@Dao
public abstract class MobilityDao {

    /* Visits */

    @Query("SELECT COUNT(*) FROM " + Visit.TABLE_NAME + " WHERE place_id = :placeId AND category != 1")
    public abstract int countVisits(long placeId);

    @Query("SELECT COUNT(*) FROM " + Visit.TABLE_NAME + " WHERE place_id = :placeId AND category != 1 AND closed = 1")
    public abstract int countClosedVisits(long placeId);

    @Query("SELECT * FROM " + Visit.TABLE_NAME + " WHERE place_id = :id AND category != 1")
    public abstract List<Visit> selectVisits(long id);

    @Query("SELECT * FROM " + Visit.TABLE_NAME + " WHERE upload = 1")
    public abstract List<Visit> selectVisitsToUpload();

    @Query("SELECT * FROM " + Visit.TABLE_NAME + " WHERE _id = :id")
    public abstract Visit selectVisit(long id);

    @Nullable
    public Visit selectVisitAndContext(long id) {
        Visit visit = selectVisit(id);

        if (visit == null)
            return null;

        this.populateCommutes(visit);

        ArrayList<Visit> visits = new ArrayList<>();
        visits.add(visit);

        if (visit.getPrevious() != null) {
            this.populateVisits(visit.getPrevious());
            if (visit.getPrevious().getOrigin() != null)
                visits.add(visit.getPrevious().getOrigin());
        }

        if (visit.getNext() != null) {
            this.populateVisits(visit.getNext());
            if (visit.getNext().getDestination() != null)
                visits.add(visit.getNext().getDestination());
        }

        this.populatePlaces(visits);

        return visit;
    }

    @Query("SELECT * FROM " + Visit.TABLE_NAME + " WHERE start_time = :time AND category != 1")
    public abstract Visit selectVisitsByStart(long time);

    @Query("SELECT * FROM " + Visit.TABLE_NAME + " WHERE end_time = :time AND category != 1")
    public abstract Visit selectVisitsByEnd(long time);

    @Query("SELECT * FROM " + Visit.TABLE_NAME + " WHERE (((start_time BETWEEN :start AND :end) OR (end_time BETWEEN :start AND :end)) OR (:start BETWEEN start_time AND end_time)) AND category != 1")
    public abstract List<Visit> selectVisitsBetween(long start, long end);
    
    @Query("SELECT " + Visit.TABLE_NAME + ".* FROM " + Visit.TABLE_NAME + " LEFT JOIN " + Place.TABLE_NAME + " ON visit.place_id = place._id WHERE closed = 1 AND ((visit.category = 2) OR (visit.category = 3 AND (visit.place_id = 0 OR place.name IS NULL OR place.name = '')))")
    public abstract List<Visit> selectVisitsToReview();

    public boolean areThereVisitsToReview() {
        return (!this.selectVisitsToReview().isEmpty());
    }

    @Query("SELECT * FROM " + Visit.TABLE_NAME + " AS v WHERE v.closed = 0 AND v.category != 1 AND NOT EXISTS (SELECT 1 FROM " + Commute.TABLE_NAME + " AS c WHERE c.category != 1 AND c.start_time > v.end_time) ORDER BY v.end_time DESC LIMIT 1")
    public abstract Visit currentVisit();

    @Query("SELECT * FROM " + Visit.TABLE_NAME + " WHERE category != 1 AND closed = 1 AND end_time < :untilTime AND place_id = :placeId")
    public abstract Visit lastVisitBefore(long untilTime, long placeId);

    public void insert(Visit visit) {
        visit.setUpload(true);
        long id = insertVisit(visit);
        visit.setId(id);
    }

    @Insert
    protected abstract long insertVisit(Visit visit);

    @Insert
    public abstract long[] insert(Visit... visits);

    public void update(Visit visit) {
        visit.setUpload(true);
        this.updateVisit(visit);
    }

    @Update
    protected abstract void updateVisit(Visit visit);

    public void markMobilityAsUploaded() {
        this.markVisitsAsUploaded();
        this.markPlacesAsUploaded();
        this.markCommutesAsUploaded();
    }

    public void deleteMobilityUploaded() {
        this.deleteUploadedPlaces();
        this.deleteUploadedCommutes();
        this.deleteUploadedVisits();
    }

    @Query("DELETE FROM " + Place.TABLE_NAME + " WHERE upload = 0 AND place_category = -3")
    protected abstract void deleteUploadedPlaces();

    @Query("DELETE FROM " + Commute.TABLE_NAME + " WHERE upload = 0 AND category = 1")
    protected abstract void deleteUploadedCommutes();

    @Query("DELETE FROM " + Visit.TABLE_NAME + " WHERE upload = 0 AND category = 1")
    protected abstract void deleteUploadedVisits();

    @Query("UPDATE " + Visit.TABLE_NAME + " SET upload = 0")
    protected abstract void markVisitsAsUploaded();


    /* Commutes */

    @Query("SELECT * FROM " + Commute.TABLE_NAME + " WHERE upload = 1")
    public abstract List<Commute> selectCommutesToUpload();

    @Query("SELECT * FROM " + Commute.TABLE_NAME + " WHERE start_time = :time AND category != 1")
    public abstract List<Commute> selectCommutesByStart(long time);

    @Query("SELECT * FROM " + Commute.TABLE_NAME + " WHERE end_time = :time AND category != 1")
    public abstract List<Commute> selectCommutesByEnd(long time);

    @Query("SELECT * FROM " + Commute.TABLE_NAME + " WHERE _id = :id")
    public abstract Commute selectCommute(long id);

    @Nullable
    public Commute selectCommuteAndContext(long id) {
        Commute commute = selectCommute(id);

        if (commute == null)
            return null;

        this.populateVisits(commute);

        ArrayList<Visit> visits = new ArrayList<>();

        if (commute.getOrigin() != null)
            visits.add(commute.getOrigin());

        if (commute.getDestination() != null)
            visits.add(commute.getDestination());

        this.populatePlaces(visits);

        return commute;
    }

    @Query("SELECT * FROM " + Commute.TABLE_NAME + " WHERE (((start_time BETWEEN :start AND :end) OR (end_time BETWEEN :start AND :end)) OR (:start BETWEEN start_time AND end_time)) AND category != 1")
    public abstract List<Commute> selectCommutesBetween(long start, long end);

    public void insert(Commute commute) {
        commute.setUpload(true);
        long id = this.insertCommute(commute);
        commute.setId(id);
    }

    @Insert
    protected abstract long insertCommute(Commute commute);

    @Insert
    public abstract long[] insert(Commute... commutes);

    public void update(Commute commute) {
        commute.setUpload(true);
        this.updateCommute(commute);
    }

    @Update
    protected abstract void updateCommute(Commute commute);

    @Query("UPDATE " + Commute.TABLE_NAME + " SET upload = 0")
    protected abstract void markCommutesAsUploaded();

    /* Places */

    @Query("SELECT * FROM " + Place.TABLE_NAME + " WHERE place_category != -3")
    public abstract List<Place> allPlaces();

    @Query("SELECT * FROM " + Place.TABLE_NAME + " WHERE place_category != -3 AND NOT((west > :east) OR (:west > east) OR (south > :north) OR (:south > north))")
    public abstract List<Place> allPlaces(double north, double south, double east, double west);

    public List<Place> near(Coordinate location, double maxDistance) {
        Bound bound = new Bound(location,maxDistance);
        return this.allPlaces(bound.getNorth(), bound.getSouth(), bound.getEast(), bound.getWest());
    }

    @Query("SELECT * FROM " + Place.TABLE_NAME + " WHERE upload = 1")
    public abstract List<Place> selectPlacesToUpload();

    @Query("SELECT * FROM " + Place.TABLE_NAME + " WHERE _id = :id")
    public abstract Place selectPlace(long id);

    public void insert(Place place) {
        place.setUpload(true);
        long id = this.insertPlace(place);
        place.setId(id);
        MapManager.getInstance().notifyChanges();
    }

    @Insert
    protected abstract long insertPlace(Place place);

    public void insert(Place... places) {
        long[] ids = this.insertPlaces(places);
        for (int i = 0; i < places.length; i++) {
            places[i].setId(ids[i]);
        }
        MapManager.getInstance().notifyChanges();
    }

    @Insert
    protected abstract long[] insertPlaces(Place... places);

    public void update(Place place) {
        place.setUpload(true);
        this.updatePlace(place);
        MapManager.getInstance().notifyChanges();
    }

    @Update
    protected abstract void updatePlace(Place place);

    @Query("UPDATE " + Place.TABLE_NAME + " SET upload = 0")
    protected abstract void markPlacesAsUploaded();

    public void delete(Place place) {
        place.setPlaceCategory(PlaceCategory.DELETED.getCode());
        this.update(place);
        MapManager.getInstance().notifyChanges();
    }

    /* Transactions */

    public boolean changeVisit(Context context, Visit visit) {
        if (visit == null)
            return false;

        synchronized (StateMachineTracker.class) {
            return changeVisitTransaction(context, visit);
        }
    }

    @Transaction
    protected boolean changeVisitTransaction(Context context, Visit visit) {
        Visit original = this.selectVisit(visit.getId());
        this.populatePlace(original);

        Place oldPlace = original.getPlace();
        if (oldPlace != null && oldPlace.getId() != visit.getPlaceId()) {
            int count = this.countVisits(oldPlace.getId());
            if (count <= 1)
                this.delete(oldPlace);
            else if (!oldPlace.isFixedLocation() && oldPlace.getArea().getType().equals(Area.AreaType.CIRCLE)) {
                Circle circle = (Circle) oldPlace.getArea();
                double lat = ((circle.getCenter().getLatitude() * count) - visit.getCenter().getLatitude()) / (count-1);
                double lng = ((circle.getCenter().getLongitude() * count) - visit.getCenter().getLongitude()) / (count-1);
                circle.setCenter(new Coordinate(lat,lng));
                this.update(oldPlace);
            }
        }

        Place newPlace = visit.getPlace();
        if (newPlace != null) {
            if (newPlace.getId() == 0) {
                this.insert(newPlace);
                visit.setPlace(newPlace);
            } else if (!newPlace.isFixedLocation() && newPlace.getArea().getType().equals(Area.AreaType.CIRCLE)) {
                Circle circle = (Circle) newPlace.getArea();
                int count = this.countClosedVisits(visit.getPlaceId());
                double lat = ((circle.getCenter().getLatitude() * count) + visit.getCenter().getLatitude()) / (count + 1);
                double lng = ((newPlace.getArea().getCenter().getLongitude() * count) + visit.getCenter().getLongitude()) / (count + 1);
                circle.setCenter(new Coordinate(lat,lng));
                this.update(newPlace);
            }
        }

        this.update(visit);

        StateMachineTracker.setVisit(context, original, visit, true);

        return true;
    }

    @Transaction
    public void setData(List<Place> places, List<Visit> visits, List<Commute> commutes) {
        this.insert(places.toArray(new Place[0]));
        this.insert(visits.toArray(new Visit[0]));
        this.insert(commutes.toArray(new Commute[0]));
    }

    public boolean deleteCommute(Context context, Commute commute) {
        if (commute == null)
            return false;

        synchronized (StateMachineTracker.class) {
            return deleteCommuteTransaction(context, commute);
        }
    }

    @Transaction
    protected boolean deleteCommuteTransaction(Context context, Commute commute) {
        this.populateVisits(commute);
        Visit origin = commute.getOrigin();
        Visit destination = commute.getDestination();

        if (origin != null) {
            if (destination != null) {
                if (origin.getPlaceId() == destination.getPlaceId()) {
                    Visit visit = new Visit(origin, destination);
                    origin.setCategory(VisitCategory.ERROR.getCode());
                    destination.setCategory(VisitCategory.ERROR.getCode());
                    this.update(origin);
                    this.update(destination);
                    this.insert(visit);
                    this.populatePlace(visit);
                    StateMachineTracker.setVisit(context, destination, visit, false);
                }
            } else {
                origin.setClosed(false);
                this.update(origin);
                StateMachineTracker.setVisit(context, commute, origin, false);
            }
        } else if (destination != null) {
            StateMachineTracker.remove(context, commute);
        } else {
            return false;
        }

        commute.setCategory(CommuteCategory.DELETED.getCode());
        this.update(commute);

        return true;
    }

    public boolean deleteVisit(Context context, Visit visit) {
        if (visit == null)
            return false;

        synchronized (StateMachineTracker.class) {
            return deleteVisitTransaction(context, visit);
        }
    }

    @Transaction
    protected boolean deleteVisitTransaction(Context context, Visit visit) {
        if (visit == null)
            return false;

        this.populateCommutes(visit);
        Commute previous = visit.getPrevious();
        Commute next = visit.getNext();

        if (previous != null) {
            if (next != null) {
                Commute commute = new Commute(previous, next);
                previous.setCategory(CommuteCategory.DELETED.getCode());
                next.setCategory(CommuteCategory.DELETED.getCode());

                this.update(previous);
                this.update(next);
                this.insert(commute);

                StateMachineTracker.setCommute(context, next, commute);
            } else {
                previous.setClosed(false);
                this.update(previous);
                StateMachineTracker.setCommute(context, visit, previous);
            }
        } else if (next != null) {
            StateMachineTracker.remove(context, visit);
        } else {
            return false;
        }

        this.populatePlace(visit);
        Place place = visit.getPlace();
        if (place != null) {
            int count = this.countVisits(place.getId());
            if (count == 1)
                this.delete(place);
        }

        visit.setCategory(VisitCategory.ERROR.getCode());
        visit.setPlace(null);

        this.update(visit);

        return true;
    }

    /* Populates */

    public void populateVisits(Commute commute) {

        if (commute.getOrigin() == null) {
            Visit origin = this.selectVisitsByEnd(commute.getStartTime());
            commute.setOrigin(origin);
        }

        if (commute.getDestination() == null) {
            Visit destination = this.selectVisitsByStart(commute.getEndTime());
            commute.setDestination(destination);
        }

    }

    public void populateVisits(Place place) {
        List<Visit> visits = this.selectVisits(place.getId());
        Collections.sort(visits);
        place.setVisits(visits);
        for (Visit visit: visits)
            visit.setPlace(place);
    }

    public void populate(List<Commute> commutes, List<Visit> visits) {

        for (Commute commute: commutes) {
            for (Visit visit: visits) {
                if (commute.getEndTime() == visit.getStartTime()) {
                    commute.setDestination(visit);
                    visit.setPrevious(commute);
                }
                else if (commute.getStartTime() == visit.getEndTime()) {
                    commute.setOrigin(visit);
                    visit.setNext(commute);
                }

                if (commute.getOrigin() != null && commute.getDestination() != null)
                    break;
            }

            if (commute.getOrigin() == null) {
                Visit visit = this.selectVisitsByEnd(commute.getStartTime());
                if (visit != null) {
                    commute.setOrigin(visit);
                    visit.setNext(commute);
                    visits.add(visit);
                }
            }

            if (commute.getDestination() == null) {
                Visit visit = this.selectVisitsByStart(commute.getEndTime());
                if (visit != null) {
                    commute.setDestination(visit);
                    visit.setPrevious(commute);
                    visits.add(visit);
                }
            }
        }

    }

    public void populateCommutes(Visit visit) {
        List<Commute> commutes = this.selectCommutesByEnd(visit.getStartTime());
        if (!commutes.isEmpty()) {
            visit.setPrevious(commutes.get(0));
            commutes.get(0).setDestination(visit);
        }

        commutes = this.selectCommutesByStart(visit.getEndTime());
        if (!commutes.isEmpty()) {
            visit.setNext(commutes.get(0));
            commutes.get(0).setOrigin(visit);
        }
    }

    public void populatePlace(Visit visit) {
        if (visit.getPlace() == null) {
            Place place = this.selectPlace(visit.getPlaceId());
            if (place != null)
                visit.setPlace(place);
        }
    }

    public void populatePlaces(List<Visit> visits) {
        List<Place> places = new ArrayList<>();
        for (Visit visit: visits) {
            Place place = null;
            for (Place aux: places) {
                if (aux.getId() == visit.getPlaceId()) {
                    place = aux;
                    break;
                }
            }
            if (place == null) {
                place = this.selectPlace(visit.getPlaceId());
                if (place != null)
                    places.add(place);
            }

            if (place != null)
                visit.setPlace(place);
        }
    }

    public void populateContext(Commute commute) {
        this.populateVisits(commute);

        ArrayList<Visit> visits = new ArrayList<>();

        if (commute.getOrigin() != null)
            visits.add(commute.getOrigin());

        if (commute.getDestination() != null)
            visits.add(commute.getDestination());

        this.populatePlaces(visits);
    }


}
