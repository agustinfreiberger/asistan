package ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Locale;

import ar.edu.unicen.isistan.asistan.utils.time.Date;

public class DateFormatter implements IAxisValueFormatter {

    private Date startDate;

    public DateFormatter(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Date day = this.startDate.nextDays((int) value);
        return String.format(Locale.US,"%d/%d/%d",day.getDay(),day.getMonth(),day.getYear());
    }

}