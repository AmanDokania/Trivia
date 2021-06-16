package com.example.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    public ArrayList<Question> questionBankArrayList=new ArrayList<>();
  String url= "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";


  public List<Question> getQuestion(final AnswerListAsyncResponse callBack)
  {
      JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, (JSONArray) null, new Response.Listener<JSONArray>() {
          @Override
          public void onResponse(JSONArray response) {
              for(int i=0;i<response.length();i++)
              {
                  try {
                      Question question=new Question();
                      question.setAnswer(response.getJSONArray(i).getString(0));
                      question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));
                      // add question object to list
                      questionBankArrayList.add(question);

                    //  Log.d("Question","  "+question);
                   //   Log.d("json",response.getJSONArray(i).getString(0));
                     // Log.d("json",response.getJSONArray(i).getString(1));
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }
              if(callBack!=null) callBack.processfinished(questionBankArrayList);
             // Log.d("Json stuff","onresponse "+response);
          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

          }
      });


      AppController.getInstance().addToRequestQueue(jsonArrayRequest);
      Log.d("Hello", "getQuestion: "+questionBankArrayList);
      return questionBankArrayList;
  }

}
