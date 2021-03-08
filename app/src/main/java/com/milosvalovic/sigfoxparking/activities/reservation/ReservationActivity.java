package com.milosvalovic.sigfoxparking.activities.reservation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.activities.main.ParkingLotActivity;
import com.milosvalovic.sigfoxparking.adapters.CarAdapter;
import com.milosvalovic.sigfoxparking.adapters.ParkingSpotAdapter;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.classes.objects.Reservation;
import com.milosvalovic.sigfoxparking.classes.objects.response.CarResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ResponseObject;
import com.milosvalovic.sigfoxparking.databinding.ActivityReservationBinding;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private Intent intent;
    ActivityReservationBinding binding;
    CarAdapter carAdapter;
    ArrayList<Reservation> reservations;
    String dateToSend;
    int spotID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        offsetAppbar(binding.appbar);
        intent = getIntent();
        spotID = intent.getIntExtra("parkingSpotID", 0);
        String json = intent.getStringExtra("json");
        if (json != null) {
            //reservations = gson.fromJson(json, List<Reservation>.class);
            Type listType = new TypeToken<List<Reservation>>() {
            }.getType();
            reservations = gson.fromJson(json, listType);
        }


        Calendar blockedDates [] = new Calendar[reservations.size()];

        for(int i = 0; i < reservations.size(); i++){
            Calendar date = Calendar.getInstance();
            DateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
            Date dateD = null;
            try {
                dateD = formatFrom.parse(reservations.get(i).reserve_for_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date.setTime(dateD);


            blockedDates[i] = date;

            Log.e("Dates",date.get(Calendar.YEAR)+ "");
        }




        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, 1);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ReservationActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setDisabledDays(blockedDates);
        dpd.setMinDate(now);





        binding.pickDate.setOnClickListener(view -> {
            dpd.show(getSupportFragmentManager(), "TAG");
        });

        binding.addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ReservationActivity.this, AddCarActivity.class);
                startActivityForResult(i, 650);
            }
        });

        binding.reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(carAdapter != null){
                    if(carAdapter.selectedCar != null){
                        if(dateToSend != null){
                            createReservation(spotID, carAdapter.selectedCar.car_id, dateToSend);
                        } else {
                            Toast.makeText(ReservationActivity.this, R.string.date_not_picked, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ReservationActivity.this, R.string.car_not_picked, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        loadCars();
    }

    public void createReservation(int spotID, int carID, String date){
        loading.show();
        Call<ResponseObject> call = methods.createReservation(spotID,carID,date);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseObject> call, @NotNull Response<ResponseObject> response) {
                if(response.isSuccessful()){
                    setResult(RESULT_OK, getIntent());
                    finish();
                    Toast.makeText(ReservationActivity.this, R.string.reservation_created,Toast.LENGTH_LONG).show();

                } else {
                    retroResponseError(response);
                }
                loading.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ReservationActivity.this, R.string.loading_failed,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadCars(){
        loading.show();
        Call<CarResponse> call = methods.carList();
        call.enqueue(new Callback<CarResponse>() {
            @Override
            public void onResponse(@NotNull Call<CarResponse> call, @NotNull Response<CarResponse> response) {
                if(response.isSuccessful()){
                    binding.carList.setItemAnimator(null);
                    binding.carList.setHasFixedSize(true);
                    carAdapter = new CarAdapter(ReservationActivity.this, response.body().data);
                    binding.carList.setAdapter(carAdapter);
                } else {
                    retroResponseError(response);
                }
                loading.dismiss();

            }

            @Override
            public void onFailure(Call<CarResponse> call, Throwable t) {
                loading.dismiss();
                finish();
                Toast.makeText(ReservationActivity.this, R.string.loading_failed,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 650 && resultCode == RESULT_OK){
            loadCars();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        binding.pickDateText.setText(String.format(Locale.getDefault(),"%d.%d.%d", dayOfMonth, monthOfYear+1, year));
        Calendar reservationDate = Calendar.getInstance();
        reservationDate.set(year,monthOfYear,dayOfMonth);

        DateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

        dateToSend = formatFrom.format(reservationDate.getTime());

    }
}