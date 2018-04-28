package com.ashwinisnv.weathersay.Model;

/**
 * Created by ashwinivishwas on 3/30/18.
 */

public class Cord {
    private double lat;
    private double lng;

    public Cord(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
