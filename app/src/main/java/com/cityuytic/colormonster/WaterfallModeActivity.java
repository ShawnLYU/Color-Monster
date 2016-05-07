package com.cityuytic.colormonster;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class WaterfallModeActivity extends AppCompatActivity {

    final int COLOR_CNT=5;
    final int UNIT_CNT=5;
    final int ADD_SCORE=1;
    int roundScore,totalScore;

    private class Question {
        private int questionType; //0: Color; 1: Text; 2: Both
        private int questionColorIdx;
        private int correctUnitIdx;
        Question() {
            Random ran = new Random();
            this.questionType=ran.nextInt(3);
            this.questionColorIdx=ran.nextInt(COLOR_CNT);
            this.correctUnitIdx=ran.nextInt(UNIT_CNT);
        }
    };

    protected final String[] TEXT = {"紅", "黃", "藍", "綠", "黑"};
    protected final int[][] imageButtonRes=new int[COLOR_CNT][COLOR_CNT];

    private Question question;
    private ImageButton[] colorUnit=new ImageButton[UNIT_CNT];
    private ObjectAnimator[] translationAnim=new ObjectAnimator[UNIT_CNT];
    private TextView totalScoreView, roundScoreView,timerView, currentTask, currentTask2, txtDialogCountdown;
    private CountDownTimer countDownTimer, startTimer;
    private long remainingTime;
    private ImageButton btnPause;
    private long translationCurrentPlayTime;
    private ImageView addOne, minusOne;
    private RelativeLayout layout_waterfallCanvas;
    private MediaPlayer btnClickMediaPlayer;
    private boolean isPaused,isFirstCountdown, allowUpload;
    private String name,gender;
    private int age;

    private final int MAX_REACTION_CNT=10000;
    private long[] reactionTime;
    private boolean[] reactionTF;
    private int[] reactionType;
    private int reactionCnt;
    private long reactionElapse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterfall_mode);
        layout_waterfallCanvas=(RelativeLayout)findViewById(R.id.waterfallCanvas);
        // Reset statistics
        roundScore=0;
        reactionCnt=0;
        reactionTime=new long[MAX_REACTION_CNT];
        reactionTF=new boolean[MAX_REACTION_CNT];
        reactionType=new int[MAX_REACTION_CNT];
        isPaused=true;
        isFirstCountdown=true;
        // Default user preferences
        allowUpload=false;
        name="";
        age=0;
        gender="M";
        // Restore preferences
        SharedPreferences sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
        if (sharedPreferences!=null) {
            totalScore=sharedPreferences.getInt("totalScore",0);
            allowUpload=sharedPreferences.getBoolean("allowUpload", false);
            name=sharedPreferences.getString("name", "");
            age=sharedPreferences.getInt("age", 0);
            gender=sharedPreferences.getString("gender","M");
        }
        // Load all cards and animations
        loadImageButtonRes();
        LinearLayout.LayoutParams unitLayout=new LinearLayout.LayoutParams(90,70);// Card size
        for (int i=0;i<UNIT_CNT; i++) {
            // Prepare images
            colorUnit[i] = new ImageButton(this);
            colorUnit[i].setId(3689 * 10 + i);
            colorUnit[i].setX(44 + 110 * i);
            colorUnit[i].setY(180);
            colorUnit[i].setLayoutParams(unitLayout);
            colorUnit[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            colorUnit[i].setBackgroundResource(R.drawable.rounded_button);
            colorUnit[i].setOnClickListener(mButtonListener);
            // Prepare animations
            translationAnim[i] = ObjectAnimator.ofFloat(colorUnit[i],"translationY", 485);
            translationAnim[i].setDuration(5000);
            translationAnim[i].setRepeatMode(ValueAnimator.RESTART);
            translationAnim[i].setRepeatCount(ValueAnimator.INFINITE);
            translationAnim[i].setCurrentPlayTime(0);
            translationAnim[i].setInterpolator(null);
            translationAnim[i].addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) { }
                @Override public void onAnimationEnd(Animator animation) { }
                @Override public void onAnimationCancel(Animator animation) { }
                @Override public void onAnimationRepeat(Animator animation) {generateQuestion();}
            });
        }
        // Prepare timer and taskbar
        timerView=(TextView)findViewById(R.id.timerView);
        currentTask=(TextView)findViewById(R.id.currentTask);
        currentTask2=(TextView)findViewById(R.id.currentTask2);
        // Prepare Scorebars (total & round)
        totalScoreView=(TextView)findViewById(R.id.totalScore);
        totalScoreView.setText("總積分: " + totalScore);
        roundScoreView=(TextView)findViewById(R.id.roundScore);
        roundScoreView.setText("" + roundScore);
        // Prepare pause button
        btnPause=new ImageButton(this);
        btnPause.setImageResource(R.drawable.btn_pause);
        btnPause.setId(3689 * 10 + UNIT_CNT);
        btnPause.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams btnPauseSize=new RelativeLayout.LayoutParams(100, 120);
        btnPauseSize.rightMargin=50;
        btnPauseSize.bottomMargin=50;
        btnPauseSize.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btnPauseSize.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btnPause.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout layout_waterfallPause=(RelativeLayout)findViewById(R.id.waterfallCanvas);
        layout_waterfallPause.addView(btnPause, btnPauseSize);
        btnPause.setVisibility(View.GONE);
        // Prepare +1 effect
        addOne=new ImageView(this);
        addOne.setImageResource(R.drawable.add_one);
        addOne.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams rpAdd = new RelativeLayout.LayoutParams(50, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rpAdd.setMargins(0, 0, 75, 0);
        rpAdd.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addOne.setLayoutParams(rpAdd);
        addOne.setY(150);
        addOne.setVisibility(View.GONE);
        layout_waterfallCanvas.addView(addOne);
        //Prepare -1 effect
        minusOne=new ImageView(this);
        minusOne.setImageResource(R.drawable.minus_one);
        minusOne.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams rpMinus = new RelativeLayout.LayoutParams(50, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rpMinus.setMargins(0, 0, 75, 0);
        rpMinus.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        minusOne.setLayoutParams(rpMinus);
        minusOne.setY(50);
        minusOne.setVisibility(View.GONE);
        layout_waterfallCanvas.addView(minusOne);

        // Countdown 4 sec to start
        currentTask.setText("");
        currentTask2.setText("");
        timerView.setText("");
        startStartCountdown();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (isFirstCountdown) {
            startTimer.cancel();
        } else if (!isPaused) {
            stopAnimation();
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isFirstCountdown) {
            startTimer.start();
        } else if (!isPaused) {
            resumeAnimation();
            startCountDown(remainingTime);
        }
    }

    // Button onClick
    private View.OnClickListener mButtonListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int btnId=v.getId();
            // Cards buttons
            if (btnId>=36890 && btnId<3689*10+UNIT_CNT) {
                // Statistic
                reactionTime[reactionCnt]=reactionElapse;
                reactionType[reactionCnt]=question.questionType;
                // Correct answer
                if (btnId % 10 == question.correctUnitIdx) {
                    roundScore += ADD_SCORE;
                    reactionTF[reactionCnt]=true;
                    // Run +1 effect
                    ObjectAnimator addOneAnim = ObjectAnimator.ofFloat(addOne,"translationY", 50);
                    addOneAnim.setDuration(500);
                    addOneAnim.setRepeatMode(ValueAnimator.RESTART);
                    addOneAnim.setRepeatCount(0);
                    addOneAnim.setCurrentPlayTime(0);
                    addOneAnim.setInterpolator(null);
                    addOneAnim.addListener(new Animator.AnimatorListener() {
                        @Override public void onAnimationStart(Animator animation) {addOne.setVisibility(View.VISIBLE);}
                        @Override public void onAnimationEnd(Animator animation) {addOne.setVisibility(View.GONE);addOne.setY(150);}
                        @Override public void onAnimationCancel(Animator animation) { }
                        @Override public void onAnimationRepeat(Animator animation) { }
                    });
                    addOneAnim.start();
                }
                // Wrong answer
                else {
                    if (roundScore > 0) {
                        roundScore -= ADD_SCORE;
                        reactionTF[reactionCnt] = false;
                        // Run -1 effect
                        ObjectAnimator minusOneAnim = ObjectAnimator.ofFloat(minusOne, "translationY", 150);
                        minusOneAnim.setDuration(500);
                        minusOneAnim.setRepeatMode(ValueAnimator.RESTART);
                        minusOneAnim.setRepeatCount(0);
                        minusOneAnim.setCurrentPlayTime(0);
                        minusOneAnim.setInterpolator(null);
                        minusOneAnim.addListener(new Animator.AnimatorListener() {
                            @Override public void onAnimationStart(Animator animation) {minusOne.setVisibility(View.VISIBLE);}
                            @Override public void onAnimationEnd(Animator animation) {minusOne.setVisibility(View.GONE);minusOne.setY(50);}
                            @Override public void onAnimationCancel(Animator animation) { }
                            @Override public void onAnimationRepeat(Animator animation) { }
                        });
                        minusOneAnim.start();
                    }
                }
                // Play sound
                btnClickMediaPlayer = MediaPlayer.create(WaterfallModeActivity.this, R.raw.button_click);
                btnClickMediaPlayer.start();
                btnClickMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                // Reset cards animations
                for (int i = 0; i < UNIT_CNT; i++) {
                    translationAnim[i].cancel();
                    translationAnim[i].start();
                }
                // Refresh scores
                roundScoreView.setText(""+roundScore);
                // Load new questions
                generateQuestion();
                reactionCnt++;
                reactionElapse=0;
            }
            // Pause button
            else if (btnId==3689*10+UNIT_CNT) {
                // Disable animations, timer and buttons
                stopAnimation();
                countDownTimer.cancel();
                btnPause.setImageResource(R.drawable.btn_start);
                btnPause.setClickable(false);
                isPaused=true;
                // Generate custom pause dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(WaterfallModeActivity.this,R.style.CustomDialog); // Create new alert dialog
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
                        btnPause.setImageResource(R.drawable.btn_pause);
                        resumeAnimation();
                        startCountDown(remainingTime);
                        btnPause.setClickable(true);
                        isPaused=false;
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
        }
    };

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

    // Self-defined methods
    private void loadImageButtonRes() {
        imageButtonRes[0][0] = R.drawable.unit00;
        imageButtonRes[0][1] = R.drawable.unit01;
        imageButtonRes[0][2] = R.drawable.unit02;
        imageButtonRes[0][3] = R.drawable.unit03;
        imageButtonRes[0][4] = R.drawable.unit04;

        imageButtonRes[1][0]=R.drawable.unit10;
        imageButtonRes[1][1]=R.drawable.unit11;
        imageButtonRes[1][2]=R.drawable.unit12;
        imageButtonRes[1][3]=R.drawable.unit13;
        imageButtonRes[1][4]=R.drawable.unit14;

        imageButtonRes[2][0]=R.drawable.unit20;
        imageButtonRes[2][1]=R.drawable.unit21;
        imageButtonRes[2][2]=R.drawable.unit22;
        imageButtonRes[2][3]=R.drawable.unit23;
        imageButtonRes[2][4]=R.drawable.unit24;

        imageButtonRes[3][0]=R.drawable.unit30;
        imageButtonRes[3][1]=R.drawable.unit31;
        imageButtonRes[3][2]=R.drawable.unit32;
        imageButtonRes[3][3]=R.drawable.unit33;
        imageButtonRes[3][4]=R.drawable.unit34;

        imageButtonRes[4][0]=R.drawable.unit40;
        imageButtonRes[4][1]=R.drawable.unit41;
        imageButtonRes[4][2]=R.drawable.unit42;
        imageButtonRes[4][3]=R.drawable.unit43;
        imageButtonRes[4][4]=R.drawable.unit44;
    }

    private void generateQuestion() {
        question=new Question();
        Random ran = new Random();
        switch (question.questionType) {
            case 0:
                //currentTask.setText("請選取\n顏色 是 "+TEXT[question.questionColorIdx]+"色\n的字卡。");
                currentTask.setText("顏色");
                currentTask2.setText(TEXT[question.questionColorIdx]);
                for (int i = 0; i < UNIT_CNT; i++)
                    if (i == question.correctUnitIdx)
                        colorUnit[i].setImageResource(imageButtonRes[question.questionColorIdx][ran.nextInt(COLOR_CNT)]);
                    else {
                        int tempId;
                        do {
                            tempId = ran.nextInt(COLOR_CNT);
                        } while (tempId == question.questionColorIdx);
                        colorUnit[i].setImageResource(imageButtonRes[tempId][ran.nextInt(COLOR_CNT)]);
                    }
                break;
            case 1:
                //currentTask.setText("請選取\n字 是 「"+TEXT[question.questionColorIdx]+"色」\n的字卡。");
                currentTask.setText("意思");
                currentTask2.setText(TEXT[question.questionColorIdx]);
                for (int i = 0; i < UNIT_CNT; i++)
                    if (i == question.correctUnitIdx)
                        colorUnit[i].setImageResource(imageButtonRes[ran.nextInt(COLOR_CNT)][question.questionColorIdx]);
                    else {
                        int tempId;
                        do {
                            tempId = ran.nextInt(COLOR_CNT);
                        } while (tempId == question.questionColorIdx);
                        colorUnit[i].setImageResource(imageButtonRes[ran.nextInt(COLOR_CNT)][tempId]);
                    }
                break;
            case 2:
                //currentTask.setText("請選取\n字和顏色 都是 "+TEXT[question.questionColorIdx]+"色\n的字卡。");
                currentTask.setText("顏色和意思");
                currentTask2.setText(TEXT[question.questionColorIdx]);
                for (int i = 0; i < UNIT_CNT; i++)
                    if (i == question.correctUnitIdx)
                        colorUnit[i].setImageResource(imageButtonRes[question.questionColorIdx][question.questionColorIdx]);
                    else {
                        int tempId1, tempId2;
                        do {
                            tempId1 = ran.nextInt(COLOR_CNT);//color
                            tempId2 = ran.nextInt(COLOR_CNT);//text
                        }
                        while (tempId1 == question.questionColorIdx || tempId2 == question.questionColorIdx);
                        colorUnit[i].setImageResource(imageButtonRes[tempId1][tempId2]);
                    }
                break;
        }
    }

    private void startCountDown(long timeInterval) {
        countDownTimer = new CountDownTimer(timeInterval, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { // Show remaining time
                reactionElapse++;
                remainingTime=millisUntilFinished;
                output(millisUntilFinished);
            }
            @Override
            public void onFinish() { // Time's up
                output(0);
                // Stop all
                stopAnimation();
                countDownTimer.cancel();
                isPaused=false;
                btnPause.setImageResource(R.drawable.btn_start);
                btnPause.setClickable(false);
                // Save preferences
                int oldTotalScore=totalScore;
                totalScore+=roundScore;
                SharedPreferences sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("totalScore", totalScore);
                editor.commit();
                totalScoreView.setText("總積分: " + totalScore);

                // HTTP POST
                if (allowUpload)
                    new UploadStatData(WaterfallModeActivity.this).execute(buildPostQuery());
                // Judge have new totem or not
                boolean newTotem=false;
                if (TotemActivity.getLevel(oldTotalScore)!=TotemActivity.getLevel(totalScore))
                    newTotem=true;
                // Generate custom pause dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(WaterfallModeActivity.this,R.style.CustomDialog); // Create new alert dialog
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_scoreboard, null); // Inflate the custom layout in to a View object
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                final AlertDialog scoreboardDialog = dialog.create(); // Create the dialog in a final context
                // Find the Button object within the inflated view
                ImageButton btnDialogScoreboardReplay=(ImageButton)dialogView.findViewById(R.id.btnDialogScoreboardReplay);
                ImageButton btnDialogScoreboardHome=(ImageButton)dialogView.findViewById(R.id.btnDialogScoreboardHome);
                ImageButton btnDialogScoreboardTotem=(ImageButton)dialogView.findViewById(R.id.btnDialogScoreboardTotem);
                TextView txtDialogScoreboardScore=(TextView)dialogView.findViewById(R.id.txtDialogScoreboardScore);
                txtDialogScoreboardScore.setText("" + roundScore);
                // Set the onClickListener
                btnDialogScoreboardReplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { // Restart activity
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
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
                        startActivity(new Intent(WaterfallModeActivity.this, TotemActivity.class));
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
            }
            // Output remaining time
            private void output(long millisUntilFinished) {
                long second=millisUntilFinished/1000;
                long minute=second/60;
                second%=60;
                timerView.setText(String.format("%02d:%02d",minute,second));
            }
        };
        countDownTimer.start();
    }

    private void startStartCountdown() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(WaterfallModeActivity.this,R.style.CustomDialog); // Create new alert dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_countdown, null); // Inflate the custom layout in to a View object
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final AlertDialog countdownDialog = dialog.create(); // Create the dialog in a final context
        countdownDialog.show();
        txtDialogCountdown=(TextView)countdownDialog.findViewById(R.id.txtDialogCountdown);
        txtDialogCountdown.setText("4");
        startTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long second=millisUntilFinished/1000;
                long minute=second/60;
                second%=60;
                //timerView.setText(String.format("%02d:%02d", minute, second));
                txtDialogCountdown.setText(""+second);
            }
            @Override
            public void onFinish() {
                countdownDialog.cancel();
                isFirstCountdown=false;
                timerView.setText("01:00");
                // Load questions, images and start animations
                generateQuestion();
                for (int i = 0;i<UNIT_CNT;i++) {
                    layout_waterfallCanvas.addView(colorUnit[i]);
                    translationAnim[i].start();
                }
                startCountDown(60 * 1000);
                reactionElapse=0;
                btnPause.setOnClickListener(mButtonListener);
                btnPause.setOnTouchListener(mBottonTouchListener);
                btnPause.setVisibility(View.VISIBLE);
            }
        };
        startTimer.start();
        isPaused=false;
    }

    private void stopAnimation() {
        translationCurrentPlayTime=translationAnim[0].getCurrentPlayTime();
        for (int i=0;i<UNIT_CNT;i++) {
            translationAnim[i].cancel();
            colorUnit[i].setClickable(false);
            //colorUnit[i].setEnabled(false);
        }
    }

    private void resumeAnimation() {
        for (int i=0;i<UNIT_CNT;i++) {
            //colorUnit[i].setEnabled(true);
            colorUnit[i].setClickable(true);
            translationAnim[i].start();
            translationAnim[i].setCurrentPlayTime(translationCurrentPlayTime);
        }
    }

    protected String[] buildPostQuery() {
        String[] query=new String[12];
        // Get device wifi module's MAC address
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        query[1] = wInfo.getMacAddress();
        // Set basic information
        query[0]=name;
        query[2]="3";
        query[3]="60";
        query[10]=""+age;
        query[11]=gender;
        // Statistics
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
