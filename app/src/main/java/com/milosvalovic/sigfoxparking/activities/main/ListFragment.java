package com.milosvalovic.sigfoxparking.activities.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.adapters.ParkingLotAdapter;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.databinding.FragmentListBinding;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    MainActivity act;
    FragmentListBinding binding;
    ParkingLotAdapter parkingLotAdapter;

    public static ListFragment newInstance() {
        ListFragment f = new ListFragment();
        return f;
    }

    public ListFragment(MainActivity act) {
        this.act = act;
    }

    public ListFragment() {
        this.act = (MainActivity) getActivity();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(getLayoutInflater());
        parkingLotAdapter = new ParkingLotAdapter(act);
        binding.parkingLotList.setAdapter(parkingLotAdapter);
        binding.parkingLotList.setItemAnimator(null);
        binding.parkingLotList.setHasFixedSize(true);
        binding.loading.setVisibility(View.GONE);


        return binding.getRoot();
    }

    public void addData(ArrayList<ParkingLot> data) {
        parkingLotAdapter.addData(data);
        binding.loading.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.loading.setVisibility(View.GONE);
        if(act.data != null && parkingLotAdapter.data != act.data) {
            parkingLotAdapter.data.clear();
            parkingLotAdapter.addData(act.data);
        }
    }


}