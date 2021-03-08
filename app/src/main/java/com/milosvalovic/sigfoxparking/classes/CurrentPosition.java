package com.milosvalovic.sigfoxparking.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.milosvalovic.sigfoxparking.activities.BaseActivity;


public class CurrentPosition implements LocationListener {


    LocationListener locationListener;
    public LocationManager locationManager;
    BaseActivity act;
    public Criteria criteria;
    Location loc;

    public double longitude;
    public double latitude;

    @SuppressLint("MissingPermission")
    public CurrentPosition(BaseActivity act) {
        this.act = act;
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);
        //locationManager.requestLocationUpdates(provider, 2000, 0, this);
    }


    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public Location getLocation() {
        try {

            if (locationManager != null) {
                String provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, this, act.getMainLooper());
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, act.getMainLooper());
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, act.getMainLooper());
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location == null) location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                return location;

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
