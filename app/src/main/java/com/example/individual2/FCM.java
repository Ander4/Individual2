package com.example.individual2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCM extends FirebaseMessagingService {
    public FCM() {
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "registrado");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel elCanal = new NotificationChannel("registrado", "Registrado",
                        NotificationManager.IMPORTANCE_DEFAULT);
                elManager.createNotificationChannel(elCanal);
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true);
                elCanal.setDescription("Registro correcto");
                elCanal.enableLights(true);
                elCanal.setLightColor(Color.RED);
                elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                elCanal.enableVibration(true);
                elManager.notify(1, elBuilder.build());
            }
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}