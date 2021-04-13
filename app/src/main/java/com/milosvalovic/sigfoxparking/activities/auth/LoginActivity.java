package com.milosvalovic.sigfoxparking.activities.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.activities.main.ListFragment;
import com.milosvalovic.sigfoxparking.activities.main.MainActivity;
import com.milosvalovic.sigfoxparking.activities.main.MapFragment;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingLotResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.UserResponse;
import com.milosvalovic.sigfoxparking.databinding.ActivityLoginBinding;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        offsetAppbar(binding.appbar);

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(binding.email.getText().toString(), binding.password.getText().toString());
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,
                        RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }


    public void login(String email, String password){
        loading.show();
        Call<UserResponse> call = methods.login(email, password);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NotNull Call<UserResponse> call,
                                   @NotNull Response<UserResponse> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    storeUser(response.body().data);
                    storeToken(response.body().token);
                    setResult(RESULT_OK, getIntent());
                    finish();
                } else {
                    retroResponseError(response);
                }
                loading.dismiss();

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, R.string.loading_failed,Toast.LENGTH_LONG).show();
            }
        });

    }
}