package ar.edu.unicen.isistan.asistan.utils.time;

import org.jetbrains.annotations.NotNull;
import java.util.Calendar;

public class Time {

    private int hour;
    private int minutes;

    public static Time MIN = new Time(0,0);
    public static Time MAX = new Time(24,0);

    public Time(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minutes = calendar.get(Calendar.MINUTE);
    }

    public Time(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public boolean between(Time start, Time end) {
        return (!this.before(start) && !this.after(end));
    }

    public boolean before(Time time) {
        if (this.hour < time.getHour())
            return true;
        else return (this.hour == time.getHour() && this.minutes < time.getMinutes());
    }

    public boolean after(Time time) {
        if (this.hour > time.getHour())
            return true;
        else return (this.hour == time.getHour() && this.minutes > time.getMinutes());
    }

    public boolean equals(Time time) {
        return (this.hour == time.getHour() && this.minutes == time.getMinutes());
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void set(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    @Override
    @NotNull
    public String toString() {
        String hour_string = String.valueOf(hour);
        if (this.hour < 10) {
            hour_string = "0"+ hour_string;
        }
        String minutes_string = String.valueOf(minutes);
        if (this.minutes < 10) {
            minutes_string = "0" + minutes_string;
        }
        return hour_string + ":" + minutes_string;
    }

    public static String asDuration(Number time) {
        double aux = time.doubleValue() / 3600000D;
        int hour = (int) aux;
        aux -= hour;
        int minutes = (int) (aux * 60);

        String out = "";
        if (hour != 0)
            out += String.valueOf(hour) + "hs";

        if (minutes != 0) {
            if (hour != 0)
                out += " ";
            out += String.valueOf(minutes) + "m";
        }

        return out;
    }

}
