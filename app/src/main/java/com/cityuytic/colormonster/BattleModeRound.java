package com.cityuytic.colormonster;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
/**
 * Created by user on 2015/10/20.
 */
public class BattleModeRound {

    private BattleModeQuestion singleModeQuestion;
    private ArrayList<Integer> validButtons = new ArrayList<>();
    private ArrayList<Integer> validIndex = new ArrayList<>();
    private ArrayList<ImageButton> buttons = new ArrayList<>();
    private HashMap<String,Integer> images = new HashMap<>();
    private String flag;
    private MediaPlayer player;
    private Context context;

    private TextView view_taskType;
    private TextView view_taskContent;
    private TextView view_total_score_this;
    private TextView view_total_score_that;

    private ImageView view_add;
    private ImageView view_minus;

    private int state;

    public static int STATE_NORMAL = 0;
    public static int STATE_LOSE = 1;
    public static int STATE_PAUSE = 2;




    public BattleModeRound(ArrayList<ImageButton> buttons, HashMap<String,Integer> images, TextView view_taskType,
                           TextView view_taskContent, TextView view_total_score_this,
                           TextView view_total_score_that, String flag, Context context, ImageView add, ImageView minus){
        this.flag = flag;
        this.buttons = buttons;
        this.images = images;
        this.view_taskType = view_taskType;
        this.view_taskContent = view_taskContent;
        this.view_total_score_this = view_total_score_this;
        this.view_total_score_that = view_total_score_that;
        state = STATE_NORMAL;
        this.context = context;
        this.view_add = add;
        this.view_minus = minus;
    }

    public void start(){
        singleModeQuestion = new BattleModeQuestion();
        switch (singleModeQuestion.getQuestionType()){
            case BattleModeQuestion.QUESTION_TYPE_COLOR:
                validButtons = questionForColor(flag);
                break;
            case BattleModeQuestion.QUESTION_TYPE_TEXT:
                validButtons = questionForText(flag);
                break;
            case BattleModeQuestion.QUESTION_TYPE_BOTH:
                validButtons = questionForBoth(flag);
                break;
        }

        setOnButtonListener();
    }
    public void pause(){
        setOffButtonListener();


    }
    public void resume(){

        for(int i=0;i<buttons.size();i++){

            buttons.get(i).setClickable(true);

        }

    }
    public void stop(){
        for(int i=0;i<buttons.size();i++){
            buttons.get(i).setImageResource(R.drawable.unit55);
        }
        setOffButtonListener();


    }



    private ArrayList<Integer> questionForColor(String flag){
        int numOfValid = singleModeQuestion.getNumOfValid();
        validIndex = new ArrayList<>();
        int validColor;
        Random random = new Random();
        int num=0;
        validColor = singleModeQuestion.getValidColor();
        while(num<numOfValid){
            int indexTmp = random.nextInt(buttons.size());
            if(!validIndex.contains(indexTmp)){
                validIndex.add(indexTmp);
                num++;
            }
        }
        if(flag.equals("ColorMonster")){
            for(int i=0;i<validIndex.size();i++){
                buttons.get(validIndex.get(i)).setImageResource(images.get(validColor+""+random.nextInt(5)));
            }
        }else{
            for(int i=0;i<validIndex.size();i++){
                buttons.get(validIndex.get(i)).setImageResource(images.get("#"+validColor+""+random.nextInt(5)));
            }
        }


        //set image src for other buttons
        for(int i=0;i<25;i++){
            if(!validIndex.contains(i)){
                int isBlank = random.nextInt(2);//0:non blank 1:blank
                int colorTmp = random.nextInt(5);
                int textTmp = random.nextInt(5);
                if(isBlank==1){
                    colorTmp = 5;
                    textTmp = 5;
                }else {
                    while(colorTmp == validColor){
                        colorTmp = random.nextInt(5);
                    }
                }
                if(flag.equals("ColorMonster")){
                    buttons.get(i).setImageResource(images.get(colorTmp+""+textTmp));
                }else{
                    buttons.get(i).setImageResource(images.get("#"+colorTmp+""+textTmp));
                }

            }
        }
        view_taskType.setText("颜色");
        view_taskContent.setText(BattleModeActivity.colors[validColor]);
        return validIndex;

    }

    private ArrayList<Integer> questionForText(String flag){
        int numOfValid = singleModeQuestion.getNumOfValid();
        validIndex = new ArrayList<>();
        int validColor;
        Random random = new Random();
        int num=0;
        validColor = singleModeQuestion.getValidColor();
        while(num<numOfValid){
            int indexTmp = random.nextInt(buttons.size());
            if(!validIndex.contains(indexTmp)){
                validIndex.add(indexTmp);
                num++;
            }
        }
        if(flag.equals("ColorMonster")){
            for(int i=0;i<validIndex.size();i++){
                buttons.get(validIndex.get(i)).setImageResource(images.get(random.nextInt(5)+""+validColor));
            }
        }else{
            for(int i=0;i<validIndex.size();i++){
                buttons.get(validIndex.get(i)).setImageResource(images.get("#"+random.nextInt(5)+""+validColor));
            }
        }


        for(int i=0;i<25;i++){
            if(!validIndex.contains(i)){
                int isBlank = random.nextInt(2);//0:non blank 1:blank
                int colorTmp = random.nextInt(5);
                int textTmp = random.nextInt(5);
                if(isBlank==1){
                    colorTmp = 5;
                    textTmp = 5;
                }else {
                    while(textTmp == validColor){
                        textTmp = random.nextInt(5);
                    }
                }
                if(flag.equals("ColorMonster")){
                    buttons.get(i).setImageResource(images.get(colorTmp+""+textTmp));
                }else{
                    buttons.get(i).setImageResource(images.get("#"+colorTmp+""+textTmp));
                }

            }
        }
        view_taskType.setText("意思");
        view_taskContent.setText(BattleModeActivity.colors[validColor]);
        return validIndex;
    }

    private ArrayList<Integer> questionForBoth(String flag){
        int numOfValid = singleModeQuestion.getNumOfValid();
        validIndex = new ArrayList<>();
        int validColor;
        Random random = new Random();
        int num=0;
        validColor = singleModeQuestion.getValidColor();
        //generate random valid unit
        while(num<numOfValid){
            int indexTmp = random.nextInt(buttons.size());
            if(!validIndex.contains(indexTmp)){
                validIndex.add(indexTmp);
                num++;
            }
        }
        if(flag.equals("ColorMonster")){
            for(int i=0;i<validIndex.size();i++){
                buttons.get(validIndex.get(i)).setImageResource(images.get(validColor+""+validColor));
            }
        }else{
            for(int i=0;i<validIndex.size();i++){
                buttons.get(validIndex.get(i)).setImageResource(images.get("#"+validColor+""+validColor));
            }
        }


        for(int i=0;i<25;i++){
            if(!validIndex.contains(i)){
                int isBlank = random.nextInt(2);//0:non blank 1:blank
                int colorTmp = random.nextInt(5);
                int textTmp = random.nextInt(5);
                if(isBlank==1){
                    colorTmp = 5;
                    textTmp = 5;
                }else {
                    while((colorTmp == validColor) && (textTmp == validColor)){
                        colorTmp = random.nextInt(5);
                        textTmp = random.nextInt(5);
                    }
                }

                if(flag.equals("ColorMonster")){
                    buttons.get(i).setImageResource(images.get(colorTmp+""+textTmp));
                }else{
                    buttons.get(i).setImageResource(images.get("#"+colorTmp+""+textTmp));
                }

            }
        }
        view_taskType.setText("颜色和意思");
        view_taskContent.setText(BattleModeActivity.colors[validColor]);
        return validIndex;

    }

    private void setOnButtonListener(){
        for(int i=0;i<buttons.size();i++){

            buttons.get(i).setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("BattleMode", "                                    onclick id = "+v.getId());
                    player = MediaPlayer.create(context,R.raw.button_click);
                    player.setVolume(1100,1100);
                    player.start();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        }

                    });

                    int currentButton = v.getId();
                    for(ImageButton btn : buttons){
                        if(btn.getId() == currentButton){
                            btn.setImageResource(R.drawable.unit_false);
                            break;
                        }

                    }
                    int score = Integer.parseInt(view_total_score_this.getText().toString());
                    boolean isSuccess = false;
                    for (Integer validIndex : validButtons) {
                        int correctId = buttons.get(validIndex).getId();
                        if (currentButton == correctId) {
                            isSuccess = true;
                            break;
                        }
                    }
                    handleScore(isSuccess, score);
                    if(isSuccess)
                        start();

                    Log.e("BattleMode", "                                      onclick id = " + v.getId());
                }
            });

        }
    }

    public void handleScore(boolean isSuccess, int score){
        if (isSuccess) {
            score += 1;
            view_total_score_this.setText(score+"");
            view_total_score_that.setText(score+"");

            // Run +1 effect
            float curTranslationY = view_add.getTranslationY();
            ObjectAnimator addOneAnim = ObjectAnimator.ofFloat(view_add,"translationY",curTranslationY, -50f);
            addOneAnim.setDuration(500);
            addOneAnim.setRepeatMode(ValueAnimator.RESTART);
            addOneAnim.setRepeatCount(0);
            addOneAnim.setCurrentPlayTime(0);
            addOneAnim.setInterpolator(null);
            addOneAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view_add.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view_add.setVisibility(View.INVISIBLE);
                    view_add.setY(100);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            addOneAnim.start();

        } else {
            if (score == 0) {
            } else {
                score -= 1;
                view_total_score_this.setText(score+"");
                view_total_score_that.setText(score+"");

                // Run -1 effect
                float curTranslationY = view_minus.getTranslationY();
                ObjectAnimator minusOneAnim = ObjectAnimator.ofFloat(view_minus,"translationY", curTranslationY, 50f);
                minusOneAnim.setDuration(500);
                minusOneAnim.setRepeatMode(ValueAnimator.RESTART);
                minusOneAnim.setRepeatCount(0);
                minusOneAnim.setCurrentPlayTime(0);
                minusOneAnim.setInterpolator(null);
                minusOneAnim.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {view_minus.setVisibility(View.VISIBLE);}
                    @Override public void onAnimationEnd(Animator animation) {view_minus.setVisibility(View.INVISIBLE);view_minus.setY(100);}
                    @Override public void onAnimationCancel(Animator animation) { }
                    @Override public void onAnimationRepeat(Animator animation) { }
                });
                minusOneAnim.start();


            }

        }
    }

    public void setOffButtonListener(){

        for(int i=0;i<buttons.size();i++){

            buttons.get(i).setClickable(false);

        }
    }
}
