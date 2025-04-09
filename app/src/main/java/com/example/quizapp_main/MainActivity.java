package com.example.quizapp_main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    MaterialCardView easyCard, exitCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        easyCard = findViewById(R.id.easyCard);
        exitCard = findViewById(R.id.exitCard);

        // Nút "Chơi" → mở quiz
        easyCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BasicQuiz.class));
            finish();
        });

        // Nút "Thoát" → hiện hộp thoại xác nhận
        exitCard.setOnClickListener(v -> {
            showExitDialog();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Bạn có chắc chắn muốn thoát khỏi trò chơi?");
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Thoát", (dialog, which) -> finishAffinity());
        builder.show();
    }
}
