package com.example.hp.foodiesserverside;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    FButton buttonSignin;
    TextView slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSignin = (FButton) findViewById(R.id.signIn);
        slogan = (TextView) findViewById(R.id.slogon);
        slogan.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF"));

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SigninActivity.class));

            }
        });

    }
}
