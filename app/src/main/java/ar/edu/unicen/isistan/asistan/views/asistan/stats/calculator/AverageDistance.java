package ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator;

import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.CommuteCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Step;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.utils.time.Date;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters.DistanceFormatter;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters.PredefinedValuesFormatter;

public class AverageDistance extends StatsCalculator {

    private CombinedChart combinedChart;
    private ArrayList<TransportMode> transportModes;

    public AverageDistance() {
        this.description = "Promedio de distancia recorrida";
        this.transportModes = null;
    }

    public AverageDistance(String modeDescription, TransportMode... transportMode) {
        this.description = "Promedio de distancia recorrida " + modeDescription.toLowerCase();
        this.transportModes = new ArrayList<>(Arrays.asList(transportMode));
    }

    @Override
    protected void init() {
        this.view.removeAllViews();

        this.combinedChart = new CombinedChart(this.view.getContext());

        this.combinedChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.combinedChart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        this.combinedChart.setDescription(description);
        YAxis yAxis = this.combinedChart.getAxisLeft();
        yAxis.setDrawLabels(true);
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawZeroLine(true);
        yAxis.setAxisMinimum(0);
        this.combinedChart.getAxisRight().setEnabled(false);

        XAxis xAxis = this.combinedChart.getXAxis();
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setValueFormatter(new PredefinedValuesFormatter(new String[] {"Domingo","Lunes","Martes","Miercoles","Jueves","Viernes","Sabado"} ));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setLabelRotationAngle(315);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(6.5f);
        this.view.addView(this.combinedChart);
    }

    @Override
    protected void calculate() {

        ArrayList<Long>[] distanceByDay = new ArrayList[7];

        for (int index = 0; index < 7; index++)
            distanceByDay[index] = new ArrayList<>();

        Date currentDate = this.startDate;
        long meters = 0;
        int index = 0;

        while (index < this.movements.size()) {
            Movement movement = movements.get(index);
            if (movement.getType().equals(Movement.MovementType.COMMUTE)) {
                Commute commute = (Commute) movement;
                Date commuteStart = Date.from(commute.getStartTime());
                Date commuteEnd = Date.from(commute.getEndTime());
                if (commute.getCategory() == CommuteCategory.COMMUTE.getCode()) {
                    if (currentDate.between(commuteStart, commuteEnd)) {
                        for (Step step : commute.getSteps()) {
                            if (this.transportModes == null || this.transportModes.contains(step.transportMode())) {
                                Date start = Date.from(step.getStartTime());
                                Date end = Date.from(step.getEndTime());
                                if (currentDate.between(start, end)) {
                                    long startMillis = step.getStartTime();
                                    Calendar startCalendar = currentDate.toCalendar();
                                    if (startMillis < startCalendar.getTimeInMillis())
                                        startMillis = startCalendar.getTimeInMillis();
                                    long endMillis = currentDate.nextDay().toCalendar().getTimeInMillis();
                                    if (endMillis > step.getEndTime())
                                        endMillis = step.getEndTime();

                                    double distance = step.distanceBetween(startMillis, endMillis);
                                    meters += distance;

                                }
                            }
                        }
                        Date tomorrow = currentDate.nextDay();
                        if (tomorrow.between(commuteStart, commuteEnd)) {
                            if (meters != 0) {
                                int day = currentDate.getDayOfWeek();
                                distanceByDay[day - 1].add(meters);
                                meters = 0;
                            }
                            currentDate = tomorrow;
                        } else {
                            index++;
                        }
                    } else if (currentDate.after(commuteEnd)) {
                        index++;
                    } else {
                        if (meters != 0) {
                            int day = currentDate.getDayOfWeek();
                            distanceByDay[day - 1].add(meters);
                            meters = 0;
                        }
                        currentDate = currentDate.nextDay();
                    }
                } else {
                    index++;
                }
            } else {
                index++;
            }
        }

        if (meters != 0) {
            int day = currentDate.getDayOfWeek();
            distanceByDay[day - 1].add(meters);
        }

        ArrayList<BarEntry> averageEntries = new ArrayList<>();
        ArrayList<Entry> maxEntries = new ArrayList<>();
        ArrayList<Entry> minEntries = new ArrayList<>();

        for (int day = 0; day < 7; day++) {
            if (distanceByDay[day].size() != 0) {
                averageEntries.add(new BarEntry(day,average(distanceByDay[day])/1000F));
                if (distanceByDay[day].size() > 1) {
                    maxEntries.add(new Entry(day, max(distanceByDay[day]) / 1000F));
                    minEntries.add(new Entry(day, min(distanceByDay[day]) / 1000F));
                }
            }
        }

        if (!averageEntries.isEmpty()) {
            BarDataSet averageDataSet = new BarDataSet(averageEntries, null);
            averageDataSet.setColor(ColorTemplate.rgb("#008577"));
            BarData averageData = new BarData(averageDataSet);
            averageData.setValueFormatter(new DistanceFormatter());
            averageData.setValueTextSize(Utils.convertDpToPixel(4));

            LineDataSet maxDataSet = new LineDataSet(maxEntries, "Max");
            maxDataSet.setColor(ColorTemplate.rgb("#D81B60"));
            maxDataSet.setCircleColor(ColorTemplate.rgb("#D81B60"));
            maxDataSet.setValueTextSize(Utils.convertDpToPixel(4));
            LineDataSet minDataSet = new LineDataSet(minEntries, "Min");
            minDataSet.setColor(ColorTemplate.rgb("#D81B60"));
            minDataSet.setCircleColor(ColorTemplate.rgb("#D81B60"));
            minDataSet.setValueTextSize(Utils.convertDpToPixel(4));

            LineData limitsData = new LineData(minDataSet, maxDataSet);
            limitsData.setValueFormatter(new DistanceFormatter());
            CombinedData combinedData = new CombinedData();

            combinedData.setData(averageData);
            combinedData.setData(limitsData);

            this.combinedChart.setData(combinedData);
        }
    }

    private long max(ArrayList<Long> values) {
        long out = Integer.MIN_VALUE;
        for (Long value: values)
            if (out < value)
                out = value;
        return out;
    }

    private long min(ArrayList<Long> values) {
        long out = Integer.MAX_VALUE;
        for (Long value: values)
            if (out > value)
                out = value;
        return out;
    }

    private long sum(ArrayList<Long> values) {
        long out = 0;
        for (Long value: values)
            out += value;
        return out;
    }

    private float average(ArrayList<Long> values) {
        return (float) sum(values) / (float) values.size();
    }

    @Override
    protected void clear() {
        this.combinedChart.setData(null);
    }

    @Override
    public void updateView() {
        this.combinedChart.invalidate();
    }

}
