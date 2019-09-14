package com.example.serviceexample1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static final String MYH_NOT = "my notif";
    ServiceConnection connection;
    BindExService service;
    boolean isBound;
    ImageView iv_war;
    AnimationDrawable animationDrawable;
    /**
     * для установки громкости
     */
    SeekBar seekBar;
    /**
     * сколько трека проиграло в процентах
     */
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isBound = false;
        //создание канала для уведомлений
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(MYH_NOT,"service example channel",
                    NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }


        setContentView(R.layout.activity_main);
        //для связи со службой
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                service = ((BindExService.Mybinder)iBinder).getMyService();
                isBound = true;
                seekBar.setEnabled(isBound);
                iv_war.setVisibility(View.VISIBLE);
                if(animationDrawable!=null){
                    animationDrawable.start();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isBound = false;
                seekBar.setEnabled(isBound);
                if(animationDrawable!=null){
                    iv_war.setVisibility(View.INVISIBLE);
                    animationDrawable.stop();
                }
            }
        };

        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(isBound);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(isBound && service != null){
                    service.setVolume(i/10.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        iv_war = findViewById(R.id.iv_wariior);
        iv_war.setBackgroundResource(R.drawable.warrior);
        iv_war.setVisibility(View.INVISIBLE);
        animationDrawable = (AnimationDrawable)iv_war.getBackground();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                if(isBound && service!=null){
                    progressBar.setProgress(service.getInfo());
                }
                progressBar.postDelayed(this,2000);
            }
        });
    }

    /**
     * запускаем "запущенную" службу
     * @param view
     */
    public void startService(View view) {
        Intent intent = new Intent(this, StartExService.class);
        intent.putExtra("max",1e8);
        startService(intent);

    }

    /***
     * пытаемся остановить привязанную службу
     * @param view
     */
    public void stopStartedService(View view) {
        stopService(new Intent(this, BindExService.class));
    }

    /**
     * связываемся со службой (если она не запущена, то она запуститься)
     * @param view
     */
    public void bindExService(View view) {
      bindService(new Intent(this, BindExService.class),connection,Context.BIND_AUTO_CREATE);
    }

    /**
     * запускаем службу, с которой потом можно связаться
     * @param view
     */
    public void startForBind(View view) {
        startService(new Intent(this, BindExService.class));
    }

    /***
     * отвязываемся от службы если выход
     */
    @Override
    protected void onStop() {
        if(isBound){
            unbindService(connection);
            isBound = false;
        }
        super.onStop();
    }

    /**
     * отвязка от привязанной службы
     * @param view
     */
    public void unbindMyService(View view) {
        if(isBound){
            unbindService(connection);
            isBound = false;
        }
    }

    public void startForegroundService(View view) {
        startService(new Intent(this, ForegroundBindExService.class));
    }

    public void setScheduler(View view) {
        ComponentName name = new ComponentName(this,MyJobScheduler.class);
        JobInfo jobInfo = new JobInfo.Builder(1111,name)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
               // .setRequiresDeviceIdle(true)
              //  .setMinimumLatency(3000)
                .setPeriodic(15*60*1000)
                .build();
        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobInfo);
      // fileWriter();

    }
    void fileWriter(){
        File sd = getExternalFilesDir(null);
        Log.d("file",sd.getAbsolutePath());
        File dir = new File(sd+"/my_dir");
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir+"/log.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {

            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write("\nsheduler launched:"+ Calendar.getInstance().getTime().toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
