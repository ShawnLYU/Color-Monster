package com.cityuytic.colormonster;

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

/**
 * Created by user on 2015/10/18.
 */
public class BluetoothModeRound extends AppCompatActivity {
    private BluetoothModeQuestion bluetoothModeQuestion;
    private ArrayList<Integer> validButtons = new ArrayList<>();
    private ArrayList<Integer> validIndex = new ArrayList<>();
    private ArrayList<ImageButton> buttons = new ArrayList<>();
    private HashMap<String,Integer> images = new HashMap<>();

    private TextView view_taskType;
    private TextView view_taskContent;
    private TextView view_total_score;
    private Context context;
    private MediaPlayer player;

    private int state;

    public static int STATE_NORMAL = 0;
    public static int STATE_LOSE = 1;
    public static int STATE_PAUSE = 2;




    public BluetoothModeRound(ArrayList<ImageButton> buttons, HashMap<String, Integer> images, TextView view_taskType,
                              TextView view_taskContent, TextView view_total_score, ImageView view_xiaozhi, Context context){
        this.buttons = buttons;
        this.images = images;
        this.view_taskType = view_taskType;
        this.view_taskContent = view_taskContent;
        this.view_total_score = view_total_score;
        this.context= context;
        state = STATE_NORMAL;
    }

    public void start(){

        bluetoothModeQuestion = new BluetoothModeQuestion();


        switch (bluetoothModeQuestion.getQuestionType()){
            case BluetoothModeQuestion.QUESTION_TYPE_COLOR:
                validButtons = questionForColor();
                break;
            case BluetoothModeQuestion.QUESTION_TYPE_TEXT:
                validButtons = questionForText();
                break;
            case BluetoothModeQuestion.QUESTION_TYPE_BOTH:
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
        int numOfValid = bluetoothModeQuestion.getNumOfValid();
        validIndex = new ArrayList<>();
        int validColor;
        Random random = new Random();
        int num=0;
        validColor = bluetoothModeQuestion.getValidColor();
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
        view_taskType.setText("颜色");
        view_taskContent.setText(BluetoothActivity.colors[validColor]+"");
        return validIndex;

    }

    private ArrayList<Integer> questionForText(){
        int numOfValid = bluetoothModeQuestion.getNumOfValid();
        validIndex = new ArrayList<>();
        int validColor;
        Random random = new Random();
        int num=0;
        validColor = bluetoothModeQuestion.getValidColor();
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
        view_taskContent.setText(BluetoothActivity.colors[validColor]+"");
        return validIndex;
    }

    private ArrayList<Integer> questionForBoth(){
        int numOfValid = bluetoothModeQuestion.getNumOfValid();
        validIndex = new ArrayList<>();
        int validColor;
        Random random = new Random();
        int num=0;
        validColor = bluetoothModeQuestion.getValidColor();
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
        view_taskType.setText("颜色和意思");
        view_taskContent.setText(BluetoothActivity.colors[validColor]+"");
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
                    boolean isSuccess = false;
                    for (Integer validIndex : validButtons) {
                        int correctId = buttons.get(validIndex).getId();
                        if (currentButton == correctId) {
                            isSuccess = true;
                            break;
                        }
                    }
                    handleScore(isSuccess, score);
                    start();
                }
            });

        }
    }

    public void handleScore(boolean isSuccess, int score){
        if (isSuccess) {
            score += 1;
            view_total_score.setText(score+"");


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

}
