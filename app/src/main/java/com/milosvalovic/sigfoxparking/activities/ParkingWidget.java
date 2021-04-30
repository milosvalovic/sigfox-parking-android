package com.milosvalovic.sigfoxparking.activities;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.main.ParkingLotActivity;
import com.milosvalovic.sigfoxparking.classes.Consts;
import com.milosvalovic.sigfoxparking.classes.api.IApiMethods;
import com.milosvalovic.sigfoxparking.classes.api.ServiceGenerator;
import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingLotDetailResponse;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */
public class ParkingWidget extends AppWidgetProvider {

    public IApiMethods methods;
    public ParkingLot parkingLot;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.parkingLotName, parkingLot.parking_lot_name);
        views.setTextViewText(R.id.capacity, String.format(Locale.getDefault(), "%d/%d", parkingLot.getOccupiedSpots(), parkingLot.parking_lot_total_spots));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        methods = ServiceGenerator.generate(Consts.API_URL, context, false).create(IApiMethods.class);
        loadDetail(1);

        Call<ParkingLotDetailResponse> call = methods.parkingLotDetail(0, 0, 1);
        call.enqueue(new Callback<ParkingLotDetailResponse>() {
            @Override
            public void onResponse(Call<ParkingLotDetailResponse> call, Response<ParkingLotDetailResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().result) {
                        parkingLot = response.body().data;
                        for (int appWidgetId : appWidgetIds) {
                            updateAppWidget(context, appWidgetManager, appWidgetId);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ParkingLotDetailResponse> call, Throwable t) {
            }
        });


    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    public void loadDetail(int parkingLotID) {

    }
}