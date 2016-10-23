package android.pogodynka;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    TextView updatedField1;
    TextView detailsField1;
    TextView currentTemperatureField1;
    TextView weatherIcon1;
    TextView updatedField2;
    TextView detailsField2;
    TextView currentTemperatureField2;
    TextView weatherIcon2;
    Handler handler;
    String lon;
    String lat;

    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getLon(), new CityPreference(getActivity()).getLat());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        updatedField1 = (TextView)rootView.findViewById(R.id.updated_field2);
        updatedField2 = (TextView)rootView.findViewById(R.id.updated_field3);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        detailsField1 = (TextView)rootView.findViewById(R.id.details_field2);
        detailsField2 = (TextView)rootView.findViewById(R.id.details_field3);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        currentTemperatureField1 = (TextView)rootView.findViewById(R.id.current_temperature_field2);
        currentTemperatureField2 = (TextView)rootView.findViewById(R.id.current_temperature_field3);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        weatherIcon1 = (TextView)rootView.findViewById(R.id.weather_icon2);
        weatherIcon2 = (TextView)rootView.findViewById(R.id.weather_icon3);
        weatherIcon.setTypeface(weatherFont);
        weatherIcon1.setTypeface(weatherFont);
        weatherIcon2.setTypeface(weatherFont);

        return rootView;
    }

    public void updateWeatherData(final String lon, final String lat) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), lon, lat);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                Toast.makeText(getActivity(),
                                        getActivity().getString(R.string.place_not_found),
                                        Toast.LENGTH_LONG).show();
                            } catch(Exception e) {
                                Log.e("Places not found exception", e.toString());
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getJSONObject("city").getString("name").toUpperCase(Locale.US) +
                    ", " + json.getJSONObject("city").getString("country"));

            JSONObject details = json.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONArray("list").getJSONObject(0).getJSONObject("main");
            JSONObject details1 = json.getJSONArray("list").getJSONObject(6).getJSONArray("weather").getJSONObject(0);
            JSONObject main1 = json.getJSONArray("list").getJSONObject(6).getJSONObject("main");
            JSONObject details2 = json.getJSONArray("list").getJSONObject(12).getJSONArray("weather").getJSONObject(0);
            JSONObject main2 = json.getJSONArray("list").getJSONObject(12).getJSONObject("main");

            detailsField.setText(
                    details.getString("description") +
                            "\n" + main.getString("pressure") + " hPa");
            detailsField1.setText(
                    details1.getString("description") +
                            "\n" + main1.getString("pressure") + " hPa");
            detailsField2.setText(
                    details2.getString("description") +
                            "\n" + main2.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp")) + " ℃");
            currentTemperatureField1.setText(
                    String.format("%.2f", main1.getDouble("temp")) + " ℃");
            currentTemperatureField2.setText(
                    String.format("%.2f", main2.getDouble("temp")) + " ℃");

            Date data = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    .parse(json.getJSONArray("list").getJSONObject(0).getString("dt_txt"));
            Date data1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    .parse(json.getJSONArray("list").getJSONObject(6).getString("dt_txt"));
            Date data2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    .parse(json.getJSONArray("list").getJSONObject(12).getString("dt_txt"));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String updatedOn = sdf.format(data);
            String updatedOn1 = sdf.format(data1);
            String updatedOn2 = sdf.format(data2);

            updatedField.setText(updatedOn);
            updatedField1.setText(updatedOn1);
            updatedField2.setText(updatedOn2);

            Log.e("weather id", details.getString("id"));
            weatherIcon.setText(setWeatherIcon(details.getInt("id"), 0, 0));
            weatherIcon1.setText(setWeatherIcon(details1.getInt("id"), 0, 0));
            weatherIcon2.setText(setWeatherIcon(details2.getInt("id"), 0,0));
            // json.getJSONArray("list").getJSONObject(0).getJSONObject("sys").getLong("sunrise") * 1000,
            // json.getJSONArray("list").getJSONObject(0).getJSONObject("sys").getLong("sunset") * 1000);

            ArrayList<String> temp = new ArrayList<>();
            temp.add(String.format("%.2f", main.getDouble("temp")));
            temp.add(String.format("%.2f", main1.getDouble("temp")));
            temp.add(String.format("%.2f", main2.getDouble("temp")));

        } catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800) {
            //long currentTime = new Date().getTime();
            //if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            //} else {
              //  icon = getActivity().getString(R.string.weather_clear_night);
            //}
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    public void changeLocation(String lon, String lat){
        updateWeatherData(lon, lat);
    }
}
