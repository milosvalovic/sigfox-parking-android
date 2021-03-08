package com.milosvalovic.sigfoxparking.classes.objects.response;

import com.milosvalovic.sigfoxparking.classes.objects.ParkingLot;

import java.util.ArrayList;

public class ParkingLotResponse extends ResponseObject {
    public ArrayList<ParkingLot> data = new ArrayList<>();
}
