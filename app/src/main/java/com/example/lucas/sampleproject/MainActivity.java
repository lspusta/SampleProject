package com.example.lucas.sampleproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btnAnimations;
    Button btnCoordinator;
    Button btnTransition;
    Button btnScrollView;
    Button btnAndroidCoordinator;
    Button btnSound;
    Button btnAudio;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAnimations = findViewById(R.id.btnAnimations);
        btnCoordinator = findViewById(R.id.btnCoordinator);
        btnTransition = findViewById(R.id.btnTransition);
        btnScrollView = findViewById(R.id.btnScrollView);
        btnAndroidCoordinator = findViewById(R.id.btnAndroidCoordinator);
        btnSound = findViewById(R.id.btnSound);
        btnAudio = findViewById(R.id.btnAudio);

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, activity_audio.class));
            }
        });

       btnSound.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, activity_sound.class));
           }
       });

       btnAnimations.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, activty_animations.class));
           }
       });


       btnCoordinator.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, activity_Coordinator.class));
           }
       });


       btnTransition.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, activity_transition.class));
           }
       });

       btnScrollView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, activity_scrollview.class));
           }
       });

        btnAndroidCoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScrollingActivity.class));
            }
        });


    }
}
