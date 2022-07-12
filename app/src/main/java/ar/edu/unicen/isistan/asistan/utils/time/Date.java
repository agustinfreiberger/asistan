package ar.edu.unicen.isistan.asistan.utils.time;

import java.util.Calendar;

public class Date {

    private int day;
    private int month;
    private int year;

    public static Date now() {
        Calendar calendar = Calendar.getInstance();
        return new Date(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR));
    }

    public static Date defaultDate() {
        return new Date(1,1,2000);
    }

    public static Date from(Calendar calendar) {
        return new Date(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR));
    }

    public static Date from(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return Date.from(calendar);
    }

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Calendar toCalendar() {
        Calendar out = Calendar.getInstance();
        out.set(Calendar.YEAR,this.year);
        out.set(Calendar.MONTH,this.month-1);
        out.set(Calendar.DAY_OF_MONTH,this.day);
        out.set(Calendar.HOUR_OF_DAY,0);
        out.set(Calendar.MINUTE,0);
        out.set(Calendar.SECOND,0);
        out.set(Calendar.MILLISECOND,0);
        return out;
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (o instanceof Date) {
            Date aux = (Date) o;
            return  (this.getDay() == aux.getDay() && this.getMonth() == aux.getMonth() && this.getYear() == aux.getYear());
        }
        return false;
    }

    public boolean after(Date date) {
        if (this.getYear() > date.getYear())
            return true;
        if (this.getYear() < date.getYear())
            return false;

        if (this.getMonth() > date.getMonth())
            return true;
        if (this.getMonth() < date.getMonth())
            return false;

        if (this.getDay() > date.getDay())
            return true;

        return false;
    }

    public boolean before(Date date) {
        if (this.getYear() < date.getYear())
            return true;
        if (this.getYear() > date.getYear())
            return false;

        if (this.getMonth() < date.getMonth())
            return true;
        if (this.getMonth() > date.getMonth())
            return false;

        if (this.getDay() < date.getDay())
            return true;

        return false;
    }

    public boolean between(Date startDate, Date endDate) {
        return (!this.before(startDate) && !this.after(endDate));
    }

    public Date nextDay() {
        return nextDays(1);
    }

    public Date previousDay() {
        return nextDays(-1);
    }

    public int getDayOfWeek() {
        return this.toCalendar().get(Calendar.DAY_OF_WEEK);
    }

    public Date nextDays(int count) {
        Calendar aux = this.toCalendar();
        aux.add(Calendar.DAY_OF_WEEK,count);
        return Date.from(aux);
    }

    @Override
    public String toString() {
        return "{ day: " + this.day + ", month: " + month + ", year: " + year + " }";
    }


}
