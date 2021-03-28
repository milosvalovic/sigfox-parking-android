package com.milosvalovic.sigfoxparking.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.main.ParkingLotActivity;
import com.milosvalovic.sigfoxparking.activities.reservation.ReservationActivity;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingSpot;
import com.milosvalovic.sigfoxparking.databinding.ItemParkingSpotBinding;

import java.util.ArrayList;

public class ParkingSpotAdapter extends RecyclerView.Adapter<ParkingSpotAdapter.ViewHolder> {

    ParkingLotActivity act;
    ArrayList<ParkingSpot> data;
    private LayoutInflater inflater;

    public ParkingSpotAdapter(ParkingLotActivity act, ArrayList<ParkingSpot> data) {
        this.act = act;
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ParkingSpotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        inflater = LayoutInflater.from(context);
        ItemParkingSpotBinding binding = ItemParkingSpotBinding.inflate(inflater, parent, false);

        return new ParkingSpotAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingSpotAdapter.ViewHolder holder, int position) {
        ParkingSpot item = data.get(position);
        if (item.parking_spot_occupied == 1) {
            holder.binding.currentOccupancy.setText(R.string.currently_occupied);
            holder.binding.background.setBackground(act.getResources().getDrawable(R.drawable.occupied_background));
            holder.binding.occupied.setVisibility(View.VISIBLE);
        } else {
            holder.binding.currentOccupancy.setText(R.string.currently_free);
            holder.binding.background.setBackground(act.getResources().getDrawable(R.drawable.free_background));
            holder.binding.occupied.setVisibility(View.GONE);
        }

        if (item.parking_spot_has_charger == 1)
            holder.binding.charger.setVisibility(View.VISIBLE);
        else
            holder.binding.charger.setVisibility(View.GONE);

        holder.binding.reserved.setVisibility(View.GONE);
        holder.binding.reserve.setVisibility(View.VISIBLE);
        if (item.parking_spot_reserved == 1) {
            //holder.binding.reserve.setVisibility(View.GONE);
            holder.binding.reserved.setVisibility(View.VISIBLE);
            holder.binding.currentOccupancy.setText(R.string.currently_reserved);
            holder.binding.background.setBackground(act.getResources().getDrawable(R.drawable.occupied_background));
            holder.binding.occupied.setVisibility(View.VISIBLE);
        } else {

            holder.binding.reserved.setVisibility(View.GONE);
            /*if(act.isUserLoggedIn()) {
                holder.binding.reserve.setVisibility(View.VISIBLE);
            } else {
                holder.binding.reserve.setVisibility(View.GONE);
            }*/
        }
        holder.binding.spotNumber.setText("" + item.parking_spot_number);


        holder.binding.reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (act.isUserLoggedIn()) {
                    Intent intent = new Intent(act, ReservationActivity.class);
                    intent.putExtra("parkingSpotID", item.parking_spot_id);
                    String json = act.gson.toJson(item.reservation);
                    intent.putExtra("json", json);
                    act.startActivityForResult(intent, 200);
                } else {
                    act.alert(act.getString(R.string.please_login), act.getString(R.string.warning));
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemParkingSpotBinding binding;

        public ViewHolder(@NonNull ItemParkingSpotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
