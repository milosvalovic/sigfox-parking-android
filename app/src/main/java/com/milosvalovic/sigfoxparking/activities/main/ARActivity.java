package com.milosvalovic.sigfoxparking.activities.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.databinding.ActivityARBinding;

import java.util.ArrayList;

public class ARActivity extends BaseActivity {

    ActivityARBinding binding;
    public ArrayList<ParkingLot> data = new ArrayList<>();
    public double LAT;
    public double LNG;
    private FragmentTransaction fTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityARBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LAT = getIntent().getDoubleExtra("lat",0.0);
        LNG = getIntent().getDoubleExtra("lng",0.0);
        offsetAppbar(binding.appbar);
        String json = getIntent().getStringExtra("json");
        data.add(gson.fromJson(json, ParkingLot.class));

        fTransaction = getSupportFragmentManager().beginTransaction();
        ARFragment fragment = new ARFragment(this);

        fTransaction.replace(R.id.fragment, fragment);
        fTransaction.commit();


    }
}