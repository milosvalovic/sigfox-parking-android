package com.milosvalovic.sigfoxparking.classes.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.milosvalovic.sigfoxparking.R;


public class CustomViewSettings {
    public final static int ARC_CONCAVE = 0;
    public final static int ARC_CONVEX = 1;
    public final static int ROUNDED_RECT = 2;
    public final static int WAVES = 3;
    public final static int BOTTOM_ROUND = 4;
    public final static int FULL_ROUND = 5;
    public final static int WAVES_INDEFINITE = 6;
    public final static int NORMAL = 8;
    public final static int END_CORNER_ROUNDED = 7;
    private int shapeType = 8;
    private int radius;
    private Drawable backgroundDrawable = new ColorDrawable(Color.WHITE); //default background color of navigation view

    public CustomViewSettings(Context context, AttributeSet attrs) {
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomNavigationView, 0, 0);
        final int shape = styledAttributes.getInt(R.styleable.CustomNavigationView_drawerShape, NORMAL);
        this.radius = styledAttributes.getDimensionPixelSize(R.styleable.CustomNavigationView_cornerRadius, 0);

        switch (shape) {
            case 0:
                shapeType = ARC_CONCAVE;
                break;
            case 1:
                shapeType = ARC_CONVEX;
                break;
            case 2:
                shapeType = ROUNDED_RECT;
                break;
            case 3:
                shapeType = WAVES;
                break;
            case 4:
                shapeType = BOTTOM_ROUND;
                break;
            case 5:
                shapeType = FULL_ROUND;
                break;
            case 6:
                shapeType = WAVES_INDEFINITE;
                break;
            case 7:
                shapeType = END_CORNER_ROUNDED;
                break;
            default:
                shapeType = NORMAL;
        }

        int[] attrsArray = new int[]{
                android.R.attr.background,
                android.R.attr.layout_gravity,
        };

        TypedArray androidAttrs = context.obtainStyledAttributes(attrs, attrsArray);
        backgroundDrawable = androidAttrs.getDrawable(0);

        androidAttrs.recycle();
        styledAttributes.recycle();
    }

    public int getShapeType() {
        return shapeType;
    }

    public int getCornerRadius(){return radius;}

    public void setCornerRadius(int radius){this.radius = radius;}

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }

    public Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }
}
