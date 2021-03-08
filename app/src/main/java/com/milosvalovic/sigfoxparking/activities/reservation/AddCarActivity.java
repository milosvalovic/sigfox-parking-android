package com.milosvalovic.sigfoxparking.activities.reservation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.activities.main.ParkingLotActivity;
import com.milosvalovic.sigfoxparking.classes.objects.Car;
import com.milosvalovic.sigfoxparking.classes.objects.response.ResponseObject;
import com.milosvalovic.sigfoxparking.databinding.ActivityAddCarBinding;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCarActivity extends BaseActivity {

    ActivityAddCarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCarBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        offsetAppbar(binding.appbar);

        binding.add.setOnClickListener(view -> {
            Car car = new Car(binding.brand.getText().toString(), binding.model.getText().toString(), binding.plate.getText().toString(), binding.color.getText().toString(), binding.year.getText().toString());
            addCar(car);
        });

    }

    public void addCar(Car car){
        loading.show();
        Call<ResponseObject> call = methods.addCar(car);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if(response.isSuccessful()){
                    if(response.body().result){
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                } else {
                    retroResponseError(AddCarActivity.this,response);
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                loading.dismiss();
            }
        });
    }
}