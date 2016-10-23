package android.pogodynka;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {
    SharedPreferences prefsLon;
    SharedPreferences prefsLat;


    public CityPreference(Activity activity){
        prefsLon = activity.getPreferences(Activity.MODE_PRIVATE);
        prefsLat = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Krakow as the default city
    String getLon() {
        return prefsLon.getString("lon", "19.91667");
    }

    String getLat() {
        return prefsLat.getString("lat", "50.083328");
    }

    void setCity(String lon, String lat){
        prefsLon.edit().putString("lon", lon).commit();
        prefsLat.edit().putString("lat", lat).commit();
    }
}
