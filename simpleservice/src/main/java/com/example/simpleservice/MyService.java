package com.example.simpleservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;

public class MyService extends Service {
    MyAsync task;
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getStringExtra("url")!=null) {
            task = new MyAsync(new WeakReference<MyService>(this), startId);
            task.execute(intent.getStringExtra("url"));
            String url = intent.getStringExtra("url");
            startForeground(1110, createLoadNotify(startId,url.substring(url.lastIndexOf('/')+1)));
        } else {
            stopSelf(startId);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public static class MyAsync extends AsyncTask<String,Integer, JSONObject> {
        private WeakReference<MyService> reference;
        private int startId;
        public MyAsync(WeakReference<MyService> reference, int startId){
            this.reference = reference;
            this.startId = startId;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyService service = reference.get();
                if(service == null){
                return;
            }
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                sleep(5000);
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                int progress = 0;
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine())!=null){
                    buffer.append(line);
                    progress += line.length();
                    Log.d("data","fetch data");
                    publishProgress(progress);
                }
                return new JSONObject(buffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject object = new JSONObject();
                try {
                    object.put("error",e.getMessage()+"\n"+e.toString());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return object;
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            /*WebAcivityGoodAsync acivityGoodAsync = reference.get();
            if(acivityGoodAsync == null||acivityGoodAsync.isFinishing()){
                return;
            }
            acivityGoodAsync.tv_progress.setText(""+values[0]);*/
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            MyService service = reference.get();
            if(service == null){
                return;
            }
            service.createDoneNotify(jsonObject.toString(),startId);
            service.stopSelf(startId);

        }
    }

    void createDoneNotify(String data, int startId){
        Intent intent = new Intent(this,ResultActivity.class);
        intent.putExtra("data",data);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.MY_NOT);
        builder.setSmallIcon(android.R.drawable.ic_menu_upload)
                .setContentTitle("loading...")
                .setContentText("data has been loaded")
                .setColor(Color.GREEN)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

       // NotificationManagerCompat.from(this).cancel(1110);
        NotificationManagerCompat.from(this).notify(1111+startId,builder.build());
    }
    Notification createLoadNotify(int startId, String task){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.MY_NOT);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("loading...")
                .setColor(Color.RED)
                .setContentText("loading data...."+task);
        return builder.build();
    }
}
