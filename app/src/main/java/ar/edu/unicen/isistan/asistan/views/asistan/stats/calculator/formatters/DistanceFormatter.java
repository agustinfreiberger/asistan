package ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.Locale;

public class DistanceFormatter implements IValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (value != 0F)
            return  String.format(Locale.US,"%.1fkm", value);
        else
            return "";
    }
}