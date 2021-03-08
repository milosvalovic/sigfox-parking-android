package com.milosvalovic.sigfoxparking.activities.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.classes.objects.response.ResponseObject;
import com.milosvalovic.sigfoxparking.classes.objects.response.UserResponse;
import com.milosvalovic.sigfoxparking.databinding.ActivityLoginBinding;
import com.milosvalovic.sigfoxparking.databinding.ActivityRegistrationBinding;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends BaseActivity {

    ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        offsetAppbar(binding.appbar);

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    register(binding.name.getText().toString(), binding.email.getText().toString(), binding.password.getText().toString());
                }
            }
        });
    }

    public boolean validateForm(){
        if(binding.name.getText().length() < 1){
            alert(getString(R.string.empty_name), getString(R.string.error));
            return false;
        } else if(binding.email.getText().length() < 1){
            alert(getString(R.string.empty_email), getString(R.string.error));
            return false;
        } else if(binding.password.getText().equals(binding.repeatPassword.getText())){
            alert(getString(R.string.passwords_dont_match), getString(R.string.error));
            return false;
        } else if (binding.password.getText().length() < 6 ){
            alert(getString(R.string.password_short), getString(R.string.error));
            return false;
        }

        return true;
    }

    public void register(String name, String email, String password){
        loading.show();
        Call<ResponseObject> call = methods.register(name,email, password);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(@NotNull Call<ResponseObject> call, @NotNull Response<ResponseObject> response) {
                if(response.isSuccessful()){
                    if(response.body().result) {
                        Toast.makeText(RegistrationActivity.this, R.string.registration_success,Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    retroResponseError(response);
                }
                loading.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(RegistrationActivity.this, R.string.loading_failed,Toast.LENGTH_LONG).show();
            }
        });

    }

}