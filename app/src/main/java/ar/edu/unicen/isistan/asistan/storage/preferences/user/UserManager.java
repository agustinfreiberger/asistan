package ar.edu.unicen.isistan.asistan.storage.preferences.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class UserManager {

    private static final String PREFERENCE = "ar.edu.unicen.isistan.asistan.storage.preferences.user";
    private static final String PROFILE_IMAGE = "ar.edu.unicen.isistan.asistan.storage.preferences.user.photo";
    private static final String KEY_PROFILE = "value";

    @Nullable
    public synchronized static User loadComplete(@NotNull Context context) {
        try {
            context = context.getApplicationContext();

            SharedPreferences preferences = context.getSharedPreferences(UserManager.PREFERENCE, Context.MODE_PRIVATE);
            String json = preferences.getString(UserManager.KEY_PROFILE, null);
            if (json == null)
                return null;
            User user = new Gson().fromJson(json, User.class);
            FileInputStream input = context.openFileInput(UserManager.PROFILE_IMAGE);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            user.setPhoto(bitmap);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public synchronized static User loadProfile(@NotNull Context context) {
        context = context.getApplicationContext();
        try {
            SharedPreferences preferences = context.getSharedPreferences(UserManager.PREFERENCE, Context.MODE_PRIVATE);
            String json = preferences.getString(UserManager.KEY_PROFILE, null);
            if (json == null)
                return null;
            return new Gson().fromJson(json, User.class);
        }  catch (Exception ignored) {
            return null;
        }
    }

    public synchronized static void deleteProfile(@NotNull Context context) {
        context = context.getApplicationContext();

        SharedPreferences preferences = context.getSharedPreferences(UserManager.PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(UserManager.KEY_PROFILE);
        editor.apply();
        context.deleteFile(UserManager.PROFILE_IMAGE);
    }

    public synchronized static void storeComplete(@NotNull Context context, @NotNull User user) {
        try {
            context = context.getApplicationContext();

            SharedPreferences preferences = context.getSharedPreferences(UserManager.PREFERENCE, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = gson.toJson(user);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(UserManager.KEY_PROFILE,json);
            editor.apply();
            if (user.getPhoto() != null) {
                FileOutputStream output = context.openFileOutput(UserManager.PROFILE_IMAGE, Context.MODE_PRIVATE);
                user.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush();
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void store(@NotNull Context context, @NotNull User user) {
        context = context.getApplicationContext();

        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPreferences preferences = context.getSharedPreferences(UserManager.PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserManager.KEY_PROFILE,json);
        editor.apply();
    }

}
