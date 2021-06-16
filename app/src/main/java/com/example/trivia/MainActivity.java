package com.example.trivia;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Spref;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private TextView questionTextView;
private TextView counterTextView;
private Button trueButton;
private Button falseButton;
private Button sharebutton;
private ImageButton nextButton;
private ImageButton preButton;
private TextView scoreview;
private TextView total;
private Score score;
int currIndex=0;
int currscore=0;
private List<Question>questionList;
private Spref prefs;
private SoundPool soundPool;
int sound1,sound2;
    private static final String MESSAGE_ID = "message_pref";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         score=new Score();
        final SharedPreferences sharedPreferences = getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
          prefs =new Spref(sharedPreferences);

        Log.d("Second", "onCreate: "+prefs.getHighScore());

         scoreview=findViewById(R.id.score_text);
        questionTextView=findViewById(R.id.question_text);
        counterTextView=findViewById(R.id.counter_button);
        trueButton=findViewById(R.id.true_button);
        falseButton=findViewById(R.id.false_button);
        nextButton=findViewById(R.id.next_button);
        preButton=findViewById(R.id.pre_button);
        sharebutton=findViewById(R.id.sharebutton);
        total=findViewById(R.id.total_score);


        AudioAttributes audioAttributes =new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();
        sound1 = soundPool.load(this,R.raw.complete,1);
        sound2 = soundPool.load(this,R.raw.correct,1);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        preButton.setOnClickListener(this);
        sharebutton.setOnClickListener(this);
        questionList = new QuestionBank().getQuestion(new AnswerListAsyncResponse() {
            @Override
            public void processfinished(ArrayList<Question> questionArrayList) {
                Log.d("Onclick","  "+questionArrayList);
                currIndex=prefs.getState();
                questionTextView.setText(questionArrayList.get(currIndex).getAnswer());
                counterTextView.setText(currIndex+1+" / "+questionArrayList.size());
                scoreview.setText("Current Score "+score.getScore());
                total.setText("Highest Score "+prefs.getHighScore());

            }
        });
       // Log.d("Onclick","  "+questionList.size());

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.true_button :
            checkAnswer(true);
            //updateQuestion();
            break;

            case R.id.false_button :
                       checkAnswer(false);
                   // updateQuestion();
                break;

            case R.id.next_button:

                //  currIndex=(currIndex+1)%questionList.size();
               // Log.d("Onclick","  "+questionList.size());
             //   Log.d("Onclick","  "+questionList);
             // updateQuestion();
                gonext();
                break;

            case  R.id.pre_button :
                if(currIndex==0)
                    Toast.makeText(MainActivity.this,"Question is not available",Toast.LENGTH_SHORT).show();
                else {
                    currIndex = (currIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;

            case R.id.sharebutton :
                shareScore();
                break;
    }}
public void updateQuestion()
{
    String question=questionList.get(currIndex).getAnswer();
    questionTextView.setText(question);
    counterTextView.setText(currIndex+1+" / "+questionList.size());

}

    public void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerIsTrue) {
            addpoint();
            fadeview();
            toastMessageId = R.string.correct_answer;
           soundPool.play(sound2,1,1,0,0,1);

        } else {
            detectpoint();
            shakeAnimation();
            toastMessageId = R.string.wrong_answer;
            soundPool.play(sound1,1,1,0,0,1);
        }
        Toast.makeText(MainActivity.this, toastMessageId,
                Toast.LENGTH_SHORT)
                .show();
    }

    public void addpoint()
    {
        currscore+=100;
        score.setScore(currscore);
        scoreview.setText("Current Score "+score.getScore());
        Log.d("score","addpoint "+score.getScore());
    }
    public void detectpoint()
    {
        currscore-=100;
        if(currscore>0) {
            score.setScore(currscore);
            scoreview.setText("Current Score "+score.getScore());
            Log.d("score", "addpoint " + score.getScore());
        }
        else
        {
            currscore=0;
            score.setScore(currscore);
            scoreview.setText("Current Score "+score.getScore());
            Log.d("score", "detectpoint " + score.getScore());
        }
    }

private void  fadeview()
{
    final CardView card=findViewById(R.id.cardView);
    AlphaAnimation alphaAnimation=new AlphaAnimation(1.0f,0.0f);
    alphaAnimation.setDuration(200);
    alphaAnimation.setRepeatCount(2);
    alphaAnimation.setRepeatMode(Animation.REVERSE);
    card.setAnimation(alphaAnimation);
    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            card.setCardBackgroundColor(Color.GREEN);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
           card.setCardBackgroundColor(Color.WHITE);
           gonext();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
           card.setCardBackgroundColor(Color.RED);
        }
    });
}

    private void shakeAnimation()
    {
        Animation shake= AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView card=findViewById(R.id.cardView);
        card.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                card.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
              card.setCardBackgroundColor(Color.WHITE);
              gonext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
          card.setCardBackgroundColor(Color.RED);
            }
        });
    }

    /**
     * Dispatch onPause() to fragments.
     */

    public void gonext()
    {
        currIndex=(currIndex+1)%questionList.size();
        updateQuestion();
    }

    public void shareScore()
    {
        prefs.saveHighScore(score.getScore());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        int highscore=prefs.getHighScore();
        intent.putExtra(Intent.EXTRA_SUBJECT,"I am playing Trivia game");
        intent.putExtra(Intent.EXTRA_TEXT,"My Current Score is "+score.getScore()+"  and My highest score is "+highscore);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.saveState(currIndex);
        super.onPause();
    }

}