package com.milosvalovic.sigfoxparking.classes.objects.response;

import com.milosvalovic.sigfoxparking.classes.objects.ParkingSpot;

import java.util.ArrayList;

public class ParkingSpotListResponse extends ResponseObject{
    public ArrayList<ParkingSpot> data = new ArrayList<>();
}
