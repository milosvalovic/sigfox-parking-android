package com.milosvalovic.sigfoxparking.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.main.MainActivity;
import com.milosvalovic.sigfoxparking.activities.main.ParkingLotActivity;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.databinding.ItemParkingLotBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class ParkingLotAdapter extends RecyclerView.Adapter<ParkingLotAdapter.ViewHolder>{

    MainActivity act;
    public ArrayList<ParkingLot> data = new ArrayList<>();
    private LayoutInflater inflater;

    public ParkingLotAdapter(MainActivity act) {
        this.act = act;
    }

    public void addData(ArrayList<ParkingLot> data){
        this.data = data;
        notifyDataSetChanged();
    }

    public void clearData(){
        this.data.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        inflater = LayoutInflater.from(context);
        ItemParkingLotBinding binding = ItemParkingLotBinding.inflate(inflater, parent, false);


        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParkingLot item = data.get(position);
        holder.binding.parkingLotName.setText(item.parking_lot_name);
        holder.binding.partner.setText(item.partner.partner_name);
        holder.binding.capacity.setText(String.format(Locale.getDefault(),"%d/%d",item.getOccupiedSpots(), item.parking_lot_total_spots ));
        holder.binding.distance.setText(String.format(Locale.getDefault(),"%.1f km",item.distance ));
        holder.binding.address.setText(String.format(Locale.getDefault(), "%s, %s %s" , item.parking_lot_city, item.parking_lot_street, item.parking_lot_street_number));

        if(item.parking_lot_has_charger == 0)
            holder.binding.charger.setText(R.string.no);
        else
            holder.binding.charger.setText(R.string.yes);



        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( act, ParkingLotActivity.class);
                i.putExtra("lat", act.LAT);
                i.putExtra("lng", act.LNG);
                String json = act.gson.toJson(item);
                i.putExtra("json", json);
                act.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemParkingLotBinding binding;

        public ViewHolder(@NonNull ItemParkingLotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
