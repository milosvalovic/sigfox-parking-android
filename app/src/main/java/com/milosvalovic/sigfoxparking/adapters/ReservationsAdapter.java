package com.milosvalovic.sigfoxparking.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.main.MainActivity;
import com.milosvalovic.sigfoxparking.activities.main.ParkingLotActivity;
import com.milosvalovic.sigfoxparking.activities.reservation.MyReservationsActivity;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.classes.objects.Reservation;
import com.milosvalovic.sigfoxparking.databinding.ItemParkingLotBinding;
import com.milosvalovic.sigfoxparking.databinding.ItemReservationBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder>{

    MyReservationsActivity act;
    public ArrayList<Reservation> data = new ArrayList<>();
    private LayoutInflater inflater;

    public ReservationsAdapter(MyReservationsActivity act) {
        this.act = act;
    }

    public void setData(ArrayList<Reservation> data){
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
        ItemReservationBinding binding = ItemReservationBinding.inflate(inflater, parent, false);


        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation item = data.get(position);
        holder.binding.parkingLotName.setText(String.format(Locale.getDefault(),"%s - %d", item.parking_lot.parking_lot_name, item.spot.parking_spot_number));
        holder.binding.car.setText(String.format(Locale.getDefault(),"%s %s, %s", item.car.car_brand, item.car.car_model, item.car.car_licence_plate));

        DateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        DateFormat formatTo = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        Date date = null;
        try {
            date = formatFrom.parse(item.reserve_for_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.binding.date.setText(String.format(Locale.getDefault(),"%s",formatTo.format(date)));
        holder.binding.address.setText(String.format(Locale.getDefault(), "%s, %s %s" , item.parking_lot.parking_lot_city, item.parking_lot.parking_lot_street, item.parking_lot.parking_lot_street_number));

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( act, ParkingLotActivity.class);
                i.putExtra("lat", act.LAT);
                Log.e("Lat", act.LAT+"");
                i.putExtra("lng", act.LNG);
                i.putExtra("parkingLotID", item.parking_lot.parking_lot_id);
                act.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemReservationBinding binding;

        public ViewHolder(@NonNull ItemReservationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
