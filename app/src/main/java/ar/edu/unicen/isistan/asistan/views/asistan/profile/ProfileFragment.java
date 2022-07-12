package ar.edu.unicen.isistan.asistan.views.asistan.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.CivilStatus;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Education;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Employment;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Gender;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Income;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.synchronizer.data.Synchronizer;
import ar.edu.unicen.isistan.asistan.synchronizer.data.works.ProfileSyncWork;
import ar.edu.unicen.isistan.asistan.utils.time.Date;
import ar.edu.unicen.isistan.asistan.views.asistan.MainActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private OnFragmentInteractionListener listener;

    private DatePickerDialog.OnDateSetListener date_listener;
    private EditText birth_date;
    private CircleImageView profile_photo;
    private EditText name;
    private EditText last_name;
    private Spinner gender;
    private Spinner civilStatus;
    private Spinner minors;
    private Spinner education;
    private Spinner employment;
    private Spinner income;
    private Spinner transportMode;
    private TextView email;
    private User user;
    private LinearLayout cancel_layout;
    private FloatingActionButton confirm_button;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.init(view);
        return view;
    }

    private void init(View view) {
        MainActivity parent = (MainActivity) this.getActivity();
        if (this.getContext() != null && parent != null && parent.getUser() != null) {

            this.profile_photo = view.findViewById(R.id.profile_image);

            this.name = view.findViewById(R.id.name);
            this.name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ProfileFragment.this.user.setName(ProfileFragment.this.name.getText().toString());
                    ProfileFragment.this.check();
                }
            });

            this.last_name = view.findViewById(R.id.last_name);
            this.last_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ProfileFragment.this.user.setLastName(ProfileFragment.this.last_name.getText().toString());
                    ProfileFragment.this.check();
                }
            });

            this.gender = view.findViewById(R.id.gender);
            this.gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ProfileFragment.this.user.setGender(Gender.get(position));
                    ProfileFragment.this.check();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            this.civilStatus = view.findViewById(R.id.civilStatus);
            this.civilStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ProfileFragment.this.user.setCivilStatus(CivilStatus.get(position));
                    ProfileFragment.this.check();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            this.minors = view.findViewById(R.id.minors);
            this.minors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ProfileFragment.this.user.setMinors(position);
                    ProfileFragment.this.check();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            this.income = view.findViewById(R.id.incomes);
            this.income.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ProfileFragment.this.user.setIncomeLevel(Income.get(position));
                    ProfileFragment.this.check();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            this.education = view.findViewById(R.id.educationLevel);
            this.education.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ProfileFragment.this.user.setEducationLevel(Education.get(position));
                    ProfileFragment.this.check();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            this.employment = view.findViewById(R.id.work);
            this.employment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ProfileFragment.this.user.setEmployment(Employment.get(position));
                    ProfileFragment.this.check();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            this.transportMode = view.findViewById(R.id.mainVehicle);
            this.transportMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TransportMode mode;
                    switch (position) {
                        case 0:
                            mode = TransportMode.UNSPECIFIED;
                            break;
                        case 1:
                            mode = TransportMode.BICYCLE;
                            break;
                        case 2:
                            mode = TransportMode.MOTORCYCLE;
                            break;
                        case 3:
                            mode = TransportMode.VEHICLE;
                            break;
                        default:
                            mode = TransportMode.UNSPECIFIED;
                            break;
                    }
                    ProfileFragment.this.user.setMainTransportMode(mode);
                    ProfileFragment.this.check();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            this.birth_date = view.findViewById(R.id.birth_date);
            this.date_listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    ProfileFragment.this.user.setBirthDate(new Date(day, month+1, year));
                    ProfileFragment.this.birth_date.setText(String.format(Locale.US,"%d / %d / %d (%d años)",user.getBirthDate().getDay(), user.getBirthDate().getMonth(), user.getBirthDate().getYear(), user.getAge()));
                    ProfileFragment.this.check();
                }
            };
            this.birth_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date date = ProfileFragment.this.user.getBirthDate();
                    int year = date.getYear();
                    int month = date.getMonth() - 1;
                    int day = date.getDay();

                    DatePickerDialog dialog = new DatePickerDialog(
                            ProfileFragment.this.getContext(),
                            ProfileFragment.this.date_listener,
                            year, month, day);

                    dialog.show();
                }
            });

            this.email = view.findViewById(R.id.email);

            this.cancel_layout = view.findViewById(R.id.cancel_layout);
            this.cancel_layout.setVisibility(View.INVISIBLE);

            this.confirm_button = view.findViewById(R.id.confirm_button);
            this.confirm_button.setBackgroundTintList(ColorStateList.valueOf(this.getContext().getResources().getColor(R.color.colorGray)));
            this.confirm_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment.this.confirm();
                }
            });

            FloatingActionButton cancel_button = view.findViewById(R.id.cancel_button);
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment.this.revert();
                }
            });

            this.user = parent.getUser().copy();
            this.loadUserData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    private void loadUserData() {
        if (this.user.getPhoto() != null)
            this.profile_photo.setImageBitmap(this.user.getPhoto());
        this.name.setText(this.user.getName());
        this.last_name.setText(this.user.getLastName());
        this.gender.setSelection(this.user.getGender().getCode());
        this.income.setSelection(this.user.getIncomeLevel().getCode());
        this.education.setSelection(this.user.getEducationLevel().getCode());
        this.employment.setSelection(this.user.getEmployment().getCode());
        this.civilStatus.setSelection(this.user.getCivilStatus().getCode());
        int pos;
        switch (this.user.getMainTransportMode()) {
            case BICYCLE:
                pos = 1;
                break;
            case MOTORCYCLE:
                pos = 2;
                break;
            case VEHICLE:
                pos = 3;
                break;
            default:
                pos = 0;
        }
        this.minors.setSelection(this.user.getMinors());
        this.transportMode.setSelection(pos);
        this.birth_date.setText(String.format(Locale.US, "%d / %d / %d (%d años)", user.getBirthDate().getDay(), user.getBirthDate().getMonth(), user.getBirthDate().getYear(), user.getAge()));
        this.email.setText(this.user.getEmail());
    }

    private void confirm() {
        MainActivity parent = (MainActivity) this.getActivity();
        if (parent != null && parent.getUser() != null && this.getContext() != null) {
            User aux = parent.getUser();
            aux.copyFrom(this.user);
            UserManager.storeComplete(this.getContext(),user);
            ProfileSyncWork.createWork(parent.getApplicationContext());
            this.cancel_layout.setVisibility(View.INVISIBLE);
            this.confirm_button.setEnabled(false);
            this.confirm_button.setBackgroundTintList(ColorStateList.valueOf(this.getContext().getResources().getColor(R.color.colorGray)));
            parent.loadUserData();
        }
    }

    private void revert() {
        MainActivity parent = (MainActivity) this.getActivity();
        if (parent != null && parent.getUser() != null) {
            this.user = parent.getUser().copy();
            this.loadUserData();
            this.cancel_layout.setVisibility(View.INVISIBLE);
            this.confirm_button.setEnabled(false);
            this.confirm_button.setBackgroundTintList(ColorStateList.valueOf(this.getContext().getResources().getColor(R.color.colorGray)));
        }
    }

    private void check() {
        MainActivity parent = (MainActivity) this.getActivity();
        if (parent != null && parent.getUser() != null && !parent.getUser().equals(this.user)) {
            this.cancel_layout.setVisibility(View.VISIBLE);
            this.confirm_button.setEnabled(true);
            this.confirm_button.setBackgroundTintList(ColorStateList.valueOf(this.getContext().getResources().getColor(R.color.colorAccent)));
        } else {
            this.cancel_layout.setVisibility(View.INVISIBLE);
            this.confirm_button.setEnabled(false);
            this.confirm_button.setBackgroundTintList(ColorStateList.valueOf(this.getContext().getResources().getColor(R.color.colorGray)));
        }
    }

}
