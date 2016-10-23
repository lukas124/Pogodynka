package android.pogodynka;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class WeatherActivity extends AppCompatActivity {

    String lon;
    String lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("lon") != null &&
                    !getIntent().getExtras().getString("lon").isEmpty())
                lon = getIntent().getExtras().getString("lon");
            else
                lon = "19.91667";
            if(getIntent().getExtras().getString("lat") != null &&
                    !getIntent().getExtras().getString("lat").isEmpty())
                lat = getIntent().getExtras().getString("lat");
            else
                lat = "50.083328";
        } else {
            lon = "19.91667";
            lat = "50.083328";
        }

        Log.e("lon1", lon);

        //Bundle bundle = new Bundle();
        //bundle.putString("lon", lon);
        //bundle.putString("lat", lat);
        // set Fragment Arguments
        WeatherFragment wf = new WeatherFragment();
        //wf.setArguments(bundle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, wf)
                    .commit();
        }

        wf.changeLocation(lon, lat);
        new CityPreference(this).setCity(lon, lat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home) {
            WeatherFragment wf = new WeatherFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, wf)
                    .commit();
        }

        if(item.getItemId() == R.id.change_city) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        }

        if(item.getItemId() == R.id.temp) {
            TempChart ftc = new TempChart();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ftc)
                    .commit();

        }

        if(item.getItemId() == R.id.pressure) {
            PressureGraph pgf = new PressureGraph();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, pgf)
                    .commit();
        }

        return false;
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //changeLocation(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeLocation(String lon, String lat) {
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeLocation(lon, lat);
        new CityPreference(this).setCity(lon, lat);
    }
}
