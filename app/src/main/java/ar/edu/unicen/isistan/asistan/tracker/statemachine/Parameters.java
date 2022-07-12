package ar.edu.unicen.isistan.asistan.tracker.statemachine;

import org.jetbrains.annotations.NotNull;

public class Parameters {

    private static final double MIN_RADIUS = 30D;
    private static final double MAX_RADIUS = 100D;
    private static final long TWO_MINUTES = 120000L;
    private static final long FIVE_MINUTES = 300000L;
    private static final long THIRTY_SECONDS = 30000L;

    private long minTime;
    private long maxTime;
    private double maxRadius;
    private double minRadius;
    private double maxSurface;
    private double minSurface;
    private double activePenalty;
    private double knownPlaceBenefit;

    @NotNull
    public static Parameters defaultParameters() {
       Parameters parameters = new Parameters();
       parameters.setMinTime(TWO_MINUTES);
       parameters.setMaxTime(FIVE_MINUTES);
       parameters.setMinRadius(MIN_RADIUS);
       parameters.setMaxRadius(MAX_RADIUS);
       parameters.setActivePenalty(THIRTY_SECONDS);
       parameters.setKnownPlaceBenefit(THIRTY_SECONDS);
       parameters.setMinSurface(Math.PI * Math.pow(parameters.getMinRadius(),2));
       parameters.setMaxSurface(Math.PI * Math.pow(parameters.getMaxRadius(),2));
       return parameters;
    }

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public double getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(double maxRadius) {
        this.maxRadius = maxRadius;
    }

    public double getMinRadius() {
        return minRadius;
    }

    public void setMinRadius(double minRadius) {
        this.minRadius = minRadius;
    }

    public double getMaxSurface() {
        return maxSurface;
    }

    public void setMaxSurface(double maxSurface) {
        this.maxSurface = maxSurface;
    }

    public double getMinSurface() {
        return minSurface;
    }

    public void setMinSurface(double minSurface) {
        this.minSurface = minSurface;
    }

    public double getActivePenalty() {
        return activePenalty;
    }

    public void setActivePenalty(double activePenalty) {
        this.activePenalty = activePenalty;
    }

    public double getKnownPlaceBenefit() {
        return knownPlaceBenefit;
    }

    public void setKnownPlaceBenefit(double knownPlaceBenefit) {
        this.knownPlaceBenefit = knownPlaceBenefit;
    }

}
