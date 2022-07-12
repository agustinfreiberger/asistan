package ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes;

import android.os.Looper;

import com.google.android.gms.location.DetectedActivity;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMBusLine;
import ar.edu.unicen.isistan.asistan.map.MapManager;
import ar.edu.unicen.isistan.asistan.utils.markov.HMM;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

class CommuteAnalyzer {

    private static final double MAX_BIKE = 6.94; // 6.94 m/s equivale a 25 km/h
    private static final double MAX_RUNNING = 3.61; // 3.61 m/s equivale a 13 km/h
    private static final double MAX_WALKING = 1.95; // 1.95 m/s equivale a 7 km/h
    private static final double MAX_STILL = 0.27; // 0.27 m/s equivale a 1 km/h

    private static final int SLOW_STATE = 0;
    private static final int FAST_STATE = 1;
    private static final long MIN_TIME = 50000L;
    private static final double MIN_DISTANCE = 100D;
    private static final double[] HMM_INITIAL_STATE_PROBABILITIES = {0.78125, 0.21875};
    private static final double[][] HMM_STATE_TRANSITION_PROBABILITIES = {{0.9795918367346939, 0.02040816326530612}, {0.004229607250755287, 0.9957703927492447}};
    private static final double[][] HMM_SYMBOL_EMISSION_PROBABILITIES = {{0.0, 0.004464285714285714, 0.004464285714285714, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.013392857142857142, 0.013392857142857142, 0.0, 0.008928571428571428, 0.0, 0.0, 0.0, 0.0, 0.0, 0.05357142857142857, 0.08928571428571429, 0.39285714285714285, 0.07142857142857142, 0.03125, 0.0, 0.0, 0.0, 0.0, 0.0, 0.08035714285714286, 0.07589285714285714, 0.04017857142857143, 0.008928571428571428, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.008928571428571428, 0.05803571428571429, 0.03571428571428571, 0.004464285714285714, 0.004464285714285714}, {0.012055455093429777, 0.02230259192284509, 0.07172995780590717, 0.07172995780590717, 0.6106088004822182, 0.0, 0.0, 0.0, 0.0, 0.0, 6.027727546714888E-4, 6.027727546714888E-4, 0.013261000602772756, 0.011452682338758288, 0.08559373116335142, 0.0, 0.0, 0.0, 0.0, 0.0, 6.027727546714888E-4, 6.027727546714888E-4, 6.027727546714888E-4, 6.027727546714888E-4, 0.0054249547920434, 0.0, 0.0, 0.0, 0.0, 0.0, 0.004219409282700422, 0.01567209162145871, 0.004219409282700422, 0.0018083182640144665, 0.007233273056057866, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0012055455093429777, 0.009041591320072333, 0.006630500301386378, 0.011452682338758288, 0.03074141048824593}};

    private HMM<Integer> model;
    private ArrayList<Step> steps;
    private ArrayList<ArrayList<Event>> subTravels;
    private ArrayList<Integer> estimatedTransportModes;
    private Commute commute;

    public CommuteAnalyzer(Commute commute) {
        this.commute = commute;
        this.model = new HMM<>(HMM_INITIAL_STATE_PROBABILITIES, HMM_STATE_TRANSITION_PROBABILITIES, HMM_SYMBOL_EMISSION_PROBABILITIES);
        this.subTravels = new ArrayList<>();
        this.estimatedTransportModes = new ArrayList<>();
    }

    public void estimateSteps() {
        ArrayList<Event> events = commute.getFullTravel();
        int[] values = this.generateSymbols(events);
        int[] states = this.model.predict(values);
        this.generateSteps(events, states);
        this.commute.setSteps(this.steps);
    }

    private int[] generateSymbols(ArrayList<Event> events) {
        int[] out = new int[events.size()];

        Event previous = null;
        for (int index = 0; index < events.size(); index++) {
            Event event = events.get(index);
            out[index] = getSymbol(previous, event);
            previous = event;
        }

        return out;
    }

    private int getSymbol(Event previous, Event event) {

        int value;
        if (event.getActivity() == DetectedActivity.IN_VEHICLE || event.getActivity() == DetectedActivity.ON_BICYCLE
                || event.getActivity() == DetectedActivity.STILL)
            value = event.getActivity() * 10;
        else if (event.getActivity() == DetectedActivity.ON_FOOT || event.getActivity() == DetectedActivity.WALKING
                || event.getActivity() == DetectedActivity.RUNNING)
            value = DetectedActivity.ON_FOOT * 10;
        else
            value = 10 * DetectedActivity.UNKNOWN;

        if (previous != null) {
            if (event.getTime() - previous.getTime() > 3000) {
                double vel = velocity(previous, event);
                if (vel < 40) {
                    if (vel < MAX_STILL)
                        value += 1;
                    else if (vel <= MAX_WALKING)
                        value += 2;
                    else if (vel <= MAX_RUNNING)
                        value += 3;
                    else
                        value += 4;
                }
            }
        }

        return value;
    }

    private double velocity(Event a, Event b) {
        double distance = a.getLocation().distance(b.getLocation());
        return distance / ((b.getTime() - a.getTime()) / 1000.0D);
    }

    private double maxVelocity(ArrayList<Event> step) {
        double max = 0;
        for (int index = 0; index < step.size()-1; index++) {
            Event a = step.get(index);
            Event b = step.get(index+1);

            if (b.getTime()-a.getTime() > 3000L) {
                double vel = velocity(step.get(index),step.get(index+1));
                if (vel < 40 && vel > max)
                    max = vel;
            }
        }
        return max;
    }

    private void generateSteps(ArrayList<Event> events, int[] states) {
        int state = -1;

        ArrayList<Event> current = new ArrayList<>();

        for (int index = 0; index < states.length; index++) {
            if (state == states[index]) {
                current.add(events.get(index));
            } else {
                if (current.size() > 3 && distance(current) > MIN_DISTANCE && (state == FAST_STATE || time(current) > MIN_TIME)) {
                    this.subTravels.add(new ArrayList<>(current));
                    this.estimatedTransportModes.add(state);
                    current.clear();
                }

                current.add(events.get(index));
                state = states[index];
            }
        }

        if (!current.isEmpty()) {
            if (this.subTravels.isEmpty() || (current.size() > 3 && distance(current) > MIN_DISTANCE && (state == FAST_STATE || time(current) > MIN_TIME))) {
                this.subTravels.add(new ArrayList<>(current));
                this.estimatedTransportModes.add(state);
                current.clear();
            } else {
                this.subTravels.get(this.subTravels.size() - 1).addAll(current);
                current.clear();
            }
        }

        int index = 0;
        while (index < this.estimatedTransportModes.size() - 1) {
            if (this.estimatedTransportModes.get(index).equals(this.estimatedTransportModes.get(index + 1))) {
                this.subTravels.get(index).addAll(this.subTravels.get(index + 1));
                this.subTravels.remove(index + 1);
                this.estimatedTransportModes.remove(index + 1);
            } else if ( (index+1) != this.estimatedTransportModes.size()-1 && this.estimatedTransportModes.get(index+1) == SLOW_STATE) {
                this.subTravels.get(index).addAll(this.subTravels.get(index + 1));
                this.subTravels.remove(index + 1);
                this.estimatedTransportModes.remove(index + 1);
            } else {
                index++;
            }
        }

        index = 0;
        while (index < this.estimatedTransportModes.size()) {
            if (this.estimatedTransportModes.get(index) == SLOW_STATE) {
                this.estimatedTransportModes.set(index, TransportMode.FOOT.getCode());
            } else {
                this.estimatedTransportModes.set(index, this.estimateFastTransportMode(subTravels.get(index)));
            }
            index++;
        }

        this.steps = new ArrayList<>();
        for (index = 0; index < this.subTravels.size(); index++) {
            ArrayList<Event> stepEvents = new ArrayList<>(this.subTravels.get(index));
            if (index > 0)
                stepEvents.add(0,this.subTravels.get(index-1).get(this.subTravels.get(index-1).size()-1));
            Step step = new Step(stepEvents,TransportMode.UNSPECIFIED,TransportMode.get(this.estimatedTransportModes.get(index)));
            this.steps.add(step);
        }

    }

    private long time(ArrayList<Event> current) {
        return current.get(current.size() - 1).getTime() - current.get(0).getTime();
    }

    private double distance(ArrayList<Event> current) {
        return current.get(0).getLocation().distance(current.get(current.size() - 1).getLocation());
    }

    private int maxFastActivity(ArrayList<Event> step) {
        int car = 0;
        int bicycle = 0;

        for (Event event: step) {
            if (event.getActivity() == DetectedActivity.IN_VEHICLE)
                car++;
            else if (event.getActivity() == DetectedActivity.ON_BICYCLE)
                bicycle++;
        }

        int total = car + bicycle;
        if (total == 0 || ((double) bicycle / (double)total < 0.8))
            return TransportMode.VEHICLE.getCode();
        else {
            double velocity = this.maxVelocity(step);
            if (velocity < MAX_BIKE)
                return TransportMode.BICYCLE.getCode();
            else
                return TransportMode.VEHICLE.getCode();
        }
    }

    private int estimateFastTransportMode(ArrayList<Event> events) {
        int result = this.maxFastActivity(events);

        if (result == TransportMode.BICYCLE.getCode())
            return result;

        if ((this.commute.getOrigin() != null && this.commute.getOrigin().getPlace() != null && this.commute.getOrigin().getPlace().getPlaceCategory() == PlaceCategory.BUS_STOP.getCode())) {
            // TODO Esto chequea que no este en el main thread. Tendria que asegurarme que el main no invoque este metodo
            if (!Thread.currentThread().equals( Looper.getMainLooper().getThread())) {
                ArrayList<Coordinate> coordinates = new ArrayList<>();
                for (Event event : events)
                    coordinates.add(event.getLocation());
                Bound bound = new Bound(coordinates);
                bound.increase(0.2F);
                List<OSMBusLine> busLines = MapManager.getInstance().getOSMBusLines(bound);
                for (OSMBusLine line : busLines) {
                    if (line.matchEvents(events))
                        return TransportMode.BUS.getCode();
                }
            }
        }

        return TransportMode.VEHICLE.getCode();
    }

}