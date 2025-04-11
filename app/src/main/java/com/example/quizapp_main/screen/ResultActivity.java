package com.example.quizapp_main.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp_main.model.AppConfig;
import com.example.quizapp_main.R;
import com.example.quizapp_main.model.PrefHelper;
import com.example.quizapp_main.model.SoundManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    SoundManager soundManager;
    MaterialCardView home, playAgain;
    TextView resultInfo, resultMoney;
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
//        số tiền
        resultMoney = findViewById(R.id.resultMoney);
        int totalMoney = getIntent().getIntExtra("totalMoney", 0);
        PrefHelper.updateMoney(this, totalMoney);

        saveToFirebase(totalMoney);

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
    private void saveToFirebase(int newMoney) {
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

                if (newMoney > currentMoney) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("money", newMoney);
                    updates.put("lastUpdate", ServerValue.TIMESTAMP);

                    userRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Đã cập nhật tiền mới: " + newMoney))
                            .addOnFailureListener(e -> Log.e("Firebase", "Lỗi khi lưu tiền mới: " + e.getMessage()));
                } else {
                    Log.d("Firebase", "Không lưu: tiền mới thấp hơn hoặc bằng hiện tại (" + currentMoney + ")");
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
