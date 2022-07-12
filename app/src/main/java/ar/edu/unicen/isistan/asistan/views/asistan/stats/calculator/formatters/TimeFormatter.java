package ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.common.internal.StringResourceValueReader;

import java.util.Locale;

public class TimeFormatter implements IValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (value != 0F) {
            int hours = (int) value;
            int minutes = (int) ((value - hours) * 60F);
            String minutesString = String.valueOf(minutes);
            if (minutes < 10)
                minutesString = "0" + minutesString;
            return String.format(Locale.US, "%d:%s", hours, minutesString);
        }
        else
            return "";
    }
}