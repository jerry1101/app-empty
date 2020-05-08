package com.jerry.empty;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Button brandSEOButton;
    Button onDemandButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brandSEOButton = findViewById(R.id.brandSEOButton);
        onDemandButton = findViewById(R.id.onDemandButton);

        brandSEOButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent activity2Intent = new Intent(getApplicationContext(), SeoBrandActivity.class);
                        startActivity(activity2Intent);
                    }
                }
        );

        onDemandButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent activity2Intent = new Intent(getApplicationContext(), OnDemandActivity.class);
                        startActivity(activity2Intent);
                    }
                }
        );





    }



}
