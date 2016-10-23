package android.pogodynka;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetch {
    private static final String OPEN_WEATHER_MAP_API =
              "http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&APPID=08f87197b9e05887ee84e50d113c8565&units=metric";

    public static JSONObject getJSON(Context context, String lon, String lat){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, lat, lon));

            //Log.e("lat", lat);
            Log.e("JSON URL", url.toString());

            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.connect();
           //connection.addRequestProperty("x-api-key",
           //   sscontext.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while((tmp=reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            Log.e("Json data", data.toString());

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        } catch(Exception e) {
            Log.e("Exception", e.toString());
            return null;
        }
    }
}
