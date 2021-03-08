package com.milosvalovic.sigfoxparking.activities.reservation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.activities.main.ParkingLotActivity;
import com.milosvalovic.sigfoxparking.adapters.ParkingSpotAdapter;
import com.milosvalovic.sigfoxparking.adapters.ReservationsAdapter;
import com.milosvalovic.sigfoxparking.classes.CurrentPosition;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingSpotListResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ReservationsResponse;
import com.milosvalovic.sigfoxparking.databinding.ActivityMyReservationsBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReservationsActivity extends BaseActivity {

    ActivityMyReservationsBinding binding;
    public double LAT = 0.0;
    public double LNG = 0.0;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReservationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        offsetAppbar(binding.appbar);
        setDrawer(binding.drawerLayout);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        else {
            CurrentPosition currentPosition = new CurrentPosition(this);
            currentPosition.getLocation();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                            LAT = position.latitude;
                            LNG = position.longitude;
                            loadData();
                            Log.e("LAT", ""+LAT);

                        }
                    });
        }
    }

    public void loadData(){
        loading.show();
        Call<ReservationsResponse> call = methods.myReservations();
        call.enqueue(new Callback<ReservationsResponse>() {
            @Override
            public void onResponse(Call<ReservationsResponse> call, Response<ReservationsResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().result){

                        binding.myReservationsList.setItemAnimator(null);
                        binding.myReservationsList.setHasFixedSize(true);
                        ReservationsAdapter reservationsAdapter = new ReservationsAdapter(MyReservationsActivity.this);
                        reservationsAdapter.setData(response.body().data);
                        binding.myReservationsList.setAdapter(reservationsAdapter);
                    }
                } else {
                    retroResponseError(MyReservationsActivity.this,response);
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(Call<ReservationsResponse> call, Throwable t) {
                loading.dismiss();
            }
        });
    }

    public void getLocation() {

        CurrentPosition currentPosition = new CurrentPosition(this);
        currentPosition.getLocation();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LAT = location.getLatitude();
                                LNG = location.getLongitude();
                                loadData();

                            }
                        }
                    });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0) {
            if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //alert(getString(R.string.first_location_setup), getString(R.string.information));
                CurrentPosition cp = new CurrentPosition(this);
                cp.getLocation();
                getLocation();


            }
        }
    }
}