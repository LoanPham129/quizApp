package com.example.quizapp_main.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp_main.model.AppConfig;
import com.example.quizapp_main.R;
import com.example.quizapp_main.model.PrefHelper;
import com.example.quizapp_main.model.SoundManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
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

                Integer currentHighScore = snapshot.child("highScore").getValue(Integer.class);
                if (currentHighScore == null) currentHighScore = 0;

                // Cập nhật số tiền và số câu hỏi nếu mới lớn hơn
                if (newMoney > currentMoney) {
                    currentMoney = newMoney;
                }

                if (newQuestionNumber > currentQuestionNumber) {
                    currentQuestionNumber = newQuestionNumber;
                }

                // Cập nhật điểm số cao nhất nếu mới lớn hơn
                if (newMoney > currentHighScore) {
                    currentHighScore = newMoney;
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("money", currentMoney);
                updates.put("questionNumber", currentQuestionNumber);
                updates.put("highScore", currentHighScore);
                updates.put("lastUpdate", ServerValue.TIMESTAMP);

                userRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Đã cập nhật thành công"))
                        .addOnFailureListener(e -> Log.e("Firebase", "Lỗi khi lưu: " + e.getMessage()));

                // Gọi hàm để xác định hạng người chơi
                getPlayerRank(currentHighScore);

            } else {
                Log.e("Firebase", "Không tìm thấy user snapshot");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Lỗi khi lấy dữ liệu user: " + e.getMessage());
        });
    }

    private void getPlayerRank(int currentHighScore) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("money").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                int rank = 1;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Integer userMoney = userSnapshot.child("money").getValue(Integer.class);
                    if (userMoney == null) userMoney = 0;

                    if (userMoney > currentHighScore) {
                        rank++;
                    }
                }

                // Gọi hàm để hiển thị hạng người chơi
                showRankDialog(rank);

            } else {
                Log.e("Firebase", "Không tìm thấy dữ liệu người chơi");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Lỗi khi lấy danh sách người chơi: " + e.getMessage());
        });
    }
    private void showRankDialog(int rank) {
        // Kiểm tra nếu hạng người chơi từ 5 trở lên
        if (rank > 5) {
            return; // Không làm gì và thoát khỏi phương thức nếu hạng nhỏ hơn 5
        }

        String title, message, emoji;

        // Xử lý thông điệp dựa trên hạng
        if (rank == 1) {
            title = "🥇 Huyền thoại!";
            message = "Bạn đang đứng đầu bảng xếp hạng!";
            emoji = "✨";
        } else if (rank == 2) {
            title = "🥈 Xuất sắc!";
            message = "Bạn đang đứng thứ 2, chỉ còn một bước nữa!";
            emoji = "🔥";
        } else if (rank == 3) {
            title = "🥉 Tuyệt vời!";
            message = "Bạn nằm trong top 3 người chơi giỏi nhất!";
            emoji = "🏆";
        } else {
            title = "👍 Cố gắng hơn nữa!";
            message = "Bạn đạt hạng #" + rank + " trên bảng xếp hạng.";
            emoji = "🎯";
        }

        // Tạo và hiển thị dialog với các tùy chỉnh
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(emoji + " " + title)  // Thêm emoji vào tiêu đề
                .setMessage(message)            // Thêm thông điệp vào nội dung
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())  // Nút OK để đóng dialog
                .setCancelable(false)           // Ngừng người dùng thoát bằng cách nhấn ngoài
                .show();  // Hiển thị dialog
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
