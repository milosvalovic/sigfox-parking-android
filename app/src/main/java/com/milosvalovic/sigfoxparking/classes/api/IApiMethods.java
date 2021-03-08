package com.milosvalovic.sigfoxparking.classes.api;

import com.milosvalovic.sigfoxparking.classes.objects.Car;
import com.milosvalovic.sigfoxparking.classes.objects.Place;
import com.milosvalovic.sigfoxparking.classes.objects.response.CarResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.DistanceMatrixResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingLotDetailResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingLotResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ParkingSpotListResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ReservationsResponse;
import com.milosvalovic.sigfoxparking.classes.objects.response.ResponseObject;
import com.milosvalovic.sigfoxparking.classes.objects.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IApiMethods {

    @FormUrlEncoded
    @POST("parking_lot/list")
    Call<ParkingLotResponse> parkingLotList(@Field("lat") double lat, @Field("lng") double lng, @Field("offset") int offset);

    @FormUrlEncoded
    @POST("parking_lot/notify_me")
    Call<ResponseObject> notifyMe(@Field("parking_lot_id") int parkingLotID, @Field("firebase_token") String firebaseToken);

    @GET("parking_lot/parking_spots/{id}")
    Call<ParkingSpotListResponse> parkingSpots(@Path("id") int parkingLotID);

    @FormUrlEncoded
    @POST("user/login")
    Call<UserResponse> login(@Field("user_email") String email, @Field("user_password") String password);

    @FormUrlEncoded
    @POST("user/register")
    Call<ResponseObject> register(@Field("user_name") String name, @Field("user_email") String email, @Field("user_password") String password);

    @DELETE("car/delete/{id}")
    Call<ResponseObject> deleteCar(@Path("id") int car_id);

    @GET("car/list")
    Call<CarResponse> carList();

    @POST("car/add")
    Call<ResponseObject> addCar(@Body Car car);

    @FormUrlEncoded
    @POST("reservation/create")
    Call<ResponseObject> createReservation(@Field("spot_id") int spotID, @Field("car_id") int carID, @Field("reserve_for_date") String date);

    @GET("reservation/list")
    Call<ReservationsResponse> myReservations();


    @FormUrlEncoded
    @POST("parking_lot/detail")
    Call<ParkingLotDetailResponse> parkingLotDetail(@Field("lat") double lat, @Field("lng") double lng, @Field("parking_lot_id") int id);
}
