package com.example.serviceexample1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.lang.ref.WeakReference;

import static com.example.serviceexample1.MainActivity.MYH_NOT;

public class ForeGroundRemoteService extends Service {
    public static final int MSG_PAUSE = 1;
    public static final int MSG_PLAY = 2;
    public static final int MSG_EXIT = 3;
    MediaPlayer mediaPlayer;
    ServiceHandler serviceHandler;
    Messenger messenger;

    public ForeGroundRemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this,R.raw.track
                /*RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_RINGTONE)*/);
        mediaPlayer.start();
        serviceHandler = new ServiceHandler(this);
        messenger = new Messenger(serviceHandler);
        //без этого удаленный сервис будет остановлен
        startForegroundExample();

    }
    void startForegroundExample(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(MYH_NOT,"service example channel", NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,MYH_NOT);
        builder.setContentTitle("remote service example")
                .setContentText("Yo! We are connect from another app!")
                .setSmallIcon(android.R.drawable.ic_dialog_alert);
        startForeground(345,builder.build());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    public static class ServiceHandler extends Handler{
        WeakReference<ForeGroundRemoteService> reference;
        public ServiceHandler(ForeGroundRemoteService service){
            reference = new WeakReference<ForeGroundRemoteService>(service);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MSG_EXIT:
                    if(reference.get()!=null){
                        reference.get().stopSelf();
                    }
                    break;
                case MSG_PAUSE:
                    if(reference.get()!=null){
                        reference.get().mediaPlayer.pause();
                    }
                    break;
                case MSG_PLAY:
                    if(reference.get()!=null){
                        reference.get().mediaPlayer.start();
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }
}
