package com.milosvalovic.sigfoxparking.classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Notification.Builder;
import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.VISIBILITY_PUBLIC;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    NotificationManager notificationManager;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer(String token) {
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.e("notification", "mess"+remoteMessage.get);
        //FirebaseMessaging.getInstance().subscribeToTopic("post");
        //Notificatio_Helper helper = new Notificatio_Helper(this);
        //helper.sendNotification(remoteMessage);


        Log.e("notification", remoteMessage.getData().toString());
        sendNotification(remoteMessage);
    }

    // [END receive_message]
    private void sendNotification(RemoteMessage remoteMessage) {
        int requestID = (int) System.currentTimeMillis();
        Gson gson = new Gson();
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        /*Log.e("Notification", gson.toJson(remoteMessage.getData()));
        if(remoteMessage.getNotification().getClickAction() != null) {
            if (remoteMessage.getNotification().getClickAction().equals("voucher"))
                intent = new Intent(this, MyVoucherDetailActivity.class);
            else if (remoteMessage.getNotification().getClickAction().equals("product"))
                intent = new Intent(this, ProductDetailActivity.class);
            else
                intent = new Intent(this, SplashActivity.class);
            intent.putExtra("id", remoteMessage.getData().get("id"));
        }*/

        //intent.putExtra("type", remoteMessage.getData().get("click_action"));
        //intent.putExtra("message_title", remoteMessage.getNotification().getTitle());
        //intent.putExtra("message_body", remoteMessage.getNotification().getBody());*/

        Log.i(Consts.APP_TAG, "********************FCM*********************************");
        Log.i(Consts.APP_TAG, " FCM MESSAGE = " + remoteMessage.getData().get("message"));
        Log.i(Consts.APP_TAG, " FCM TYPE = " + remoteMessage.getData().get("type"));
        Log.i(Consts.APP_TAG, " FCM TITLE = " + remoteMessage.getData().get("title"));
        Log.i(Consts.APP_TAG, " FCM ID = " + remoteMessage.getNotification().getTag());
        Log.i(Consts.APP_TAG, "***************************************************************");

        Log.i(Consts.APP_TAG, "********************FCM*********************************");
        Log.i(Consts.APP_TAG, " FCM MESSAGE = " + remoteMessage.getNotification());
        Log.i(Consts.APP_TAG, " FCM TYPE = " + remoteMessage.getData().get("type"));
        Log.i(Consts.APP_TAG, " FCM TITLE = " + remoteMessage.getData().get("title"));
        Log.i(Consts.APP_TAG, " FCM ID = " + remoteMessage.getData().get("id"));
        Log.i(Consts.APP_TAG, "***************************************************************");

// Create the TaskStackBuilder and add the intent, which inflates the back stack
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1001, intent, PendingIntent.FLAG_ONE_SHOT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannel();
            Notification notif = new Builder(this)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSmallIcon(R.drawable.ic_parking_sign)
                    .setChannelId("fcm_default_channel")
                    .setVisibility(VISIBILITY_PUBLIC)
                    .addExtras(intent.getExtras())
                    .build();
            notif.flags |= FLAG_AUTO_CANCEL;

            notificationManager.notify(1, notif);
            //ContextCompat.startForegroundService(this, new Intent(this, MyFirebaseMessagingService.class));
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(1, notif);
            }*/
        } else{
            Notification notif = new Builder(this)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSmallIcon(R.drawable.ic_parking_sign)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .addExtras(intent.getExtras())
                    .build();
            notif.flags |= FLAG_AUTO_CANCEL;
            notificationManager.notify(1, notif);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setupChannel() {
        NotificationChannel mainChannel = new NotificationChannel("fcm_default_channel", "Default", NotificationManager.IMPORTANCE_HIGH);
        mainChannel.enableLights(true);
        mainChannel.setLightColor(Color.BLUE);
        mainChannel.enableVibration(false);
        mainChannel.setLockscreenVisibility(VISIBILITY_PUBLIC);

        if (mainChannel != null) {
            notificationManager.createNotificationChannel(mainChannel);
        }
    }
}
