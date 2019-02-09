package com.example.lucas.sampleproject;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class activity_sound extends AppCompatActivity {
    MediaPlayer player;
    Button btnSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        btnSound = findViewById(R.id.btnSound);
        play();

    }

    private void play(){
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null){
                    player = MediaPlayer.create(activity_sound.this, R.raw.incoming);
                }

                player.start();
            }
        });
    }
}
