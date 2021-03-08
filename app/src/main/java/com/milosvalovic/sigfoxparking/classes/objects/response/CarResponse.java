package com.milosvalovic.sigfoxparking.classes.objects.response;

import com.milosvalovic.sigfoxparking.classes.objects.Car;

import java.util.ArrayList;

public class CarResponse extends ResponseObject {
    public ArrayList<Car> data = new ArrayList<>();
}
