package com.example.serviceexample1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyReceiver extends BroadcastReceiver {
    public static final String MY_ACTION = "com.example.service.RESULT";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("onReceive","received action"+intent.getAction());
        if(intent.getAction().equals(MY_ACTION)){
            Intent activity_intent = new Intent(context, ResultActivity.class);
           // context.startActivity(activity_intent.putExtras(intent).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            context.startActivity(activity_intent.putExtras(intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
        }

    }
}
