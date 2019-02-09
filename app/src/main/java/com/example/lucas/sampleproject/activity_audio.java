package com.example.lucas.sampleproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.ConnectivityManager;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class activity_audio extends AppCompatActivity {
    private static final String TAG = "activity_audio";
//    private MediaPlayerService player;
//    boolean serviceBound = false;
    String url = "http://cdn.mainhomepage.com/dailydozen/DailyDozen.mp3"; // your URL here

    public static final String ACTION_PLAY = "com.example.lucas.sampleproject.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.lucas.sampleproject.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.example.lucas.sampleproject.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.lucas.sampleproject.ACTION_NEXT";
    public static final String ACTION_STOP = "com.example.lucas.sampleproject.ACTION_STOP";

    //MediaSession
//    private MediaSessionManager mediaSessionManager;
//    private MediaSessionCompat mediaSession;
//    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;
    MediaPlayer mediaPlayer = new MediaPlayer();
    Button btnPlay;
    Button btnPause;
    Button btnForward;
    Button btnRewind;
    SeekBar seekBar;
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    Integer currentMediaposition=0;
    TextView txtTime;
    TextView txtEndTime;
    Timer mSecTimer;
    private PlaybackInfoListener mPlaybackInfoListener;
    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarPositionUpdateTask;
    Timer timer;
    private ArrayList<MediaStore.Audio> audioList;
    private int audioIndex = -1;
    private MediaStore.Audio activeAudio; //an object of the currently playing audio
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.lucas.sampleproject.PlayNewAudio";
    boolean serviceBound = false;
    NotificationCompat.Builder mBuilder;
    NotificationManagerCompat notificationManager;
    Integer currentMediapositionRewind;
    NotificationCompat.Builder mBuilder2;
    BroadcastReceiver br = new MyBroadcastReceiver();
    private static activity_audio instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        instance = this;
//        playAudio("http://cdn.mainhomepage.com/dailydozen/DailyDozen.mp3");
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnForward = findViewById(R.id.btnForward);
        btnRewind = findViewById(R.id.btnRewind);
        seekBar = findViewById(R.id.seekBar);
        txtTime = findViewById(R.id.txtTime);
        txtEndTime = findViewById(R.id.txtEndTime);
        btnPause.setVisibility(View.INVISIBLE);
        createNotificationChannel();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);
//                txtTime.setText(progress);
//                txtTime.toString();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setProgress(0);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();// might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "getDuration" + mp.getDuration());
                seekBar.setMax(mp.getDuration());
                long millis =mp.getDuration();
                String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                if (TimeUnit.MILLISECONDS.toHours(millis) == 0){
                    endHms = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                }
                txtEndTime.setText(endHms.toString());

            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(0);
                btnPause.setVisibility(View.INVISIBLE);
                btnPlay.setVisibility(View.VISIBLE);
                mediaPlayer.seekTo(0);
            }
        });



        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMedia();
            }
        });



        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseMedia();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer currentMediapositionFastForward=0;
                currentMediapositionFastForward = mediaPlayer.getCurrentPosition() +  30000;
                 mediaPlayer.seekTo(currentMediapositionFastForward);
                seekBar.setProgress(currentMediapositionFastForward);
            }
        });

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentMediapositionRewind = mediaPlayer.getCurrentPosition()  - 30000;
                mediaPlayer.seekTo(currentMediapositionRewind);
                seekBar.setProgress(currentMediapositionRewind);
                seekBar.getProgress();
            }
        });



    }

    public void updateTimerText(){
        runOnUiThread(new Runnable(){
            @Override
            public void run () {
                Log.d(TAG, "run: current postion" + mediaPlayer.getCurrentPosition());

                long millis2 = mediaPlayer.getDuration();
                String endHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis2),
                        TimeUnit.MILLISECONDS.toMinutes(millis2) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis2)),
                        TimeUnit.MILLISECONDS.toSeconds(millis2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis2)));


                long millis =mediaPlayer.getCurrentPosition();
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                if (TimeUnit.MILLISECONDS.toHours(millis) == 0){
                    hms = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                }
                txtTime.setText(hms);
//                txtEndTime.setText((int) (millis2 -1));





            }
        });
    }





    private void playMedia(){
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.start();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: current postion" + mediaPlayer.getCurrentPosition());
                updateTimerText();

            }
        },0,1000);
        callStateListener();
        registerBecomingNoisyReceiver();
        Intent playIntent = new Intent(this, activity_audio.class);
        Intent pauseIntent = new Intent(this, activity_audio.class);
        pauseIntent.setAction("pause");
        pauseIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);

        playIntent.setAction("play");
        playIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);

        Intent broadcastIntent = new Intent(this, MyBroadcastReceiver.class);
        broadcastIntent.putExtra("action", "pause");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, activity_audio.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent goToAppIntent =
                PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_library_music_black);

        mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_library_music_black)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.chrisbrady))
                .addAction(R.drawable.ic_pause_black_48dp, "pause", actionIntent)
                .setContentTitle("Life Info")
                .setContentText("Chris Brady")
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0))
                .setSubText("Sub text")
                .setContentIntent(goToAppIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        notificationManager = NotificationManagerCompat.from(this);
        Notification mNotification = null;

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
        mBuilder.setPublicVersion(mNotification);
//        mBuilder.setPublicVersion();
        btnPause.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.INVISIBLE);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Life Audio";
            String description = "Life Audio Player";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected void pauseMedia(){
        mediaPlayer.pause();
        btnPause.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
        timer = new Timer();
        timer.cancel();

        Intent playIntent = new Intent(this, activity_audio.class);
        Intent pauseIntent = new Intent(this, activity_audio.class);
//        playIntent.setAction(mediaPlayer.ca);
        playIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);

        Intent broadcastIntent = new Intent(this, MyBroadcastReceiver.class);
        broadcastIntent.putExtra("action2", "play");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder2 = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_library_music_black)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.chrisbrady))
                .addAction(R.drawable.ic_play_arrow_black_48dp, "Play", actionIntent)
                .setContentTitle("Life Info")
                .setContentText("Chris Brady")
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0))
                .setSubText("Sub text")
                .setPriority(NotificationCompat.PRIORITY_LOW);
        notificationManager = NotificationManagerCompat.from(this);
        Notification mNotification = null;

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder2.build());
        mBuilder2.setPublicVersion(mNotification);
    }


    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    //Becoming noisy
    //Headphones unplug and plug in
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            Toast.makeText(context, "Headphones unpluged", Toast.LENGTH_SHORT).show();
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );

    }


    private void updateProgressCallbackTask() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {

                mPlaybackInfoListener.onPositionChanged(currentPosition);
// display it in the textview

            }

        }


    }

    public static activity_audio getAudioActivityInstance() {
        return instance;
    }

    public void publicPause() {
        pauseMedia();
    }
    public void publicPlay() {
        playMedia();
    }





}




