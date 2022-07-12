package ar.edu.unicen.isistan.asistan.views.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;
import java.util.UUID;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.CivilStatus;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Education;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Employment;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Gender;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Income;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.synchronizer.AsistanAPI;
import ar.edu.unicen.isistan.asistan.synchronizer.data.Data;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.StateMachineTracker;
import ar.edu.unicen.isistan.asistan.utils.time.Date;
import ar.edu.unicen.isistan.asistan.views.asistan.MainActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmProfileActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_confirm_profile);

        this.profile_photo = this.findViewById(R.id.profile_image);

        this.name = this.findViewById(R.id.name);
        this.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ConfirmProfileActivity.this.user.setName(ConfirmProfileActivity.this.name.getText().toString());
            }
        });

        this.last_name = this.findViewById(R.id.last_name);
        this.last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ConfirmProfileActivity.this.user.setLastName(ConfirmProfileActivity.this.last_name.getText().toString());
            }
        });

        this.gender = this.findViewById(R.id.gender);
        this.gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ConfirmProfileActivity.this.user.setGender(Gender.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.civilStatus = this.findViewById(R.id.civilStatus);
        this.civilStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ConfirmProfileActivity.this.user.setCivilStatus(CivilStatus.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.minors = this.findViewById(R.id.minors);
        this.minors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ConfirmProfileActivity.this.user.setMinors(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.income = this.findViewById(R.id.incomes);
        this.income.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // ConfirmProfileActivity.this.user.setIncomeLevel(Income.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.education = this.findViewById(R.id.educationLevel);
        this.education.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // ConfirmProfileActivity.this.user.setEducationLevel(Education.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.employment = this.findViewById(R.id.work);
        this.employment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // ConfirmProfileActivity.this.user.setEmployment(Employment.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.transportMode = this.findViewById(R.id.mainVehicle);
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
                //ConfirmProfileActivity.this.user.setMainTransportMode(mode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.birth_date = this.findViewById(R.id.birth_date);
        this.date_listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
              //  ConfirmProfileActivity.this.user.setBirthDate(new Date(day, month+1, year));
               // ConfirmProfileActivity.this.birth_date.setText(String.format(Locale.US,"%d / %d / %d (%d años)",user.getBirthDate().getDay(), user.getBirthDate().getMonth(), user.getBirthDate().getYear(), user.getAge()));
            }
        };

        this.birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = ConfirmProfileActivity.this.user.getBirthDate();
                int year = date.getYear();
                int month = date.getMonth() - 1;
                int day = date.getDay();

                DatePickerDialog dialog = new DatePickerDialog(
                        ConfirmProfileActivity.this,
                        ConfirmProfileActivity.this.date_listener,
                        year, month, day);

                dialog.show();
            }
        });

        this.email = this.findViewById(R.id.email);

        LinearLayout cancel_layout = this.findViewById(R.id.cancel_layout);
        cancel_layout.setVisibility(View.INVISIBLE);

        FloatingActionButton confirm = this.findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmProfileActivity.this.confirm();
            }
        });

        this.user = UserManager.loadComplete(this.getApplicationContext());

        if (this.user != null)
            init(this.user);

        this.onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ConfirmProfileActivity.this , LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init(User user) {
        this.profile_photo.setImageBitmap(user.getPhoto());
        this.name.setText(user.getName());
        this.last_name.setText(user.getLastName());
        this.gender.setSelection(user.getGender().getCode());
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
        this.birth_date.setText(String.format(Locale.US,"%d / %d / %d (%d años)",user.getBirthDate().getDay(), user.getBirthDate().getMonth(), user.getBirthDate().getYear(), user.getAge()));
        this.email.setText(user.getEmail());
    }

    private void confirm() {

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(progressBar);

        View view = View.inflate(this,R.layout.dialog_progress,null);
        TextView text = view.findViewById(R.id.text_message);
        text.setText("Sincronizando con el servidor...");

        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //ConfirmProfileActivity.this.user.setInstallationId(UUID.randomUUID().toString());
                //String token = ConfirmProfileActivity.this.user.getToken(ConfirmProfileActivity.this);
                if (AsistanAPI.postProfile(ConfirmProfileActivity.this.user,"1")) {
                    Data data = AsistanAPI.getMobilityData("1","1");
                    if (data != null) {
                        Database.getInstance().mobility().setData(data.getPlaces(), data.getVisits(), data.getCommutes());
                        ConfirmProfileActivity.this.user.setConfirmed(true);
                        UserManager.store(ConfirmProfileActivity.this, ConfirmProfileActivity.this.user);
                        StateMachineTracker.userSynchronized(ConfirmProfileActivity.this);
                        ConfirmProfileActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Intent intent = new Intent(ConfirmProfileActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    ConfirmProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmProfileActivity.this, R.style.Dialog);
                            builder.setTitle("Servidor no disponible");
                            builder.setMessage("Imposible sincronizarse. El servidor de AsisTan está caido o es inaccesible. Por favor, reintente mas tarde.");
                            builder.setPositiveButton("Aceptar", null);
                            builder.show();
                        }
                    });
                }


            }
        });

    }

}
