package ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class PredefinedValuesFormatter implements IAxisValueFormatter {

    private String[] values;

    public PredefinedValuesFormatter(String[] values) {
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return values[(int) value];
    }

}