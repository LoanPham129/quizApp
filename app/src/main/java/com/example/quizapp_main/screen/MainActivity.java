package com.example.quizapp_main.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp_main.model.AppConfig;
import com.example.quizapp_main.R;
import com.example.quizapp_main.model.SoundManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
public class MainActivity extends AppCompatActivity {
    ImageView volumeToggle;
    MaterialCardView easyCard, exitCard, helpCard;
    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        soundManager = new SoundManager();
        if (AppConfig.isVolumeOn) {
            soundManager.play(this, R.raw.manhinhchinh, false);
        }
        easyCard = findViewById(R.id.easyCard);
        exitCard = findViewById(R.id.exitCard);
        helpCard = findViewById(R.id.helpCard);

        volumeToggle = findViewById(R.id.volumeToggle);

        volumeToggle.setOnClickListener(v -> {
            AppConfig.isVolumeOn = !AppConfig.isVolumeOn;

            if (AppConfig.isVolumeOn) {
                soundManager.play(MainActivity.this, R.raw.manhinhchinh, true);
                volumeToggle.setImageResource(R.drawable.volume_on);
            } else {
                soundManager.stop();
                volumeToggle.setImageResource(R.drawable.volume_off);
            }
        });

        easyCard.setOnClickListener(v -> {
            soundManager.stop(); // Dừng nhạc
            startActivity(new Intent(MainActivity.this, BasicQuiz.class));
            finish();
        });

        exitCard.setOnClickListener(v -> {
            showExitDialog();
        });

        helpCard.setOnClickListener(v -> {
            TextView guideText = new TextView(MainActivity.this);
            guideText.setText(
                    "MỤC TIÊU\n" +
                            "Bạn cần trả lời đúng 15 câu hỏi để giành chiến thắng và nhận phần thưởng tối đa.\n\n" +

                            "CÁCH CHƠI\n" +
                            "- Mỗi câu hỏi có 4 đáp án A, B, C, D, chọn 1 đáp án. \n- Nếu TRẢ LỜI ĐÚNG, bạn sẽ nhận phần thưởng và đến câu tiếp theo. \n- Nếu TRẢ LỜI SAI, bạn sẽ nhận số tiền tương ứng với mốc an toàn gần nhất đã đạt được.\n\n" +

                            "CÁC MỐC AN TOÀN\n" +
                            "- Câu 5: 2.000.000 VNĐ\n" +
                            "- Câu 10: 22.000.000 VNĐ\n" +
                            "- Câu 15: 150.000.000 VNĐ\n\n" +

                            "TRỢ GIÚP (chỉ dùng 1 lần):\n" +
                            "- 50:50: Loại bỏ 2 đáp án sai\n" +
                            "- Hỏi khán giả: Hiển thị tỷ lệ chọn của khán giả\n" +
                            "- Gọi điện: Nhận gợi ý từ người thân\n\n" +

                            "BẠN CÓ THỂ BỎ CUỘC bất kỳ lúc nào để giữ số tiền hiện tại."
            );
            guideText.setPadding(48, 32, 48, 0);
            guideText.setTextSize(16);
            guideText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // 👈 Căn trái

            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Hướng dẫn chơi")
                    .setView(guideText)
                    .setPositiveButton("OK", null)
                    .show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog(); // gọi hàm hiển thị dialog xác nhận
            }
        });
    }
    private void showExitDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Bạn có chắc chắn muốn thoát khỏi trò chơi?");
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Thoát", (dialog, which) -> {
            soundManager.stop();
            finishAffinity();
        });
        builder.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.stop();
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

        if (volumeToggle != null) {
            if (AppConfig.isVolumeOn) {
                volumeToggle.setImageResource(R.drawable.volume_on);
            } else {
                volumeToggle.setImageResource(R.drawable.volume_off);
            }
        }

        if (soundManager != null && AppConfig.isVolumeOn && !soundManager.isPlaying()) {
            soundManager.play(this, R.raw.manhinhchinh, true);
        }
    }
}
