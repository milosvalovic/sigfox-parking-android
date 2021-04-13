package com.milosvalovic.sigfoxparking.activities.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.activities.reservation.MyReservationsActivity;
import com.milosvalovic.sigfoxparking.adapters.ParkingLotAdapter;
import com.milosvalovic.sigfoxparking.adapters.ParkingSpotAdapter;
import com.milosvalovic.sigfoxparking.adapters.ReservationsAdapter;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingLotDetailResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingSpotListResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ReservationsResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ResponseObject;
import com.milosvalovic.sigfoxparking.databinding.ActivityParkingLotBinding;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingLotActivity extends BaseActivity implements OnMapReadyCallback {

    ActivityParkingLotBinding binding;
    private boolean shownMore = true;
    Intent intent;
    private SupportMapFragment mapView;
    private GoogleMap map;
    ParkingLot parkingLot;
    public double LAT;
    public double LNG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingLotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();

        ViewCompat.setTransitionName(binding.mainContainer, "LOT_VIEW");

        LAT = intent.getDoubleExtra("lat",0.0);
        LNG = intent.getDoubleExtra("lng",0.0);

        String json = intent.getStringExtra("json");
        if (json != null) {
            binding.scrollView.setVisibility(View.VISIBLE);
            setLayout(parkingLot = gson.fromJson(json, ParkingLot.class));
        } else {
            loadDetail(intent.getIntExtra("parkingLotID",0));
        }



        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(this);
        offsetAppbar(binding.appbar);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.notifyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.show();
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        String firebase_token = task.getResult().getToken();
                                        Call<ResponseObject> call = methods.notifyMe(parkingLot.parking_lot_id,firebase_token);
                                        call.enqueue(new Callback<ResponseObject>() {
                                            @Override
                                            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                                                if(response.isSuccessful()){
                                                    if(response.body().result){
                                                        Toast.makeText(ParkingLotActivity.this, R.string.notification_success, Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    retroResponseError(ParkingLotActivity.this,response);
                                                }
                                                loading.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseObject> call, Throwable t) {
                                                loading.dismiss();
                                            }
                                        });
                                    } catch (RuntimeExecutionException e) {
                                        loading.dismiss();
                                    }
                                }
                            }
                        });
            }
        });

        binding.showAR.setOnClickListener(view -> {
            Intent intent = new Intent(ParkingLotActivity.this, ARActivity.class);
            intent.putExtra("lat", LAT);
            intent.putExtra("lng", LNG);
            intent.putExtra("json", gson.toJson(parkingLot));
            startActivity(intent);

        });

    }


    public void setLayout(ParkingLot parkingLot){
        binding.parkingLotName.setText(parkingLot.parking_lot_name);
        binding.partner.setText(parkingLot.partner.partner_name);
        binding.capacity.setText(String.format(Locale.getDefault(),"%d/%d",parkingLot.getOccupiedSpots(), parkingLot.parking_lot_total_spots ));
        binding.distance.setText(String.format(Locale.getDefault(),"%.1f",parkingLot.distance ));
        binding.address.setText(String.format(Locale.getDefault(), "%s, %s %s" , parkingLot.parking_lot_city, parkingLot.parking_lot_street, parkingLot.parking_lot_street_number));
        if(parkingLot.parking_lot_total_spots - parkingLot.getOccupiedSpots() == 0){
            binding.notifyMe.setVisibility(View.VISIBLE);
        } else {
            binding.notifyMe.setVisibility(View.GONE);
        }

        binding.descritpionContent.setText(parkingLot.parking_lot_description);
        if (binding.descritpionContent.getLineCount() < 6)
            binding.showMoreDescription.setVisibility(View.GONE);
        else binding.showMoreDescription.setVisibility(View.VISIBLE);
        binding.descritpionContent.setMaxLines(6);
        binding.descritpionContent.setMinLines(0);
        binding.showMoreDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shownMore) {
                    binding.descritpionContent.setMaxLines(6);
                    SpannableString content = new SpannableString(getString(R.string.show_more));
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    binding.showMoreDescriptionText.setText(content);
                    binding.showMoreDescriptionText.requestLayout();
                    binding.showMoreDescription.requestLayout();
                    shownMore = false;
                } else {
                    binding.descritpionContent.setMaxLines(256);
                    SpannableString content = new SpannableString(getString(R.string.show_less));
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    binding.showMoreDescriptionText.setText(content);
                    binding.showMoreDescriptionText.requestLayout();
                    binding.showMoreDescription.requestLayout();
                    shownMore = true;
                }
            }
        });
        binding.loading.setVisibility(View.VISIBLE);
        Call<ParkingSpotListResponse> call = methods.parkingSpots(parkingLot.parking_lot_id);
        call.enqueue(new Callback<ParkingSpotListResponse>() {
            @Override
            public void onResponse(Call<ParkingSpotListResponse> call, Response<ParkingSpotListResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().result){

                        binding.parkingSpots.setItemAnimator(null);
                        binding.parkingSpots.setHasFixedSize(true);
                        ParkingSpotAdapter parkingSpotAdapter = new ParkingSpotAdapter(ParkingLotActivity.this, response.body().data);
                        binding.parkingSpots.setAdapter(parkingSpotAdapter);
                    }
                } else {
                    retroResponseError(ParkingLotActivity.this,response);
                }
                binding.loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ParkingSpotListResponse> call, Throwable t) {
                binding.loading.setVisibility(View.GONE);
            }
        });

        binding.navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + parkingLot.parking_lot_lat + "," + parkingLot.parking_lot_lng);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);

        if(parkingLot != null) {
            if (parkingLot.parking_lot_lat != 0.0 && parkingLot.parking_lot_lng != 0.0) {
                MarkerOptions eventMarkerOptions = new MarkerOptions().position(new LatLng(parkingLot.parking_lot_lat, parkingLot.parking_lot_lng)).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(ParkingLotActivity.this, R.drawable.parking_marker))).anchor(0.5f, 1f);
                map.addMarker(eventMarkerOptions);
                map.setMaxZoomPreference(20);
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(parkingLot.parking_lot_lat, parkingLot.parking_lot_lng)));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(parkingLot.parking_lot_lat, parkingLot.parking_lot_lng))
                        .zoom(13f)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200 && resultCode == RESULT_OK){
            setLayout(parkingLot);
        }
    }

    public void loadDetail(int parkingLotID){
        loading.show();
        Call<ParkingLotDetailResponse> call = methods.parkingLotDetail(LAT, LNG, parkingLotID);
        call.enqueue(new Callback<ParkingLotDetailResponse>() {
            @Override
            public void onResponse(Call<ParkingLotDetailResponse> call, Response<ParkingLotDetailResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().result){
                        parkingLot = response.body().data;
                        setLayout(response.body().data);
                        onMapReady(map);
                        binding.scrollView.setVisibility(View.VISIBLE);
                    }
                } else {
                    retroResponseError(ParkingLotActivity.this,response);
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(Call<ParkingLotDetailResponse> call, Throwable t) {
                loading.dismiss();
            }
        });
    }
}