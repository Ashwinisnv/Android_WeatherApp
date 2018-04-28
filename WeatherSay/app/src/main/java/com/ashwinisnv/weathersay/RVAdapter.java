package com.ashwinisnv.weathersay;

/**
 * Created by ashwinivishwas on 4/9/18.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashwinisnv.weathersay.Model.OpenWeatherMap;
import com.ashwinisnv.weathersay.Model.coordinates;
import com.ashwinisnv.weathersay.Utilities.Helper;
import com.ashwinisnv.weathersay.Utilities.common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.weatherViewHolder> {
    Context context;

    public static class weatherViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView city;
        TextView description1;
        TextView celsius;
        ImageView img;

        weatherViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            city = (TextView) itemView.findViewById(R.id.city);
            celsius = (TextView) itemView.findViewById(R.id.celsius);
            description1 = itemView.findViewById(R.id.description1);
            img = itemView.findViewById(R.id.img);
        }
    }

    List<coordinates> coordinates;
    OpenWeatherMap openWeather = new OpenWeatherMap();

    RVAdapter(List<coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public weatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_main, viewGroup, false);
        context = viewGroup.getContext();
        weatherViewHolder pvh = new weatherViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(weatherViewHolder weatherViewHolder, int i) {
        //Call async task and pass personViewHolder and Open Weather API Object as parameters to the async task
        new GetWeather(weatherViewHolder).execute(common.getAPI(String.valueOf(coordinates.get(i).getLat()), String.valueOf(coordinates.get(i).getLng())));
    }

    @Override
    public int getItemCount() {
        return coordinates.size();
    }

    void deleteItem(int index) {
        coordinates.remove(index);
        notifyItemRemoved(index);
    }

    //In async task, fetch open weather data
    public class GetWeather extends AsyncTask<String, Void, String> {
        private weatherViewHolder weatherViewHolder;

        public GetWeather(weatherViewHolder weatherViewHolder) {
            this.weatherViewHolder = weatherViewHolder;
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
            Type mtype = new TypeToken<OpenWeatherMap>() {
            }.getType();
            openWeather = gson.fromJson(s, mtype);

            weatherViewHolder.city.setText(String.format("%s, %s", openWeather.getName(), openWeather.getSys().getCountry()));
            weatherViewHolder.description1.setText(String.format("%s", openWeather.getWeather().get(0).getDescription()));
            weatherViewHolder.celsius.setText(String.format("%.2f °C", (openWeather.getMain().getTemp() - 273.15)));
            Picasso.with(context).load(common.getImage(openWeather.getWeather().get(0).getIcon())).into(weatherViewHolder.img);
        }
    }

}

