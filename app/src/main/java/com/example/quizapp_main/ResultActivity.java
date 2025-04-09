package com.example.quizapp_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp_main.R;
import com.google.android.material.card.MaterialCardView;

public class ResultActivity extends AppCompatActivity {

    MaterialCardView home;
    TextView correct, wrong, resultInfo, resultscore;
    ImageView resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        home = findViewById(R.id.returnHome);
        correct = findViewById(R.id.correctScore);
        wrong = findViewById(R.id.wrongScore);
        resultInfo = findViewById(R.id.resultInfo);
        resultscore = findViewById(R.id.resultScore);
        resultImage = findViewById(R.id.resultImage);

        int Correct = getIntent().getIntExtra("correct", 0);
        int Wrong = getIntent().getIntExtra("wrong", 0);
        int Score = Correct * 10;

        correct.setText(String.valueOf(Correct)); // Correct number of answers
        wrong.setText(String.valueOf(Wrong)); // Wrong number of answers
        resultscore.setText(String.valueOf(Score));

        if (Correct >= 0 && Correct <=2){
            resultInfo.setText("You have to take the test again");
            resultImage.setImageResource(R.drawable.ic_sad);
        } else if (Correct >= 3 && Correct <=5) {
            resultInfo.setText("You have to try a little more");
            resultImage.setImageResource(R.drawable.ic_neutral);
        }else if (Correct >= 6 && Correct <=8) {
            resultInfo.setText("You are pretty good");
            resultImage.setImageResource(R.drawable.ic_smile);
        }else{
            resultInfo.setText("You are very good congratulations");
            resultImage.setImageResource(R.drawable.ic_smile);
        }

        home.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
                finish();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public  void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(ResultActivity.this, MainActivity.class));
        finish();
    }
}