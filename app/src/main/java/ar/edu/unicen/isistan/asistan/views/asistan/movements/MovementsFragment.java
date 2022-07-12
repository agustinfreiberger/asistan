package ar.edu.unicen.isistan.asistan.views.asistan.movements;

import android.app.DatePickerDialog;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.utils.time.Date;

public class MovementsFragment extends Fragment {

    private OnFragmentInteractionListener listener;

    private DatePickerDialog.OnDateSetListener date_listener;
    private Date date;
    private EditText date_text;
    private View loading;
    private View fragmentLayout;
    private MutableLiveData<ArrayList<Movement>> movements;

    public MovementsFragment() {
        this.movements = new MutableLiveData<>();
    }

    public static MovementsFragment newInstance() {
        MovementsFragment fragment = new MovementsFragment();
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
        View view = inflater.inflate(R.layout.fragment_movements, container, false);
        this.init(view);
        return view;
    }

    private void init(final View view) {
        this.date_text = view.findViewById(R.id.date);

        this.date_listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (MovementsFragment.this.date.getYear() != year || MovementsFragment.this.date.getMonth() != month+1 || MovementsFragment.this.date.getDay() != day) {
                    MovementsFragment.this.date = new Date(day, month + 1, year);
                    MovementsFragment.this.update();
                }
            }
        };

        this.date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = date.getYear();
                int month = date.getMonth() - 1;
                int day = date.getDay();

                if (MovementsFragment.this.getContext() != null) {
                    DatePickerDialog dialog = new DatePickerDialog(
                            MovementsFragment.this.getContext(),
                            MovementsFragment.this.date_listener,
                            year, month, day);

                    dialog.show();
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        this.date = Date.from(calendar);

        View previousDay = view.findViewById(R.id.previousDay);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovementsFragment.this.date = MovementsFragment.this.date.previousDay();
                MovementsFragment.this.update();
            }
        });

        View nextDay = view.findViewById(R.id.nextDay);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovementsFragment.this.date = MovementsFragment.this.date.nextDay();
                MovementsFragment.this.update();
            }
        });

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.movements_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        fragment = MyMapFragment.newInstance(MovementsFragment.this.movements);
                        break;
                    case R.id.navigation_stats:
                        fragment = MyStatsFragment.newInstance(MovementsFragment.this.movements,MovementsFragment.this);
                        break;
                    default:
                        fragment = MyDiaryFragment.newInstance(MovementsFragment.this.movements,MovementsFragment.this);
                }

                FragmentManager fragmentManager = MovementsFragment.this.getChildFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

                return true;
            }
        });

        this.fragmentLayout = view.findViewById(R.id.fragment_layout);
        this.loading = view.findViewById(R.id.loading);

        MyDiaryFragment fragment = MyDiaryFragment.newInstance(this.movements,MovementsFragment.this);
        FragmentManager fragmentManager = MovementsFragment.this.getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

        this.update();
    }

    private void update() {
        this.date_text.setText(String.format(Locale.US,"%d / %d / %d",date.getDay(), date.getMonth(), date.getYear()));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, this.date.getDay());
        calendar.set(Calendar.MONTH, this.date.getMonth()-1);
        calendar.set(Calendar.YEAR, this.date.getYear());
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        final long start = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        final long end = calendar.getTimeInMillis();

        this.loading.setVisibility(View.VISIBLE);
        this.fragmentLayout.setVisibility(View.GONE);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MovementsFragment.this.loadData(start, end);
                if (MovementsFragment.this.getActivity() != null) {
                    MovementsFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MovementsFragment.this.fragmentLayout.setVisibility(View.VISIBLE);
                            MovementsFragment.this.loading.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void loadData(long start, long end) {
        Database database = Database.getInstance();

        ArrayList<Visit> visits = new ArrayList<>(database.mobility().selectVisitsBetween(start, end));
        ArrayList<Commute> commutes = new ArrayList<>(database.mobility().selectCommutesBetween(start, end));

        database.mobility().populate(commutes, new ArrayList<>(visits));
        database.mobility().populatePlaces(visits);

        ArrayList<Movement> movements = new ArrayList<>();
        movements.addAll(visits);
        movements.addAll(commutes);
        Collections.sort(movements);
        this.movements.postValue(movements);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    public void focus(Movement movement) {
        if (movement != null) {
            long time = movement.getStartTime();
            MovementsFragment.this.date = Date.from(time);
            MovementsFragment.this.update();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.update();
    }

    public Date getDate() {
        return this.date;
    }

}