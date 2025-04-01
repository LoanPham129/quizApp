package com.example.quizapp_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DifficultQuiz extends AppCompatActivity {

    TextView quiztext, Aans, Bans, Cans, Dans;
    List<QuestionItem> questionItems;
    int currentQuestion = 0;
    int correct = 0;
    int wrong = 0;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_basic_quiz);

        quiztext = findViewById(R.id.quizText);
        Aans = findViewById(R.id.Aanswer);
        Bans = findViewById(R.id.Banswer);
        Cans = findViewById(R.id.Canswer);
        Dans = findViewById(R.id.Danswer);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loadAllQuestion();
        Collections.shuffle(questionItems);
        setQuestionScreen(currentQuestion);

        Aans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer1().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Aans.setBackgroundResource(R.color.green);
                    Aans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Aans.setBackgroundResource(R.color.red);
                    Aans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion);
                            Aans.setBackgroundResource(R.color.white);
                            Aans.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }
                    },500);
                } else {
                    Intent intent = new Intent(DifficultQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Aans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer1().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Aans.setBackgroundResource(R.color.green);
                    Aans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Aans.setBackgroundResource(R.color.red);
                    Aans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion);
                            Aans.setBackgroundResource(R.color.white);
                            Aans.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }
                    },500);
                } else {
                    Intent intent = new Intent(DifficultQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Bans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer2().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Bans.setBackgroundResource(R.color.green);
                    Bans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Bans.setBackgroundResource(R.color.red);
                    Bans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion);
                            Bans.setBackgroundResource(R.color.white);
                            Bans.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }
                    },500);
                } else {
                    Intent intent = new Intent(DifficultQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Cans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer3().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Cans.setBackgroundResource(R.color.green);
                    Cans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Cans.setBackgroundResource(R.color.red);
                    Cans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion);
                            Cans.setBackgroundResource(R.color.white);
                            Cans.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }
                    },500);
                } else {
                    Intent intent = new Intent(DifficultQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Dans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer4().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Dans.setBackgroundResource(R.color.green);
                    Dans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Dans.setBackgroundResource(R.color.red);
                    Dans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion);
                            Dans.setBackgroundResource(R.color.white);
                            Dans.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }
                    },500);
                } else {
                    Intent intent = new Intent(DifficultQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    private void setQuestionScreen(int currentQuestion){
        quiztext.setText(questionItems.get(currentQuestion).getQuestion());
        Aans.setText(questionItems.get(currentQuestion).getAnswer1());
        Bans.setText(questionItems.get(currentQuestion).getAnswer2());
        Cans.setText(questionItems.get(currentQuestion).getAnswer3());
        Dans.setText(questionItems.get(currentQuestion).getAnswer4());
    }

    private void loadAllQuestion(){
        questionItems = new ArrayList<>();
        String jsonquiz = loadJsonFromAsset("difficultquestion.json");
        try {
            JSONObject jsonObject = new JSONObject(jsonquiz);
            JSONArray questions = jsonObject.getJSONArray("difficultquestion");
            for(int i = 0; i<questions.length(); i++){
                JSONObject question = questions.getJSONObject(i);

                String questionString = question.getString("question");
                String answer1String = question.getString("answer1");
                String answer2String = question.getString("answer2");
                String answer3String = question.getString("answer3");
                String answer4String = question.getString("answer4");
                String correctString = question.getString("correct");

                questionItems.add(new QuestionItem(questionString, answer1String, answer2String, answer3String,answer4String, correctString));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    private String loadJsonFromAsset(String s){
        String json = "";
        try {
            InputStream inputStream =  getAssets().open(s);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e){
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to exit the quiz?")
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                    super.onBackPressed(); // Gọi lại hành vi mặc định nếu cần
                })
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    startActivity(new Intent(DifficultQuiz.this, MainActivity.class));
                    finish();
                })
                .show();
    }
}