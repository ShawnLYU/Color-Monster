package com.cityuytic.colormonster;

import java.util.Random;

public class SingleModeQuestion {

    public static final int QUESTION_TYPE_COLOR = 0;
    public static final int QUESTION_TYPE_TEXT = 1;
    public static final int QUESTION_TYPE_BOTH = 2;
    public static final int MIN_NUM_OF_VALID = 1;
    public static final int MAX_NUM_OF_VALID = 3;

    private String[] colors = {"紅","黃","藍","綠","黑"};

    private int singleModeQuestionType;
    private int numOfValid;
    private int validColor;


    public SingleModeQuestion(){
        Random random = new Random();
        singleModeQuestionType = random.nextInt(3);
        numOfValid = MIN_NUM_OF_VALID + random.nextInt(MAX_NUM_OF_VALID - MIN_NUM_OF_VALID + 1);
        validColor = random.nextInt(colors.length);
    }

    public int getQuestionType() {
        return singleModeQuestionType;
    }

    public int getNumOfValid() {
        return numOfValid;
    }

    public int getValidColor() {
        return validColor;
    }

}