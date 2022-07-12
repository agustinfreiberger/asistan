package ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class IntegerFormatter  implements IValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        int intV = (int) value;
        if (intV != 0)
            return String.valueOf((int) value);
        else
            return "";
    }
}