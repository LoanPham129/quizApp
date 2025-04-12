package com.example.quizapp_main.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp_main.model.AppConfig;
import com.example.quizapp_main.R;
import com.example.quizapp_main.model.PrefHelper;
import com.example.quizapp_main.model.SoundManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    SoundManager soundManager;
    MaterialCardView home, playAgain;
    TextView resultMoney;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        soundManager = new SoundManager();
        if (AppConfig.isVolumeOn) {
            soundManager.play(this, R.raw.end, false);
        }

        EdgeToEdge.enable(this);

        resultMoney = findViewById(R.id.resultMoney);
        int totalMoney = getIntent().getIntExtra("totalMoney", 0);
        int totalQuestion = getIntent().getIntExtra("totalQuestion", 0);

        PrefHelper.updateMoney(this, totalMoney);
        PrefHelper.updateQuestionNumber(this, totalQuestion);

        // Gọi hàm saveToFirebase để cập nhật số tiền và số câu trả lời đúng vào Firebase
        saveToFirebase(totalMoney, totalQuestion);

        home = findViewById(R.id.returnHome);
        playAgain = findViewById(R.id.playAgain);

        home.setOnClickListener(v -> {
            soundManager.stop();
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
            finish();
        });

        playAgain.setOnClickListener(v -> {
            soundManager.stop();
            startActivity(new Intent(ResultActivity.this, BasicQuiz.class));  // Assuming QuizActivity is your quiz activity
            finish();
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
    private void saveToFirebase(int newMoney, int newQuestionNumber) {
        String userId = PrefHelper.getUserId(this);
        if (userId == null) {
            Log.e("Firebase", "Chưa có userId");
            return;
        }

        DatabaseReference userRef = databaseRef.child(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Integer currentMoney = snapshot.child("money").getValue(Integer.class);
                if (currentMoney == null) currentMoney = 0;

                Integer currentQuestionNumber = snapshot.child("questionNumber").getValue(Integer.class);
                if (currentQuestionNumber == null) currentQuestionNumber = 0;

                Map<String, Object> updates = new HashMap<>();

                // Cập nhật số tiền nếu mới lớn hơn
                if (newMoney > currentMoney) {
                    updates.put("money", newMoney);
                }

                // Cập nhật số câu hỏi nếu mới lớn hơn
                if (newQuestionNumber > currentQuestionNumber) {
                    updates.put("questionNumber", newQuestionNumber);
                }

                updates.put("lastUpdate", ServerValue.TIMESTAMP);  // Thêm thời gian cập nhật

                // Nếu có bất kỳ giá trị nào thay đổi, thực hiện cập nhật
                if (!updates.isEmpty()) {
                    userRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Đã cập nhật thành công"))
                            .addOnFailureListener(e -> Log.e("Firebase", "Lỗi khi lưu: " + e.getMessage()));
                } else {
                    Log.d("Firebase", "Không thay đổi gì.");
                }

            } else {
                Log.e("Firebase", "Không tìm thấy user snapshot");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Lỗi khi lấy dữ liệu user: " + e.getMessage());
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
