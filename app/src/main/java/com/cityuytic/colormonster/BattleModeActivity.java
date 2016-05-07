package com.cityuytic.colormonster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
//1,up:color monster 2,down:xiaozhi
//11:monster self 12:monster enemy
//21:xiaozhi enemy 22:xiaozhi self
public class BattleModeActivity extends AppCompatActivity {

    private TextView view_time_remaining1;
    private ImageButton view_btn_start1;
    private TextView view_total_score11;
    private TextView view_task_type1;
    private TextView view_task_content1;
    private ImageView view_add1;
    private ImageView view_minus1;

    private TextView view_time_remaining2;
    private ImageButton view_btn_start2;
    private TextView view_total_score22;
    private TextView view_task_type2;
    private TextView view_task_content2;
    private ImageView view_add2;
    private ImageView view_minus2;

//    private TextView txtDialogCountdown;

    private TextView view_total_score12;
    private TextView view_total_score21;

    private BattleModeLevel battleModeLevel1;
    private BattleModeLevel battleModeLevel2;

    final Context context= this;




    private ArrayList<ImageButton> buttons1 = new ArrayList<>();
    private HashMap<String,Integer> images1 = new HashMap<>();
    BattleModeRound round1;

    public static String[] colors = {"紅","黃","藍","綠","黑"};



    private ArrayList<ImageButton> buttons2 = new ArrayList<>();
    private HashMap<String,Integer> images2 = new HashMap<>();
    BattleModeRound round2;





    //Declare a variable to hold count down timer's paused status
    private boolean isPaused2 = false;
    //Declare a variable to hold count down timer's paused status
    private boolean isCanceled2 = false;

    //Declare a variable to hold CountDownTimer remaining time
    private long timeRemaining2 = 0;
    private boolean isStartedEver = false;

//    private boolean isStartedEver = false;
//    private boolean normal = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_mode);

        LinearLayout l = (LinearLayout) findViewById(R.id.battle_mode_upCanvas);
        l.setRotation(180);


        battleModeLevel1 = new BattleModeLevel();
        battleModeLevel2 = new BattleModeLevel();
        //binding the views with java
        processViews();
        //inject the ids of images into ArrayList
        register();
//        view_btn_start2.setClickable(false);
//        startStartCountdown();

        view_btn_start2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_btn_start2.getTag().equals("start")){
                    isStartedEver = true;
                    mainActivityStart1();
                    mainActivityStart2();
                    view_btn_start2.setTag("pause");
                    view_btn_start2.setImageResource(R.drawable.btn_pause);
                }else if(view_btn_start2.getTag().equals("pause")){
                    pause();
                    view_btn_start2.setTag("resume");
                    view_btn_start2.setImageResource(R.drawable.btn_start);
                }else if(view_btn_start2.getTag().equals("resume")){
                    resume1();
                    resume2();
                    view_btn_start2.setTag("pause");
                    view_btn_start2.setImageResource(R.drawable.btn_pause);
                }else if(view_btn_start2.getTag().equals("restart")){
                    mainActivityStart1();
                    mainActivityStart2();
                    view_btn_start2.setTag("pause");
                    view_btn_start2.setImageResource(R.drawable.btn_pause);
                }
            }
        });

        view_btn_start1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //create a dialog
//        Dialog alertDialog = new AlertDialog().Builder(this)
//                .setTitle("")


    }

//    public Dialog createAlertDialog(String title, String msg,String positiveButton, String negativeButton, String neutralButton){
//
//    }

    public void processViews(){
        view_time_remaining1 = (TextView)findViewById(R.id.battle_mode_time_remaining1);
        view_btn_start1 = (ImageButton)findViewById(R.id.battle_mode_btn_home);
        view_total_score11 = (TextView)findViewById(R.id.battle_mode_text_score11);
        view_task_type1 = (TextView)findViewById(R.id.battle_mode_task_type1);
        view_task_content1 = (TextView)findViewById(R.id.battle_mode_task_content1);
        view_add1 = (ImageView)findViewById(R.id.battle_mode_view_addOne1);
        view_minus1 = (ImageView)findViewById(R.id.battle_mode_view_minusOne1);

        view_time_remaining2 = (TextView)findViewById(R.id.battle_mode_time_remaining2);
        view_btn_start2 = (ImageButton)findViewById(R.id.battle_mode_btn_start2);
        view_total_score22 = (TextView)findViewById(R.id.battle_mode_text_score22);
        view_task_type2 = (TextView)findViewById(R.id.battle_mode_task_type2);
        view_task_content2 = (TextView)findViewById(R.id.battle_mode_task_content2);
        view_btn_start2.setTag("start");
        view_total_score12 = (TextView)findViewById(R.id.battle_mode_text_score12);
        view_total_score21 = (TextView)findViewById(R.id.battle_mode_text_score21);
        view_add2 = (ImageView)findViewById(R.id.battle_mode_view_addOne2);
        view_minus2 = (ImageView)findViewById(R.id.battle_mode_view_minusOne2);



    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if(!isStartedEver) {
//            startStartCountdown();
//            normal = true;
//        }
//        else{

//            resume1();
        if(isStartedEver){
            isPaused2 = false;
            resume2();
            view_btn_start2.setTag("pause");
            view_btn_start2.setImageResource(R.drawable.btn_pause);
        }

//        }
    }

    @Override
    protected void onStop() {
//        if (!isStartedEver) {
//            normal = false;
//        }
        super.onStop();
        if (isStartedEver) {
            isPaused2 = true;
            pause();
            view_btn_start2.setTag("resume");
            view_btn_start2.setImageResource(R.drawable.btn_start);
        } else {

        }

    }

    public void mainActivityStart1(){


        round1 = new BattleModeRound(buttons1, images1, view_task_type1,view_task_content1,view_total_score11,view_total_score21,"ColorMonster",this,view_add1,view_minus1);
        round1.start();
//        battleModeLevel1.handleLevel(Integer.parseInt(view_total_score11.getText().toString()));

//        int timeInterval1 = battleModeLevel1.getTimeInterval();
//        new CountDownTimer(50000, 1000){
//            public void onTick(long millisUntilFinished){
//                //Do something in every tick
//                if(isPaused1 || isCanceled1)
//                {
//                    //If user requested to pause or cancel the count down timer
//                    cancel();
//                }
//                else {
//                    view_time_remaining1.setText("" + millisUntilFinished / 1000);
//                    //Put count down timer remaining time in a variable
//                    timeRemaining1 = millisUntilFinished;
//                }
//            }
//            public void onFinish(){
//                cancel();
//                mainActivityStart1();
//            }
//        }.start();

    }

    public void mainActivityStart2(){

        round2 = new BattleModeRound(buttons2, images2, view_task_type2,view_task_content2,view_total_score22,view_total_score12,"XiaoZhi",this,view_add2,view_minus2);
        round2.start();//add '#'
//        battleModeLevel2.handleLevel(Integer.parseInt(view_total_score22.getText().toString()));

//        int timeInterval2 = battleModeLevel2.getTimeInterval();


        new CountDownTimer(50000, 1000){
            public void onTick(long millisUntilFinished){
                //Do something in every tick
                if(isPaused2 || isCanceled2)
                {
                    //If user requested to pause or cancel the count down timer
                    cancel();
                }
                else {
                    view_time_remaining1.setText("" + millisUntilFinished / 1000);
                    view_time_remaining2.setText("" + millisUntilFinished / 1000);
                    //Put count down timer remaining time in a variable
                    timeRemaining2 = millisUntilFinished;
                }
            }
            public void onFinish(){
                isStartedEver = false;
                showDialog();
                round1.stop();
                view_time_remaining1.setText(0 + "");
                round2.stop();
                view_time_remaining2.setText(0 + "");
                view_btn_start2.setTag("restart");
                view_btn_start2.setImageResource(R.drawable.btn_start);
            }
        }.start();
    }

    public void pause(){
        try {
            round1.pause();
            round2.pause();
        } catch (NullPointerException e) {
        }
        isPaused2 = true;
    }

    public void resume1(){

//        isPaused1 = false;
//        long millisInFuture1 = timeRemaining1;
//        long countDownInterval1 = 1000;
        if(round1!=null)
            round1.resume();
//        new CountDownTimer(millisInFuture1, countDownInterval1){
//            public void onTick(long millisUntilFinished){
//                //Do something in every tick
//                if(isPaused1 || isCanceled1)
//                {
//                    //If user requested to pause or cancel the count down timer
//                    cancel();
//                }
//                else {
//                    view_time_remaining1.setText("" + millisUntilFinished / 1000);
//                    //Put count down timer remaining time in a variable
//                    timeRemaining1 = millisUntilFinished;
//                }
//            }
//            public void onFinish(){
//                cancel();
//                mainActivityStart1();
//            }
//        }.start();

    }

    public void resume2(){

        isPaused2 = false;
        long millisInFuture2 = timeRemaining2;
        long countDownInterval2 = 1000;

        round1.resume();
        round2.resume();


        new CountDownTimer(millisInFuture2, countDownInterval2){
            public void onTick(long millisUntilFinished){
                //Do something in every tick
                if(isPaused2 || isCanceled2)
                {
                    //If user requested to pause or cancel the count down timer
                    cancel();
                }
                else {
                    view_time_remaining1.setText("" + millisUntilFinished / 1000);
                    view_time_remaining2.setText("" + millisUntilFinished / 1000);
                    //Put count down timer remaining time in a variable
                    timeRemaining2 = millisUntilFinished;
                }
            }
            public void onFinish(){
                isStartedEver = false;
                round1.stop();
                view_time_remaining1.setText(0+"");
                round2.stop();
                view_time_remaining2.setText(0+"");
                view_btn_start1.setTag("restart");
                view_btn_start1.setImageResource(R.drawable.btn_start);
                showDialog();
            }
        }.start();
    }


    public void showDialog()
    {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.battle_dialog_layout);
        dialog.findViewById(R.id.battle_dialog_home1).setRotation(180);
        dialog.findViewById(R.id.battle_dialog_replay1).setRotation(180);

        ImageView dialog_background = (ImageView) dialog.findViewById(R.id.battle_dialog_scoreboard);
        int score11 = Integer.parseInt(view_total_score11.getText().toString());
        int score22 = Integer.parseInt(view_total_score22.getText().toString());
        if(score11 > score22)
        {
            dialog_background.setImageResource(R.drawable.battle_dialog1);
        }else if(score22 > score11)
        {
            dialog_background.setImageResource(R.drawable.battle_dialog2);
        }else
        {
            dialog_background.setImageResource(R.drawable.battle_dialog0);
        }

        dialog.findViewById(R.id.battle_dialog_home1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.findViewById(R.id.battle_dialog_home2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.findViewById(R.id.battle_dialog_replay1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });

        dialog.findViewById(R.id.battle_dialog_replay2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });


        dialog.show();
    }

//    private void startStartCountdown() {
//        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(BattleModeActivity.this,R.style.CustomDialog); // Create new alert dialog
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_countdown, null); // Inflate the custom layout in to a View object
//        dialog.setView(dialogView);
//        dialog.setCancelable(false);
//        final android.support.v7.app.AlertDialog countdownDialog = dialog.create(); // Create the dialog in a final context
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
//                    view_btn_start2.setClickable(true);
//                    mainActivityStart1();
//                    mainActivityStart2();
//                    view_btn_start2.setTag("pause");
//                    view_btn_start2.setImageResource(R.drawable.btn_pause);
//                }
//                else{
//
//                }
//
//            }
//        }.start();
//    }
    public void register(){
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn00));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn01));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn02));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn03));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn04));

        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn10));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn11));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn12));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn13));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn14));

        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn20));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn21));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn22));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn23));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn24));

        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn30));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn31));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn32));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn33));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn34));

        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn40));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn41));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn42));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn43));
        buttons1.add((ImageButton) findViewById(R.id.battle_mode_upImgBtn44));


        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn00));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn01));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn02));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn03));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn04));

        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn10));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn11));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn12));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn13));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn14));

        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn20));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn21));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn22));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn23));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn24));

        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn30));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn31));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn32));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn33));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn34));

        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn40));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn41));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn42));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn43));
        buttons2.add((ImageButton) findViewById(R.id.battle_mode_downImgBtn44));

        images1.put("00", R.drawable.unit00);
        images1.put("01", R.drawable.unit01);
        images1.put("02", R.drawable.unit02);
        images1.put("03", R.drawable.unit03);
        images1.put("04", R.drawable.unit04);
        images1.put("10", R.drawable.unit10);
        images1.put("11", R.drawable.unit11);
        images1.put("12", R.drawable.unit12);
        images1.put("13", R.drawable.unit13);
        images1.put("14", R.drawable.unit14);
        images1.put("20", R.drawable.unit20);
        images1.put("21", R.drawable.unit21);
        images1.put("22", R.drawable.unit22);
        images1.put("23", R.drawable.unit23);
        images1.put("24", R.drawable.unit24);
        images1.put("30", R.drawable.unit30);
        images1.put("31", R.drawable.unit31);
        images1.put("32", R.drawable.unit32);
        images1.put("33", R.drawable.unit33);
        images1.put("34", R.drawable.unit34);
        images1.put("40", R.drawable.unit40);
        images1.put("41", R.drawable.unit41);
        images1.put("42", R.drawable.unit42);
        images1.put("43", R.drawable.unit43);
        images1.put("44", R.drawable.unit44);
        images1.put("55", R.drawable.unit55);

        images2.put("#00", R.drawable.unit00);
        images2.put("#01", R.drawable.unit01);
        images2.put("#02", R.drawable.unit02);
        images2.put("#03", R.drawable.unit03);
        images2.put("#04", R.drawable.unit04);
        images2.put("#10", R.drawable.unit10);
        images2.put("#11", R.drawable.unit11);
        images2.put("#12", R.drawable.unit12);
        images2.put("#13", R.drawable.unit13);
        images2.put("#14", R.drawable.unit14);
        images2.put("#20", R.drawable.unit20);
        images2.put("#21", R.drawable.unit21);
        images2.put("#22", R.drawable.unit22);
        images2.put("#23", R.drawable.unit23);
        images2.put("#24", R.drawable.unit24);
        images2.put("#30", R.drawable.unit30);
        images2.put("#31", R.drawable.unit31);
        images2.put("#32", R.drawable.unit32);
        images2.put("#33", R.drawable.unit33);
        images2.put("#34", R.drawable.unit34);
        images2.put("#40", R.drawable.unit40);
        images2.put("#41", R.drawable.unit41);
        images2.put("#42", R.drawable.unit42);
        images2.put("#43", R.drawable.unit43);
        images2.put("#44", R.drawable.unit44);
        images2.put("#55", R.drawable.unit55);
    }

    /*protected String[] buildPostQuery() {
        String[] query=new String[10];
        // Get device wifi module's MAC address
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        query[1] = wInfo.getMacAddress();
        // Set basic information
        query[0]="Test";
        query[2]="3";
        query[3]="60";
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
    }*/
}
