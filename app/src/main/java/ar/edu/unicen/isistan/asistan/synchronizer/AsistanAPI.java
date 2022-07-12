package ar.edu.unicen.isistan.asistan.synchronizer;

import android.content.Context;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import ar.edu.unicen.isistan.asistan.storage.database.reports.Report;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;
import ar.edu.unicen.isistan.asistan.synchronizer.data.Data;

public class AsistanAPI {

    private static final String ACCESS_TOKEN = "access-token";

    private static final String END_POINT = "https://si.isistan.unicen.edu.ar/asistan/api/";
    private static final String PROFILE = "/profile";
    private static final String DATA = "/data";
    private static final String REPORTS = "reports";

    public synchronized static User getUser(@NotNull Context context) {
        User user = UserManager.loadProfile(context);
        if (user != null) {
            if (user.getInstallationId() == null) {
                // NOTA ESTO NO DEBERIA OCURRIR NUNCA
                String token = user.getToken(context);
                user.setInstallationId(UUID.randomUUID().toString());
                if (AsistanAPI.postProfile(user, token))
                    UserManager.storeComplete(context, user);
                else
                    user.setInstallationId(null);
            }
        }
        return user;
    }

    @Nullable
    public static User getProfile(User user, String token) {
        try {
            //URL url = new URL(AsistanAPI.END_POINT + user.getId() + PROFILE);
            URL url = new URL(AsistanAPI.END_POINT + 1 + PROFILE);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty(ACCESS_TOKEN,token);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader buffered = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = buffered.readLine()) != null)
                    builder.append(line);

                return new Gson().fromJson(builder.toString(), User.class);
            }
            else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                return user;
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean postProfile(User user, String token) {
        try {
            URL url = new URL(AsistanAPI.END_POINT + "1" + PROFILE);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(ACCESS_TOKEN,token);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            bw.write(new Gson().toJson(user));
            bw.flush();
            bw.close();

            return true;
            //return (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean putProfile(User user, String token) {
        try {
            URL url = new URL(AsistanAPI.END_POINT + user.getId() + PROFILE);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(false);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty(ACCESS_TOKEN, token);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            bw.write(new Gson().toJson(user));
            bw.flush();
            bw.close();
            return true;

            //return (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    public static Data getMobilityData(String id, String token) {
        try {
            //URL url = new URL(AsistanAPI.END_POINT + id + DATA );
            URL url = new URL(AsistanAPI.END_POINT + 1 + DATA );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty(ACCESS_TOKEN,token);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader buffered = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = buffered.readLine()) != null)
                    builder.append(line);
                //CREAR UN DATA Y RETORNARLO
                return new Gson().fromJson(builder.toString(), Data.class);
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean postData(Data data, String token) {
        try {
            URL url = new URL(AsistanAPI.END_POINT + data.getUser().getId() + DATA);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(ACCESS_TOKEN,token);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            bw.write(new Gson().toJson(data));
            bw.flush();
            bw.close();
            return true;
            //return (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean postReport(Report report, String token) {
        try {
            URL url = new URL(AsistanAPI.END_POINT + REPORTS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(ACCESS_TOKEN, token);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            bw.write(new Gson().toJson(report));
            bw.flush();
            bw.close();
            return true;

            // NOTE QUIZAS EN EL FUTURO ALGUNOS REPORTES DEVUELVAN UN ID LUEGO DE INSERTARSE
            // UTILES PARA HACER UN SEGUIMIENTO DEL REPORTE
            //return (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
