package com.ashwinisnv.weathersay;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashwinisnv.weathersay.Model.OpenWeatherMap;
import com.ashwinisnv.weathersay.Utilities.Helper;
import com.ashwinisnv.weathersay.Utilities.common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.Date;

public class WeatherDetailsActivity extends AppCompatActivity {

    TextView city, lastUpdate, description, time, humidity, celsius,sunsetVal,sunriseVal;
    TextView humidityVal,windVal,minTempVal,maxTempVal,describe;
    ImageView imageView;
    OpenWeatherMap openWeather = new OpenWeatherMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);

        city = findViewById(R.id.city);
        //lastUpdate = findViewById(R.id.lastUpdate);
        celsius = findViewById(R.id.celsius);
        imageView = findViewById(R.id.imageView);
       // description = findViewById(R.id.description);
        time = findViewById(R.id.time);
        sunriseVal = findViewById(R.id.sunriseVal);
        sunsetVal = findViewById(R.id.sunsetVal);
        humidityVal= findViewById(R.id.humidityVal);
        windVal= findViewById(R.id.windVal);
        minTempVal = findViewById(R.id.minTempVal);
        maxTempVal = findViewById(R.id.maxTempVal);

        describe = findViewById(R.id.describe);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
          // double lat = valueOf(bd.get("lat");
            double lat = (double) bd.get("lat");
            double lng = (double) bd.get("lng");

            new GetWeather().execute(common.getAPI(String.valueOf(lat),String.valueOf(lng)));
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        WeatherDetailsActivity.this.finish();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public class GetWeather extends AsyncTask<String,Void,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper httpCall = new Helper();
            stream = httpCall.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new Gson();
            Type mtype = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeather = gson.fromJson(s,mtype);

            city.setText(String.format("%s, %s",openWeather.getName(),openWeather.getSys().getCountry()));
            //lastUpdate.setText(String.format("%s", common.getTime()));
           // description.setText(String.format("%s",openWeather.getWeather().get(0).getDescription()));
           // humidity.setText(String.format("Humidity:%d%%",openWeather.getMain().getHumidity()));
           // time.setText(String.format("%s/%s", common.timeStampToDateTime(openWeather.getSys().getSunrise()),common.timeStampToDateTime(openWeather.getSys().getSunset())));
            celsius.setText(String.format("%.2f °C",(openWeather.getMain().getTemp()-273.15)));
            Picasso.with(WeatherDetailsActivity.this).load(common.getImage(openWeather.getWeather().get(0).getIcon())).into(imageView);

            Date dateObject = new Date((long) openWeather.getSys().getSunrise());
            //sunriseVal.setText(String.format("%s",common.formatTime(dateObject)));

            Date dateObject1 = new Date((long) openWeather.getSys().getSunset());
            //sunsetVal.setText(String.format("%s",common.formatTime(dateObject1)));

            humidityVal.setText(String.format("%d%%",openWeather.getMain().getHumidity()));
            windVal.setText(String.format("%.2f mph",openWeather.getWind().getSpeed()));

            minTempVal.setText(String.format("%.2f °C",(openWeather.getMain().getTemp_min()-273.15)));
            maxTempVal.setText(String.format("%.2f °C",(openWeather.getMain().getTemp_max()-273.15)));

            describe.setText(String.format("Today: %s. The high will be %.2f ° and low will be %.2f °",(openWeather.getWeather().get(0).getDescription()),(openWeather.getMain().getTemp_max()-273.15),(openWeather.getMain().getTemp_min()-273.15)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WeatherDetailsActivity.this.finish();
    }
}
