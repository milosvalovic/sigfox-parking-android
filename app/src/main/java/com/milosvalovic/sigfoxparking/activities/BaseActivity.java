package com.milosvalovic.sigfoxparking.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.auth.LoginActivity;
import com.milosvalovic.sigfoxparking.activities.main.MainActivity;
import com.milosvalovic.sigfoxparking.activities.reservation.MyReservationsActivity;
import com.milosvalovic.sigfoxparking.activities.reservation.ReservationActivity;
import com.milosvalovic.sigfoxparking.classes.Consts;
import com.milosvalovic.sigfoxparking.classes.api.IApiMethods;
import com.milosvalovic.sigfoxparking.classes.api.ServiceGenerator;
import com.milosvalovic.sigfoxparking.classes.objects.Filter;
import com.milosvalovic.sigfoxparking.classes.objects.User;
import com.milosvalovic.sigfoxparking.classes.objects.response.ResponseObject;
import com.milosvalovic.sigfoxparking.classes.views.CustomDrawerLayout;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {
    public IApiMethods methods;
    public Gson gson;
    public Dialog loading;
    public CustomDrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        methods = ServiceGenerator.generate(Consts.API_URL, BaseActivity.this, false).create(IApiMethods.class);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        gson = new Gson();

        loading = new Dialog(this);
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading.getWindow().setDimAmount(0.5f);
        loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loading.setContentView(R.layout.dialog_loading);
        loading.setCancelable(false);
    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public void offsetAppbar(View v) {
        v.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    public Shader getTextShader() {
        Shader textShader = new LinearGradient(0, 0, 0, 20,
                new int[]{getResources().getColor(R.color.gradient_start), getResources().getColor(R.color.gradient_end)},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        return textShader;

    }

    public void saveFilter(Filter filter) {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("filter", gson.toJson(filter));
        editor.apply();
    }

    public Filter getSavedFilter() {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String filter = sharedPreferences.getString("filter", "");
        if (filter.equals("")) return null;
        return gson.fromJson(filter, Filter.class);
    }

    public void removeFilter() {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("filter");
        editor.apply();
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void retroResponseError(Activity act, Response<?> response) {
        Converter<ResponseBody, ResponseObject> errorConverter = ServiceGenerator.generate(Consts.API_URL, act, false).responseBodyConverter(ResponseObject.class, new Annotation[0]);
        try {
            ResponseObject data = errorConverter.convert(response.errorBody());
            Log.i(Consts.APP_TAG, data.error);

            Toast.makeText(act, data.error, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(act, R.string.unknown_error, Toast.LENGTH_LONG).show();
        }
    }

    public void storeUser(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jUser = gson.toJson(user);
        editor.putString("user", jUser);
        editor.apply();
    }

    public void storeToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public User getStoredUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        User user = gson.fromJson(sharedPreferences.getString("user", gson.toJson(new User())), User.class);
        return user;
    }

    public String getStoredToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        return token;
    }

    public void removeUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user");
        editor.apply();

    }

    public void removeToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();

    }

    public boolean isUserLoggedIn() {
        if (getStoredUser().user_id != 0) return true;
        return false;
    }

    public void setDrawer(CustomDrawerLayout dl) {
        drawer = dl;
        NavigationView navigationView = findViewById(R.id.navView);
        View view = navigationView.getHeaderView(0);

        View loggedIn = view.findViewById(R.id.logged_in_header);
        View loggedOut = view.findViewById(R.id.logged_out_header);
        offsetAppbar(loggedIn);
        offsetAppbar(loggedOut);

        if (isUserLoggedIn()) {
            loggedIn.setVisibility(View.VISIBLE);
            loggedOut.setVisibility(View.GONE);

            /*LinearLayout myProfileBtn = loggedIn.findViewById(R.id.myProfileBtn);
            myProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BaseActivity.this, ProfileActivity.class);
                    startActivity(i);
                }
            });*/
            TextView name = loggedIn.findViewById(R.id.name);
            name.setText(getStoredUser().user_name);
            TextView email = loggedIn.findViewById(R.id.email);
            email.setText(getStoredUser().user_email);

        } else {
            loggedIn.setVisibility(View.GONE);
            loggedOut.setVisibility(View.VISIBLE);
        }
    }

    public void openMenu(View v) {
        if (drawer != null) {
            drawer.openDrawer(Gravity.LEFT);
        }
    }

    public void closeMenu(View v) {
        if (drawer != null) {
            drawer.closeDrawer(Gravity.LEFT);
        }
    }

    public void home(View view) {

        if (!(this instanceof MainActivity)) {
            finish();
        } else {
            ((MainActivity) this).reloadData();
            if (drawer != null)
                drawer.closeDrawers();
        }
    }

    public void reservations(View view) {
        if(isUserLoggedIn()){
            if (this instanceof MainActivity) {
                Intent i = new Intent(BaseActivity.this, MyReservationsActivity.class);
                startActivity(i);
                if (drawer != null)
                    drawer.closeDrawers();
            } else {
                ((MyReservationsActivity) this).loadData();
                if (drawer != null)
                    drawer.closeDrawers();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.you_are_logged_out, Toast.LENGTH_LONG).show();
        }
    }

    public void back(View view){
        finish();
    }

    public void report(View view){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:report@valovic.studenthosting.sk?&subject=Nahlásenie"));
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
        }

    }

    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 10);
    }

    public void logout(View v) {
        removeUser();
        removeToken();
        setDrawer(drawer);
        drawer.closeDrawers();
        //FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
        if (this instanceof MyReservationsActivity) finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            setDrawer(drawer);
            drawer.closeDrawers();
            Toast.makeText(getApplicationContext(), R.string.you_were_logged_in, Toast.LENGTH_LONG).show();
        }
    }

    public void alert(String messege, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(messege).setTitle(title);

        //Setting message manually and performing action on button click
        builder.setCancelable(false)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void retroResponseError(Response<?> response) {
        Converter<ResponseBody, ResponseObject> errorConverter = ServiceGenerator.generate(Consts.API_URL, BaseActivity.this, false).responseBodyConverter(ResponseObject.class, new Annotation[0]);
        try {
            ResponseObject data = errorConverter.convert(response.errorBody());
            Log.i(Consts.APP_TAG, data.error);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //Uncomment the below code to Set the message and title from the strings.xml file
            builder.setMessage(data.error).setTitle(R.string.error);

            //Setting message manually and performing action on button click
            builder.setCancelable(false)
                    .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String currentDateTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = Calendar.getInstance().getTime();
        return format.format(time);
    }

    public boolean isDateBeforeToday(String startDateTime, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date strDateStart = null;
        Date currentTime = null;
        try {
            strDateStart = sdf.parse(startDateTime);
            currentTime = sdf.parse(currentDateTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert strDateStart != null;
        if (strDateStart.before(currentTime)) {
            return true;
        }
        return false;
    }
}