package com.milosvalovic.sigfoxparking.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.activities.main.ARActivity;
import com.milosvalovic.sigfoxparking.activities.main.ARFragment;
import com.milosvalovic.sigfoxparking.activities.main.ParkingLotActivity;
import com.milosvalovic.sigfoxparking.classes.objects.ARScreenLocation;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.databinding.FragmentARBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.hardware.SensorManager.getOrientation;

/**
 * Created by ntdat on 1/13/17.
 */

public class AROverlayView extends View {

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ParkingLot> arPoints;
    private float pitch;
    Bitmap img;
    BaseActivity act;
    public ArrayList<ARScreenLocation> screenLocations = new ArrayList<>();
    FragmentARBinding binding;

    public AROverlayView(BaseActivity context, List<ParkingLot> arPoints ) {
        super(context);
        this.context = context;
        this.arPoints = arPoints;

        img = BitmapFactory.decodeResource(context.getResources(), R.drawable.parking_lot_ar);
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        float[] orientation = new float[3];
        getOrientation(rotatedProjectionMatrix, orientation);
        this.pitch = (float) (Math.toDegrees(orientation[1])*3);
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();

    }

    public void setData(List<ParkingLot> data, FragmentARBinding binding, BaseActivity act){
        arPoints = data;
        this.binding = binding;
        this.act = act;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        screenLocations.clear();
        for (int i = 0; i < arPoints.size(); i ++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            Location location = new Location("ARPoint");
            location.setLatitude(arPoints.get(i).parking_lot_lat);
            location.setLongitude(arPoints.get(i).parking_lot_lng);
            float[] pointInECEF = LocationHelper.WSG84toECEF(location);
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                Rect bounds = new Rect();
                double scaleMultiplayer = 1;
                if (arPoints.get(i).distance < 5) scaleMultiplayer = 1;
                else if (arPoints.get(i).distance < 50) scaleMultiplayer = 0.9;
                else if (arPoints.get(i).distance < 100) scaleMultiplayer = 0.75;
                else if (arPoints.get(i).distance < 200) scaleMultiplayer = 0.6;
                else scaleMultiplayer = 0.5;
                img = scaleDown(img, (float) (250* scaleMultiplayer), true);
                paint.setTextSize((float) (50*scaleMultiplayer));
                String text = String.format(Locale.getDefault(),"%.1f km", arPoints.get(i).distance);

                paint.getTextBounds(text, 0, text.length(), bounds);
                //canvas.rotate(pitch);

                canvas.drawBitmap(img,x - (img.getWidth()/2.0f),y- (img.getHeight()/2.0f),paint);
                canvas.drawText(text, x  - bounds.width()/2  , (float) (y + (img.getHeight()/2.0f) +bounds.height()+20*scaleMultiplayer), paint);

                screenLocations.add(new ARScreenLocation(x - (img.getWidth()/2.0f), y- (img.getHeight()/2.0f), img.getWidth(), img.getHeight()));
            }
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float touchX = event.getX();
            float touchY = event.getY();

            for(int i = 0; i < arPoints.size(); i++){
                ARScreenLocation loc = screenLocations.get(i);
                ParkingLot item = arPoints.get(i);
                Log.e("Loc", loc.toString());
                Log.e("Loc", touchX + " - " + touchY);
                if((loc.x <= touchX && loc.y <= touchY) && (loc.x + loc.width >= touchX &&  loc.y + loc.height >= touchY)){
                    Log.e("Showing", "Showing");
                    binding.info.setVisibility(VISIBLE);
                    binding.parkingLotName.setText(item.parking_lot_name);
                    binding.partner.setText(item.partner.partner_name);
                    binding.capacity.setText(String.format(Locale.getDefault(),"%d/%d",item.parking_lot_available_spots, item.parking_lot_total_spots ));
                    binding.distance.setText(String.format(Locale.getDefault(),"%.1f",item.distance ));
                    binding.info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*Intent i = new Intent( act, ParkingLotActivity.class);
                            String json = act.gson.toJson(item);
                            i.putExtra("json", json);
                            act.startActivity(i);*/

                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + item.parking_lot_lat + "," + item.parking_lot_lng);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            ((ARActivity)act).startActivity(mapIntent);
                        }
                    });
                    break;
                }
            }
        }


        return super.onTouchEvent(event);
    }
}
