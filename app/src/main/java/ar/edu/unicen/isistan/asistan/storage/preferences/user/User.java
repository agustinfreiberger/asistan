package ar.edu.unicen.isistan.asistan.storage.preferences.user;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.TransportMode;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.CivilStatus;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Education;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Employment;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Gender;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.profile.Income;
import ar.edu.unicen.isistan.asistan.synchronizer.data.Synchronizer;
import ar.edu.unicen.isistan.asistan.synchronizer.data.works.ProfileSyncWork;
import ar.edu.unicen.isistan.asistan.utils.time.Date;

public class User {

    private String id;
    private String name;
    private String lastName;
    private Date birthDay;
    private String email;
    private int gender;
    private int educationLevel;
    private int employment;
    private int incomeLevel;
    private int civilStatus;
    private int mainTransportMode;
    private int minors;
    private boolean confirmed;
    private String installationId;
    private String photoUrl;
    private transient Bitmap photo;

    public User(String id) {
        this.id = id;
        this.name = null;
        this.lastName = null;
        this.birthDay = Date.defaultDate();
        this.gender = Gender.UNSPECIFIED.getCode();
        this.educationLevel = Education.UNSPECIFIED.getCode();
        this.employment = Employment.UNSPECIFIED.getCode();
        this.incomeLevel = Income.UNSPECIFIED.getCode();
        this.civilStatus = CivilStatus.UNSPECIFIED.getCode();
        this.mainTransportMode = TransportMode.UNSPECIFIED.getCode();
        this.email = null;
        this.photo = null;
    }

    public User copy() {
        User user = new User(this.id);
        user.setPhoto(this.photo);
        user.setName(this.name);
        user.setLastName(this.lastName);
        user.setGender(Gender.get(this.gender));
        user.setBirthDate(this.birthDay);
        user.setEmail(this.email);
        user.setMainTransportMode(TransportMode.get(this.mainTransportMode));
        user.setEmployment(Employment.get(this.employment));
        user.setEducationLevel(Education.get(this.educationLevel));
        user.setIncomeLevel(Income.get(this.incomeLevel));
        user.setCivilStatus(CivilStatus.get(this.civilStatus));
        user.setMinors(this.minors);
        user.setConfirmed(this.confirmed);
        user.setInstallationId(this.installationId);
        return user;
    }

    public void copyFrom(User user) {
        this.id = user.getId();
        this.setPhoto(user.getPhoto());
        this.setName(user.getName());
        this.setLastName(user.getLastName());
        this.setGender(user.getGender());
        this.setBirthDate(user.getBirthDate());
        this.setEmail(user.getEmail());
        this.setMainTransportMode(user.getMainTransportMode());
        this.setEmployment(user.getEmployment());
        this.setEducationLevel(user.getEducationLevel());
        this.setIncomeLevel(user.getIncomeLevel());
        this.setCivilStatus(user.getCivilStatus());
        this.setMinors(user.getMinors());
        this.setConfirmed(user.isConfirmed());
        this.setInstallationId(user.getInstallationId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String last_name) {
        this.lastName = last_name;
    }

    public Date getBirthDate() {
        return birthDay;
    }

    public void setBirthDate(Date birth_day) {
        this.birthDay = birth_day;
    }

    public Gender getGender() {
        return Gender.get(gender);
    }

    public void setGender(Gender gender) {
        this.gender = gender.getCode();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Education getEducationLevel() {
        return Education.get(educationLevel);
    }

    public void setEducationLevel(Education educationLevel) {
        this.educationLevel = educationLevel.getCode();
    }

    public Employment getEmployment() {
        return Employment.get(employment);
    }

    public void setEmployment(Employment employment) {
        this.employment = employment.getCode();
    }

    public Income getIncomeLevel() {
        return Income.get(incomeLevel);
    }

    public void setIncomeLevel(Income incomeLevel) {
        this.incomeLevel = incomeLevel.getCode();
    }

    public TransportMode getMainTransportMode() {
        return TransportMode.get(mainTransportMode);
    }

    public void setMainTransportMode(TransportMode mainTransportMode) {
        this.mainTransportMode = mainTransportMode.getCode();
    }

    public CivilStatus getCivilStatus() {
        return CivilStatus.get(civilStatus);
    }

    public void setCivilStatus(CivilStatus civilStatus) {
        this.civilStatus = civilStatus.getCode();
    }

    public int getMinors() {
        return minors;
    }

    public void setMinors(int minors) {
        this.minors = minors;
    }


    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getAge() {
        Calendar calendar = Calendar.getInstance();
        int now_year = calendar.get(Calendar.YEAR);
        int now_month = calendar.get(Calendar.MONTH) + 1;
        int result = now_year - this.birthDay.getYear();

        if (this.birthDay.getMonth() > now_month) {
            result--;
        } else if (this.birthDay.getMonth() == now_month) {
            int now_day = calendar.get(Calendar.DAY_OF_MONTH);
            if (this.birthDay.getDay() > now_day)
                result--;
        }
        return result;
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (o instanceof User) {
            User aux = (User) o;
            return (
                    this.getName().equals(aux.getName()) &&
                    this.getLastName().equals(aux.getLastName()) &&
                    this.getGender().equals(aux.getGender()) &&
                    this.getBirthDate().equals(aux.getBirthDate()) &&
                    this.getEducationLevel().equals(aux.getEducationLevel()) &&
                    this.getIncomeLevel().equals(aux.getIncomeLevel()) &&
                    this.getEmployment().equals(aux.getEmployment()) &&
                    this.getCivilStatus().equals(aux.getCivilStatus()) &&
                    this.getMainTransportMode().equals(aux.getMainTransportMode()) &&
                    this.getMinors() == aux.getMinors()
            );
        }
        return false;
    }

    @Nullable
    public String getToken(Context context) {
         try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getApplicationContext().getString(R.string.client_id))
                    .requestId()
                    .build();

            GoogleSignInClient google_client = GoogleSignIn.getClient(context.getApplicationContext(), gso);
            Task<GoogleSignInAccount> task = google_client.silentSignIn();
            GoogleSignInAccount account = Tasks.await(task, 10, TimeUnit.SECONDS);
            String token = account.getIdToken();
            if (account.getPhotoUrl() != null && !account.getPhotoUrl().toString().equals(this.getPhotoUrl())) {
                this.setPhotoUrl(account.getPhotoUrl().toString());
                ProfileSyncWork.createWork(context);
                UserManager.store(context,this);
            }
            return token;
        } catch (Exception e) {
            return null;
        }
    }

}
