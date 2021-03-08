package com.milosvalovic.sigfoxparking.activities.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.classes.ARCamera;
import com.milosvalovic.sigfoxparking.classes.AROverlayView;
import com.milosvalovic.sigfoxparking.databinding.FragmentARBinding;

import java.io.IOException;

import static android.hardware.SensorManager.AXIS_MINUS_X;
import static android.hardware.SensorManager.AXIS_MINUS_Y;
import static android.hardware.SensorManager.AXIS_X;
import static android.hardware.SensorManager.AXIS_Y;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static android.hardware.SensorManager.getOrientation;
import static android.hardware.SensorManager.getRotationMatrixFromVector;
import static android.hardware.SensorManager.remapCoordinateSystem;
import static android.view.Surface.ROTATION_180;
import static android.view.Surface.ROTATION_270;
import static android.view.Surface.ROTATION_90;

public class ARFragment extends Fragment implements SensorEventListener, LocationListener  {

    final static String TAG = "ARActivity";
    private SurfaceView surfaceView;
    private FrameLayout cameraContainerLayout;
    private AROverlayView arOverlayView;
    private Camera camera;
    private ARCamera arCamera;

    private SensorManager sensorManager;
    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private LocationManager locationManager;
    public Location location = new Location("ARPoint");
    private float declination;
    ARActivity act;

    FragmentARBinding binding;

    public ARFragment(ARActivity act) {
        this.act = act;
    }

    public ARFragment(){
        this.act = (ARActivity) getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentARBinding.inflate(inflater);
        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.info.setVisibility(View.GONE);
            }
        });
        sensorManager = (SensorManager) act.getSystemService(Context.SENSOR_SERVICE);
        cameraContainerLayout = binding.cameraContainerLayout;
        surfaceView = binding.surfaceView;
        arOverlayView = new AROverlayView(act, act.data);
        arOverlayView.setData(act.data, binding, act);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (act.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && act.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                initARCameraView();

            } else {
                requestCameraPermission();
                requestLocationPermission();
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestCameraPermission();
        requestLocationPermission();
        registerSensors();
        initAROverlayView();
        updateLatestLocation();

    }

    @Override
    public void onPause() {
        releaseCamera();
        super.onPause();
    }

    public void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                act.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        } else {
            initARCameraView();
        }
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                act.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {

        }
    }

    public void initAROverlayView() {

        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
        cameraContainerLayout.addView(arOverlayView);
    }

    public void initARCameraView() {
        reloadSurfaceView();

        if (arCamera == null) {
            arCamera = new ARCamera(act, binding.surfaceView);
        }
        if (arCamera.getParent() != null) {
            ((ViewGroup) arCamera.getParent()).removeView(arCamera);
        }
        cameraContainerLayout.addView(arCamera);
        arCamera.setKeepScreenOn(true);

        initCamera();
    }

    private void initCamera() {
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open(0);
                camera.startPreview();
                camera.reconnect();
                arCamera.setCamera(camera);
            } catch (RuntimeException | IOException ex) {
                Toast.makeText(act, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void reloadSurfaceView() {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }

        cameraContainerLayout.addView(surfaceView);
    }

    private void releaseCamera() {
        if (camera != null) {

            camera.setPreviewCallback(null);
            camera.stopPreview();

            arCamera.setCamera(null);
            camera.release();
            camera = null;



        }
    }

    private void registerSensors() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrixFromVector = new float[16];
            float[] rotationMatrix = new float[16];
            getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values);
            final int screenRotation = act.getWindowManager().getDefaultDisplay()
                    .getRotation();

            switch (screenRotation) {
                case ROTATION_90:
                    remapCoordinateSystem(rotationMatrixFromVector,
                            AXIS_Y,
                            AXIS_MINUS_X, rotationMatrix);
                    break;
                case ROTATION_270:
                    remapCoordinateSystem(rotationMatrixFromVector,
                            AXIS_MINUS_Y,
                            AXIS_X, rotationMatrix);
                    break;
                case ROTATION_180:
                    remapCoordinateSystem(rotationMatrixFromVector,
                            AXIS_MINUS_X, AXIS_MINUS_Y,
                            rotationMatrix);
                    break;
                default:
                    remapCoordinateSystem(rotationMatrixFromVector,
                            AXIS_X, AXIS_Y,
                            rotationMatrix);
                    break;
            }

            float[] projectionMatrix = arCamera.getProjectionMatrix();
            float[] rotatedProjectionMatrix = new float[16];
            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrix, 0);
            this.arOverlayView.updateRotatedProjectionMatrix(rotatedProjectionMatrix);

            //Heading
            float[] orientation = new float[3];
            getOrientation(rotatedProjectionMatrix, orientation);
            double bearing = Math.toDegrees(orientation[0]) + declination;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.w("DeviceOrientation", "Orientation compass unreliable");
        }
    }


    private void updateLatestLocation() {
        if (arOverlayView != null && location != null) {
            location.setLatitude(act.LAT);
            location.setLongitude(act.LNG);
            arOverlayView.updateCurrentLocation(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLatestLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public void reloadPoints() {
        if(arOverlayView != null)
            arOverlayView.setData(act.data, binding, act);
    }
}