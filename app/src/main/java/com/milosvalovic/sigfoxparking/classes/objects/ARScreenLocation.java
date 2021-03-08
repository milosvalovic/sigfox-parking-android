package com.milosvalovic.sigfoxparking.classes.objects;

public class ARScreenLocation {
    public float x;
    public float y;
    public float width;
    public float height;

    public ARScreenLocation(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "ARScreenLocation{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
