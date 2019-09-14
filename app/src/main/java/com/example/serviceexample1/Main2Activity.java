package com.example.serviceexample1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    ServiceConnection connection;
    boolean isBound = false;
    MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        connection = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                isBound = true;
                MyService.MyBinder myBinder = (MyService.MyBinder)iBinder;
                myService = myBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isBound = false;
                myService = null;
            }
        };

    }


    public void unbindMyservice(View view) {
        unbindService(connection);
    }

    public void binMyService(View view) {
        Intent intent = new Intent(this,MyService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    public void playClick(View view) {
        if(isBound){
            myService.play();
        }
    }

    public void pauseClick(View view) {
        if(isBound){
            myService.pause();
        }
    }

    public void startMyService(View view) {
        startService(new Intent(this,MyService.class));
    }
}
