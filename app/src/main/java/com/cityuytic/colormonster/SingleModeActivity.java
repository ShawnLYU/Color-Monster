package com.cityuytic.colormonster;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleModeActivity extends AppCompatActivity {

    private TextView view_time_remaining;
    private ImageView view_btn_start;
    private TextView view_single_mode_score;
    private TextView view_task_type;
    private TextView view_task_content;
    private ImageView view_xiaozhi;
    private TextView view_total_score;
    private TextView txtDialogCountdown;

    @Override
    protected void onRestart() {
        Log.e("MainActivity", "onRestart()");
        super.onRestart();
//        if(!isStartedEver) {
//            startStartCountdown();
//            normal = true;
//        }
//        else{
            if (view_btn_start.getTag().equals("pause")) {
                view_btn_start.setTag("pause");
                view_btn_start.setImageResource(R.drawable.btn_pause);
                resume();
            }
//        }



    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (!isStartedEver) {
//            normal = false;
//        }
        if (view_btn_start.getTag().equals("pause")) {
            pause_onStop();
            Log.e("onStop()", "moster Y" + view_monster.getTranslationY());
            Log.e("onStop()", "xiaozhi Y" + view_xiaozhi.getTranslationY());
        }
    }

    private ImageView view_monster;
    private ObjectAnimator monster_animator;
    private CountDownTimer timer;

    private SingleModeLevel singleModeLevel;

    private boolean[] reactionTF;
    private int[] reactionType;
    private int reactionCnt = 0;

    private ArrayList<ImageButton> buttons = new ArrayList<>();
    private HashMap<String,Integer> images = new HashMap<>();
    SingleModeRound round;

    public static String[] colors = {"紅","黃","藍","綠","黑"};


    public final String TAG = "SingleMode";


    //Declare a variable to hold count down timer's paused status
    private boolean isPaused = false;
    //Declare a variable to hold count down timer's paused status
    private boolean isCanceled = false;

    //Declare a variable to hold CountDownTimer remaining time
    private long timeRemaining = 0;

    private SharedPreferences sharedPreferences;
    private int totalScore=0;
    private boolean allowUpload=false;
    private String name="";
    private int age=0;
    private String gender="M";
    private boolean isStartedEver = false;
    private boolean normal = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode);

        singleModeLevel = new SingleModeLevel();
        //binding the views with java
        processViews();
        //inject the ids of images into ArrayList
        register();
        // Set total score
        sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
        if (sharedPreferences!=null) {
            totalScore=sharedPreferences.getInt("totalScore",0);
            allowUpload=sharedPreferences.getBoolean("allowUpload",false);
            name=sharedPreferences.getString("name","");
            age=sharedPreferences.getInt("age",0);
            gender=sharedPreferences.getString("gender","M");
        }
        view_total_score.setText("" + totalScore);


        view_btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view_btn_start.getTag().equals("start")) {
                    mainActivityStart();
                    view_btn_start.setTag("pause");
                    view_btn_start.setImageResource(R.drawable.btn_pause);
                } else if (view_btn_start.getTag().equals("pause")) {
                    pause();
                    view_btn_start.setTag("resume");
                    view_btn_start.setImageResource(R.drawable.btn_start);
                } else if (view_btn_start.getTag().equals("resume")) {
                    resume();
                    view_btn_start.setTag("pause");
                    view_btn_start.setImageResource(R.drawable.btn_pause);
                } else if (view_btn_start.getTag().equals("restart")) {
                    mainActivityStart();
                    view_btn_start.setTag("pause");
                    view_btn_start.setImageResource(R.drawable.btn_pause);
                }
            }
        });
        view_btn_start.setOnTouchListener(new View.OnTouchListener() {
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
//        startStartCountdown();


      /*view_monster.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
          @Override
          public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
              Log.e("MainActivity", "monster top=" + top);

              float curPositionX = view_xiaozhi.getY();
              Log.e("MainActivity", "xiaozhi Y=" + curPositionX);
              float curPositionM = view_monster.getY();
              Log.e("MainActivity", "monster Y=" + curPositionM);

              if(curPositionM - curPositionX < 20){
                  round.stop();
                  view_time_remaining.setText(0 + "");
                  view_btn_start.setTag("restart");
                  view_btn_start.setImageResource(R.drawable.unit_start);
                  monster_animator.cancel();
                  Log.e("MainActivity", "<20 should stop");
              }

          }
      });*/

    }

    public void processViews(){
        view_time_remaining = (TextView)findViewById(R.id.single_mode_time_remaining);
        view_btn_start = (ImageView)findViewById(R.id.single_mode_btn_start);
        view_btn_start.setImageResource(R.drawable.btn_start);
        view_btn_start.setTag("start");
//        view_btn_start.setClickable(false);
        view_single_mode_score = (TextView)findViewById(R.id.single_mode_score);
        view_task_type = (TextView)findViewById(R.id.single_mode_task_type);
        view_task_content = (TextView)findViewById(R.id.single_mode_task_content);
        view_xiaozhi = (ImageView) findViewById(R.id.single_mode_xiaozhi);
        view_monster = (ImageView)findViewById(R.id.single_mode_monster);
        view_total_score=(TextView)findViewById(R.id.single_mode_total_score);
    }

    public void mainActivityStart(){

        round = new SingleModeRound(buttons, images, view_task_type,view_task_content, view_single_mode_score, view_xiaozhi,this);
        round.start();


        //singleModeLevel.handleLevel(Integer.parseInt(view_single_mode_score.getText().toString()));

        //animation start
        view_monster.setX(125);
        view_monster.setY(500);
        view_xiaozhi.setX(125);
        view_xiaozhi.setY(350);
        float curTranslationY = view_monster.getTranslationY();
        monster_animator = ObjectAnimator.ofFloat(view_monster, "translationY", curTranslationY, -500f);
        monster_animator.setDuration(70000);
        monster_animator.start();

        //animation end

//        int timeInterval = singleModeLevel.getTimeInterval();
        timer = new CountDownTimer(70000, 1000){
            public void onTick(long millisUntilFinished){
                //Do something in every tick
                if(isPaused || isCanceled)
                {
                    //If user requested to unit_pause or cancel the count down timer
                    cancel();
                }
                else {
                    view_time_remaining.setText("" + millisUntilFinished / 1000);
                    //Put count down timer remaining time in a variable
                    timeRemaining = millisUntilFinished;
                    CheckMonster();
                }
            }
            public void onFinish(){
                round.stop();
                view_time_remaining.setText(0 + "");
                view_btn_start.setTag("restart");
                view_btn_start.setImageResource(R.drawable.btn_start);
                reactionCnt = round.getReactionCnt();
                reactionTF = round.getAllReactionTF();
                reactionType = round.getAllReactionType();
            }
        }.start();
    }

    public void pause(){
        if(round!=null)
            round.pause();
        isPaused = true;
        if (monster_animator!=null)
            monster_animator.cancel();
        // Generate custom pause dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(SingleModeActivity.this,R.style.CustomDialog); // Create new alert dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pause, null); // Inflate the custom layout in to a View object
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final AlertDialog pauseDialog = dialog.create(); // Create the dialog in a final context
        // Find the Button object within the inflated view
        ImageButton btnDialogPauseResume=(ImageButton)dialogView.findViewById(R.id.btnDialogPauseResume);
        ImageButton btnDialogPauseReplay=(ImageButton)dialogView.findViewById(R.id.btnDialogPauseReplay);
        ImageButton btnDialogPauseHome=(ImageButton)dialogView.findViewById(R.id.btnDialogPauseHome);
        // Set the onClickListener
        btnDialogPauseResume.setOnClickListener(new View.OnClickListener() { // Dismiss and resume game
            @Override
            public void onClick(View v) {
                resume();
                view_btn_start.setTag("pause");
                view_btn_start.setImageResource(R.drawable.btn_pause);
                pauseDialog.dismiss();
            }
        });
        btnDialogPauseReplay.setOnClickListener(new View.OnClickListener() { // Restart activity
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });
        btnDialogPauseHome.setOnClickListener(new View.OnClickListener() { // Finish activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Set onTouchListener
        btnDialogPauseResume.setOnTouchListener(mBottonTouchListener);
        btnDialogPauseReplay.setOnTouchListener(mBottonTouchListener);
        btnDialogPauseHome.setOnTouchListener(mBottonTouchListener);
        // Show the dialog
        pauseDialog.show();
    }
    public void pause_onStop(){
        if(timer!=null)
            timer.cancel();
        if(round!=null)
            round.pause();
        isPaused = true;
        if(monster_animator!=null)
            monster_animator.cancel();
    }


    public void resume(){

        isPaused = false;
        long millisInFuture = timeRemaining;
        long countDownInterval = 1000;
        if (round!=null)
            round.resume();
        if(monster_animator!=null){
            //animation start
            float curTranslationY = view_monster.getTranslationY();
            long remain_time = 1000* Long.parseLong(view_time_remaining.getText().toString());
            monster_animator = ObjectAnimator.ofFloat(view_monster, "translationY", curTranslationY, -450f);
            monster_animator.setDuration(remain_time);
            monster_animator.start();
        }


        if(timer!=null){
            timer = new CountDownTimer(millisInFuture, countDownInterval){
                public void onTick(long millisUntilFinished){
                    //Do something in every tick
                    if(isPaused || isCanceled)
                    {
                        //If user requested to unit_pause or cancel the count down timer
                        cancel();
                    }
                    else {
                        view_time_remaining.setText("" + millisUntilFinished / 1000);
                        //Put count down timer remaining time in a variable
                        timeRemaining = millisUntilFinished;
                        CheckMonster();

                    }
                }
                public void onFinish(){
                    round.stop();
                    view_time_remaining.setText(0+"");
                    view_btn_start.setTag("restart");
                    view_btn_start.setImageResource(R.drawable.btn_start);
                    reactionCnt = round.getReactionCnt();
                    reactionTF = round.getAllReactionTF();
                    reactionType = round.getAllReactionType();
                }
            }.start();
        }

    }

    public void CheckMonster()
    {
        float curPositionX = view_xiaozhi.getY();
        Log.d("MainActivity", "                xiaozhi Y=" + curPositionX);
        float curPositionM = view_monster.getY();
        Log.d("MainActivity", "monster Y=" + curPositionM);

        if(curPositionM - curPositionX < 20){
            round.stop();
            timer.cancel();
            view_time_remaining.setText(0 + "");
            view_btn_start.setTag("restart");
            view_btn_start.setImageResource(R.drawable.btn_start);
            monster_animator.cancel();
            reactionCnt = round.getReactionCnt();
            reactionTF = round.getAllReactionTF();
            reactionType = round.getAllReactionType();;
            Log.d("MainActivity", "<20 should stop");
        }else if (curPositionX < 100) {
            timer.cancel();
            round.stop();
            view_time_remaining.setText(0 + "");
            view_btn_start.setTag("restart");
            view_btn_start.setImageResource(R.drawable.btn_start);
            monster_animator.cancel();
            reactionCnt = round.getReactionCnt();
            reactionTF = round.getAllReactionTF();
            reactionType = round.getAllReactionType();
            Log.d("MainActivity", "xiaozhi win, should stop");
        }

        if (curPositionM - curPositionX < 20 || curPositionX < 100) {
            if (allowUpload)
                new UploadStatData(SingleModeActivity.this).execute(buildPostQuery());
            // Generate custom pause dialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(SingleModeActivity.this,R.style.CustomDialog); // Create new alert dialog
            View dialogView = getLayoutInflater().inflate(R.layout.single_dialog_scoreboard, null); // Inflate the custom layout in to a View object
            dialog.setView(dialogView);
            dialog.setCancelable(false);
            final AlertDialog scoreboardDialog = dialog.create(); // Create the dialog in a final context
            // Find the Button object within the inflated view
            ImageButton btnDialogScoreboardReplay=(ImageButton)dialogView.findViewById(R.id.btnSingleDialogScoreboardReplay);
            ImageButton btnDialogScoreboardHome=(ImageButton)dialogView.findViewById(R.id.btnSingleDialogScoreboardHome);
            ImageButton btnDialogScoreboardTotem=(ImageButton)dialogView.findViewById(R.id.btnSingleDialogScoreboardTotem);
            TextView txtDialogScoreboardScore=(TextView)dialogView.findViewById(R.id.txtSingleDialogScoreboardScore);
            ImageView dialogScoreboardBg=(ImageView)dialogView.findViewById(R.id.singleDialogScoreboardBg);
            // Judge have new totem or not
            int single_mode_score=Integer.parseInt(view_single_mode_score.getText().toString());
            int oldTotalScore=totalScore;
            totalScore+=single_mode_score;
            boolean newTotem=false;
            if (TotemActivity.getLevel(oldTotalScore)!=TotemActivity.getLevel(totalScore))
                newTotem=true;
            // Save preference
            SharedPreferences sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("totalScore", totalScore);
            editor.apply();
            txtDialogScoreboardScore.setText(""+single_mode_score);
            view_total_score.setText("" + totalScore);

            // Set the onClickListener
            btnDialogScoreboardReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // Restart activity
                    scoreboardDialog.dismiss();
                    mainActivityStart();
                    view_btn_start.setTag("pause");
                    view_btn_start.setImageResource(R.drawable.btn_pause);
                }
            });
            btnDialogScoreboardHome.setOnClickListener(new View.OnClickListener() { // Finish activity
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            btnDialogScoreboardTotem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // Restart activity
                    startActivity(new Intent(SingleModeActivity.this, TotemActivity.class));
                    finish();
                }
            });
            if (newTotem) {
                btnDialogScoreboardHome.setVisibility(View.GONE);
                btnDialogScoreboardReplay.setVisibility(View.GONE);
                btnDialogScoreboardTotem.setVisibility(View.VISIBLE);
            } else {
                btnDialogScoreboardHome.setVisibility(View.VISIBLE);
                btnDialogScoreboardReplay.setVisibility(View.VISIBLE);
                btnDialogScoreboardTotem.setVisibility(View.GONE);
            }
            scoreboardDialog.show(); // Show the dialog
            if(curPositionM - curPositionX < 20)
                dialogScoreboardBg.setImageResource(R.drawable.dialog_scoreboard_lose_bg);
            else
                dialogScoreboardBg.setImageResource(R.drawable.dialog_scoreboard_win_bg);
        }



    }

//    private void startStartCountdown() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(SingleModeActivity.this,R.style.CustomDialog); // Create new alert dialog
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_countdown, null); // Inflate the custom layout in to a View object
//        dialog.setView(dialogView);
//        dialog.setCancelable(false);
//        final AlertDialog countdownDialog = dialog.create(); // Create the dialog in a final context
//        countdownDialog.show();
//        txtDialogCountdown=(TextView)countdownDialog.findViewById(R.id.txtDialogCountdown);
//        txtDialogCountdown.setText("4");
//        new CountDownTimer(4000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                long second=millisUntilFinished/1000;
//                long minute=second/60;
//                second%=60;
//                //timerView.setText(String.format("%02d:%02d", minute, second));
//                txtDialogCountdown.setText(""+second);
//            }
//            @Override
//            public void onFinish() {
//                countdownDialog.cancel();
//                if(normal){
//                    isStartedEver = true;
//                    view_btn_start.setClickable(true);
//                    mainActivityStart();
//                    view_btn_start.setTag("pause");
//                    view_btn_start.setImageResource(R.drawable.btn_pause);
//                }
//                else{
//
//                }
//
//            }
//        }.start();
//    }
    public void register(){
        buttons.add((ImageButton) findViewById(R.id.imgBtn00));
        buttons.add((ImageButton) findViewById(R.id.imgBtn01));
        buttons.add((ImageButton) findViewById(R.id.imgBtn02));
        buttons.add((ImageButton) findViewById(R.id.imgBtn03));
        buttons.add((ImageButton) findViewById(R.id.imgBtn04));

        buttons.add((ImageButton) findViewById(R.id.imgBtn10));
        buttons.add((ImageButton) findViewById(R.id.imgBtn11));
        buttons.add((ImageButton) findViewById(R.id.imgBtn12));
        buttons.add((ImageButton) findViewById(R.id.imgBtn13));
        buttons.add((ImageButton) findViewById(R.id.imgBtn14));

        buttons.add((ImageButton) findViewById(R.id.imgBtn20));
        buttons.add((ImageButton) findViewById(R.id.imgBtn21));
        buttons.add((ImageButton) findViewById(R.id.imgBtn22));
        buttons.add((ImageButton) findViewById(R.id.imgBtn23));
        buttons.add((ImageButton) findViewById(R.id.imgBtn24));

        buttons.add((ImageButton) findViewById(R.id.imgBtn30));
        buttons.add((ImageButton) findViewById(R.id.imgBtn31));
        buttons.add((ImageButton) findViewById(R.id.imgBtn32));
        buttons.add((ImageButton) findViewById(R.id.imgBtn33));
        buttons.add((ImageButton) findViewById(R.id.imgBtn34));

        buttons.add((ImageButton) findViewById(R.id.imgBtn40));
        buttons.add((ImageButton) findViewById(R.id.imgBtn41));
        buttons.add((ImageButton) findViewById(R.id.imgBtn42));
        buttons.add((ImageButton) findViewById(R.id.imgBtn43));
        buttons.add((ImageButton) findViewById(R.id.imgBtn44));

        images.put("00", R.drawable.unit00);
        images.put("01", R.drawable.unit01);
        images.put("02", R.drawable.unit02);
        images.put("03", R.drawable.unit03);
        images.put("04", R.drawable.unit04);
        images.put("10", R.drawable.unit10);
        images.put("11", R.drawable.unit11);
        images.put("12", R.drawable.unit12);
        images.put("13", R.drawable.unit13);
        images.put("14", R.drawable.unit14);
        images.put("20", R.drawable.unit20);
        images.put("21", R.drawable.unit21);
        images.put("22", R.drawable.unit22);
        images.put("23", R.drawable.unit23);
        images.put("24", R.drawable.unit24);
        images.put("30", R.drawable.unit30);
        images.put("31", R.drawable.unit31);
        images.put("32", R.drawable.unit32);
        images.put("33", R.drawable.unit33);
        images.put("34", R.drawable.unit34);
        images.put("40", R.drawable.unit40);
        images.put("41", R.drawable.unit41);
        images.put("42", R.drawable.unit42);
        images.put("43", R.drawable.unit43);
        images.put("44", R.drawable.unit44);
        images.put("55", R.drawable.unit55);
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

    protected String[] buildPostQuery() {
        String[] query=new String[12];
        // Get device wifi module's MAC address
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        query[1] = wInfo.getMacAddress();
        // Set basic information
        query[0]=name; //username
        query[2]="1";    //singlemode -->  1
        query[3]="70";   //duration for a round --> 70s
        // Statistics
        /*
        4 : no of rounds "color-matching"
        5 : no of rounds "meaning-matching"
        6 : no of rounds "both-matching"
        7 : no of correct rounds "color-matching"
        8 : no of correct rounds "color-matching"
        9  : no of correct rounds "color-matching"
        */
        query[10]=""+age;
        query[11]=gender;

        int[] stat=new int[6];
        for (int i=0;i<reactionCnt;i++) {
            stat[reactionType[i]]++;
            if (reactionTF[i])
                stat[reactionType[i]+3]++;
        }
        for (int i=0;i<6;i++)
            query[4+i]=""+stat[i];
        // Output
        return query;
    }

}