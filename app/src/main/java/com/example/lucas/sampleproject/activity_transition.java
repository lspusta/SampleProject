package com.example.lucas.sampleproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class activity_transition extends AppCompatActivity {
    Button btnDo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        btnDo = findViewById(R.id.btnDo);


        btnDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.item_animation_fall_down, R.anim.item_animation_from_bottom);
            }
        });
    }
}
