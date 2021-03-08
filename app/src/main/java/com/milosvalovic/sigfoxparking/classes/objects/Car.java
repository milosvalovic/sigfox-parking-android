package com.milosvalovic.sigfoxparking.classes.objects;

public class Car {
    public int car_id;
    public String car_brand;
    public String car_model;
    public String car_licence_plate;
    public String car_color;
    public String car_year;

    public Car(String car_brand, String car_model, String car_licence_plate, String car_color, String car_year) {
        this.car_brand = car_brand;
        this.car_model = car_model;
        this.car_licence_plate = car_licence_plate;
        this.car_color = car_color;
        this.car_year = car_year;
    }
}
