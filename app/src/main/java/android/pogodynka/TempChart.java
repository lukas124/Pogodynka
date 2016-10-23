package android.pogodynka;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class TempChart extends Fragment {

    Handler handler;
    LineChart chart;

    public TempChart() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            getJsonData(new CityPreference(getActivity()).getLon(), new CityPreference(getActivity()).getLat());
        } catch (JSONException e) {
            Log.e("Exception1", e.toString());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_temp_chart, container, false);
        chart = (LineChart) view.findViewById(R.id.chart);

        return view;
    }

    private void getJsonData(final String lon, final String lat) throws JSONException {

        new Thread() {
            public void run() {
                final JSONObject json = new RemoteFetch().getJSON(getActivity(), lon, lat);
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
                           getData(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void getData(JSONObject json) {
        try {
            ArrayList<Entry> yVals = new ArrayList<>();
            ArrayList<String> xVals = new ArrayList<>();

            JSONObject main = json.getJSONArray("list").getJSONObject(0).getJSONObject("main");
            JSONObject main1 = json.getJSONArray("list").getJSONObject(6).getJSONObject("main");
            JSONObject main2 = json.getJSONArray("list").getJSONObject(12).getJSONObject("main");
            //Log.e("liczba", String.valueOf(Math.round(main.getDouble("temp"))));
            //Log.e("------>", new Entry(Math.round(main.getDouble("temp")), 0).toString());
            yVals.add(new Entry(Math.round(main.getDouble("temp")), 0));
            //Log.e(":::::-->", yVals.get(0).toString());
            yVals.add(new Entry(Math.round(main1.getDouble("temp")), 1));
            yVals.add(new Entry(Math.round(main2.getDouble("temp")), 2));


            //Log.e("ArrayList", yVals.get(0).toString());

            java.util.Date data = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    .parse(json.getJSONArray("list").getJSONObject(0).getString("dt_txt"));
            java.util.Date data1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    .parse(json.getJSONArray("list").getJSONObject(6).getString("dt_txt"));
            java.util.Date data2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    .parse(json.getJSONArray("list").getJSONObject(12).getString("dt_txt"));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            xVals.add(sdf.format(data));
            xVals.add(sdf.format(data1));
            xVals.add(sdf.format(data2));

            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(yVals, "Temp chart");

            set1.setLineWidth(4f);
            set1.setCircleSize(3f);
            //set1.setDrawValues(false);

            // create a date object with the datasets
            LineData date = new LineData(xVals, set1);

            chart.setData(date);
        } catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
