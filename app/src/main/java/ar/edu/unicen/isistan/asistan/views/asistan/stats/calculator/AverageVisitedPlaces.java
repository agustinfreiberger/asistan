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

import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;
import ar.edu.unicen.isistan.asistan.utils.time.Date;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters.IntegerFormatter;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters.PredefinedValuesFormatter;

public class AverageVisitedPlaces extends StatsCalculator {

    private CombinedChart combinedChart;

    public AverageVisitedPlaces() {
        this.description = "Promedio de lugares visitados";
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

        ArrayList<Integer>[] visitedPlacesByDay = new ArrayList[7];

        for (int index = 0; index < 7; index++)
            visitedPlacesByDay[index] = new ArrayList<>();


        Date currentDate = this.startDate;
        ArrayList<Place> currentPlaces = new ArrayList<>();
        int index = 0;

        while (index < this.movements.size()) {
            Movement movement = movements.get(index);

            if (movement.getType().equals(Movement.MovementType.VISIT)) {
                Visit visit = (Visit) movement;
                // TODO el visit.getPlace() != null no deberia ser necesario.. pero alguien reportó el error
                if (visit.getCategory() == VisitCategory.VISIT.getCode() && visit.getPlace() != null) {
                    Date start = Date.from(visit.getStartTime());
                    Date end = Date.from(visit.getEndTime());

                    if (currentDate.between(start,end)) {
                        if (!currentPlaces.contains(visit.getPlace()))
                            currentPlaces.add(visit.getPlace());
                        Date tomorrow = currentDate.nextDay();
                        if (tomorrow.between(start,end)) {
                            if (!currentPlaces.isEmpty()) {
                                int day = currentDate.getDayOfWeek();
                                visitedPlacesByDay[day - 1].add(currentPlaces.size());
                                currentPlaces.clear();
                            }
                            currentDate = tomorrow;
                        }
                        else
                            index++;
                    } else if (currentDate.after(end)) {
                        index++;
                    } else {
                        if (!currentPlaces.isEmpty()) {
                            int day = currentDate.getDayOfWeek();
                            visitedPlacesByDay[day - 1].add(currentPlaces.size());
                            currentPlaces.clear();
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

        if (!currentPlaces.isEmpty()) {
            int day = currentDate.getDayOfWeek();
            visitedPlacesByDay[day - 1].add(currentPlaces.size());
            currentPlaces.clear();
        }

        ArrayList<BarEntry> averageEntries = new ArrayList<>();
        ArrayList<Entry> maxEntries = new ArrayList<>();
        ArrayList<Entry> minEntries = new ArrayList<>();

        for (int day = 0; day < 7; day++) {
            if (visitedPlacesByDay[day].size() != 0) {
                averageEntries.add(new BarEntry(day,average(visitedPlacesByDay[day])));
                if (visitedPlacesByDay[day].size() > 1) {
                    maxEntries.add(new Entry(day, max(visitedPlacesByDay[day])));
                    minEntries.add(new Entry(day, min(visitedPlacesByDay[day])));
                }
            }
        }

        if (!averageEntries.isEmpty()) {

            BarDataSet averageDataSet = new BarDataSet(averageEntries, null);
            averageDataSet.setColor(ColorTemplate.rgb("#008577"));
            BarData averageData = new BarData(averageDataSet);
            averageDataSet.setValueTextSize(Utils.convertDpToPixel(4));

            LineDataSet maxDataSet = new LineDataSet(maxEntries, "Max");
            maxDataSet.setColor(ColorTemplate.rgb("#D81B60"));
            maxDataSet.setCircleColor(ColorTemplate.rgb("#D81B60"));
            maxDataSet.setValueTextSize(Utils.convertDpToPixel(4));
            LineDataSet minDataSet = new LineDataSet(minEntries, "Min");
            minDataSet.setColor(ColorTemplate.rgb("#D81B60"));
            minDataSet.setCircleColor(ColorTemplate.rgb("#D81B60"));
            minDataSet.setValueTextSize(Utils.convertDpToPixel(4));

            LineData limitsData = new LineData(minDataSet, maxDataSet);
            limitsData.setValueFormatter(new IntegerFormatter());
            CombinedData combinedData = new CombinedData();

            combinedData.setData(averageData);
            combinedData.setData(limitsData);

            this.combinedChart.setData(combinedData);
        }

    }

    private int max(ArrayList<Integer> values) {
        int out = Integer.MIN_VALUE;
        for (Integer value: values)
            if (out < value)
                out = value;
        return out;
    }

    private int min(ArrayList<Integer> values) {
        int out = Integer.MAX_VALUE;
        for (Integer value: values)
            if (out > value)
                out = value;
        return out;
    }

    private int sum(ArrayList<Integer> values) {
        int out = 0;
        for (Integer value: values)
            out += value;
        return out;
    }

    private float average(ArrayList<Integer> values) {
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
