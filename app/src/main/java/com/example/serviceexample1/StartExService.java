package com.example.serviceexample1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

public class StartExService extends Service {
    double big_result = 0;
    int big_progress;
    double maximum;
    BigTask task;
    int startId;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras()!=null){
            maximum = intent.getDoubleExtra("max",10000);
            task = new BigTask(this, startId);
            task.execute(maximum);
        } else {
          //  stopSelf();
        }
        this.startId = startId;
        Log.d("service","onStartCommand, startid ="+startId);
        ///startForeground();
        return START_STICKY;
    }

    public StartExService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service","onCreate");

    }
    void createUpdateNotify(int progress, Context context, int startId){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,MainActivity.MYH_NOT)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(String.format("big work task №%d info!",startId))
                .setVibrate(new long[]{200,300,500})
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setProgress(100,progress,false);

        if(progress < 100){
            builder.setContentText("big work in progress!");
        }else {
            builder.setContentText("big work done!");
        }
        NotificationManagerCompat.from(context).notify(1000+startId,builder.build());
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public static class BigTask extends AsyncTask<Double,Integer,Double>{
        WeakReference<StartExService> weakReference;
        PriorityQueue<Integer> progresser;
        int startid;
        public  BigTask(StartExService service, int startid){
            weakReference = new WeakReference<>(service);
            progresser = new PriorityQueue<>(11);
            for(int i = 0; i < 11; ++i){
                progresser.add(i);
            }
            this.startid =  startid;
        }


        @Override
        protected Double doInBackground(Double... doubles) {
            double s = 0;
            double maximum = doubles[0];
            double percent;
            for(long i = 0 ; i < maximum; ++i){
                s = i*i*i+i*i-Math.sqrt(i)+10;
                //publishProgress((int) (i/maximum*100));
                percent = i/maximum*10;
                if(percent > progresser.peek()){
                    publishProgress(progresser.remove()*10);
                }
            }
            return s;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            StartExService service = weakReference.get();
            if(service != null){
                service.createUpdateNotify(values[0],service.getApplicationContext(),startid);
            } else {
                Log.d("service","service is already null");
            }

        }
        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            StartExService service = weakReference.get();
            if(service != null){
                service.big_result = result;
                Intent intent = new Intent(service.getApplicationContext(), MyReceiver.class);
                intent.setAction(MyReceiver.MY_ACTION);
                intent.putExtra("result",result);
                intent.putExtra("startid",startid);
                service.getApplicationContext().sendBroadcast(intent);
                service.createUpdateNotify(100,service.getApplicationContext(),startid);
                Log.d("service","result="+result);
                service.stopSelf(startid);
            } else {
                Log.d("service","service is already null");
            }

        }
    }


    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(),"service destroyed startId="+startId,Toast.LENGTH_SHORT).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),MainActivity.MYH_NOT)
                .setSmallIcon(android.R.drawable.ic_delete)
                .setContentTitle(String.format("service stoped! startid = %d",startId))
                .setVibrate(new long[]{200,300,500})
                .setAutoCancel(true)
                .setColor(Color.RED)
                .setOnlyAlertOnce(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            builder.setContentText("Good luck!");
        NotificationManagerCompat.from(getApplicationContext()).notify(1100+startId,builder.build());
        task.cancel(true);
        Log.d("service","service is destroing");
        super.onDestroy();
    }
}
