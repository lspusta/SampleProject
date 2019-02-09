package com.example.lucas.sampleproject;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getStringExtra("action");
        String action2 = intent.getStringExtra("action2");

if (action != null){


    if (action.equals("pause")){
        activity_audio.getAudioActivityInstance().publicPause();
    }

}

        if (action2 != null){


            if (action2.equals("play")){
                activity_audio.getAudioActivityInstance().publicPlay();
            }

        }


    }

}
