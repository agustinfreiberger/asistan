package ar.edu.unicen.isistan.asistan.views.asistan.movements;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Step;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;
import ar.edu.unicen.isistan.asistan.utils.time.Date;
import ar.edu.unicen.isistan.asistan.utils.time.Time;

public class MyStatsFragment extends Fragment {

    private static final DecimalFormat mFormat = new DecimalFormat("###,###,##0.00");

    private MyDiaryFragment.OnFragmentInteractionListener listener;

    private MutableLiveData<ArrayList<Movement>> movements;
    private Observer<ArrayList<Movement>> observer;
    private MovementsFragment movementsFragment;

    private SparseArray<ArrayList<Step>> stepsByTransportMode;
    private SparseArray<ArrayList<Visit>> visitsByPlaceCategory;

    private TextView commutesText;
    private TextView visitsText;
    private PieChart commutesChart;
    private PieChart visitsChart;

    public MyStatsFragment() {
        this.stepsByTransportMode = new SparseArray<>();
        this.visitsByPlaceCategory = new SparseArray<>();
    }

    private void setMovements(MutableLiveData<ArrayList<Movement>> movements, MovementsFragment movementsFragment) {
        this.movements = movements;
        this.movementsFragment = movementsFragment;
        this.observer = new Observer<ArrayList<Movement>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Movement> movements) {
                if (movements != null) {
                    MyStatsFragment.this.calculateStats(movements);
                }
            }
        };
    }

    public static MyStatsFragment newInstance( MutableLiveData<ArrayList<Movement>> movements, MovementsFragment movementsFragment) {
        MyStatsFragment fragment = new MyStatsFragment();
        fragment.setMovements(movements, movementsFragment);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_stats, container, false);
        this.init(view);
        return view;
    }

    private void init(View view) {
        this.commutesChart = view.findViewById(R.id.commutes_chart);
        this.visitsChart = view.findViewById(R.id.visits_chart);
        this.visitsText = view.findViewById(R.id.visits_tab);
        this.commutesText = view.findViewById(R.id.commutes_tab);

        this.visitsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyStatsFragment.this.showVisits();
            }
        });

        this.commutesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyStatsFragment.this.showCommutes();
            }
        });

        this.showVisits();
    }

    private void showVisits() {
        this.visitsText.setTypeface( this.visitsText.getTypeface(), Typeface.BOLD);
        this.commutesText.setTypeface(null, Typeface.NORMAL);

        this.commutesChart.setVisibility(View.GONE);
        this.visitsChart.setVisibility(View.VISIBLE);
    }

    private void showCommutes() {
        this.commutesText.setTypeface( this.commutesText.getTypeface(), Typeface.BOLD);
        this.visitsText.setTypeface(null, Typeface.NORMAL);

        this.visitsChart.setVisibility(View.GONE);
        this.commutesChart.setVisibility(View.VISIBLE);
    }

    private void calculateStats(ArrayList<Movement> movements) {
        this.stepsByTransportMode.clear();
        this.visitsByPlaceCategory.clear();

        for (Movement movement: movements) {
            if (movement.getType().equals(Movement.MovementType.COMMUTE))
                this.add((Commute) movement);
            else
                this.add((Visit) movement);
        }

        this.loadData();
    }

    private void add(Commute commute) {
        for (Step step: commute.getSteps()) {
            TransportMode transportMode = step.transportMode();

            ArrayList<Step> aux = this.stepsByTransportMode.get(transportMode.getCode());
            if (aux == null)
                aux = new ArrayList<>();
            aux.add(step);

            this.stepsByTransportMode.put(transportMode.getCode(),aux);
        }
    }

    private void add(Visit visit) {
        if (visit.getCategory() == VisitCategory.VISIT.getCode()) {
            PlaceCategory category = PlaceCategory.UNSPECIFIED;
            if (visit.getPlace() != null)
                category = PlaceCategory.get(visit.getPlace().getPlaceCategory());

            if (category.getParent() != null)
                category = category.getParent();

            ArrayList<Visit> aux = this.visitsByPlaceCategory.get(category.getCode());

            if (aux == null)
                aux = new ArrayList<>();
            aux.add(visit);

            this.visitsByPlaceCategory.put(category.getCode(),aux);
        }
    }

    private void loadData() {
        this.loadCommutesData();
        this.loadVisitsData();
    }

    private void loadVisitsData() {
        ArrayList<PieEntry> aux = new ArrayList<>();

        long total = 0;

        for (int index = 0; index < this.visitsByPlaceCategory.size(); index++) {
            int key = this.visitsByPlaceCategory.keyAt(index);

            long duration = 0;

            for (Visit visit: this.visitsByPlaceCategory.get(key)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTimeInMillis(visit.getStartTime());
                end.setTimeInMillis(visit.getEndTime());

                if (start.get(Calendar.DAY_OF_MONTH) != end.get(Calendar.DAY_OF_MONTH)) {
                    Date date = this.movementsFragment.getDate();
                    Calendar startCalendar = date.toCalendar();
                    Calendar endCalendar = date.toCalendar();
                    endCalendar.add(Calendar.DAY_OF_YEAR,1);

                    long start_milliseconds = start.getTimeInMillis();
                    if (start_milliseconds < startCalendar.getTimeInMillis())
                        start_milliseconds = startCalendar.getTimeInMillis();
                    long end_milliseconds = end.getTimeInMillis();
                    if (end_milliseconds > endCalendar.getTimeInMillis())
                        end_milliseconds = endCalendar.getTimeInMillis();

                    duration += end_milliseconds - start_milliseconds;
                } else {
                    duration += visit.duration();
                }

            }

            total += duration;
            PlaceCategory category = PlaceCategory.get(key);

            PieEntry entry = new PieEntry(duration, category.getDescription());
            entry.setIcon(getResources().getDrawable(category.getIconSrc()));
            aux.add(entry);
        }

        Collections.sort(aux,new Comparator<PieEntry>() {
            @Override
            public int compare(PieEntry entry1, PieEntry entry2) {
                return Float.compare(entry1.getValue(),entry2.getValue());
            }
        });

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int index = 0; index < (aux.size()+1)/2; index++) {
            PieEntry first = aux.get(index);
            PieEntry last = aux.get(aux.size()-1-index);
            if (first.getValue()/total < 0.06)
                first.setIcon(null);
            if (last.getValue()/total < 0.06)
                last.setIcon(null);
            entries.add(first);
            if (first != last)
                entries.add(last);
        }

        PieDataSet dataSet = new PieDataSet(entries,null);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setIconsOffset(new MPPointF(-16,0));
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        dataSet.setSliceSpace(5F);

        dataSet.setValueLineVariableLength(true);
        PieData data = new PieData();
        data.setDataSet(dataSet);
        data.setValueTextSize(Utils.convertDpToPixel(4));
        Description description = new Description();
        description.setText("");
        this.visitsChart.setDescription(description);
        data.setValueFormatter(new PercentFormatter());
        this.visitsChart.setCenterText("Tiempo en lugares\n" + Time.asDuration(total));
        this.visitsChart.setDrawCenterText(true);
        this.visitsChart.setDrawEntryLabels(false);
        Legend legend = this.visitsChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        this.visitsChart.setUsePercentValues(true);
        this.visitsChart.setData(data);
        this.visitsChart.invalidate();
    }

    private void loadCommutesData() {
        ArrayList<PieEntry> aux = new ArrayList<>();

        Date date = this.movementsFragment.getDate();
        float total = 0;

        for (int index = 0; index < this.stepsByTransportMode.size(); index++) {

            int key = this.stepsByTransportMode.keyAt(index);

            float distance = 0;
            long time = 0;

            for (Step step: this.stepsByTransportMode.get(key)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTimeInMillis(step.getStartTime());
                end.setTimeInMillis(step.getEndTime());

                if (start.get(Calendar.DAY_OF_MONTH) != end.get(Calendar.DAY_OF_MONTH)) {
                    end.set(Calendar.HOUR_OF_DAY,0);
                    end.set(Calendar.MINUTE,0);
                    end.set(Calendar.SECOND,0);
                    end.set(Calendar.MILLISECOND,0);
                    if (date.getDay() == start.get(Calendar.DAY_OF_MONTH)) {
                        distance += step.distanceUntil(end.getTimeInMillis());
                        time += end.getTimeInMillis() - step.getStartTime();
                    } else {
                        distance += step.distanceSince(end.getTimeInMillis());
                        time += step.getEndTime() - end.getTimeInMillis();
                    }
                } else {
                    time += step.getDuration();
                    distance += step.distance();
                }

            }

            total += distance;

            PieEntry entry = new PieEntry(distance, TransportMode.get(key).getDescription());
            entry.setData(time);
            entry.setIcon(getResources().getDrawable(TransportMode.get(key).getIconSrc()));

            aux.add(entry);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int index = 0; index < (aux.size()+1)/2; index++) {
            PieEntry first = aux.get(index);
            PieEntry last = aux.get(aux.size()-1-index);
            if (first.getValue()/total < 0.065)
                first.setIcon(null);
            if (last.getValue()/total < 0.065)
                last.setIcon(null);
            entries.add(first);
            if (first != last)
                entries.add(last);
        }

        PieDataSet dataSet = new PieDataSet(entries,null);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setIconsOffset(new MPPointF(-16,0));
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        dataSet.setSliceSpace(5F);
        dataSet.setValueLineVariableLength(true);
        PieData data = new PieData();
        data.setDataSet(dataSet);
        data.setValueTextSize(Utils.convertDpToPixel(4));
        Description description = new Description();
        description.setText("");
        this.commutesChart.setDescription(description);
        data.setValueFormatter(new CommuteFormatter());
        this.commutesChart.setCenterText("Distancia total\n" + MyStatsFragment.getDistance(total));
        this.commutesChart.setDrawCenterText(true);
        this.commutesChart.setDrawEntryLabels(false);
        Legend legend = this.commutesChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        this.commutesChart.setData(data);
        this.commutesChart.invalidate();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof MyDiaryFragment.OnFragmentInteractionListener) {
            this.listener = (MyDiaryFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.getActivity() != null)
            this.movements.removeObserver(this.observer);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.getActivity() != null)
            this.movements.observe(this.getActivity(),this.observer);
    }

    private class CommuteFormatter implements IValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            String out = MyStatsFragment.getDistance(value);
            if (entry.getData() != null && Long.class.isInstance(entry.getData())) {
                out += "\n(" + Time.asDuration((long) entry.getData()) + ")";
            }
            return out;
        }

    }

    private static String getDistance(float value) {
        if (value < 1000)
            return mFormat.format(value) + " mts";
        else
            return mFormat.format(value/1000) + " km";
    }

}
