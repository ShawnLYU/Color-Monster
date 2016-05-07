package com.cityuytic.colormonster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int secondsDelayed = 2;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
                boolean totem_firstrun=true;
                if (sharedPreferences!=null)
                    totem_firstrun=sharedPreferences.getBoolean("totem_firstrun",true);
                if (totem_firstrun)
                    startActivity(new Intent(SplashActivity.this, TotemActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);
    }
}
