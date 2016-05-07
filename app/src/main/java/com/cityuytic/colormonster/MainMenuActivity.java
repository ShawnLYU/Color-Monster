package com.cityuytic.colormonster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {

    private ImageView view_single;
    private ImageView view_battle;
    private ImageView view_waterfall;
    private ImageView view_bluetooth;
    private ImageView view_setting;
    private ImageView view_totem;
    private TextView view_totalScore;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int totalScore=0;

    private boolean single_firstrun,battle_firstrun,waterfall_firstrun,bluetooth_firstrun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        register();
        // Set total score
        getTotalScore();
        // Restore preferences
        SharedPreferences sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
        if (sharedPreferences!=null) {
            totalScore=sharedPreferences.getInt("totalScore",0);
            single_firstrun=sharedPreferences.getBoolean("single_firstrun", true);
            battle_firstrun=sharedPreferences.getBoolean("battle_firstrun",true);
            waterfall_firstrun=sharedPreferences.getBoolean("waterfall_firstrun",true);
            bluetooth_firstrun=sharedPreferences.getBoolean("bluetooth_firstrun",true);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getTotalScore();
    }

    public void register()
    {
        view_single =(ImageView)findViewById(R.id.mainMenu_singleMode);
        view_battle =(ImageView)findViewById(R.id.mainMenu_battleMode);
        view_waterfall =(ImageView)findViewById(R.id.mainMenu_waterMode);
        view_bluetooth =(ImageView)findViewById(R.id.mainMenu_bluetooth);
        view_setting = (ImageView)findViewById(R.id.mainMenu_setting);
        view_totem = (ImageView)findViewById(R.id.mainMenu_totem);
        view_totalScore=(TextView)findViewById(R.id.mainMenu_sumScore);

        view_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences!=null)
                    single_firstrun=sharedPreferences.getBoolean("single_firstrun",true);
                if (single_firstrun) {
                    single_firstrun=false;
                    editor=sharedPreferences.edit();
                    editor.putBoolean("single_firstrun", false);
                    editor.commit();
                    startActivity(new Intent(MainMenuActivity.this, SingleTutorialActivity.class));
                }
                else
                    startActivity(new Intent(MainMenuActivity.this, SingleModeActivity.class));
            }
        });

        view_battle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, BattleModeActivity.class));
            }
        });

        view_waterfall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences!=null)
                    waterfall_firstrun=sharedPreferences.getBoolean("waterfall_firstrun",true);
                if (waterfall_firstrun) {
                    waterfall_firstrun=false;
                    editor=sharedPreferences.edit();
                    editor.putBoolean("waterfall_firstrun", false);
                    editor.commit();
                    startActivity(new Intent(MainMenuActivity.this, WaterfallTutorialActivity.class));
                }
                else
                    startActivity(new Intent(MainMenuActivity.this, WaterfallModeActivity.class));
            }
        });

        view_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences!=null)
                    bluetooth_firstrun=sharedPreferences.getBoolean("bluetooth_firstrun",true);
                if (bluetooth_firstrun) {
                    bluetooth_firstrun=false;
                    editor=sharedPreferences.edit();
                    editor.putBoolean("bluetooth_firstrun", false);
                    editor.commit();
                    startActivity(new Intent(MainMenuActivity.this, BluetoothTutorialActivity.class));
                }
                else
                    startActivity(new Intent(MainMenuActivity.this, BluetoothActivity.class));
            }
        });

        view_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, SettingActivity.class));
            }
        });

        view_totem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent totemIntent=new Intent(MainMenuActivity.this, TotemActivity.class);
                totemIntent.putExtra("fromMainMenu", true);
                startActivity(totemIntent);
            }
        });

        view_single.setOnTouchListener(mBottonTouchListener);
        view_battle.setOnTouchListener(mBottonTouchListener);
        view_waterfall.setOnTouchListener(mBottonTouchListener);
        view_bluetooth.setOnTouchListener(mBottonTouchListener);
        view_setting.setOnTouchListener(mBottonTouchListener);
        view_totem.setOnTouchListener(mBottonTouchListener);
    }

    private View.OnTouchListener mBottonTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                v.animate().translationXBy(3).translationYBy(3).setInterpolator(null).setDuration(50);
            } else if (event.getAction()==MotionEvent.ACTION_UP) {
                v.animate().translationXBy(-3).translationYBy(-3).setInterpolator(null).setDuration(50);
            }
            return false;
        }
    };

    private void getTotalScore()
    {
        sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
        if (sharedPreferences!=null)
            totalScore=sharedPreferences.getInt("totalScore",0);
        view_totalScore.setText(""+totalScore);
    }
}
