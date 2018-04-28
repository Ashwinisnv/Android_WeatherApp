package com.ashwinisnv.weathersay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashwinisnv.weathersay.Model.OpenWeatherMap;
import com.ashwinisnv.weathersay.Model.coordinates;
import com.ashwinisnv.weathersay.Utilities.DataController;
import com.ashwinisnv.weathersay.Utilities.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView city, celsius;

    LocationManager locationManager;
    double lat, lng;
    String provider;
    OpenWeatherMap openWeather = new OpenWeatherMap();
    int my_perm = 0;
    ArrayList<OpenWeatherMap> openweatherlist = new ArrayList<>();
    private List<OpenWeatherMap> op;
    private RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                MainActivity.this.startActivity(myIntent);
            }
        });

        rv = findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        //UI Elements
        city = findViewById(R.id.city);
        celsius = findViewById(R.id.celsius);

        //Receive coordinates(latitude and longitude) of current location of the user
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, my_perm);
        }
        Location loc = locationManager.getLastKnownLocation(provider);
        if (loc == null)
            Log.e("Tag", "No location");
    }

    public MainActivity() {
        super();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, my_perm);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        String lat_temp;
        String lng_temp;
        final DataController mydb;
        ArrayList<coordinates> al = new ArrayList<>();

        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            lat_temp = bd.get("lat").toString();
            lng_temp = bd.get("lng").toString();
            if (lng_temp != null && lat_temp != null) {
                lat = Double.parseDouble(lat_temp);
                lng = Double.parseDouble(lng_temp);

                //Insert the user preferred location to SQLite database
                DataController dataController = new DataController(getBaseContext());
                dataController.open();
                try {
                    long retValue = dataController.insert(lat, lng);
                    if (retValue != -1) {
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_LONG;
                        Toast.makeText(context, "Location saved successfully!", duration).show();
                    }
                } catch (SQLiteConstraintException e) {
                    Context context1 = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    Toast.makeText(context1, "Location already saved!", duration).show();
                }
                dataController.close();
            }
        }

        // Fetch current location of the user from GPS
        coordinates currentCoordinate = new coordinates();
        lat = location.getLatitude();
        lng = location.getLongitude();
        currentCoordinate.setLat(lat);
        currentCoordinate.setLng(lng);
        al.add(0, currentCoordinate);


        // Fetch db data for latitude and longitudes for user saved locations
        mydb = new DataController(this);
        al.addAll(mydb.getAllLocations());


        //Call RV Adapter to list all the user saved locations
        final RVAdapter adapter = new RVAdapter(al);
        rv.setAdapter(adapter);

        final ArrayList<coordinates> finalAl = al;

        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                double latSelected = finalAl.get(position).getLat();
                double lngSelected = finalAl.get(position).getLng();
                //Call intent with lat and long to call Async Task for weather details
                Intent detailsIntent = new Intent(MainActivity.this, WeatherDetailsActivity.class);
                detailsIntent.putExtra("lat", latSelected);
                detailsIntent.putExtra("lng", lngSelected);
                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MainActivity.this.startActivity(detailsIntent);
                // Toast.makeText(getApplicationContext(),  position+ " selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, final int position) {
                if (position > 0 && position < finalAl.size()) {
                    //Delete data from DB on long click on any location
                    double latSelected = finalAl.get(position).getLat();
                    double lngSelected = finalAl.get(position).getLng();
                    mydb.deleteLocation(latSelected, lngSelected);
                    adapter.deleteItem(position);
                    Toast.makeText(getApplicationContext(), "Location deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Current Location cannot be deleted!", Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.this.finish();
    }
}
