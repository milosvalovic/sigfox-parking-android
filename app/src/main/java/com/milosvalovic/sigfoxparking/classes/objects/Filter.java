package com.milosvalovic.sigfoxparking.classes.objects;

public class Filter {
    public double lat;
    public double lng;
    public String locationName;

    public Filter(double lat, double lng, String locationName) {
        this.lat = lat;
        this.lng = lng;
        this.locationName = locationName;
    }
}
