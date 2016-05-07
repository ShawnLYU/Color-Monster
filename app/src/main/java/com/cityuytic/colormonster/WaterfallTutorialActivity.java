package com.cityuytic.colormonster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

public class WaterfallTutorialActivity extends AppCompatActivity {

    private TextView bt_tutorial;

    public static final int FIRST_STEP=0;
    public static final int SECOND_STEP=1;
    public static final int THIRD_STEP=2;
    public static final int FOURTH_STEP=3;
    public static final int FIFTH_STEP=4;
    public static final int SIXTH_STEP=5;
    public static final int SEVEN_STEP=6;
    public static final int EIGHT_STEP=7;
    public static final int FINISH = 8;

    private int tutorialStep;

    private HashMap<Integer,String> tutorialText = new HashMap<>();
    private HashMap<Integer,Integer> tutorialToHighlighte = new HashMap<>();
    private HashMap<Integer,Integer> tutorialToCancel = new HashMap<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterfall_tutorial);


        processViews();



        RelativeLayout layout=(RelativeLayout)findViewById(R.id.bt_myRelativeLayout);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tutorialStep!=FINISH){
                    bt_tutorial.setText(tutorialText.get(tutorialStep));
                    if(tutorialToHighlighte.get(tutorialStep)!=null)
                        findViewById(tutorialToHighlighte.get(tutorialStep)).setVisibility(View.VISIBLE);
                    if(tutorialToCancel.get(tutorialStep)!=null)
                        findViewById(tutorialToCancel.get(tutorialStep)).setVisibility(View.INVISIBLE);
                    tutorialStep++;
                }else{
                    startActivity(new Intent(WaterfallTutorialActivity.this, WaterfallModeActivity.class));
                    finish();
                }


            }
        });

    }


    public void processViews(){
        bt_tutorial = (TextView)findViewById(R.id.bt_tutorial);
        tutorialStep = 2;
        tutorialText.put(FIRST_STEP,"聰明的小朋友，現在我們一起來探索驚險刺激的瀑布模式啦！");
        tutorialText.put(SECOND_STEP,"點進瀑布模式之後，遊戲開始前會有3秒準備時間，小朋友在這段時間要做好準備噢~");
        tutorialText.put(THIRD_STEP,"遊戲正式開始之後，熒幕右邊你可以看到有三個顯示欄，它們依次為：“本次遊戲剩餘時間”，“任務類型”，“任務顏色”");
        tutorialText.put(FOURTH_STEP,"這個時候，中間的遊戲區域會有字塊兒落下來，小朋友要拍到符合上面要求的卡片，還要在它落地之前哦");
        tutorialText.put(FIFTH_STEP,"看到那個小星星了嗎？數字代表著本輪遊戲所得分數哦");
        tutorialText.put(SIXTH_STEP,"熒幕右上角顯示的則是小朋友的總分");
        tutorialText.put(SEVEN_STEP,"遊戲過程中還可以選擇這裡暫停哦，要是想退出這個模式也是要點這裡喲");
        tutorialText.put(EIGHT_STEP,"好啦，那就讓我們一起開始冒險吧！");

        //tutorialToHighlighte.put(FIRST_STEP,R.id.boy_hand);
        tutorialToHighlighte.put(SECOND_STEP,R.id.boy_hand);
        tutorialToHighlighte.put(THIRD_STEP,R.id.bt_question);
        tutorialToHighlighte.put(FOURTH_STEP,R.id.bt_gameZone);
        tutorialToHighlighte.put(FIFTH_STEP,R.id.bt_score_bar);
        tutorialToHighlighte.put(SIXTH_STEP,R.id.bt_totalScore);
        tutorialToHighlighte.put(SEVEN_STEP,R.id.bt_start);

        //tutorialToCancel.put(FIRST_STEP,R.id);
        //tutorialToCancel.put(SECOND_STEP,R.id.boy_hand);
        tutorialToCancel.put(THIRD_STEP,R.id.boy_hand);
        tutorialToCancel.put(FOURTH_STEP,R.id.bt_question);
        tutorialToCancel.put(FIFTH_STEP,R.id.bt_gameZone);
        tutorialToCancel.put(SIXTH_STEP,R.id.bt_score_bar);
        tutorialToCancel.put(SEVEN_STEP,R.id.bt_totalScore);

    }

}

