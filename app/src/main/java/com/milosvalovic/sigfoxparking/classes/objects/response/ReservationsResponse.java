package com.milosvalovic.sigfoxparking.classes.objects.response;

import com.milosvalovic.sigfoxparking.classes.objects.Reservation;

import java.util.ArrayList;

public class ReservationsResponse extends ResponseObject {
    public ArrayList<Reservation> data = new ArrayList<>();
}
