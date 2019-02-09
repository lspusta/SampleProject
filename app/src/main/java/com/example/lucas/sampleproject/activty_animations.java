package com.example.lucas.sampleproject;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class activty_animations extends AppCompatActivity {

    ImageView imgProfile;
    TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_animations);
        imgProfile = findViewById(R.id.imgProfile);
        txtName = findViewById(R.id.txtName);
    }


    public void sharedElementTransition(View view){
        Pair[] pair = new Pair[2];
        pair[0] = new Pair<View, String>(imgProfile,"profileImg");
        pair[1] = new Pair<View, String>(txtName,"personName");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pair);
        Intent i = new Intent(activty_animations.this, detail.class);
        startActivity(i, options.toBundle());
    }


}

