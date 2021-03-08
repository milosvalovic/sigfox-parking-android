package com.milosvalovic.sigfoxparking.activities.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.classes.objects.Filter;
import com.milosvalovic.sigfoxparking.databinding.ActivityCitySearchBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CitySearchActivity extends BaseActivity {

    Intent intent;
    ActivityCitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        PlacesClient placesClient = Places.createClient(this);
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, 5000);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setCountries("SK");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAHG", "Place: " + place.toString());

                /*intent.putExtra("lat", place.getLatLng().latitude);
                intent.putExtra("lng", place.getLatLng().longitude);
                intent.putExtra("location", place.getName());

                saveFilter(new Filter(place.getLatLng().latitude, place.getLatLng().longitude, place.getName()));
                setResult(RESULT_OK, intent);
                finish();*/
            }


            @Override
            public void onError(@NotNull Status status) {

            }
        });

    }
}