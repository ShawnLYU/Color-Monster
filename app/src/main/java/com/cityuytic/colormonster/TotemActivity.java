package com.cityuytic.colormonster;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class TotemActivity extends AppCompatActivity {

    public static final int[] LEVEL = {0,20,60,100,140,180};
    private int totalScore;
    private int curLevel;
    private HashMap<Integer,Integer > allTotem = new HashMap<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView view_score, view_story;
    private ImageButton btn_next;
    private boolean firstrun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totem);
        sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);

        /*
        editor=sharedPreferences.edit();
        editor.putInt("level", 5);
        editor.putInt("totalScore", 55);
        editor.apply();
        */
        register();

        if (sharedPreferences!=null) {
            Log.e("Totem", "get preference");
            totalScore=sharedPreferences.getInt("totalScore",0);
            Log.e("Totem", "get preference: totalScore = "+totalScore);
            curLevel =sharedPreferences.getInt("level",0);
            Log.e("Totem", "get preference: level = "+curLevel);
            firstrun=sharedPreferences.getBoolean("totem_firstrun",true);
        }
        view_score.setText("" + totalScore);
        showTotem(curLevel);

        for (int i=0; i< LEVEL.length-1; i++ )
        {
            if (totalScore >= LEVEL[i] && totalScore<LEVEL[i+1] && i> curLevel)
            {
                Log.e("Totem", "pre_level = "+curLevel +"  ;  cur_level = "+(i)+"  ;  score = "+ totalScore);
                addTotem(curLevel,i);
                curLevel=i;
                editor=sharedPreferences.edit();
                editor.putInt("level", curLevel);
                editor.apply();
                Log.e("Totem", "write preference: level = "+curLevel);
            }

        }
        if (totalScore>=LEVEL[5] && curLevel!=5)
        {
            addTotem(curLevel, 5);
        }

        Intent intent=this.getIntent();
        boolean fromMainMenu=intent.getBooleanExtra("fromMainMenu", false);

        if (firstrun) {
            firstrun=false;
            editor=sharedPreferences.edit();
            editor.putBoolean("totem_firstrun", false);
            editor.putBoolean("single_firstrun", true);
            editor.putBoolean("battle_firstrun", true);
            editor.putBoolean("waterfall_firstrun", true);
            editor.putBoolean("bluetooth_firstrun", true);
            editor.apply();
            view_story.setText("親愛的小朋友，和我們一起找回顏色圖騰吧！");
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(TotemActivity.this, InitSettingActivity.class));
                    finish();
                }
            });
        } else if (fromMainMenu) {
            view_story.setText("小朋友，你已經得到了"+curLevel+"塊圖騰，好棒呀！");
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            view_story.setText("小朋友，你得到了一塊新圖騰，繼續加油吧！");
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        btn_next.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    v.animate().translationXBy(3).translationYBy(3).setInterpolator(null).setDuration(50);
                } else if (event.getAction()==MotionEvent.ACTION_UP) {
                    v.animate().translationXBy(-3).translationYBy(-3).setInterpolator(null).setDuration(50);
                }
                return false;
            }
        });
    }

    public void register()
    {
        allTotem.put(1, R.id.totem1);
        allTotem.put(2, R.id.totem2);
        allTotem.put(3, R.id.totem3);
        allTotem.put(4, R.id.totem4);
        allTotem.put(5, R.id.totem5);
        view_score= (TextView)findViewById(R.id.totem_total_score);
        view_story=(TextView)findViewById(R.id.totem_story);
        btn_next=(ImageButton)findViewById(R.id.totem_btn_next);
    }


    public void addTotem(int pre_level, int cur_level)
    {
        for (int i = pre_level+1; i<=cur_level; i++)
        {
            Log.e("Totem", "addTotem():  i= "+i);
            int cur_id = allTotem.get(i);
            ImageView current_totem = (ImageView) findViewById(cur_id);

            float curTranslationY = current_totem.getTranslationY();
            Log.e("Totem", "addTotem():  curTranslationY= "+curTranslationY);

            current_totem.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(current_totem, "translationY", -450f, curTranslationY);
            animator.setDuration(1000);
            Log.e("Totem", "addTotem():  setStartDelay= " + (1000 * (i - pre_level - 1)));
            animator.setStartDelay(1000 * (i - pre_level - 1));
            animator.start();
            Log.e("Totem", "addTotem():  animation start() ");

            /*if(level>1) {
                ImageView previous_totem = (ImageView) findViewById(allTotem.get(level - 1));
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(previous_totem, "scaleY", 1f, 0.5f, 1f);
                animator1.setStartDelay(1000);
                animator1.setDuration(500);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(current_totem, "scaleY", 1f, 1.4f, 1f);
                animator1.setStartDelay(1000);
                animator2.setDuration(500);

                animator1.start();
                animator2.start();
            }*/
        }

    }

    public void showTotem (int cur_level)
    {
        ImageView totem;
        for (int i=0; i<cur_level; i++)
        {
            totem = (ImageView) findViewById(allTotem.get(i+1));
            totem.setVisibility(View.VISIBLE);
        }
    }

    public static int getLevel(int inputTotalScore) {
        for (int i=0;i<4;i++)
            if (inputTotalScore>=LEVEL[i] && inputTotalScore<LEVEL[i+1])
                return i;
        return 4;
    }
}
