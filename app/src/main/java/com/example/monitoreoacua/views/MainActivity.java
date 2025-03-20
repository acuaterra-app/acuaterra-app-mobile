package com.example.monitoreoacua.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use a Handler to delay the transition to RegisterActivity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create the intent to navigate to RegisterActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent); // start RegisterActivity
                finish();
            }
        }, 3000); // delay of 2000 miliseconds
    }

}