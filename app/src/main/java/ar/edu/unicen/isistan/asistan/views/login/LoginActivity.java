package ar.edu.unicen.isistan.asistan.views.login;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.synchronizer.AsistanAPI;
import ar.edu.unicen.isistan.asistan.utils.time.Date;

public class LoginActivity extends Activity {

    private static final int RC_LOGIN = 0;

    private GoogleSignInClient client;
    private User user;
    private AlertDialog dialog;
    private Button login_button;
    private int waiting;
    private Bitmap profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        this.dialog = null;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestProfile()
                .requestId()
                .build();

        this.client = GoogleSignIn.getClient(this, gso);
        Task<Void> task = this.client.signOut();
        
        UserManager.deleteProfile(this.getApplicationContext());

        task.addOnSuccessListener((Void result) -> {
            this.login_button = this.findViewById(R.id.login_button);
            this.login_button.setOnClickListener((View v) -> {
                this.disableButton();
                Intent intent = this.client.getSignInIntent();
                startActivityForResult(intent, RC_LOGIN);
            });
        });

        this.onStart();
    }

    private void enableButton() {
        this.login_button.setEnabled(true);
        this.login_button.setBackgroundResource(R.color.colorAccent);
    }

    private void disableButton() {
        this.login_button.setEnabled(false);
        this.login_button.setBackgroundResource(R.color.colorGray);
    }

    private void setPhoto(Bitmap bitmap) {
        this.profilePhoto = bitmap;
        this.endLoading();
    }

    private void startLoading() {

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(progressBar);

        View view = View.inflate(this,R.layout.dialog_progress,null);
        TextView text = view.findViewById(R.id.text_message);
        text.setText("Ingresando...");

        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        this.dialog.show();
    }

    private void endLoading() {
        this.waiting--;
        if (this.waiting == 0) {
            if (this.user == null) {
                if (dialog != null)
                    this.dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
                builder.setTitle("Servidor no disponible");
                builder.setMessage("Imposible registrarse. El servidor de AsisTan está caido o es inaccesible. Por favor, reintente mas tarde.");
                builder.setPositiveButton("Aceptar", null);
                builder.show();
                LoginActivity.this.enableButton();
            } else {
                if (this.profilePhoto != null)
                    user.setPhoto(this.profilePhoto);
                else
                    user.setPhoto(BitmapFactory.decodeResource(this.getResources(), R.drawable.neutral_user));
                UserManager.storeComplete(this.getApplicationContext(),this.user);
                if (dialog != null)
                    this.dialog.dismiss();
                Intent intent = new Intent(this, ConfirmProfileActivity.class);
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.user = new User("1");
        UserManager.storeComplete(this.getApplicationContext(),this.user);
        Intent intent = new Intent(this, ConfirmProfileActivity.class);
        finish();
        startActivity(intent);
        //if (requestCode == RC_LOGIN) {
        //    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        //    handleSignInResult(task);
        //}
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null && account.getId() != null) {
                this.startLoading();
                this.user = new User(account.getId());
                this.user.setEmail(account.getEmail());
                this.user.setName(account.getGivenName());
                this.user.setLastName(account.getFamilyName());

                GetProfileDetails get_profile_task = new GetProfileDetails(this,this.user);
                this.waiting = 1;
                if (account.getPhotoUrl() != null) {
                    this.user.setPhotoUrl(account.getPhotoUrl().toString());
                    this.waiting++;
                    DownloadImageTask download_task = new DownloadImageTask(this, account.getPhotoUrl().toString());
                    download_task.execute();
                }
                get_profile_task.execute();
            }
        } catch (ApiException e) {
            enableButton();
            e.printStackTrace();
        }
    }

    private static class GetProfileDetails extends AsyncTask<Void, Void, User> {

        private PeopleService service;
        private WeakReference<LoginActivity> activity;
        private User user;

        private GetProfileDetails(LoginActivity activity, User user) {
            this.user = user;
            this.activity = new WeakReference<>(activity);
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this.activity.get(), Collections.singleton(Scopes.PROFILE));
            credential.setSelectedAccount(new Account(user.getEmail(), "com.google"));
            HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
            JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            this.service = new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(this.activity.get().getResources().getString(R.string.app_name))
                    .build();
        }

        @Override
        protected User doInBackground(Void... params) {

            Person profile = null;
            try {
                profile = this.service
                        .people()
                        .get("people/me")
                        .setPersonFields("names,genders,birthdays")
                        .execute();
            } catch (UserRecoverableAuthIOException e) {
                e.printStackTrace();
            } catch (GoogleJsonResponseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (profile != null) {
                List<Gender> genders = profile.getGenders();
                List<Birthday> birthdays = profile.getBirthdays();
                if (genders != null && !genders.isEmpty())
                    this.activity.get().user.setGender(ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Gender.getGender(genders.get(0).getValue()));
                if (birthdays != null && !birthdays.isEmpty()) {
                    Birthday birthday = birthdays.get(0);
                    if (birthday.getDate() != null) {
                        int day = birthday.getDate().getDay() != null ? birthday.getDate().getDay() : 0;
                        int month = birthday.getDate().getMonth() != null ? birthday.getDate().getMonth() : 0;
                        int year = birthday.getDate().getYear() != null ? birthday.getDate().getYear() : 1900;
                        this.activity.get().user.setBirthDate(new Date(day, month, year));
                    }
                }
            }

            String token = this.user.getToken(this.activity.get());
            return AsistanAPI.getProfile(this.user,token);
        }

        @Override
        protected void onPostExecute(User user) {
            this.activity.get().user = user;
            this.activity.get().endLoading();
        }
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private WeakReference<LoginActivity> activity;
        private String url;

        private DownloadImageTask(LoginActivity activity, String url) {
            this.activity = new WeakReference<>(activity);
            this.url = url;
        }

        protected Bitmap doInBackground(String... urls) {
            try {
                InputStream in = new java.net.URL(url).openStream();
                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            this.activity.get().setPhoto(result);
        }

    }
}
