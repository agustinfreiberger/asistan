package ar.edu.unicen.isistan.asistan.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.views.asistan.MainActivity;
import ar.edu.unicen.isistan.asistan.views.login.ConfirmProfileActivity;
import ar.edu.unicen.isistan.asistan.views.login.LoginActivity;
import ar.edu.unicen.isistan.asistan.views.login.PrivacyPolicyActivity;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        User user = UserManager.loadComplete(this.getApplicationContext());

        if (user == null) {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } else if (!user.isConfirmed()) {
            Intent intent = new Intent(this, ConfirmProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }

    }

}
