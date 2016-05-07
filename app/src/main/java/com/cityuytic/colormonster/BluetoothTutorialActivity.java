package com.cityuytic.colormonster;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

public class BluetoothTutorialActivity extends AppCompatActivity {
    private TextView ct_tutorial;

    public static final int FIRST_STEP=0;
    public static final int SECOND_STEP=1;
    public static final int THIRD_STEP=2;
    public static final int FOURTH_STEP=3;
    public static final int FIFTH_STEP=4;
    public static final int SIXTH_STEP = 5;
    public static final int FINISH = 6;

    private int tutorialStep;
    private ImageView start;

    private HashMap<Integer,String> tutorialText = new HashMap<>();
    private HashMap<Integer,Integer> tutorialToHighlighte = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_tutorial);


        processViews();


        final RelativeLayout layout=(RelativeLayout)findViewById(R.id.ct_myRelativeLayout);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tutorialStep != FINISH) {
                    ct_tutorial.setText(tutorialText.get(tutorialStep));
                    if(tutorialToHighlighte.get(tutorialStep)!=null)
                        layout.setBackgroundResource(tutorialToHighlighte.get(tutorialStep));
                    if(tutorialStep == FIFTH_STEP)
                    {
                        start.setVisibility(View.VISIBLE);
                    }
                    tutorialStep++;
                } else {
                    startActivity(new Intent(BluetoothTutorialActivity.this, BluetoothActivity.class));
                    finish();
                }


            }
        });

    }

    public void processViews(){
        ct_tutorial = (TextView)findViewById(R.id.ct_tutorial);
        start = (ImageView) findViewById(R.id.bluetooth_tutorial_btn_start);
        tutorialStep = SECOND_STEP;
        tutorialText.put(FIRST_STEP,"聰明的小朋友們，想要和小夥伴們一起聯機對戰嗎？現在就讓我們和身邊的小夥伴一起開始玩吧");
        tutorialText.put(SECOND_STEP,"點入藍牙模式之後，首先要點“是”才可以被小夥伴的設備找到哦，注意一定要和小夥伴一起打開此模式");
        tutorialText.put(THIRD_STEP,"之後就要耐心等待幾秒鐘讓設備找到小夥伴的設備");
        tutorialText.put(FOURTH_STEP,"這時候你會看到一個彈窗，找到你的小夥伴的設備，兩個人中的一個人點擊，注意，只能一個人點哦");
        tutorialText.put(FIFTH_STEP,"點到之後會有一台設備顯示出“開始”按鈕，這時候只要點擊這個俺就就可以開始遊戲啦，遊戲方法和單人模式相同");
        tutorialText.put(SIXTH_STEP,"好啦，那就讓我們一起開始冒險吧！");


        tutorialToHighlighte.put(SECOND_STEP,R.drawable.bluetooth2);
        tutorialToHighlighte.put(THIRD_STEP,R.drawable.bluetooth5);
        tutorialToHighlighte.put(FOURTH_STEP,R.drawable.bluetooth4);
        tutorialToHighlighte.put(FIFTH_STEP,R.drawable.bluetooth3);



    }
}

