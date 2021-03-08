package com.milosvalovic.sigfoxparking.classes.objects;

public class ParkingLot {
    public int parking_lot_id = 0;
    public String parking_lot_name;
    public double parking_lot_lat;
    public double parking_lot_lng;
    public String parking_lot_city;
    public String parking_lot_street;
    public String parking_lot_street_number;
    public String parking_lot_description;
    public int parking_lot_id_partner = 0;
    public double distance = 0.0;
    public int parking_lot_total_spots = 0;
    public int parking_lot_available_spots = 0;
    public int parking_lot_occupied_spots =  parking_lot_total_spots - parking_lot_available_spots;
    public int parking_lot_has_charger = 0;
    public Partner partner;

    public int getOccupiedSpots(){
        return parking_lot_total_spots - parking_lot_available_spots;
    }
}
