package com.milosvalovic.sigfoxparking.activities.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.adapters.MainScreenAdapter;
import com.milosvalovic.sigfoxparking.classes.Consts;
import com.milosvalovic.sigfoxparking.classes.CurrentPosition;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingLotResponse;
import com.milosvalovic.sigfoxparking.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    public LatLng position;
    public ArrayList<ParkingLot> data = new ArrayList<>();
    public double LAT = 0.0;
    public double LNG = 0.0;
    public String name;
    public int PAGE = 0;
    public MainScreenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        name = getString(R.string.current_position);
        setDrawer(binding.drawerLayout);
        offsetAppbar(binding.appbar);
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        adapter = new MainScreenAdapter(getSupportFragmentManager(), this);
        binding.pager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.pager);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            CurrentPosition currentPosition = new CurrentPosition(this);
            currentPosition.getLocation();
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            position = new LatLng(location.getLatitude(), location.getLongitude());
                            LAT = position.latitude;
                            LNG = position.longitude;
                            loadData();

                        }
                    });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);
        }

        binding.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MainActivity.this, CitySearchActivity.class);
                startActivityForResult(intent, 5000);*/
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("sk")
                        .build(MainActivity.this);

                startActivityForResult(intent, 5000);
            }
        });

        binding.currentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFilter();
                getLocation();

            }
        });

    }


    public void getLocation() {

        CurrentPosition currentPosition = new CurrentPosition(this);
        currentPosition.getLocation();
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
                                name = getString(R.string.current_position);
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

    public void loadData() {
        loading.show();
        Call<ParkingLotResponse> call = methods.parkingLotList(LAT, LNG, PAGE);
        call.enqueue(new Callback<ParkingLotResponse>() {
            @Override
            public void onResponse(@NotNull Call<ParkingLotResponse> call, @NotNull Response<ParkingLotResponse> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    data = response.body().data;
                    if (adapter.getItem(0) != null)
                        ((ListFragment)adapter.getItem(0)).addData(data);
                    if (adapter.getItem(1) != null)
                        ((MapFragment)adapter.getItem(1)).reloadMarkers();


                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.loading_failed),Toast.LENGTH_LONG).show();
                }
                loading.dismiss();

            }

            @Override
            public void onFailure(Call<ParkingLotResponse> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, R.string.loading_failed,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reloadData() {
        binding.positionText.setText(name);
        PAGE = 0;
        loadData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5000 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            LAT = place.getLatLng().latitude;
            LNG = place.getLatLng().longitude;
            name = place.getName();


            reloadData();
        }
    }
}