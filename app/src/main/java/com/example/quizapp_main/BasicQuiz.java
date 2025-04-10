package com.example.quizapp_main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
    List<QuestionItem> questionItems;
    int currentQuestion = 0;
    int correct = 0;
    int wrong = 0;
    int totalMoney = 0;
    TextView questionNumberText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_basic_quiz);

        quiztext = findViewById(R.id.quizText);
        questionNumberText = findViewById(R.id.questionNumber);
        Aans = findViewById(R.id.Aanswer);
        Bans = findViewById(R.id.Banswer);
        Cans = findViewById(R.id.Canswer);
        Dans = findViewById(R.id.Danswer);

        TextView tvMoney = findViewById(R.id.currentMoney);
        tvMoney.setOnClickListener(v -> showRewardTable());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //thoát game
        Button quitButton = findViewById(R.id.btn_give_up);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một hộp thoại xác nhận khi nhấn nút bỏ cuộc
                new MaterialAlertDialogBuilder(BasicQuiz.this)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn bỏ cuộc không?")
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            // Quay lại màn hình chính
                            startActivity(new Intent(BasicQuiz.this, MainActivity.class));
                            finish();
                        })
                        .show();
            }
        });
        loadAllQuestion();
        Collections.shuffle(questionItems);



        Aans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswerClick(v, questionItems.get(currentQuestion).getAnswer1());
            }
        });

        Bans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswerClick(v, questionItems.get(currentQuestion).getAnswer2());
            }
        });

        Cans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswerClick(v, questionItems.get(currentQuestion).getAnswer3());
            }
        });

        Dans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswerClick(v, questionItems.get(currentQuestion).getAnswer4());
            }
        });

        findViewById(R.id.help_5050).setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            v.setEnabled(false);

            String correct = questionItems.get(currentQuestion).getCorrect();

            // Tạo danh sách các đáp án và TextView tương ứng
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

            // Tìm các đáp án sai
            List<Integer> wrongIndexes = new ArrayList<>();
            for (int i = 0; i < answers.size(); i++) {
                if (!answers.get(i).equals(correct)) {
                    wrongIndexes.add(i);
                }
            }

            // Trộn và chọn 2 đáp án sai bất kỳ để ẩn
            Collections.shuffle(wrongIndexes);
            int remove1 = wrongIndexes.get(0);
            int remove2 = wrongIndexes.get(1);

            answerViews.get(remove1).setVisibility(View.INVISIBLE);
            answerViews.get(remove2).setVisibility(View.INVISIBLE);
        });

        findViewById(R.id.help_audience).setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            v.setEnabled(false);

            String correctAnswer = questionItems.get(currentQuestion).getCorrect();
            showAudienceDialog(correctAnswer); // gọi đúng hàm mới có tham số
        });

        findViewById(R.id.help_call).setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            v.setEnabled(false);

            // Số điện thoại giả định để gọi
            String phoneNumber = "0123456789";

            // Tạo Intent mở ứng dụng gọi điện
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));

            // Kiểm tra xem thiết bị có ứng dụng gọi điện không
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Nếu không có app gọi điện (thường là giả lập), hiển thị đáp án đúng
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
        resetAnswerColors();

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

    private void loadAllQuestion() {
        questionItems = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("easyquestion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<QuestionItem> easyList = new ArrayList<>();
                for (DataSnapshot questionSnap : snapshot.getChildren()) {
                    QuestionItem item = questionSnap.getValue(QuestionItem.class);
                    easyList.add(item);
                }

                Collections.shuffle(easyList);
                questionItems.addAll(easyList.subList(0, Math.min(4, easyList.size()))); // Lấy 5 câu

                loadMediumQuestions(dbRef);
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private void loadMediumQuestions(DatabaseReference dbRef) {
        dbRef.child("mediumquestion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<QuestionItem> mediumList = new ArrayList<>();
                for (DataSnapshot questionSnap : snapshot.getChildren()) {
                    QuestionItem item = questionSnap.getValue(QuestionItem.class);
                    mediumList.add(item);
                }

                Collections.shuffle(mediumList);
                questionItems.addAll(mediumList.subList(0, Math.min(5, mediumList.size()))); // Lấy 4 câu

                loadHardQuestions(dbRef);
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private void handleAnswerClick(View v, String selectedAnswer) {
        // Kiểm tra câu trả lời đúng hay sai
        if (selectedAnswer.equals(questionItems.get(currentQuestion).getCorrect())) {
            correct++;
            v.setBackgroundResource(R.color.green);

            // Cộng số tiền thưởng vào tổng số tiền
            int reward = getRewardForQuestion(currentQuestion);
            totalMoney = reward;

            // Cập nhật TextView hiển thị số tiền
            TextView tvMoney = findViewById(R.id.currentMoney);
            tvMoney.setText(formatMoney(totalMoney));

            if (currentQuestion < questionItems.size() - 1) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    currentQuestion++;
                    setQuestionScreen(currentQuestion);
                }, 500);
            } else {
                // Nếu là câu cuối cùng và trả lời đúng
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(BasicQuiz.this, ResultActivity.class);
                    intent.putExtra("correct", correct);
                    intent.putExtra("wrong", wrong);
                    intent.putExtra("totalMoney", totalMoney);
                    startActivity(intent);
                    finish();
                }, 500);
            }
        } else {
            wrong++;
            v.setBackgroundResource(R.color.red);

            // Nếu trả lời sai, hiển thị đáp án đúng rồi kết thúc
            showCorrectAnswerAndFinish();
        }

        // Kiểm tra xem v có phải là Button không và nếu có thì cast nó
        if (v instanceof Button) {
            Button button = (Button) v;
            button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        }
    }

    private void loadHardQuestions(DatabaseReference dbRef) {
        dbRef.child("hardquestion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<QuestionItem> hardList = new ArrayList<>();
                for (DataSnapshot questionSnap : snapshot.getChildren()) {
                    QuestionItem item = questionSnap.getValue(QuestionItem.class);
                    hardList.add(item);
                }

                Collections.shuffle(hardList);
                questionItems.addAll(hardList.subList(0, Math.min(4, hardList.size()))); // Lấy 3 câu

                loadSuperHardQuestions(dbRef);
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private void loadSuperHardQuestions(DatabaseReference dbRef) {
        dbRef.child("superhardquestion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<QuestionItem> superHardList = new ArrayList<>();
                for (DataSnapshot questionSnap : snapshot.getChildren()) {
                    QuestionItem item = questionSnap.getValue(QuestionItem.class);
                    superHardList.add(item);
                }

                Collections.shuffle(superHardList);
                questionItems.addAll(superHardList.subList(0, Math.min(2, superHardList.size()))); // Lấy 3 câu

                setQuestionScreen(currentQuestion); // Chỉ set sau khi đủ 15 câu
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
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


//    phần thưởng
// Trong Activity/Fragment của bạn
    private void showRewardTable() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.reward_table);

        // Lấy dữ liệu tiền thưởng (có thể thay bằng dữ liệu thực)
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

    TableLayout table = dialog.findViewById(R.id.reward_table);

    // Thêm tiêu đề
        TableRow headerRow = new TableRow(this);
        addTableCell(headerRow, "Câu hỏi", true);
        addTableCell(headerRow, "Tiền thưởng", true);
        table.addView(headerRow);

        // Thêm các hàng dữ liệu
        for (int i = 0; i < rewards.length; i++) {
            TableRow row = new TableRow(this);

            // Highlight dòng hiện tại (currentQuestion + 1 vì index bắt đầu từ 0)
            if ((rewards.length - i) == currentQuestion + 1) {
                row.setBackgroundColor(Color.parseColor("#FFD700")); // Màu vàng
            } else if (i % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#F5F5F5")); // Màu xám nhạt
            } else {
                row.setBackgroundColor(Color.WHITE);
            }

            addTableCell(row, String.valueOf(rewards[i][0]), false);
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

    private String formatMoney(int amount) {
        return String.format("%,d", amount) + " VND";
    }
    private int getRewardForQuestion(int questionIndex) {
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

        // Tính toán chỉ số trong bảng phần thưởng (15 câu hỏi, index bắt đầu từ 0)
        int rewardIndex = 15 - (questionIndex + 1); // Cộng 1 vì questionIndex bắt đầu từ 0

        // Đảm bảo không vượt quá số câu hỏi có phần thưởng
        if (rewardIndex >= 0 && rewardIndex < rewards.length) {
            return rewards[rewardIndex][1]; // Trả về số tiền thưởng cho câu hỏi
        }
        return 0; // Nếu không có phần thưởng, trả về 0
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
            intent.putExtra("correct", correct);
            intent.putExtra("wrong", wrong);
            intent.putExtra("totalMoney", totalMoney);
            startActivity(intent);
            finish();
        }, 1000);
    }





    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to exit the quiz?")
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                    super.onBackPressed(); // Gọi lại hành vi mặc định nếu cần
                })
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    startActivity(new Intent(BasicQuiz.this, MainActivity.class));
                    finish();
                })
                .show();
    }
}