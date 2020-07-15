package com.example.hp.foodiesserverside;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        String name = sharedPreferences.getString("name", "");
        Log.e("Phone", phone);
        Log.e("Name", name);


        if (!TextUtils.isEmpty(name)) {

            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();

        } else {
            Log.e("SharedPrefs", name + " " + phone);
        }

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SigninActivity.class));

            }
        });

    }
}
