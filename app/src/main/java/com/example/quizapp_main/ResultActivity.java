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

    MaterialCardView home, playAgain;  // Added playAgain button
    TextView resultInfo;
    ImageView resultImage;
    TextView resultMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
//        số tiền
        resultMoney = findViewById(R.id.resultMoney);
        int totalMoney = getIntent().getIntExtra("totalMoney", 0);

        // Initialize views
        home = findViewById(R.id.returnHome);
        playAgain = findViewById(R.id.playAgain);  // New button
        resultInfo = findViewById(R.id.resultInfo);
        resultImage = findViewById(R.id.resultImage);

        // Get data from intent

        // Home button - go to main screen
        home.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
            finish();
        });

        // Play Again button - restart the quiz
        playAgain.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, BasicQuiz.class));  // Assuming QuizActivity is your quiz activity
            finish();  // Close the current activity
        });

        // Adjust layout for system bars (for edge-to-edge design)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        resultMoney.setText("Số tiền bạn đã thắng: " + formatMoney(totalMoney));

    }
    private String formatMoney(int money) {
        return String.format("%,d VND", money);  // Ví dụ: 200,000 VND
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ResultActivity.this, MainActivity.class));  // Go back to the main activity
        finish();
    }
}
