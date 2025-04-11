package com.example.quizapp_main.screen;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp_main.model.AppConfig;
import com.example.quizapp_main.model.PrefHelper;
import com.example.quizapp_main.model.QuestionItem;
import com.example.quizapp_main.R;
import com.example.quizapp_main.model.SoundManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.graphics.drawable.ColorDrawable;

public class BasicQuiz extends AppCompatActivity {

    TextView quiztext, Aans, Bans, Cans, Dans;
    SoundManager soundManager;
    List<QuestionItem> questionItems;
    int currentQuestion = 0;
    int totalMoney = 0;
    int totalQuestion = 0;

    int[][] rewards = {
            {15, 150000000},
            {14, 85000000},
            {13, 60000000},
            {12, 40000000},
            {11, 30000000},
            {10, 22000000},
            {9, 14000000},
            {8, 10000000},
            {7, 6000000},
            {6, 3000000},
            {5, 2000000},
            {4, 1000000},
            {3, 600000},
            {2, 400000},
            {1, 200000}
    };
    TextView questionNumberText, currentMoney;
    ProgressBar loadingSpinner;
    LinearLayout quizContainer, helpButtonsContainer;
    Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        soundManager = new SoundManager();
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_basic_quiz);
        SharedPreferences prefs = getSharedPreferences("quiz_prefs", MODE_PRIVATE);

        setupBackPressHandler();
        initViews();
        setupWindowInsets();
        setupQuitButton();
        setupAnswerClickListeners();
        setupLifelineButtons();

        loadAllQuestion();
        Collections.shuffle(questionItems);
    }
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new MaterialAlertDialogBuilder(BasicQuiz.this)
                        .setTitle(R.string.app_name)
                        .setMessage("Are you sure you want to exit the quiz?")
                        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            startActivity(new Intent(BasicQuiz.this, MainActivity.class));
                            finish();
                        })
                        .show();
            }
        });
    }
    private void setupAnswerClickListeners() {
        Aans.setOnClickListener(v -> handleAnswerClick(v, questionItems.get(currentQuestion).getAnswer1()));
        Bans.setOnClickListener(v -> handleAnswerClick(v, questionItems.get(currentQuestion).getAnswer2()));
        Cans.setOnClickListener(v -> handleAnswerClick(v, questionItems.get(currentQuestion).getAnswer3()));
        Dans.setOnClickListener(v -> handleAnswerClick(v, questionItems.get(currentQuestion).getAnswer4()));
    }
    private void handleAnswerClick(View v, String selectedAnswer) {
        setAnswerOptionsEnabled(false);
        setHelpButtonsEnabled(false);
        v.setBackgroundColor(Color.parseColor("#FFA500"));
        if (v instanceof TextView) {
            ((TextView) v).setTextColor(Color.BLACK);
        }

        // Delay 1s để hiển thị đáp án người chọn, sau đó xử lý tiếp
        new Handler().postDelayed(() -> {
            String correctAnswer = questionItems.get(currentQuestion).getCorrect();

            // Nếu đúng
            if (selectedAnswer.equals(correctAnswer)) {

                if (AppConfig.isVolumeOn) {
                    soundManager.play(this, R.raw.dung, false);
                }

                v.setBackgroundResource(R.color.green);

                int reward = getRewardForQuestion(currentQuestion);
                totalMoney = reward;
                totalQuestion++;
                TextView tvMoney = findViewById(R.id.currentMoney);
                tvMoney.setText(formatMoney(totalMoney));

                if (currentQuestion < questionItems.size() - 1) {
                    new Handler().postDelayed(() -> {
                        currentQuestion++;
                        int correctAnswers = currentQuestion + 1; // vì index bắt đầu từ 0
                        setQuestionScreen(currentQuestion);
                    }, 2000);
                } else {
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(BasicQuiz.this, ResultActivity.class);
                        intent.putExtra("totalMoney", totalMoney);
                        startActivity(intent);
                        finish();
                    }, 2000);
                }
            } else {
                if (AppConfig.isVolumeOn) {
                    soundManager.play(this, R.raw.sai, false);
                }
                v.setBackgroundResource(R.color.red); // Đáp án chọn sai

                // Cập nhật số tiền theo mốc an toàn đã vượt qua
                totalMoney = getSafeMoney(currentQuestion - 1);

                TextView tvMoney = findViewById(R.id.currentMoney);
                tvMoney.setText(formatMoney(totalMoney));

                // ✅ Lưu high score nếu cần (ví dụ bạn đang ở câu 4 → đã trả lời đúng 3 câu)
                int correctAnswers = currentQuestion; // Vì currentQuestion là index câu hiện tại (vừa trả lời sai)
                showCorrectAnswerAndFinish();
            }

        }, 3000);
    }
    private void showCorrectAnswerAndFinish() {
        // Tìm đáp án đúng và highlight màu xanh
        String correctAnswer = questionItems.get(currentQuestion).getCorrect();

        if (correctAnswer.equals(Aans.getText().toString())) {
            Aans.setBackgroundResource(R.color.green);
        } else if (correctAnswer.equals(Bans.getText().toString())) {
            Bans.setBackgroundResource(R.color.green);
        } else if (correctAnswer.equals(Cans.getText().toString())) {
            Cans.setBackgroundResource(R.color.green);
        } else {
            Dans.setBackgroundResource(R.color.green);
        }

        // Đợi 1 giây rồi chuyển sang màn hình kết quả
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(BasicQuiz.this, ResultActivity.class);
            intent.putExtra("totalMoney", totalMoney);
            intent.putExtra("totalQuestion", totalQuestion);
            startActivity(intent);
            finish();
        }, 3000);
    }
    private int getSafeMoney(int questionIndex) {
        if (questionIndex >= 14) return 150000000;
        else if (questionIndex >= 9) return 22000000;
        else if (questionIndex >= 4) return 2000000;
        else return 0;
    }
    private void loadAllQuestion() {
        loadingSpinner.setVisibility(View.VISIBLE);
        quizContainer.setVisibility(View.GONE);
        helpButtonsContainer.setVisibility(View.GONE);
        quitButton.setVisibility(View.GONE);
        currentMoney.setVisibility(View.GONE);

        questionItems = new ArrayList<>();

        List<QuestionLoadInfo> questionLoadList = Arrays.asList(
                new QuestionLoadInfo("easyquestion", 5),
                new QuestionLoadInfo("mediumquestion", 5),
                new QuestionLoadInfo("hardquestion", 3),
                new QuestionLoadInfo("superhardquestion", 2)
        );

        loadQuestionsSequentially(questionLoadList, 0);
    }
    private void initViews() {
        quiztext = findViewById(R.id.quizText);
        questionNumberText = findViewById(R.id.questionNumber);
        Aans = findViewById(R.id.Aanswer);
        Bans = findViewById(R.id.Banswer);
        Cans = findViewById(R.id.Canswer);
        Dans = findViewById(R.id.Danswer);

        loadingSpinner = findViewById(R.id.loading_spinner);
        quizContainer = findViewById(R.id.quiz_container);
        helpButtonsContainer = findViewById(R.id.help_buttons_container);
        quitButton = findViewById(R.id.btn_give_up);
        currentMoney = findViewById(R.id.currentMoney);
    }
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void setupQuitButton() {
        quitButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(BasicQuiz.this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn bỏ cuộc không?")
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        Intent intent = new Intent(BasicQuiz.this, ResultActivity.class);
                        intent.putExtra("totalMoney", totalMoney);
                        intent.putExtra("totalQuestion", totalQuestion);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        });

        currentMoney.setOnClickListener(v -> showRewardTable());
    }
    private void setupLifelineButtons() {
        // 50:50
        findViewById(R.id.help_5050).setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            v.setEnabled(false);

            String correct = questionItems.get(currentQuestion).getCorrect();

            List<String> answers = new ArrayList<>();
            List<TextView> answerViews = new ArrayList<>();

            answers.add(questionItems.get(currentQuestion).getAnswer1());
            answers.add(questionItems.get(currentQuestion).getAnswer2());
            answers.add(questionItems.get(currentQuestion).getAnswer3());
            answers.add(questionItems.get(currentQuestion).getAnswer4());

            answerViews.add(Aans);
            answerViews.add(Bans);
            answerViews.add(Cans);
            answerViews.add(Dans);

            List<Integer> wrongIndexes = new ArrayList<>();
            for (int i = 0; i < answers.size(); i++) {
                if (!answers.get(i).equals(correct)) {
                    wrongIndexes.add(i);
                }
            }

            Collections.shuffle(wrongIndexes);
            int remove1 = wrongIndexes.get(0);
            int remove2 = wrongIndexes.get(1);

            answerViews.get(remove1).setVisibility(View.INVISIBLE);
            answerViews.get(remove2).setVisibility(View.INVISIBLE);
        });

        // Audience
        findViewById(R.id.help_audience).setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            v.setEnabled(false);

            String correctAnswer = questionItems.get(currentQuestion).getCorrect();
            showAudienceDialog(correctAnswer);
        });

        // Call a Friend
        findViewById(R.id.help_call).setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            v.setEnabled(false);

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                String correctAnswer = questionItems.get(currentQuestion).getCorrect();
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Gợi ý từ người thân")
                        .setMessage("Người thân nghĩ đáp án đúng là: " + correctAnswer)
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }
    private void setQuestionScreen(int currentQuestion) {
        if (AppConfig.isVolumeOn) {
            soundManager.play(this, R.raw.question, false);
        }
        resetAnswerColors();
        setAnswerOptionsEnabled(true);
        setHelpButtonsEnabled(true);
        // Cập nhật số câu hỏi (cộng 1 vì index bắt đầu từ 0)
        questionNumberText.setText("Câu hỏi " + (currentQuestion + 1));

        quiztext.setText(questionItems.get(currentQuestion).getQuestion());
        Aans.setText(questionItems.get(currentQuestion).getAnswer1());
        Bans.setText(questionItems.get(currentQuestion).getAnswer2());
        Cans.setText(questionItems.get(currentQuestion).getAnswer3());
        Dans.setText(questionItems.get(currentQuestion).getAnswer4());

        // Đảm bảo tất cả đáp án đều hiện lại (nếu bị ẩn do 50:50)
        Aans.setVisibility(View.VISIBLE);
        Bans.setVisibility(View.VISIBLE);
        Cans.setVisibility(View.VISIBLE);
        Dans.setVisibility(View.VISIBLE);
    }
    private void resetAnswerColors() {
        Aans.setBackgroundResource(R.color.primary_color); // Thay bằng màu gốc của bạn
        Aans.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));

        Bans.setBackgroundResource(R.color.primary_color);
        Bans.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));

        Cans.setBackgroundResource(R.color.primary_color);
        Cans.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));

        Dans.setBackgroundResource(R.color.primary_color);
        Dans.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
    }
    private static class QuestionLoadInfo {
        String nodeName;
        int numberOfQuestions;

        QuestionLoadInfo(String nodeName, int numberOfQuestions) {
            this.nodeName = nodeName;
            this.numberOfQuestions = numberOfQuestions;
        }
    }
    private void loadQuestionsSequentially(List<QuestionLoadInfo> questionLoadList, int index) {
        if (index >= questionLoadList.size()) {
            setQuestionScreen(currentQuestion);

            loadingSpinner.setVisibility(View.GONE);
            quizContainer.setVisibility(View.VISIBLE);
            helpButtonsContainer.setVisibility(View.VISIBLE);
            quitButton.setVisibility(View.VISIBLE);
            currentMoney.setVisibility(View.VISIBLE);
            return;
        }

        QuestionLoadInfo info = questionLoadList.get(index);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(info.nodeName);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<QuestionItem> tempList = new ArrayList<>();
                for (DataSnapshot questionSnap : snapshot.getChildren()) {
                    QuestionItem item = questionSnap.getValue(QuestionItem.class);
                    tempList.add(item);
                }

                Collections.shuffle(tempList);
                questionItems.addAll(tempList.subList(0, Math.min(info.numberOfQuestions, tempList.size())));

                // Gọi tiếp mức độ tiếp theo
                loadQuestionsSequentially(questionLoadList, index + 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }
    private String formatMoney(int amount) {
        return String.format("%,d", amount) + " VND";
    }
    private void showAudienceDialog(String correctAnswer) {
        View dialogView = getLayoutInflater().inflate(R.layout.audience_poll_dialog, null);

        TextView percentA = dialogView.findViewById(R.id.percentA);
        TextView percentB = dialogView.findViewById(R.id.percentB);
        TextView percentC = dialogView.findViewById(R.id.percentC);
        TextView percentD = dialogView.findViewById(R.id.percentD);

        View barA = dialogView.findViewById(R.id.barA);
        View barB = dialogView.findViewById(R.id.barB);
        View barC = dialogView.findViewById(R.id.barC);
        View barD = dialogView.findViewById(R.id.barD);

        // Lấy index đáp án đúng
        int correctIndex = -1;
        if (correctAnswer.equals(questionItems.get(currentQuestion).getAnswer1())) correctIndex = 0;
        else if (correctAnswer.equals(questionItems.get(currentQuestion).getAnswer2())) correctIndex = 1;
        else if (correctAnswer.equals(questionItems.get(currentQuestion).getAnswer3())) correctIndex = 2;
        else correctIndex = 3;

        int[] votes = generateVotes(correctIndex);

        percentA.setText(votes[0] + "%");
        percentB.setText(votes[1] + "%");
        percentC.setText(votes[2] + "%");
        percentD.setText(votes[3] + "%");

        int maxHeight = 600; // dp
        barA.getLayoutParams().height = (votes[0] * maxHeight) / 100;
        barB.getLayoutParams().height = (votes[1] * maxHeight) / 100;
        barC.getLayoutParams().height = (votes[2] * maxHeight) / 100;
        barD.getLayoutParams().height = (votes[3] * maxHeight) / 100;

        barA.requestLayout();
        barB.requestLayout();
        barC.requestLayout();
        barD.requestLayout();

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .show();
    }
    private int[] generateVotes(int correctIndex) {
        int[] votes = new int[4];

        int correctVote = 50 + (int)(Math.random() * 31); // 50-80%
        int remain = 100 - correctVote;

        List<Integer> wrongIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != correctIndex) wrongIndices.add(i);
        }

        Collections.shuffle(wrongIndices);

        int vote1 = (int)(Math.random() * remain);
        int vote2 = (int)(Math.random() * (remain - vote1));
        int vote3 = remain - vote1 - vote2;

        votes[correctIndex] = correctVote;
        votes[wrongIndices.get(0)] = vote1;
        votes[wrongIndices.get(1)] = vote2;
        votes[wrongIndices.get(2)] = vote3;

        return votes;
    }

//  phần thưởng
    private void showRewardTable() {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.reward_table);
    TableLayout table = dialog.findViewById(R.id.reward_table);

    // Thêm tiêu đề
    TableRow headerRow = new TableRow(this);
    addTableCell(headerRow, "Câu hỏi", true);
    addTableCell(headerRow, "Tiền thưởng", true);
    table.addView(headerRow);

    for (int i = 0; i < rewards.length; i++) {
        TableRow row = new TableRow(this);

        int questionNumber = rewards[i][0];
        int displayIndex = rewards.length - i; // 15 → 1

        // ✅ Nếu là mốc hiện tại → tô đậm nền cam
        if (displayIndex == currentQuestion + 1) {
            row.setBackgroundColor(Color.parseColor("#FFA500")); // Màu cam
        }
        // ✅ Nếu là mốc 15 và đã đạt đến → tô xanh
        else if (questionNumber == 15) {
            row.setBackgroundColor(Color.parseColor("#32CD32")); // Màu xanh lá
        }
        // ✅ Nếu là mốc an toàn (5, 10, 15) → tô vàng
        else if (questionNumber == 5 || questionNumber == 10) {
            row.setBackgroundColor(Color.parseColor("#FFFACD")); // Màu vàng nhạt (LemonChiffon)
        }
        // Các dòng xen kẽ màu xám trắng
        else {
            row.setBackgroundColor(Color.WHITE);
        }

        addTableCell(row, String.valueOf(questionNumber), false);
        addTableCell(row, formatMoney(rewards[i][1]), false);
        table.addView(row);
    }

    dialog.show();
}

    private void addTableCell(TableRow row, String text, boolean isHeader) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 12, 8, 12);

        if (isHeader) {
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(null, Typeface.BOLD);
        } else {
            // Nếu là dòng được highlight, đổi màu chữ cho dễ đọc
            if (row.getBackground() != null && row.getBackground().getConstantState() != null
                    && row.getBackground().getConstantState().equals(
                    new ColorDrawable(Color.parseColor("#FFD700")).getConstantState())) {
                textView.setTextColor(Color.BLACK); // Chữ đen trên nền vàng
            } else {
                textView.setTextColor(Color.BLACK); // Chữ đen trên nền trắng/xám
            }
        }

        row.addView(textView, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
    }
    private int getRewardForQuestion(int questionIndex) {
        // Tính toán chỉ số trong bảng phần thưởng (15 câu hỏi, index bắt đầu từ 0)
        int rewardIndex = 15 - (questionIndex + 1); // Cộng 1 vì questionIndex bắt đầu từ 0

        // Đảm bảo không vượt quá số câu hỏi có phần thưởng
        if (rewardIndex >= 0 && rewardIndex < rewards.length) {
            return rewards[rewardIndex][1]; // Trả về số tiền thưởng cho câu hỏi
        }
        return 0; // Nếu không có phần thưởng, trả về 0
    }
    private void setAnswerOptionsEnabled(boolean enabled) {
        findViewById(R.id.Aanswer).setEnabled(enabled);
        findViewById(R.id.Banswer).setEnabled(enabled);
        findViewById(R.id.Canswer).setEnabled(enabled);
        findViewById(R.id.Danswer).setEnabled(enabled);
    }
    private void setHelpButtonsEnabled(boolean enabled) {
        findViewById(R.id.help_5050).setEnabled(enabled);
        findViewById(R.id.help_5050).setAlpha(enabled ? 1.0f : 0.5f);

        findViewById(R.id.help_audience).setEnabled(enabled);
        findViewById(R.id.help_audience).setAlpha(enabled ? 1.0f : 0.5f);

        findViewById(R.id.help_call).setEnabled(enabled);
        findViewById(R.id.help_call).setAlpha(enabled ? 1.0f : 0.5f);
    }
    private void updateHighScoreInFirebase(int newHighScore) {
        String userId = PrefHelper.getUserId(this);
        if (userId == null || userId.isEmpty()) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.child("highScore").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentHighScore = snapshot.getValue(Integer.class);
                if (currentHighScore == null || newHighScore > currentHighScore) {
                    // Chỉ cập nhật nếu điểm mới cao hơn
                    userRef.child("highScore").setValue(newHighScore)
                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "HighScore updated"))
                            .addOnFailureListener(e -> Log.e("Firebase", "Update failed", e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read highScore", error.toException());
            }
        });
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

        if (soundManager != null && AppConfig.isVolumeOn && questionItems != null && questionItems.size() > 0) {
            // Chỉ phát âm nếu đang bật volume
            soundManager.play(this, R.raw.question, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.stop();
        }
    }

}