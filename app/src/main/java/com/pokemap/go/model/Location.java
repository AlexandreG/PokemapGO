package com.pokemap.go.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AG on 16/07/2016.
 */
public class Location {
    private double lat;
    private double lng;

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


    public LatLng convertToMapsModel(){
        return new LatLng(lat, lng);
    }
}
