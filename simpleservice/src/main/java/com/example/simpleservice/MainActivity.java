package com.example.simpleservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String MY_NOT = "my notify channel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(MY_NOT,"simple service",
                    NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }

        setContentView(R.layout.activity_main);
    }

    public void loadData(View view) {
        startService(new Intent(this,MyService.class).putExtra("url","https://api.bittrex.com/api/v1.1/public/getmarketsummaries"));
        //this.finish();

    }

    public void loadData2(View view) {
        startService(new Intent(this,MyService.class).putExtra("url","https://api.bittrex.com/api/v1.1/public/getmarkets"));
    }
    public void loadData3(View view) {
        startService(new Intent(this,MyService.class).putExtra("url","https://api.bittrex.com/api/v1.1/public/getcurrencies"));

    }
}
