package com.cityuytic.colormonster;

import java.util.HashMap;

public class SingleModeLevel {
    final int FIRST_LEVEL = 1;
    final int SECOND_LEVEL= 2;
    final int THIRD_LEVEL= 3;
    final int FOURTH_LEVEL= 4;

    private int currentLevel;
    private HashMap<Integer,Integer> timeIntervals = new HashMap<>();//  level----time interval

    public SingleModeLevel(){
        currentLevel=1;
        timeIntervals.put(FIRST_LEVEL,8000);
        timeIntervals.put(SECOND_LEVEL,5000);
        timeIntervals.put(THIRD_LEVEL,3000);
        timeIntervals.put(FOURTH_LEVEL,2000);

    }

    public int getTimeInterval(){
        return timeIntervals.get(currentLevel);
    }

    public void handleLevel(int score){
        if(score!=0){
            switch (score%10){
                case 0:
                    if(score!=0)
                        upgradeLevel();
                    break;
            }
        }
    }

    public void upgradeLevel(){
        if(currentLevel!=FOURTH_LEVEL){
            currentLevel+=1;
        }
    }

}

