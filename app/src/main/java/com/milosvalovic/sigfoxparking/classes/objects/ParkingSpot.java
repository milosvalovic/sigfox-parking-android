package com.milosvalovic.sigfoxparking.classes.objects;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ParkingSpot {
    public int parking_spot_id;
    public int parking_spot_occupied;
    public int parking_spot_has_charger;
    public int parking_spot_reserved;
    public int parking_spot_number;
    public ArrayList<Reservation> reservation = new ArrayList<>();
}
