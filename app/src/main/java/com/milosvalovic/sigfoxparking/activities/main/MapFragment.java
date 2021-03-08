package com.milosvalovic.sigfoxparking.activities.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;


import java.util.ArrayList;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    MainActivity act;

    com.milosvalovic.sigfoxparking.databinding.FragmentMapBinding binding;
    private GoogleMap map;
    private float zoomDistance = 0.0f;
    private LatLng currentLatLng;
    ArrayList<Marker> markers = new ArrayList<>();
    MarkerOptions currentMarkerOptions;
    MarkerOptions eventMarkerOptions;
    Marker currentMarker;
    private int currentlySelected = 0;

    public MapFragment(MainActivity act) {
        this.act = act;
    }

    public MapFragment(){
        this.act = (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.milosvalovic.sigfoxparking.databinding.FragmentMapBinding.inflate(getLayoutInflater());

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.info.setVisibility(View.GONE);
                unselectMarkers();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(act, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        else {
            centerMap();
            currentMarkerOptions = new MarkerOptions().position(new LatLng(act.LAT, act.LNG)).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(act, R.drawable.icon_position))).anchor(0.5f, 1);
            eventMarkerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(act, R.drawable.parking_marker))).anchor(0.5f, 1f).alpha(0.5f);
            currentMarker = googleMap.addMarker(currentMarkerOptions);
            currentMarker.setTag(""+(-50));
        }
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (Integer.parseInt("" + marker.getTag()) == -50)
                    return false;
                binding.info.setVisibility(View.VISIBLE);
                unselectMarkers();
                marker.setAlpha(1);
                ParkingLot item = act.data.get(Integer.parseInt("" + marker.getTag()));
                binding.parkingLotName.setText(item.parking_lot_name);
                binding.partner.setText(item.partner.partner_name);
                binding.capacity.setText(String.format(Locale.getDefault(),"%d/%d",item.parking_lot_available_spots, item.parking_lot_total_spots ));
                binding.distance.setText(String.format(Locale.getDefault(),"%.1f km",item.distance ));
                binding.info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent( act, ParkingLotActivity.class);
                        String json = act.gson.toJson(item);
                        i.putExtra("json", json);
                        act.startActivity(i);
                    }
                });

                if(item.parking_lot_has_charger == 0)
                    binding.charger.setText(R.string.no);
                else
                    binding.charger.setText(R.string.yes);
                if (currentlySelected == Integer.parseInt("" + marker.getTag())) {
                    currentlySelected = -1;
                } else {
                    currentlySelected = Integer.parseInt(marker.getTag().toString());
                }


                return false;
            }
        });

    }

    public void addEventMarker(MarkerOptions options) {
        Marker m = map.addMarker(options);
        m.setTag("" + (markers.size()));

        markers.add(m);
    }

    @Override
    public void onResume() {
        binding.mapView.onResume();
        if(map != null)
            centerMap();
        if(act.data.size() != 0) {
            for(ParkingLot item : act.data) {
                addEventMarker(eventMarkerOptions.position(new LatLng(item.parking_lot_lat, item.parking_lot_lng)));
            }
        }
        super.onResume();
    }

    public void reloadMarkers(){
        markers.clear();
        currentMarker.setPosition(new LatLng(act.LAT, act.LNG));
        centerMap();
        for(ParkingLot item : act.data) {
            addEventMarker(eventMarkerOptions.position(new LatLng(item.parking_lot_lat, item.parking_lot_lng)));
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    public void centerMap() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(act.LAT, act.LNG))
                .zoom(13f)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void unselectMarkers() {
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).setAlpha(0.5f);
        }
    }
}