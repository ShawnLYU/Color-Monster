package com.cityuytic.colormonster;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class AboutActivity extends AppCompatActivity {

    private ImageButton about_btn_return;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        about_btn_return = (ImageButton) findViewById(R.id.about_btn_return);
        about_btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        about_btn_return.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.animate().translationXBy(3).translationYBy(3).setInterpolator(null).setDuration(50);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.animate().translationXBy(-3).translationYBy(-3).setInterpolator(null).setDuration(50);
                }
                return false;
            }
        });

    }
}
