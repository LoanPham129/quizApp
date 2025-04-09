package com.example.quizapp_main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp_main.R;
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

public class BasicQuiz extends AppCompatActivity {

    TextView quiztext, Aans, Bans, Cans, Dans;
    List<QuestionItem> questionItems;
    int currentQuestion = 0;
    int correct = 0;
    int wrong = 0;
    Intent intent;
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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loadAllQuestion();
        Collections.shuffle(questionItems);

        Aans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer1().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Aans.setBackgroundResource(R.color.green);
                    Aans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Aans.setBackgroundResource(R.color.red);
                    Aans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion); // Đã gọi resetAnswerColors() bên trong
                        }
                    },500);
                } else {
                    Intent intent = new Intent(BasicQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
            }
        });


        Bans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer2().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Bans.setBackgroundResource(R.color.green);
                    Bans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Bans.setBackgroundResource(R.color.red);
                    Bans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion);
                        }
                    },500);
                } else {
                    Intent intent = new Intent(BasicQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Cans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer3().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Cans.setBackgroundResource(R.color.green);
                    Cans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Cans.setBackgroundResource(R.color.red);
                    Cans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion);
                        }
                    },500);
                } else {
                    Intent intent = new Intent(BasicQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Dans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionItems.get(currentQuestion).getAnswer4().equals(questionItems.get(currentQuestion).getCorrect())){
                    correct++;
                    Dans.setBackgroundResource(R.color.green);
                    Dans.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    Dans.setBackgroundResource(R.color.red);
                    Dans.setTextColor(getResources().getColor(R.color.white));
                }
                if (currentQuestion < questionItems.size()-1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestion++;
                            setQuestionScreen(currentQuestion);
                        }
                    },500);
                } else {
                    Intent intent = new Intent(BasicQuiz.this, ResultActivity.class);
                    intent.putExtra("correct",correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();
                }
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
        Aans.setTextColor(getResources().getColor(R.color.white));

        Bans.setBackgroundResource(R.color.primary_color);
        Bans.setTextColor(getResources().getColor(R.color.white));

        Cans.setBackgroundResource(R.color.primary_color);
        Cans.setTextColor(getResources().getColor(R.color.white));

        Dans.setBackgroundResource(R.color.primary_color);
        Dans.setTextColor(getResources().getColor(R.color.white));
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

    private List<QuestionItem> loadQuestionsFromJson(String fileName, String arrayKey, int numberOfQuestions) {
        List<QuestionItem> list = new ArrayList<>();
        String json = loadJsonFromAsset(fileName);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray questions = jsonObject.getJSONArray(arrayKey);

            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < questions.length(); i++) {
                indices.add(i);
            }

            Collections.shuffle(indices);

            for (int i = 0; i < numberOfQuestions && i < indices.size(); i++) {
                JSONObject question = questions.getJSONObject(indices.get(i));

                String questionString = question.getString("question");
                String answer1String = question.getString("answer1");
                String answer2String = question.getString("answer2");
                String answer3String = question.getString("answer3");
                String answer4String = question.getString("answer4");
                String correctString = question.getString("correct");

                list.add(new QuestionItem(
                        questionString,
                        answer1String,
                        answer2String,
                        answer3String,
                        answer4String,
                        correctString
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    private String loadJsonFromAsset(String s){
        String json = "";
        try {
            InputStream inputStream =  getAssets().open(s);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e){
            e.printStackTrace();
        }
        return json;
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