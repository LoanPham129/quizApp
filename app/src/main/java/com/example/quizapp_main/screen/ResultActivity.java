package com.example.quizapp_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp_main.R;
import com.google.android.material.card.MaterialCardView;

public class ResultActivity extends AppCompatActivity {
    SoundManager soundManager;
    MaterialCardView home, playAgain;  // Added playAgain button
    TextView resultInfo;

    TextView resultMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        soundManager = new SoundManager();
        if (AppConfig.isVolumeOn) {
            soundManager.play(this, R.raw.end, false);
        }

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


        // Home button - go to main screen
        home.setOnClickListener(v -> {
            soundManager.stop();
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
            finish();
        });

        // Play Again button - restart the quiz
        playAgain.setOnClickListener(v -> {
            soundManager.stop();
            startActivity(new Intent(ResultActivity.this, BasicQuiz.class));  // Assuming QuizActivity is your quiz activity
            finish();  // Close the current activity
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        resultMoney.setText("Số tiền bạn đã thắng: " + formatMoney(totalMoney));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
                finish();
            }
        });

    }
    private String formatMoney(int money) {
        return String.format("%,d VND", money);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundManager != null) {
            soundManager.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (soundManager != null && AppConfig.isVolumeOn && !soundManager.isPlaying()) {
            soundManager.play(this, R.raw.end, true);
        }
    }

}
