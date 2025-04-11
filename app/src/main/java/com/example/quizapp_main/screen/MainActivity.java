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
    MaterialCardView easyCard, exitCard;
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

        TextView helpText = findViewById(R.id.helpText);

        helpText.setOnClickListener(v -> {
            TextView guideText = new TextView(MainActivity.this);
            guideText.setText(getString(R.string.guide_text));
            guideText.setPadding(48, 32, 48, 0);
            guideText.setTextSize(16);
            guideText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

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
