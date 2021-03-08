package com.milosvalovic.sigfoxparking.classes.views;

import android.content.Context;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.internal.ScrimInsetsFrameLayout;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Field;



import static android.os.Build.VERSION_CODES.R;


public class CustomNavigationView extends NavigationView {

    private int height = 0;
    private int width = 0;
    private CustomViewSettings settings;

    public CustomNavigationView(@NonNull Context context) {
        super(context);
    }

    public CustomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {
        settings = new CustomViewSettings(context, attrs);

        /**
         * If hardware acceleration is on (default from API 14), clipPath worked correctly
         * from API 18.
         *
         * So we will disable hardware Acceleration if API < 18
         *
         * https://developer.android.com/guide/topics/graphics/hardware-accel.html#unsupported
         * Section #Unsupported Drawing Operations
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void setInsetsColor(int color) {
        try {
            Field insetForegroundField = ScrimInsetsFrameLayout.class.getDeclaredField("mInsetForeground");
            insetForegroundField.setAccessible(true);
            ColorDrawable colorDrawable = new ColorDrawable(color);
            insetForegroundField.set(this, colorDrawable);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);

            if (v instanceof NavigationMenuView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackground(settings.getBackgroundDrawable());
                } else {
                    v.setBackgroundDrawable(settings.getBackgroundDrawable());
                }
            }
        }
    }


    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if (child instanceof NavigationMenuView) {
            child.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), View.MeasureSpec.EXACTLY));
        } else {
            super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(createClipPath(settings.getShapeType(), settings.getCornerRadius()));
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @SuppressLint("RtlHardcoded")
    private Path createClipPath(int type, int radius) {
        Path path = new Path();
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) getLayoutParams();
        boolean rtl = true;
        if (layoutParams.getLayoutDirection() == LAYOUT_DIRECTION_RTL ) {
            rtl = true;
        } else {
            rtl = false;
        }
        if (rtl) {
            switch (type) {
                case CustomViewSettings.ARC_CONVEX:
                    path.moveTo(width, 0);
                    path.lineTo(width / 8, 0);
                    path.quadTo(0, height / 2,
                            width / 8, height);
                    path.lineTo(width, height);
                    path.close();
                    break;
                case CustomViewSettings.ARC_CONCAVE:
                    path.moveTo(width, 0);
                    path.lineTo(0, 0);
                    path.quadTo(width / 8, height / 2,
                            0, height);
                    path.lineTo(width, height);
                    path.close();
                    break;
                case CustomViewSettings.WAVES:
                    path.moveTo(width, 0);
                    path.quadTo(width / 2, 0, width / 2, height / 8);
                    path.quadTo(width / 2, height / 2 - width / 2, width / 2 - width / 4, height / 2 - width / 4);
                    path.quadTo(0, height / 2, width / 2 - width / 4, height / 2 + width / 4);
                    path.quadTo(width / 2, height / 2 + width / 2, width / 2, height - height / 8);
                    path.quadTo(width / 2, height, width, height);
                    path.close();
                    break;
                case CustomViewSettings.WAVES_INDEFINITE:
                    int num = 51;
                    float wavewidth = height / num;
                    float mh = 0;
                    float fr = wavewidth / 2;
                    path.moveTo(width, 0);
                    path.lineTo(wavewidth, 0);
                    for (int i = 0; i < num + 1; ++i) {
                        if (i % 2 == 0) {
                            path.quadTo(0, mh += fr, wavewidth, mh += fr);
                        } else {
                            path.quadTo(2 * wavewidth, mh += fr, wavewidth, mh += fr);
                        }
                    }
                    path.lineTo(width, height);
                    path.close();
                    break;
                case CustomViewSettings.ROUNDED_RECT:
                    path.moveTo(0, width / 8);
                    path.quadTo(0, 0, width / 8, 0);
                    path.lineTo(width - width / 8, 0);
                    path.quadTo(width, 0, width, width / 8);
                    path.lineTo(width, height - width / 8);
                    path.quadTo(width, height, width - width / 8, height);
                    path.lineTo(width / 8, height);
                    path.quadTo(0, height, 0, height - width / 8);
                    path.close();
                    break;

                case CustomViewSettings.BOTTOM_ROUND:
                    path.moveTo(0, 0);
                    path.lineTo(width, 0);
                    path.lineTo(width, height - height / 4);
                    path.quadTo(width / 2, height, 0, height - height / 4);
                    path.close();
                    break;
                case CustomViewSettings.FULL_ROUND:
                    path.moveTo(width, 0);
                    path.quadTo(0, 0, 0, height / 2);
                    path.quadTo(0, height, width, height);
                    path.close();
                    break;
                case CustomViewSettings.NORMAL:
                    path.moveTo(0, 0);
                    path.lineTo(width, 0);
                    path.lineTo(width, height);
                    path.lineTo(0, height);
                    path.close();
                case CustomViewSettings.END_CORNER_ROUNDED:
                    path.moveTo(0, 0);
                    path.lineTo(width - radius, 0);
                    path.quadTo(width, 0, width, radius);
                    path.lineTo(width, height - radius);
                    path.quadTo(width, height, width - radius, height);
                    path.lineTo(0, height);
                    path.close();
                    break;
            }
        } else {
            switch (type) {
                case CustomViewSettings.ARC_CONVEX:
                    path.moveTo(0, 0);
                    path.lineTo(width, 0);
                    path.quadTo(width - width / 4, height / 2,
                            width, height);
                    path.lineTo(0, height);
                    path.close();
                    break;
                case CustomViewSettings.ARC_CONCAVE:
                    path.moveTo(0, 0);
                    path.lineTo(width - width / 8, 0);
                    path.quadTo(width + width / 8, height / 2,
                            width - width / 8, height);
                    path.lineTo(0, height);
                    path.close();
                    break;
                case CustomViewSettings.WAVES:
                    path.moveTo(0, 0);
                    path.quadTo(width / 2, 0, width / 2, height / 8);
                    path.quadTo(width / 2, height / 2 - width / 2, width / 2 + width / 4, height / 2 - width / 4);
                    path.quadTo(width, height / 2, width / 2 + width / 4, height / 2 + width / 4);
                    path.quadTo(width / 2, height / 2 + width / 2, width / 2, height - height / 8);
                    path.quadTo(width / 2, height, 0, height);
                    path.close();
                    break;
                case CustomViewSettings.WAVES_INDEFINITE:
                    path.moveTo(0, 0);
                    int num = 51;
                    float wavewidth = height / num;
                    path.lineTo(width - height / num, 0);
                    float mh = 0;
                    float fr = wavewidth / 2;
                    for (int i = 0; i < num + 1; ++i) {
                        if (i % 2 == 0) {
                            path.quadTo(width, mh += fr, width - wavewidth, mh += fr);
                        } else {
                            path.quadTo(width - 2 * wavewidth, mh += fr, width - wavewidth, mh += fr);
                        }
                    }
                    path.lineTo(0, height);
                    path.close();
                    break;
                case CustomViewSettings.ROUNDED_RECT:
                    path.moveTo(0, width / 8);
                    path.quadTo(0, 0, width / 8, 0);
                    path.lineTo(width - width / 8, 0);
                    path.quadTo(width, 0, width, width / 8);
                    path.lineTo(width, height - width / 8);
                    path.quadTo(width, height, width - width / 8, height);
                    path.lineTo(width / 8, height);
                    path.quadTo(0, height, 0, height - width / 8);
                    path.close();
                    break;
                case CustomViewSettings.BOTTOM_ROUND:
                    path.moveTo(0, 0);
                    path.lineTo(width, 0);
                    path.lineTo(width, height - height / 4);
                    path.quadTo(width / 2, height, 0, height - height / 4);
                    path.close();
                    break;
                case CustomViewSettings.FULL_ROUND:
                    path.moveTo(0, 0);
                    path.quadTo(width, 0, width, height / 2);
                    path.quadTo(width, height, 0, height);
                    path.close();
                    break;
                case CustomViewSettings.NORMAL:
                    path.moveTo(0, 0);
                    path.lineTo(width, 0);
                    path.lineTo(width, height);
                    path.lineTo(0, height);
                    path.close();
                case CustomViewSettings.END_CORNER_ROUNDED:
                    path.moveTo(0, 0);
                    path.lineTo(width - radius, 0);
                    path.quadTo(width, 0, width, radius);
                    path.lineTo(width, height - radius);
                    path.quadTo(width, height, width - radius, height);
                    path.lineTo(0, height);
                    path.close();
                    break;
            }
        }
        return path;
    }

    public CustomViewSettings getSettings() {
        return settings;
    }
}
