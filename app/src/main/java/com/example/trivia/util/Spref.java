package com.example.trivia.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Spref {
    private static final String MESSAGE_ID = "message_pref";
   private SharedPreferences sharedPreferences;

    public Spref(SharedPreferences sharedPreferences) {
        this.sharedPreferences=sharedPreferences;
    }

    public  void saveHighScore(int score)
    {
        int currscore=score;
        int lastscore=sharedPreferences.getInt("high_score",0);
        if(currscore>lastscore)
        {
          //  we have a new score and save it
            sharedPreferences.edit().putInt("high_score",currscore).apply();
        }
    }
    public  int getHighScore()
    {
        return  sharedPreferences.getInt("high_score",0);
    }

    public void saveState(int index)
    {
        sharedPreferences.edit().putInt("curr_state",index).apply();
    }

    public int getState()
    {
        return sharedPreferences.getInt("curr_state",0);
    }
}
