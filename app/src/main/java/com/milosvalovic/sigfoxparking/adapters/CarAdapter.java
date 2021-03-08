package com.milosvalovic.sigfoxparking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.activities.reservation.ReservationActivity;
import com.milosvalovic.sigfoxparking.classes.objects.Car;
import com.milosvalovic.sigfoxparking.classes.objects.response.ResponseObject;
import com.milosvalovic.sigfoxparking.databinding.ItemCarBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder>{

    BaseActivity act;
    ArrayList<Car> data;
    private LayoutInflater inflater;
    public int selectedIndex = -1;
    public Car selectedCar;

    public CarAdapter(ReservationActivity act, ArrayList<Car> data) {
        this.act = act;
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        inflater = LayoutInflater.from(context);
        ItemCarBinding binding = ItemCarBinding.inflate(inflater, parent, false);

        return new CarAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarAdapter.ViewHolder holder, int position) {
        Car item = data.get(position);
        holder.binding.carName.setText(String.format("%s %s (%s)", item.car_brand, item.car_model, item.car_year));
        if(item.car_color != null)
            holder.binding.color.setText(String.format("Farba: %s", item.car_color));
        else
            holder.binding.color.setText(String.format("Farba: %s", act.getString(R.string.unknown)));
        holder.binding.licancePlate.setText(item.car_licence_plate);

        holder.binding.deleteCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCar(position);
            }
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedIndex = position;
                selectedCar = data.get(position);
                notifyDataSetChanged();
            }
        });

        if(position == selectedIndex) {
            holder.binding.selected.setBackgroundColor(act.getResources().getColor(R.color.transparent));

        } else {
            holder.binding.selected.setBackgroundColor(act.getResources().getColor(R.color.semi_transparent_white));
        }



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemCarBinding binding;

        public ViewHolder(@NonNull ItemCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void deleteCar(int position){
        act.loading.show();
        Call<ResponseObject> call = act.methods.deleteCar(data.get(position).car_id);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseObject> call, @NotNull Response<ResponseObject> response) {
                if(response.isSuccessful()){
                    data.remove(position);
                    notifyDataSetChanged();
                } else {
                    act.retroResponseError(response);
                }
                act.loading.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                act.loading.dismiss();
                Toast.makeText(act, R.string.loading_failed,Toast.LENGTH_LONG).show();
            }
        });
    }
}
