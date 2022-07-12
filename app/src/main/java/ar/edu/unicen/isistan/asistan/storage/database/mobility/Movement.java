package ar.edu.unicen.isistan.asistan.storage.database.mobility;

import java.util.ArrayList;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import com.google.gson.annotations.JsonAdapter;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.labels.LabelListConverter;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.movement.UserMovement;

@JsonAdapter(MovementAdapter.class)
public abstract class Movement implements Comparable<Movement> {

    public enum MovementType {
        VISIT(1), COMMUTE(2);

        private int code;

        MovementType(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    @Ignore
    private MovementType type;
    @ColumnInfo(name = "start_time")
    private long startTime;
    @ColumnInfo(name = "end_time")
    private long endTime;
    @ColumnInfo(name = "closed")
    private boolean closed;
    @ColumnInfo(name = "labels")
    @TypeConverters(LabelListConverter.class)
    protected ArrayList<Integer> labels;
    @ColumnInfo(name = "upload")
    private boolean upload;

    public Movement(MovementType type) {
        this.type = type;
        this.labels = new ArrayList<>();
        this.upload = false;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @NotNull
    public ArrayList<Integer> getLabels() {
        return labels;
    }

    public void setLabels(@NotNull ArrayList<Integer> labels) {
        this.labels.clear();
        this.labels.addAll(labels);
    }

    public void addLabel(int label) {
        if (!this.labels.contains(label))
            this.labels.add(label);
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public long duration() {
        return this.endTime - this.startTime;
    }

    @Override
    public int compareTo(Movement movement) {
        if (this.getStartTime() < movement.getStartTime())
            return 1;
        else if (this.getStartTime() == movement.getStartTime())
            return 0;
        else
            return -1;
    }

    public MovementType getType() {
        return this.type;
    }

    public void close() {
        this.closed = true;
        this.upload = true;
    }

    public boolean before(@NotNull Movement movement) {
        return this.getEndTime() < movement.getEndTime();
    }

    public boolean after(@NotNull Movement movement) {
        return this.getEndTime() > movement.getEndTime();
    }

    public abstract UserMovement simplify();

}
