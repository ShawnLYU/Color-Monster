package com.cityuytic.colormonster;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SingleModeRound extends AppCompatActivity {
    private SingleModeQuestion singleModeQuestion;
    private ArrayList<Integer> validButtons = new ArrayList<>();
    private ArrayList<Integer> validIndex = new ArrayList<>();
    private ArrayList<ImageButton> buttons = new ArrayList<>();
    private HashMap<String,Integer> images = new HashMap<>();

    private TextView view_taskType;
    private TextView view_taskContent;
    private TextView view_total_score;
    private ImageView view_xiaozhi;
    private Context context;
    private float distance=0f;
    private MediaPlayer player;

    private int state;

    public static int STATE_NORMAL = 0;
    public static int STATE_LOSE = 1;
    public static int STATE_PAUSE = 2;

    private boolean isSuccess;

    private boolean[] reactionTF;
    private int[] reactionType;
    private int reactionCnt = 0;




    public SingleModeRound(ArrayList<ImageButton> buttons, HashMap<String,Integer> images, TextView view_taskType,
                           TextView view_taskContent, TextView view_total_score, ImageView view_xiaozhi,Context context){
        this.buttons = buttons;
        this.images = images;
        this.view_taskType = view_taskType;
        this.view_taskContent = view_taskContent;
        this.view_total_score = view_total_score;
        this.view_xiaozhi = view_xiaozhi;
        this.context= context;
        state = STATE_NORMAL;

        reactionType=new int[10000];
        reactionTF=new boolean[10000];
    }

    public void start(){

        singleModeQuestion = new SingleModeQuestion();
        reactionType[reactionCnt] = singleModeQuestion.getQuestionType();

        switch (singleModeQuestion.getQuestionType()){
            case SingleModeQuestion.QUESTION_TYPE_COLOR:
                validButtons = questionForColor();
                break;
            case SingleModeQuestion.QUESTION_TYPE_TEXT:
                validButtons = questionForText();
                break;
            case SingleModeQuestion.QUESTION_TYPE_BOTH:
                validButtons = questionForBoth();
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

    private ArrayList<Integer> questionForColor(){
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

        for(int i=0;i<validIndex.size();i++){
            buttons.get(validIndex.get(i)).setImageResource(images.get(validColor+""+random.nextInt(5)));
        }

        //set image src for other buttons
        for(int i=0;i<25;i++){
            if(!validIndex.contains(i)){
                int isBlank = random.nextInt(2);//0:non unit55 1:unit55
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

                buttons.get(i).setImageResource(images.get(colorTmp+""+textTmp));
            }
        }
        view_taskType.setText("顏色");
        view_taskContent.setText(SingleModeActivity.colors[validColor]);
        return validIndex;

    }

    private ArrayList<Integer> questionForText(){
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

        for(int i=0;i<validIndex.size();i++){
            buttons.get(validIndex.get(i)).setImageResource(images.get(random.nextInt(5)+""+validColor));
        }
        for(int i=0;i<25;i++){
            if(!validIndex.contains(i)){
                int isBlank = random.nextInt(2);//0:non unit55 1:unit55
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

                buttons.get(i).setImageResource(images.get(colorTmp+""+textTmp));
            }
        }
        view_taskType.setText("意思");
        view_taskContent.setText(SingleModeActivity.colors[validColor]);
        return validIndex;
    }

    private ArrayList<Integer> questionForBoth(){
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

        for(int i=0;i<validIndex.size();i++){
            buttons.get(validIndex.get(i)).setImageResource(images.get(validColor+""+validColor));
        }
        for(int i=0;i<25;i++){
            if(!validIndex.contains(i)){
                int isBlank = random.nextInt(2);//0:non unit55 1:unit55
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


                buttons.get(i).setImageResource(images.get(colorTmp+""+textTmp));
            }
        }
        view_taskType.setText("顏色和意思");
        view_taskContent.setText(SingleModeActivity.colors[validColor]);
        return validIndex;

    }

    private void setOnButtonListener(){
        for(int i=0;i<buttons.size();i++){

            buttons.get(i).setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {


                    player = MediaPlayer.create(context, R.raw.button_click);
                    player.setVolume(1100,1100);
                    player.start();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        }

                    });

                    int currentButton = v.getId();
                    for(ImageButton btn : buttons){
                        btn.setClickable(false);
                    }
                    int score = Integer.parseInt(view_total_score.getText().toString());
                    isSuccess = false;
                    for (Integer validIndex : validButtons) {
                        int correctId = buttons.get(validIndex).getId();
                        if (currentButton == correctId) {
                            isSuccess = true;
                            break;
                        }
                    }
                    handleScore(isSuccess, score);
                    start();


                    reactionTF[reactionCnt] = isSuccess;
                    reactionCnt += 1;
                }
            });

        }
    }

    public void handleScore(boolean isSuccess, int score){
        if (isSuccess) {
            score += 1;
            view_total_score.setText(score+"");

            distance -= 20f;
            float curTranslationY = view_xiaozhi.getTranslationY();
            ObjectAnimator animator = ObjectAnimator.ofFloat(view_xiaozhi, "translationY", curTranslationY, distance);
            animator.setDuration(500);
            animator.start();

        } else {
            if (score == 0) {
            } else {
                score -= 1;
                view_total_score.setText(score+"");
            }

        }
    }

    public void setOffButtonListener(){

        for(int i=0;i<buttons.size();i++){

            buttons.get(i).setClickable(false);

        }
    }

    public int[] getAllReactionType()
    {
        return reactionType;
    }
    public boolean[] getAllReactionTF()
    {
        return reactionTF;
    }
    public int getReactionCnt()
    {
        return reactionCnt;
    }




}
