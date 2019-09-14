package com.example.serviceexample1;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    MediaPlayer mediaPlayer;
    MyBinder myBinder = new MyBinder();
    NotificationCompat.Builder builder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this,R.raw.track);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        builder = new NotificationCompat.Builder(this,MainActivity.MYH_NOT);
        startForeground(12365,getNotification());
    }
    void pause(){
        mediaPlayer.pause();
    }
    void play(){
        mediaPlayer.start();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    public class MyBinder extends Binder{
        public MyService getService(){
            return MyService.this;
        }
    }

    Notification getNotification(){
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("hello")
                .setContentText("content");

        return builder.build();
    }


    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }
}
