package com.example.serviceexample1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class BindExService extends Service {
    private final Mybinder mybinder = new Mybinder();
    private MediaPlayer mediaPlayer;
    public BindExService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this,R.raw.track);
        mediaPlayer.setVolume(0.5f,0.5f);
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
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
        public BindExService getMyService(){
            return BindExService.this;
        }
    }
}
