package com.example.quizapp_main.screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.quizapp_main.model.AppConfig;
import com.example.quizapp_main.R;
import com.example.quizapp_main.model.PrefHelper;
import com.example.quizapp_main.model.SoundManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView userGreeting;
    ImageView userIcon;
    DatabaseReference dbRef;
    ImageView volumeToggle;
    MaterialCardView easyCard, exitCard;
    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userIcon = findViewById(R.id.userIcon);

        EdgeToEdge.enable(this);
        userGreeting = findViewById(R.id.userGreeting);

        if (PrefHelper.isLoggedIn(this)) {
            String username = PrefHelper.getUsername(this);
            userGreeting.setText("Hi " + username);
            userGreeting.setVisibility(View.VISIBLE);
            userIcon.setVisibility(View.GONE);
//            đăng xuất
            userGreeting.setOnClickListener(v -> showLogoutDialog());

        }
        soundManager = new SoundManager();
        if (AppConfig.isVolumeOn) {
            soundManager.play(this, R.raw.manhinhchinh, false);
        }
        easyCard = findViewById(R.id.easyCard);
        exitCard = findViewById(R.id.exitCard);

        volumeToggle = findViewById(R.id.volumeToggle);
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        userIcon.setOnClickListener(v -> showNameInputDialog());

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

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    PrefHelper.logout(MainActivity.this);
                    userGreeting.setVisibility(View.GONE);
                    userIcon.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Không", null)
                .show();
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

    private void showNameInputDialog() {
        // Tạo layout custom mỗi lần nhấn
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(64, 32, 64, 0);

        final EditText input = new EditText(MainActivity.this);
        input.setHint("Nhập tên của bạn");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setBackgroundResource(R.drawable.edit_text_background);
        input.setPadding(32, 24, 32, 24);
        layout.addView(input);

        // Tạo dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle("Chào bạn!")
                .setMessage("Hãy nhập tên để bắt đầu hành trình nhé ✨")
                .setView(layout)
                .setPositiveButton("OK", null)
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(view -> {
                String name = input.getText().toString().trim();
                if (name.isEmpty()) {
                    input.setError("Tên không được để trống");
                    return;
                }

                // Query chính xác theo trường 'name' thay vì orderByValue()
                Query nameQuery = dbRef.orderByChild("name_lowercase").equalTo(name.trim().toLowerCase());

                nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Tìm user có tên trùng khớp chính xác (không phân biệt hoa thường)
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userName = userSnapshot.child("name").getValue(String.class);
                                if (userName != null && userName.equalsIgnoreCase(name)) {
                                    String existingUserId = userSnapshot.getKey();
                                    Integer money = userSnapshot.child("money").getValue(Integer.class);

                                    // Lưu thông tin người dùng
                                    PrefHelper.saveUserData(MainActivity.this, existingUserId, name, money != null ? money : 0);

                                    updateUserUI(name);
                                    dialog.dismiss();
                                    showWelcomeDialog("Chào mừng trở lại ✨");
                                    return;
                                }
                            }
                        }

                        // Nếu không tìm thấy user trùng khớp chính xác -> tạo mới
                        createNewUser(name, dialog);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Lỗi khi kiểm tra tên", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
        dialog.show();
    }

    private void createNewUser(String name, AlertDialog dialog) {
        String userId = dbRef.push().getKey();
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("money", 0);
        userData.put("createdAt", ServerValue.TIMESTAMP);
        userData.put("name_lowercase", name.toLowerCase()); // Thêm trường phụ để search không phân biệt hoa thường

        dbRef.child(userId).updateChildren(userData)
                .addOnSuccessListener(aVoid -> {
                    PrefHelper.saveUserData(MainActivity.this, userId, name, 0);
                    updateUserUI(name);
                    dialog.dismiss();
                    showWelcomeDialog("Xin chào " + name + "! Chúc bạn có trải nghiệm tuyệt vời ✨");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Lỗi khi tạo tài khoản", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserUI(String username) {
        userGreeting.setText("Hi " + username);
        userGreeting.setVisibility(View.VISIBLE);
        userIcon.setVisibility(View.GONE);
    }

    private void showWelcomeDialog(String message) {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle("Chào mừng!")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
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
