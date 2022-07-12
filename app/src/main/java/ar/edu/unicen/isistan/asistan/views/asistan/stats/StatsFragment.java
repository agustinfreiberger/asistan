package ar.edu.unicen.isistan.asistan.views.asistan.stats;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.utils.time.Date;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.AverageDistance;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.AverageVisitedPlaces;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.AverageVisits;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.Distance;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.StatsCalculator;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.TimeInPlace;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.VisitedPlaces;
import ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator.Visits;

public class StatsFragment extends Fragment {

    private OnFragmentInteractionListener listener;

    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;
    private Date startDate;
    private EditText startDateText;
    private Date endDate;
    private EditText endDateText;
    private LinearLayout statsLayout;

    private ArrayList<StatsCalculator> statsCalculators;
    private StatsCalculator currentStat;
    private ArrayList<Movement> movements;

    public StatsFragment() {
        this.movements = new ArrayList<>();
        this.currentStat = null;
        this.statsCalculators = new ArrayList<>();
        this.statsCalculators.add(new AverageVisitedPlaces());
        this.statsCalculators.add(new VisitedPlaces());
        this.statsCalculators.add(new AverageVisits());
        this.statsCalculators.add(new Visits());
        this.statsCalculators.add(new TimeInPlace(PlaceCategory.HOME));
        this.statsCalculators.add(new TimeInPlace(PlaceCategory.WORK_CATEGORY));
        this.statsCalculators.add(new Distance());
        this.statsCalculators.add(new Distance(TransportMode.FOOT.getDescription(),TransportMode.FOOT));
        this.statsCalculators.add(new Distance(TransportMode.VEHICLE.getDescription(),TransportMode.VEHICLE,TransportMode.TAXI,TransportMode.BUS));
        this.statsCalculators.add(new AverageDistance());
        this.statsCalculators.add(new AverageDistance(TransportMode.FOOT.getDescription(),TransportMode.FOOT));
        this.statsCalculators.add(new AverageDistance(TransportMode.VEHICLE.getDescription(),TransportMode.VEHICLE,TransportMode.TAXI,TransportMode.BUS));
    }

    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
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
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        this.init(view);
        return view;
    }

    private void init(final View view) {
        if (this.getContext() != null) {
            this.statsLayout = view.findViewById(R.id.stats_layout);
            this.startDateText = view.findViewById(R.id.start_date);
            this.endDateText = view.findViewById(R.id.end_date);

            this.startDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    if (StatsFragment.this.startDate.getYear() != year || StatsFragment.this.startDate.getMonth() != month + 1 || StatsFragment.this.startDate.getDay() != day) {
                        StatsFragment.this.startDate = new Date(day, month + 1, year);
                        StatsFragment.this.update();
                    }
                }
            };

            this.endDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    if (StatsFragment.this.endDate.getYear() != year || StatsFragment.this.endDate.getMonth() != month + 1 || StatsFragment.this.endDate.getDay() != day) {
                        StatsFragment.this.endDate = new Date(day, month + 1, year);
                        StatsFragment.this.update();
                    }
                }
            };

            this.startDateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int year = startDate.getYear();
                    int month = startDate.getMonth() - 1;
                    int day = startDate.getDay();

                    if (StatsFragment.this.getContext() != null) {
                        DatePickerDialog dialog = new DatePickerDialog(
                                StatsFragment.this.getContext(),
                                StatsFragment.this.startDateListener,
                                year, month, day);

                        dialog.show();
                    }
                }
            });

            this.endDateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int year = endDate.getYear();
                    int month = endDate.getMonth() - 1;
                    int day = endDate.getDay();

                    if (StatsFragment.this.getContext() != null) {
                        DatePickerDialog dialog = new DatePickerDialog(
                                StatsFragment.this.getContext(),
                                StatsFragment.this.endDateListener,
                                year, month, day);

                        dialog.show();
                    }
                }
            });

            String[] items = new String[this.statsCalculators.size()];
            for (int index = 0; index < this.statsCalculators.size(); index++) {
                items[index] = this.statsCalculators.get(index).getDescription();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, items);

            Spinner statsSelector = view.findViewById(R.id.stats_selector);
            statsSelector.setAdapter(adapter);

            statsSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StatsCalculator calculator = StatsFragment.this.statsCalculators.get(position);
                    if (calculator != StatsFragment.this.currentStat) {
                        StatsFragment.this.currentStat = calculator;
                        StatsFragment.this.updateView();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });

            Calendar calendar = Calendar.getInstance();
            this.endDate = Date.from(calendar);
            calendar.add(Calendar.MONTH,-1);
            this.startDate = Date.from(calendar);
        }
    }

    private void update() {
        this.startDateText.setText(String.format(Locale.US,"%d/%d/%d",this.startDate.getDay(), this.startDate.getMonth(), this.startDate.getYear()));
        this.endDateText.setText(String.format(Locale.US,"%d/%d/%d",this.endDate.getDay(), this.endDate.getMonth(), this.endDate.getYear()));

        Calendar startCalendar = this.startDate.toCalendar();
        final long start = startCalendar.getTimeInMillis();

        Calendar endCalendar = this.endDate.toCalendar();
        endCalendar.add(Calendar.DAY_OF_MONTH,1);
        final long end = endCalendar.getTimeInMillis();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                StatsFragment.this.loadData(start, end);
            }
        });
    }

    private void loadData(long start, long end) {
        Database database = Database.getInstance();
        ArrayList<Visit> visits = new ArrayList<>(database.mobility().selectVisitsBetween(start, end));
        ArrayList<Commute> commutes = new ArrayList<>(database.mobility().selectCommutesBetween(start, end));

        database.mobility().populate(commutes, new ArrayList<>(visits));
        database.mobility().populatePlaces(visits);

        this.movements.clear();
        this.movements.addAll(visits);
        this.movements.addAll(commutes);
        Collections.sort(this.movements);
        Collections.reverse(this.movements);
        this.updateView();
    }

    private void updateView() {
        if (this.currentStat != null) {
            if (this.getActivity() != null) {
                this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StatsFragment.this.currentStat.setView(StatsFragment.this.statsLayout);
                        StatsFragment.this.currentStat.newData(StatsFragment.this.startDate, StatsFragment.this.endDate, StatsFragment.this.movements);
                        StatsFragment.this.currentStat.updateView();
                    }
                });
            }
        }
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.update();
    }

}