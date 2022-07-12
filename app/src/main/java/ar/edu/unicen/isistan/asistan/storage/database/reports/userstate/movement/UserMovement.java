package ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.movement;

import com.google.gson.annotations.JsonAdapter;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;

@JsonAdapter(UserMovementAdapter.class)
public abstract class UserMovement {

    private Movement.MovementType type;

    private long startTime;
    private long endTime;
    private boolean closed;

    protected UserMovement(Movement.MovementType type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Movement.MovementType getType() {
        return type;
    }

    public void setType(Movement.MovementType type) {
        this.type = type;
    }

}
