package com.example.servcieex1starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

public class RemoteServiceStarter extends AppCompatActivity {
    ServiceConnection remoteConnection;
    Messenger messenger;
    boolean isBound = false;
    public static final int MSG_PAUSE = 1;
    public static final int MSG_PLAY = 2;
    public static final int MSG_EXIT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_service_starter);
        remoteConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                messenger = new Messenger(iBinder);
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isBound = false;
                messenger = null;
            }
        };
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.serviceexample1","com.example.serviceexample1.StartExService"));
        intent.putExtra("max",1e8);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public void startServiceGood(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.serviceexample1","com.example.serviceexample1.ForegroundBindExService"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public void bindRemote(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.serviceexample1","com.example.serviceexample1.ForeGroundRemoteService"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bindService(intent, remoteConnection,BIND_AUTO_CREATE);

        } else {
            startService(intent);
        }

    }

    public void pauseClick(View view) {
        if(isBound && messenger!=null){
            try {
                messenger.send(Message.obtain(null,MSG_PAUSE));
            } catch (RemoteException e) {
                Toast.makeText(this,"can't send message to service error"+e.getMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void playClick(View view) {
        if(isBound && messenger!=null){
            try {
                messenger.send(Message.obtain(null,MSG_PLAY));
            } catch (RemoteException e) {
                Toast.makeText(this,"can't send message to service error"+e.getMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void unBindClick(View view) {
        if(isBound) {
            unbindService(remoteConnection);
        }
    }

    public void stopRemoteService(View view) {
        if(isBound && messenger!=null){
            try {
                messenger.send(Message.obtain(null,MSG_EXIT));
            } catch (RemoteException e) {
                Toast.makeText(this,"can't send message to service error"+e.getMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        if(isBound){
            unbindService(remoteConnection);
        }
        super.onStop();
    }

    public void startRemoteService(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.serviceexample1","com.example.serviceexample1.ForeGroundRemoteService"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }else {
            startService(intent);
        }
    }
}
