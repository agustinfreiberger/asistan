package ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator;

import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters.DateFormatter;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters.DistanceFormatter;

public class DistanceBetween extends StatsCalculator {

    private BarChart barChart;
    private ArrayList<TransportMode> transportModes;

    public DistanceBetween() {
        this.description = "Distancia recorrida";
        this.transportModes = null;
    }

    public DistanceBetween(String modeDescription, TransportMode... transportMode) {
        this.description = "Distancia recorrida " + modeDescription;
        this.transportModes = new ArrayList<>(Arrays.asList(transportMode));
    }

    @Override
    protected void init() {
        this.view.removeAllViews();

        this.barChart = new BarChart(this.view.getContext());

        this.barChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.barChart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        this.barChart.setDescription(description);
        YAxis yAxis = this.barChart.getAxisLeft();
        yAxis.setDrawLabels(true);
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawZeroLine(true);
        yAxis.setAxisMinimum(0F);
        this.barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = this.barChart.getXAxis();
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setLabelRotationAngle(315);
        this.view.addView(this.barChart);
    }

    @Override
    protected void calculate() {
        ArrayList<Long> distanceByDate = new ArrayList<>();

        Date currentDate = this.startDate;
        long meters = 0;
        int index = 0;

        while (index < this.movements.size()) {
            Movement movement = movements.get(index);

            if (movement.getType().equals(Movement.MovementType.COMMUTE)) {
                Commute commute = (Commute) movement;
                if (commute.getCategory() == CommuteCategory.COMMUTE.getCode()) {

                    for (Step step: commute.getSteps()) {
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

                                Date tomorrow = currentDate.nextDay();

                                if (tomorrow.between(start, end)) {
                                    distanceByDate.add(meters);
                                    meters = 0;
                                    currentDate = tomorrow;
                                } else
                                    index++;
                            } else if (currentDate.after(end)) {
                                index++;
                            } else {
                                distanceByDate.add(meters);
                                meters = 0;
                                currentDate = currentDate.nextDay();
                            }
                        } else {
                            index++;
                        }
                    }
                } else {
                    index++;
                }
            } else {
                index++;
            }
        }

        while(!currentDate.after(this.endDate)) {
            distanceByDate.add(meters);
            meters = 0;
            currentDate = currentDate.nextDay();
        }

        ArrayList<BarEntry> entries = new ArrayList<>();

        boolean hasData = false;
        for (int day = 0; day < distanceByDate.size(); day++) {
            entries.add(new BarEntry(day,distanceByDate.get(day)/1000F));
            if (distanceByDate.get(day) != 0)
                hasData = true;
        }

        if (hasData) {

            BarDataSet dataSet = new BarDataSet(entries, null);
            dataSet.setColor(ColorTemplate.rgb("#008577"));
            BarData data = new BarData(dataSet);
            data.setValueFormatter(new DistanceFormatter());
            data.setValueTextSize(Utils.convertDpToPixel(4));

            this.barChart.getXAxis().setValueFormatter(new DateFormatter(this.startDate));
            this.barChart.setData(data);
        }
    }

    @Override
    protected void clear() {
        this.barChart.setData(null);
    }

    @Override
    public void updateView() {
        this.barChart.invalidate();
    }
}
