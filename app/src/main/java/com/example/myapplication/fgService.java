package com.example.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class fgService extends Service {
    public static BroadcastReceiver mReciver;
    private static final String TAG = "SMSReceiver";
    public static MediaPlayer mp = new MediaPlayer();
    public static boolean b_foregroundService = false;

    public static class MyReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast myToast = Toast.makeText(context.getApplicationContext(),"문자가 도착했습니다!",Toast.LENGTH_LONG);
            myToast.show();

            mp = MediaPlayer.create(context,R.raw.do_sound);
            mp.start();
        }
    }

    public void fgService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        mReciver = new MyReciver();
        registerReceiver(mReciver,filter);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        //end option

        Intent notificationcancleIntent = new Intent(this, MainActivity.class);
        //stop userIntent input
        notificationcancleIntent.setAction("Stop");
        PendingIntent canclependingIntent = PendingIntent.getActivity(this,-99,notificationcancleIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(this, TAG)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Foreground Service App"))
                .addAction(R.drawable.ic_launcher_foreground,"EXIT", canclependingIntent)
                .setAutoCancel(true)
                .build();

        b_foregroundService = true;
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReciver);
        b_foregroundService = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    TAG,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
