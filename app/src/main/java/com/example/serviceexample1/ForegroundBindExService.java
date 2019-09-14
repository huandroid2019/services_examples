package com.example.serviceexample1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ForegroundBindExService extends Service {
    private final Mybinder mybinder = new Mybinder();
    private MediaPlayer mediaPlayer;
    final int NOTIFY_ID = 1234;
    static final String ACTION_PAUSE = "pause music";
    static final String ACTION_PLAY = "play music";
    NotificationCompat.Builder builder;
    final Handler handler = new Handler();



    public ForegroundBindExService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this,R.raw.track);
        mediaPlayer.setVolume(0.5f,0.5f);
        mediaPlayer.setLooping(true);
        startForeground(NOTIFY_ID,createUpdateNotify());
        handler.post(new Runnable() {
            @Override
            public void run() {
                NotificationManagerCompat.from(ForegroundBindExService.this).notify(NOTIFY_ID,createUpdateNotify());
                handler.postDelayed(this,2000);

            }
        });

    }
    boolean isNotifuExist = false;
    Notification createUpdateNotify(){
        if(!isNotifuExist) {
            PendingIntent pendingIntentPause =
                    PendingIntent
                            .getService(this,
                                    0,
                                    new Intent(this, ForegroundBindExService.class).setAction(ACTION_PAUSE),
                                    0);
            NotificationCompat.Action actionPause = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "pause", pendingIntentPause);
            PendingIntent pendingIntentPlay =
                    PendingIntent
                            .getService(this,
                                    0,
                                    new Intent(this, ForegroundBindExService.class).setAction(ACTION_PLAY),
                                    0);
            NotificationCompat.Action actionPlay = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "play", pendingIntentPlay);

            PendingIntent pendingIntentActivity =
                    PendingIntent
                            .getActivity(this,
                                    0,
                                    new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                    0);

            builder = new NotificationCompat.Builder(this, MainActivity.MYH_NOT);
            builder
                    .setContentTitle("war craft")
                    .setContentText("playin")
                    .setProgress(100, getInfo(), false)
                    .setSmallIcon(R.drawable.ic_audiotrack_black_24dp)
                   // .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.w1))
                    .setProgress(100,getInfo(),false)
                    .addAction(actionPause)
                    .addAction(actionPlay)
                    .setContentIntent(pendingIntentActivity);

            isNotifuExist = true;
            return builder.build();
        }
        String title = mediaPlayer.isPlaying()?"playing":"stopped";

            builder.setContentText(title)
                    .setProgress(100,getInfo(),false);

        return builder.build();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(ACTION_PAUSE.equals(intent.getAction())){
            mediaPlayer.pause();
            NotificationManagerCompat.from(this).notify(NOTIFY_ID,createUpdateNotify());
        } else if(ACTION_PLAY.equals(intent.getAction())){
            mediaPlayer.start();
            NotificationManagerCompat.from(this).notify(NOTIFY_ID,createUpdateNotify());
        }else {
            mediaPlayer.start();
        }
        return START_STICKY;
    }

    public void setVolume(float v){
        mediaPlayer.setVolume(v,v);
    }

    /**
     * вызвается раз, далее возвращенное значение передается для всех попыток соединиться
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {

        mediaPlayer.start();
        return mybinder;
    }

    @Override
    public void onRebind(Intent intent) {
        mediaPlayer.start();
        super.onRebind(intent);
    }

    public int getInfo(){
        return (int) (mediaPlayer.getCurrentPosition()/(float)(mediaPlayer.getDuration()) *100);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

    public class Mybinder extends Binder {
        public ForegroundBindExService getMyService(){
            return ForegroundBindExService.this;
        }
    }
}
