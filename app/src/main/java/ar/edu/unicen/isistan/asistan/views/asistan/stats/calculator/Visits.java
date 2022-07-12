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

import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;
import ar.edu.unicen.isistan.asistan.utils.time.Date;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters.DateFormatter;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.formatters.IntegerFormatter;

public class Visits extends StatsCalculator {

    private BarChart barChart;

    public Visits() {
        this.description = "Cantidad de visitas";
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
        yAxis.setAxisMinimum(0);
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
        ArrayList<Integer> visitedPlacesByDate = new ArrayList<>();

        Date currentDate = this.startDate;
        int currentVisits = 0;
        int index = 0;

        while (index < this.movements.size()) {
            Movement movement = movements.get(index);

            if (movement.getType().equals(Movement.MovementType.VISIT)) {
                Visit visit = (Visit) movement;
                if (visit.getCategory() == VisitCategory.VISIT.getCode()) {
                    Date start = Date.from(visit.getStartTime());
                    Date end = Date.from(visit.getEndTime());

                    if (currentDate.between(start,end)) {
                        currentVisits++;
                        Date tomorrow = currentDate.nextDay();
                        if (tomorrow.between(start,end)) {
                            visitedPlacesByDate.add(currentVisits);
                            currentVisits = 0;
                            currentDate = tomorrow;
                        }
                        else
                            index++;
                    } else if (currentDate.after(end)) {
                        index++;
                    } else {
                        visitedPlacesByDate.add(currentVisits);
                        currentVisits = 0;
                        currentDate = currentDate.nextDay();
                    }
                } else {
                    index++;
                }
            } else {
                index++;
            }
        }

        while(!currentDate.after(this.endDate)) {
            visitedPlacesByDate.add(currentVisits);
            currentVisits = 0;
            currentDate = currentDate.nextDay();
        }

        ArrayList<BarEntry> entries = new ArrayList<>();

        boolean hasData = false;
        for (int day = 0; day < visitedPlacesByDate.size(); day++) {
            entries.add(new BarEntry(day,visitedPlacesByDate.get(day)));
            if (visitedPlacesByDate.get(day) != 0)
                hasData = true;
        }

        if (hasData) {
            BarDataSet dataSet = new BarDataSet(entries, null);
            dataSet.setValueFormatter(new IntegerFormatter());
            dataSet.setColor(ColorTemplate.rgb("#008577"));
            BarData data = new BarData(dataSet);
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
